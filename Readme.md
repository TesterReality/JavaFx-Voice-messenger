[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/GitLogoVoice.jpg)]()
# Vacoom Voice Messenger (используя JavaFx)
![JDK version](https://img.shields.io/static/v1?label=JDK&message=1.8%2B&color=<COLOR>)
![Database](https://img.shields.io/static/v1?label=Database&message=PostgreSQL&color=<COLOR>)

Обмен голосовыми сообщениями в реальном времени между двумя пользователями является актуальной проблемой на данное время.

 Основной проблемой обмена сообщениями в реальном времени является их задержка и качество связи. Далеко не все голосовые мессенджеры позволяют качественно и без существенных задержек совершать голосовые звонки.

 Данный проект является попыткой создания собственного голосового мессенджера с криптографической защитой, ведь мы далеко не всегда может быть уверены в том, что нас не прослушивают.
## _Основные(!) Актуальные задачи_
- [x] Главная форма авторизации
- [x] Подгрузка формы внутрь существущей формы
    - [x] Регистрация
    - [x] Восстановление пароля
    - [x] QR-код
- [x] Генерация QR-кода с логотпом
- [x] Генерация индивидуальных аватарок на основе ника
- [x] Возможность изменения аватарки (хранится на сервере)
- [x] Вывод списка друзей
- [x] Принятие запроса на дружбу (с формой)
- [x] Отклонение запроса на дружбу (с формой)
- [ ] Сбственный движок для смайликов
    - [x] Разбитие спрайта со смайлами на отдельные смайлики
    - [x] Окно вывода смайликов
    - [x] Информация о смайлике при наведении
    - [x] Возможность вставлять смайлики в текстовое поле
    - [ ] Корретная обработка вставляемых в текстовое поле смайликов
- [ ] Собственный протокола обмена данными (на подобии XMPP)
    - [ ] Обмена ключами шифрования между клиентом и сервером
    - [x] Проверка QR-кода
    - [x] Авторизация
    - [x] Регистрация
    - [x] Изменение пароля
    - [x] Получение аватара пользователя
    - [x] Изменение аватара пользователя
    - [x] Получение списка друзей(и подавших заявку) со статусом онлайна и аватаркой
    - [x] Принятие заявки в друзья
    - [x] Отклонение заявки в друзья
    - [ ] Подача заявки в друзья
    - [ ] Поиск пользователей
    - [ ] Установление соединения при звонке другу
    - [ ] Обработка звонка от другого пользователя
    - [ ] Отправка сообщений пользвателю
    - [ ] Обмена ключами шифрования между пользователями
- [ ] Локальная БД
    - [ ] Хранение ключей шифрования для обеспечение непрерывности ключевого материала
    - [ ] Хранение сообщений от пользвателей
- [ ] Создать форму звонка
- [ ] Создать форму настроек
- [ ] Создать форму входящего звонка (когда ты или тебе звонят)
- [ ] Исправить стилестические ошибки кода
- [ ] Удалить бекапы из гита
- [ ] Подключить анализатор кода
- [ ] Использовать Logger вместо sout
- [ ] Использовать отсутствие классов (где хранятся пароли), переделать чтобы пароли и логины хранились в файле
- [ ] Добавить в README информацию о том, что необходимо для работы проекта
- [ ] Добавить SQL-файлик
- [ ] Постараться перевести проект на Maven

## _Скриношты (на текущий момент времени, проект еще в разработке)_
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/1.png)]()
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/2.png)]()
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/3.jpg)]()
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/gif_qr.gif)]()
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/5.png)]()
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/6.jpg)]()
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/7.jpg)]()
[![Vacoom Voice Messenger](https://raw.githubusercontent.com/TesterReality/JavaFx-Voice-messenger/main/gitRes/smile_gif.gif)]()