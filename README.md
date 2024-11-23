# Дипломная работа "Облачное хранилище"


## Описание проекта


REST-сервис.

Сервис предоставляет интерфейс для загрузки файлов по заранее описанной спецификации.

Работает с заранее подготовленным FRONT по адресу http://localhost:8080
>Описание запуска FRONT указано здесь: [ссылка](https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md)


## Запуск приложения



* Запуск производится с помощью Docker Compose
> docker-compose up -d


* Для совместимости с FRONT приложение работает на порту 8081

> http://localhost:8081/


Также в приложении используется Spring Security. 

В БД добавлены два пользователя:
* login: user1@gmail.com, password: 12345
* login: user2@gmail.com, password: 123456
