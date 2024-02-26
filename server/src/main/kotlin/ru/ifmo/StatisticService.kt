package ru.ifmo

import io.ktor.util.collections.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.ifmo.models.FindResult
import ru.ifmo.models.Result
import ru.ifmo.models.StatisticId
import ru.ifmo.models.StatisticInfo
import ru.ifmo.models.Status
import java.util.*

class StatisticService {
    private val tasks: MutableMap<StatisticId, Result> = ConcurrentMap()

    fun find(title: String) = FindResult(tasks.values.filter { title in it.title })

    fun getStatus(statisticId: String): Status = when (tasks[StatisticId(statisticId)]) {
        is Result -> Status.COMPLETED
        else -> Status.PROCESSING
    }

    fun process(statisticInfo: StatisticInfo, scope: CoroutineScope): StatisticId {
        val statisticId = StatisticId(UUID.randomUUID().toString()) // assuming no collision will be
        scope.launch {
            val stat = statisticInfo.files.map { (_, content) ->
                "[A-Za-z0-9]+".toRegex().findAll(content).groupingBy { it.value.lowercase() }.eachCount()
            }.fold(mutableMapOf<String, Int>()) { accumulator, currentMap ->
                currentMap.forEach { (key, value) ->
                    accumulator[key] = accumulator.getOrDefault(key, 0) + value
                }
                accumulator
            }

            tasks[statisticId] = Result(
                statisticId.id,
                statisticInfo.title,
                stat.values.sum(),
                stat,
            )
        }
        return statisticId
    }

    fun getStatistic(statisticId: String): Result? = tasks[StatisticId(statisticId)]
}
