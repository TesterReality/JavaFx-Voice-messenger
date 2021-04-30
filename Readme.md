[![Vacoom Voice Messenger](https://thumb.cloud.mail.ru/weblink/thumb/xw1/yKtd/KV9eVB4VN/GitLogoVoice.jpg)]()
# Vacoom Voice Messenger (используя JavaFx)
![JDK version](https://img.shields.io/static/v1?label=JDK&message=1.8%2B&color=<COLOR>)
![Database](https://img.shields.io/static/v1?label=Database&message=PostgreSQL&color=<COLOR>)
![build](https://img.shields.io/static/v1?label=build&message=Gradle&color=<COLOR>)

Vacoom Voice Messenger - голосовой мессенджер с криптографической защитой. Мессенджер содержит клиентскую и серверную часть. Мессенджер позволяет:

* совершать криптографически защищенный сеанс аудиосвязи между двумя пользователями
* передавать сообщения по защищенному каналу
 
В данном проекте реализован собственный движок для смайликов на JavaFx.

## _Причины создания_

 Данный проект является попыткой создания собственного голосового мессенджера с криптографической защитой, ведь мы далеко не всегда может быть уверены в том, что нас не прослушивают.

## _Скриношты (на текущий момент времени, проект еще в разработке)_
[![Vacoom Voice Messenger](https://thumb.cloud.mail.ru/weblink/thumb/xw1/yKtd/KV9eVB4VN/1.png)]()
[![Vacoom Voice Messenger](https://thumb.cloud.mail.ru/weblink/thumb/xw1/yKtd/KV9eVB4VN/2.png)]()
[![Vacoom Voice Messenger](https://thumb.cloud.mail.ru/weblink/thumb/xw1/yKtd/KV9eVB4VN/3.jpg)]()
[![Vacoom Voice Messenger](https://media.giphy.com/media/1OX6RCF1I4siH0mmHM/giphy.gif)]()
[![Vacoom Voice Messenger](https://thumb.cloud.mail.ru/weblink/thumb/xw1/yKtd/KV9eVB4VN/5.png)]()
[![Vacoom Voice Messenger](https://thumb.cloud.mail.ru/weblink/thumb/xw1/yKtd/KV9eVB4VN/6.jpg)]()
[![Vacoom Voice Messenger](https://thumb.cloud.mail.ru/weblink/thumb/xw1/yKtd/KV9eVB4VN/7.jpg)]()
[![Vacoom Voice Messenger](https://media.giphy.com/media/AaJsnzyNRIJMIGcD7I/giphy.gif)]()

## _Участие в разработке_

Перед разработкой необходимо создать файл `config.properties` в директории `src/main/resources/conf` в gradle-модуле `server` со следущющими полями:

* db.login - имя пользователя БД
* db.password - пароль пользователя БД
* db.url - url базы данных pgsql, включая `jdbc:postgresql://`

Серверная часть проекта использует сервис для хранения изображений (https://cloudinary.com/), поэтому в файле `config.properties` также обязательно указываются следующие поля, название которых соответствует разделу **Account Details** на сайте:

* cloud&#46;name - Cloud name
* api.key - API Key
* api.secret - API Secret

Пример файла `config.properties` :

```
db.login = qnrdhkiobgd
db.password = qwca75e4e677y9a43df89e483cbe52fcb8cbf5bchhf9
db.url= jdbc:postgresql://ecw2-7771-4.eu-west.compute.amazonaws.com/88fjdhgshgnds
cloud.name= vacoommem
api.key= 777375577712777
api.secret= McELkeGtiA-hghdfhdfh
```
Все зависимости устанавливаются при помощи Gradle. Достаточно выполнить следующую команду:

```
gradle --refresh-dependencies clean build
```

Если Вы используете IDE, то при открытии проекта это выполнится автоматически.

Перед внесением изменений делайте pull request. В случае больших изменений открывайте issue для обсуждения того, что Вы хотели бы изменить.

## _Сборка проекта в JAR_

Чтобы собрать **клиентскую** часть в jar необходимо выполнить следующую команду:
```
gradle jfxJar
```
Чтобы собрать **серверную** часть в jar необходимо выполнить следующую команду:
```
gradle :server:jar
```

## _Лицензия_
Этот репозиторий находится под лицензией MIT. Подробную информаци вы можете найти [здесь](https://github.com/TesterReality/JavaFx-Voice-messenger/blob/main/LICENSE "MIT лицензия") или на [официальном сайте](https://opensource.org/licenses/MIT "MIT лицензия").
