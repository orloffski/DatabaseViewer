<b>Приложение для управления БД</b>

#Скриншоты приложения

![login](https://cloud.githubusercontent.com/assets/12079742/21699806/6911a44e-d3ae-11e6-9ded-fbf7073ca3e8.png)
![dblist](https://cloud.githubusercontent.com/assets/12079742/21699805/68fbf7de-d3ae-11e6-9e88-4fe7a8aaf4ca.png)
![menudb](https://cloud.githubusercontent.com/assets/12079742/21699810/691428b8-d3ae-11e6-8b31-a07e366cbd9b.png)
![renamedb](https://cloud.githubusercontent.com/assets/12079742/21699811/69165c32-d3ae-11e6-9c33-37873f4bf43e.png)
![createdb](https://cloud.githubusercontent.com/assets/12079742/21699803/68f891ca-d3ae-11e6-8409-b18036b709e9.png)
![tablelist](https://cloud.githubusercontent.com/assets/12079742/21699800/68f6bdc8-d3ae-11e6-98b6-74609462371b.png)
![menutable](https://cloud.githubusercontent.com/assets/12079742/21699807/69132602-d3ae-11e6-86d5-c315eeb7a020.png)
![altertable](https://cloud.githubusercontent.com/assets/12079742/21699802/68f7c63c-d3ae-11e6-91bd-6155df865635.png)
![createtable](https://cloud.githubusercontent.com/assets/12079742/21699801/68f7be30-d3ae-11e6-8f0f-033719a895d6.png)
![recordlist](https://cloud.githubusercontent.com/assets/12079742/21699809/69142f7a-d3ae-11e6-8000-e019ee6f0629.png)
![editrecord](https://cloud.githubusercontent.com/assets/12079742/21699808/6913ee70-d3ae-11e6-8019-2b75bb0a148a.png)
![addrecord](https://cloud.githubusercontent.com/assets/12079742/21699804/68f9d40e-d3ae-11e6-9c41-fa65b118928d.png)

***
#Описание

Доступны следующие действия:
- подключение к серверу
- вывод списка БД на сервере (создание/переименование/удаление БД)
- вывод списка таблиц в БД (создание/изменение/удаление таблиц)
- вывод записей из таблиц (добавление/изменение/удаление записей)
- изменено внешнее оформление списков (укрупнены надписи, изменена иконка меню элемента списка)
- из окна списка таблиц доступно окно списка запросов (через меню активности)
- добавлен вывод списка запросов с меню на создание/изменение/удаление запросов (запросы хранятся в базе приложения на устройстве)
- добавлено окно редактирования/выполнения запросов запроса
- добавлен вывод результата запроса в окне запроса
- изменены View элементы при заполнении записей для типов IMAGE и BIT (на кнопку загрузки файлов и чекбокс соответственно, кнопка временно отключена - пока нет времени писать диалог выбора файлов, то что удалось найти не устраивает)

Чего пока нет - но скоро все будет:
- типы данных доступны не все - пока не лезем в дебри и делаем по минимуму (предложения в issues приветствуются).
- типы БД ограничены только MSSQL - далее добавим выбор типов БД
- аналитика еще не подключена - этот релиз для сдачи курсача (собственно с чего и родился проект)

Релизы доступны: https://github.com/orloffski/DatabaseViewer/releases
