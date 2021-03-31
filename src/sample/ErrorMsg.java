package sample;

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
    public int checkLogin()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode()) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка отправки","Не удалось отправить код регистрации");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Отправка кода подтверждения","На Ваш email выслан код подтверждения действия");
                return 0;
        }
        return 1;
    }

    public int checkCode()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode()) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка кода подтверждения","Неверный код регистрации! (возможно устарел)");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Код регистрации","Данные из QR-кода верные");
                return 0;
        }
        return 1;
    }

    public int getCode()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode()) {
            case 2:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка отправки почты","Сервер не смог отправить данные на указанную почту");
                return 2;
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка почты","На данную почту уже зарегестрирован пользователь!");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Код регистрации","На указанную почту выслан QR-код проверки");
                return 0;
        }
        return 1;
    }

    public int registration()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode()) {
            case 1:
                showErrorWindow(Alert.AlertType.ERROR,"Ошибка","Ошибка регистрации","Пользователь с таким логином уже существует. Выберите другой логин.");
                return 1;
            case 0:
                showErrorWindow(Alert.AlertType.INFORMATION,"Успех","Регистрация","Поздравляем! Вы успешно зарегистрированы");
                return 0;
        }
        return 1;
    }


}
