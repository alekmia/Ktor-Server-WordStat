# Service for collecting statistics

Service counts the amount of words in files and saves the information.

* **Ktor** was used for implementing the server
* **kotlinx.serialization** was used for serialization
* Statistics are calculated in parallel for different files - **coroutines**.


File with documentation - [./openapi/statistics.yaml](./openapi/statistics.yaml) 

## /statistic/create 
Creates a new statistics in the service and returns its ID. If the Idempotency-Key header is passed, then you should remember it and give the previously created identifier when you repeat the request with the same value. The operation itself can be long, so use /statistic/status to track the status

## /statistic/status
Returns information about current and completed calculations of statistics.

## /statistic/item
Allows you to get previously calculated statistics.

## /statistic/find
Allows you to get a list of statistics based on the occurrence of a given phrase in the header of saved statistics.

