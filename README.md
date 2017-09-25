#Приложение для управления БД

<b>Скриншоты приложения</b>

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
<b>Описание</b>

Программа для просмотра и редактирования информации на сервере БД MS SQL. Кроме просмотра реализованы механизмы редактирования баз данных, таблиц, записей. Также доступны пользовательские запросы к базе данных, с возможностью сохранения запросов и повторного запуска (вывод результатов запроса осуществляется в окне самого запроса). Проект в альфа-стадии, есть мысли его развить.

***
<b>Настройки серверов для подключения программы</b>
***
#MS SQL
- изменить проверку подлинности сервера: 

в настройках сервера изменить тип проверки подлинности на проверку SQL и Windows
- изменить порт сервера MS SQL: 

Компьютер -> Управление -> Службы и приложения -> Сетевая конфигурация SQL Server -> протоколы для SQLEXPRESS -> (здесь включить TCP/IP если отключено) свойства в TCP/IP -> IP-адреса -> IPALL (в самом низу) очистить "Динамические TCP-порты" и в "TCP-порт" прописать 1433. 
После найстройки перезапустить службу SQL Server.
- добавить правила в брэндмауэр: 

панель управления -> все элементы -> брэндмауэр -> дополнительные параметры -> правила для входящих подключений -> создать правило -> для порта -> протокол TCP (потом аналогично сделать и для UDP) порт 1433 -> Разрешить подключения -> все профили выбраны -> произвольное имя (лучше осмысленное, например: SQL Server UDP 1433). 
Не забыть прописать TCP и UDP.
***
#MySQL
- необходимо прописать права на подключение пользователю root с хоста вашего устройства (либо создать пользователя с правами подключения с хоста вашего устройства)
- в брэндмауэре добавить правила аналогично правилам из MS SQL, только для порта 3306

***
<b>В проекте были использованы</b>

RecyclerView, CardView, Custom View, Custom Adapter, Broadcast Receiver, Service, SQLite, Content Provider, Support Library. написан свой сервис для прямых запросов к БД, написан генератор View элементов полей для отображения при редактировании таблиц...

***
<b>Актуальный релиз</b>

DatabaseViewer.v.1.005.alpha (https://github.com/orloffski/DatabaseViewer/releases/tag/1.005)

***
Все релизы доступны: https://github.com/orloffski/DatabaseViewer/releases

# License
	Copyright 2016 Orlovsky Engen

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
