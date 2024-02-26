package ru.ifmo.statistic

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class ServiceTests {
    @Test
    fun scenario() = testApplication {
        coroutineScope {
            val (marioId, spiderId) = awaitAll(
                async { runStatistic("Super Mario", Mario) },
                async { runStatistic("Spider Man", Spider) },
            )

            val (marioCompletion, spiderCompletion) = awaitAll(
                async { awaitCompleted(marioId) },
                async { awaitCompleted(spiderId) },
            )
            assertTrue(marioCompletion)
            assertTrue(spiderCompletion)

            val (marioStatistic, spiderStatistic) = awaitAll(
                async { getStatistic(marioId) },
                async { getStatistic(spiderId) },
            )

            val expectedMarioStatistic = getMarioStatistic(marioId)
            val expectedSpiderStatistic = getSpiderStatistic(spiderId)

            assertEquals(expectedMarioStatistic, marioStatistic)
            assertEquals(expectedSpiderStatistic, spiderStatistic)

            val compoId = runStatistic("Super Mario & Spider Man", Mario, Spider)
            assertTrue(awaitCompleted(compoId))
            val comboStatistic = getStatistic(compoId)

            val expectedComboStatistic = getComboStatistic(compoId)
            assertEquals(expectedComboStatistic, comboStatistic)

            val findResult = findStatistic("Super Mario")
            val findStatistics = findResult.getValue("statistics").jsonArray.toSet()
            assertEquals(setOf(expectedComboStatistic, expectedMarioStatistic), findStatistics)
        }
    }

    private suspend fun ApplicationTestBuilder.runStatistic(title: String, vararg files: File): String {
        val response = client.post("/statistic/create") {
            contentType(ContentType.MultiPart.FormData)
            setBody(createFormData(title, *files))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val responseData = response.bodyAsJsonObject()
        return responseData.getString("id")
    }

    private suspend fun ApplicationTestBuilder.runStatistic409(title: String, vararg files: File) {
        val response = client.post("/statistic/create") {
            contentType(ContentType.MultiPart.FormData)
            setBody(createFormData(title, *files))
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    private suspend fun ApplicationTestBuilder.awaitCompleted(id: String): Boolean {
        for (i in 0 until 5) {
            delay(100.milliseconds)
            val response = client.get("/statistic/status?id=$id")
            assertEquals(HttpStatusCode.OK, response.status)
            val responseData = response.bodyAsJsonObject()
            val status = responseData.getString("status")
            assertTrue(status in listOf("PROCESSING", "COMPLETED"))
            if (status == "COMPLETED") return true
        }
        return false
    }

    private suspend fun ApplicationTestBuilder.getStatistic(id: String): JsonObject {
        val response = client.get("/statistic/item?id=$id")
        assertEquals(HttpStatusCode.OK, response.status)
        return response.bodyAsJsonObject()
    }

    private suspend fun ApplicationTestBuilder.findStatistic(title: String): JsonObject {
        val response = client.get("/statistic/find?title=$title")
        assertEquals(HttpStatusCode.OK, response.status)
        return response.bodyAsJsonObject()
    }

    private fun createFormData(title: String, vararg files: File): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            parts = formData {
                append("title", title)
                files.forEach { file ->
                    val headers = Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Text.Plain)
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    }
                    append("files", file.readBytes(), headers)
                }
            },
            boundary = "WebAppBoundary",
        )
    }

    companion object {
        private val Mario = resourceFile("super_mario.txt")
        private val Spider = resourceFile("spider_man.txt")

        private fun getMarioStatistic(id: String) = resourceJson("super_mario.json").withId(id)

        private fun getSpiderStatistic(id: String) = resourceJson("spider_man.json").withId(id)

        private fun getComboStatistic(id: String) = resourceJson("combo.json").withId(id)

        private fun resourceFile(name: String): File {
            val path = "/ru/ifmo/statistic/ServiceTests/$name"
            val url = ServerReady::class.java.getResource(path)
                ?: error("Missing resource file by path: $path")
            return File(url.toURI())
        }

        private fun resourceJson(path: String): JsonElement =
            Json.parseToJsonElement(resourceFile(path).readText())

        private fun JsonElement.withId(id: String): JsonElement {
            val obj = this.jsonObject
            return JsonObject(obj + ("id" to JsonPrimitive(id)))
        }

        private suspend fun HttpResponse.bodyAsJsonObject(): JsonObject =
            Json.parseToJsonElement(bodyAsText()).jsonObject

        private fun JsonObject.getString(property: String): String {
            val value = get(property)?.jsonPrimitive
            assertTrue(value != null && value.isString)
            return value.content
        }
    }
}
