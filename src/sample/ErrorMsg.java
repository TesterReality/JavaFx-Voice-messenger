package sample;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ErrorMsg {

    public ErrorMsg() {
    }

    public int checkLogin()
    {
        switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode()) {
            case 1:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Ошибка отправки");
                        alert.setContentText("Не удалось отправить код регистрации");
                        alert.showAndWait();
                    }
                });
                return 1;
            case 0:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Успех");
                        alert.setHeaderText("Отправка кода подтверждения");
                        alert.setContentText("На Ваш email выслан код подтверждения действия");
                        alert.showAndWait();
                    }
                });
                return 0;
        }
        return 1;
    }
}
