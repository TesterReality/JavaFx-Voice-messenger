package org.voicemessanger.client.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;
import org.voicemessanger.client.qr.QrCheckController;

import java.io.IOException;


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

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(registrationUser(user_login.getText(),new SHA256Class().getSHA256(password.getText()),parent.codeInput.getText()));
       // ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        //допиши тут ошибка и все такое, тестируй на другом мыле

        new Thread(() -> {

            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("registration"));
            ErrorMsg t = new ErrorMsg();

            if( t.registration(false)==0 )
            {
                System.out.println("[Клиент] Пользователь зареган. Можно открывать новое окно");
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("registration");


                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(authorizationUser(user_login.getText(),new SHA256Class().getSHA256(password.getText())));
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
                // loginXML.getChildren().add(qrCheck);

                new Thread(() -> {

                    do {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("authorization"));
                    ErrorMsg t1 = new ErrorMsg();
                    if( t1.checkLogin(false)==0 )
                    {
                        ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("authorization");

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                if(parent.getParents() instanceof RegistrationController)
                                {
                                    RegistrationController back = (RegistrationController) parent.getParents();
                                    try {
                                        back.parents.getParents().loadWorkArea(user_login.getText(),false);
                                        System.out.println("[Клиент] Успешная регистрация. Открытие нового окна");

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    }

                }).start();
            }


        }).start();

    }
}
