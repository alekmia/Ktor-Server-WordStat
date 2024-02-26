package ru.ifmo

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import ru.ifmo.models.StatisticInfo

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) { json() }
    val service = StatisticService()
    val scope = CoroutineScope(Job() + Dispatchers.Default)
    routing {
        post("/statistic/create") {
            var title = ""
            val files = mutableMapOf<String, String>()
            call.receiveMultipart().forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        title = part.value
                    }
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName as String
                        files += fileName to part.streamProvider().bufferedReader().readText()
                    }
                    else -> {}
                }
                part.dispose()
            }
            call.respond(status = HttpStatusCode.Created, service.process(StatisticInfo(title, files), scope))
        }

        get("/statistic/status") {
            call.respond(status = HttpStatusCode.OK, service.getStatus(call.parameters.getOrFail("id")))
        }

        get("/statistic/item") {
            call.respond(status = HttpStatusCode.OK, service.getStatistic(call.parameters.getOrFail("id"))!!)
        }

        get("/statistic/find") {
            call.respond(status = HttpStatusCode.OK, service.find(call.parameters.getOrFail("title")))
        }
    }
}
