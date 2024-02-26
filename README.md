# Зачетное задание по курсу Kotlin

В данном задании требуется написать небольшой веб-сервис по сбору статистики.

Сервис должен считать количество слов в файлах и сохранять эту информацию в памяти или базе данных.

Задание состоит из трех блоков. Полная реализация каждого из блоков оценивается в 1 балл (100 / 7 баллов в БАРС). 

## Часть 1. Базовое API сервиса

Реализуйте api методы сервиса в соответствии openapi документацией в файле [./openapi/statistics.yaml](./openapi/statistics.yaml).

* Для реализации веб-сервера следует воспользоваться [Ktor](https://ktor.io/). 
* Для сериализации следует использовать kotlinx.serialization.
* Для управления зависимостями (Dependency Injection) можно использовать [Koin](https://insert-koin.io/).
* Подсчет статистики **должен выполняться параллельно** для разных файлов. В связи с этим особенно внимательно стоит отнестись к вопросу разделённой памяти между потоками.
* Весь подсчет статистики, и общение между процессами (например, от сервера к базе) должны происходить с использованием корутин.
* Для этой части достаточно хранить всю информацию о запросах в оперативной памяти (см часть 2).
* Для этой части достаточно корректно обрабатывать только успешные запросы (см часть 3).

## Часть 2. Использование базы данных
 Реализуйте сохранение информации о запросах в персистентной базе данных.

* Рекомендуется использовать MySQL для данного задания, чтобы не мучится с правкой тестов.
* Для взаимодействия следует использовать соответствующую kotlin-специфичную библиотеку.

## Часть 3. Поддержать все указанные в API статусы ошибок

Поддержите все http статусы и корректные сообщения об ошибках в соответствии с документацией.
