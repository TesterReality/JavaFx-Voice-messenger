package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import sample.ClientXmlPorocol.VacoomProtocol;
import sample.qr.QrCheckController;

public class RegistrationUserController extends VacoomProtocol {
    public AnchorPane loginXML;
    public TextField user_login;
    public PasswordField password;
    public PasswordField password1;
    QrCheckController parent;
    RegistrationUserController thisNode;
    ErrorMsg error;


    public QrCheckController getParent() {
        return parent;
    }

    public void setParent(QrCheckController parent) {
        this.parent = parent;
    }

    public RegistrationUserController getThisNode() {
        return thisNode;
    }

    public void setThisNode(RegistrationUserController thisNode) {
        this.thisNode = thisNode;
    }

    public RegistrationUserController() {
    }

    @FXML
    private void initialize()
    {
        error = new ErrorMsg();
    }

    public void registration(MouseEvent mouseEvent) {

        if(user_login.getText().length()<4 )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка логина");
            alert.setContentText("Логин должен быть больше 3х символов");
            alert.showAndWait();
            return;
        }
        if( password.getText().length()<8)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка пароля");
            alert.setContentText("Пароль должен быть длинее 7 символов");
            alert.showAndWait();
            return;
        }

        if (!password.getText().equals(password1.getText()))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка пароля");
            alert.setContentText("Пароли не совпадают! Проверьте правильность ввода");
            alert.showAndWait();
            return;
        }
/*
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(registrationUser("127.0.0.1","registration",user_login.getText(),md5.md5Custom(password.getText()),parent.codeInput.getText()));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);

        //допиши тут ошибка и все такое, тестируй на другом мыле

        new Thread(() -> {

            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode() == -1);
            ErrorMsg t = new ErrorMsg();
            if( t.checkRegistration()==0 )
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        parent.toStartPages();

                    }
                });
            }

            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);
        }).start();

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);
*/
    }
}
