package org.voicemessanger.client.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;
import org.voicemessanger.client.qr.QrCheckController;


import java.io.IOException;


public class ChangePasswordController extends VacoomProtocol {
    public AnchorPane loginXML;
    public PasswordField check_password;
    public PasswordField password;
    public Text textHelloUserName;
    ChangePasswordController thisNode;
    QrCheckController parent;
    String nameLogin;
    String login;

    public String getNameLogin() {
        return nameLogin;
    }

    public void setNameLogin(String nameLogin) {
        this.nameLogin = "Здравствуйте, "+nameLogin;
        login = nameLogin;
    }

    public Text getTextHelloUserName() {
        return textHelloUserName;
    }

    public void setTextHelloUserName(String text) {
         textHelloUserName.setText(text);
    }

    public ChangePasswordController getThisNode() {
        return thisNode;
    }

    public void setThisNode(ChangePasswordController thisNode) {
        this.thisNode = thisNode;
    }

    public QrCheckController getParent() {
        return parent;
    }

    public void setParent(QrCheckController parent) {
        this.parent = parent;
    }

    public ChangePasswordController(String username) {
        setNameLogin(username);
    }
    public ChangePasswordController()
    {

    }

    @FXML
    private void initialize() throws IOException {
        setTextHelloUserName(nameLogin);
    }
    public void change_pass(MouseEvent mouseEvent) {

        if( password.getText().length()<8)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка пароля");
            alert.setContentText("Пароль должен быть длинее 7 символов");
            alert.showAndWait();
            return;
        }

        if (!password.getText().equals(check_password.getText()))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка пароля");
            alert.setContentText("Пароли не совпадают! Проверьте правильность ввода");
            alert.showAndWait();
            return;
        }

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(changePswd(login,new SHA256Class().getSHA256(password.getText())));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);

        new Thread(() -> {

            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("changePswd"));
            ErrorMsg t = new ErrorMsg();
            if( t.changerPswd()==0 )
            {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                     //  ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);
                      parent.toStartPage();
                    }
                });
            }
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("changePswd");


        }).start();

    }
}
