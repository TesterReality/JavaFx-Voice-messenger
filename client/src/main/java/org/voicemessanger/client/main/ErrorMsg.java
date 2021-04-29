package org.voicemessanger.client.main;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ErrorMsg {

    public ErrorMsg() {
    }

    private void showErrorWindow(Alert.AlertType type,String title, String header, String content)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(header);
                alert.setContentText(content);
                alert.showAndWait();
            }
        });
    }

    public int chnageAvatar()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("updateAvatars")) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Произошла ошибка","Невозмжно изменить аватарку");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Изменение аватара","Аватар успешно изменен");
                return 0;
        }
        return 1;
    }
    public int changerPswd()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("changePswd")) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Произошла ошибка","Ошибка изменения пароля");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Изменение пароля","Пароль успешно изменен");
                return 0;
        }
        return 1;
    }

    public int checkLogin()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("authorization")) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка отправки","Не удалось отправить код регистрации");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Отправка кода подтверждения","На Ваш email выслан код подтверждения действия");
                return 0;
        }
        return 1;
    }

    public int checkCode(int number)
    {
        switch (number) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка кода подтверждения","Неверный код регистрации! (возможно устарел)");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Код регистрации","Данные из QR-кода верные");
                return 0;
        }
        return 1;
    }

    public int getCode(int number)
    {

        switch (number) {
            case 3:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка отправки почты","На данную почту не зарегистрирован пользователь");
                return 3;
            case 2:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка отправки почты","Сервер не смог отправить данные на указанную почту");
                return 2;
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка почты","На данную почту уже зарегестрирован пользователь!");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Код выслан","На указанную почту выслан QR-код проверки");
                return 0;
        }
        return 1;
    }

    public int registration()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("registration")) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка регистрации","Пользователь с таким логином уже существует. Выберите другой логин.");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Регистрация","Поздравляем! Вы успешно зарегистрированы");
                return 0;
        }
        return 1;
    }
    public int checkFriend()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("getFriend")) {
            case 2:
                System.out.println("У тебя нет друзей (:");
                return 2;
            case 1:
                System.out.println("Не удалось обновить список друзей");
                return 1;
            case 0:
                System.out.println("Список друзей обновлен");
                return 0;
        }
        return 1;
    }
    public int confirmFriend()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("confirmFriend")) {
            case 1:
                System.out.println("Не удалось обновить список друзей");
                return 1;
            case 0:
                System.out.println("Список друзей обновлен");
                return 0;
        }
        return 1;
    }

    public int cancelFriend()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("cancelFriend")) {
            case 1:
                System.out.println("Не удалось отказать в дружбе");
                return 1;
            case 0:
                System.out.println("В дружбе успешно отказано");
                return 0;
        }
        return 1;
    }

}
