package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import sample.ClientXmlPorocol.VacoomProtocol;
import sample.qr.QrCheckController;

import java.io.IOException;

public class LoginController extends VacoomProtocol {

    public TextField input;
    public AnchorPane loginXML;
    public Controller crt;
    public AnchorPane registration = null;
    public AnchorPane qrCheck = null;
    public AnchorPane refreshingForm = null;

    public PasswordField password;
    StartWindowController parents;
    LoginController thisNode;
    FXMLLoader loaderQr;

    public LoginController() {

    }

    @FXML
    private void initialize() throws IOException {
        loadRegistration();
        loadQrCheck();
    }

    public void setController(Controller controller){
        crt=controller;
    }

    public void loadRegistration()
    {
        FXMLLoader loader = new FXMLLoader();
        RegistrationController registrationController =
                new RegistrationController();
        registrationController.setParent(thisNode);
        registrationController.setThisNode(registrationController);
        loader = new FXMLLoader(
                getClass().getResource(
                        "fxml/registration.fxml"
                )
        );
        loader.setController(registrationController);


        try {
            registration = (AnchorPane) loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadQrCheck()
    {
        loaderQr = new FXMLLoader();
        QrCheckController refresh = new QrCheckController();
        refresh.setParent(thisNode);
        refresh.setNode(refresh);
        loaderQr = new FXMLLoader(getClass().getResource("fxml/qrCheck.fxml"));
        loaderQr.setController(refresh);

        try {
            qrCheck= (AnchorPane) loaderQr.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRefreshingPassword()
    {
        FXMLLoader loaderRefreshPswd = new FXMLLoader();
        RefreshingPasswordController refresh = new RefreshingPasswordController();
        refresh.setParent(thisNode);
        refresh.setThisNode(refresh);
        loaderRefreshPswd = new FXMLLoader(getClass().getResource("fxml/refreshingPswd.fxml"));
        loaderRefreshPswd.setController(refresh);

        try {
            refreshingForm= (AnchorPane) loaderRefreshPswd.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setParent  (StartWindowController ctr)
    {
        parents = ctr;
    }

    public void setNode (LoginController control)
    {
          thisNode = control;
    }

    public void backToLogin ()
    {
       loginXML.getChildren().remove(loginXML.getChildren().size()-1);

    }

    public void come(MouseEvent mouseEvent) throws IOException {

        if(input.getText().length()<4) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка Ввода");
            alert.setContentText("Логин должен содержать больше 3х символов");
            alert.showAndWait();
            return;
        }
        if( password.getText().length()<8)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка пароля");
            alert.setContentText("Пароль должен быть длиннее 7 символов");
            alert.showAndWait();
            return;
        }
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(authorizationUser(input.getText(),new SHA256Class().getSHA256(password.getText())));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
       // loginXML.getChildren().add(qrCheck);

        /*
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(autorizationUser
                ("127.0.0.1","authorization",input.getText(),md5.md5Custom(password.getText())));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);*/

        new Thread(() -> {

            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode() == -1);
            ErrorMsg t = new ErrorMsg();
            if( t.checkLogin()==0 )
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // ThreadClientInfoSingleton.getInstance().getClientMsgThread().setUserLogin(true);
                        // parents.loadWorkrArea(input.getText());
                        loadQrCheck();
                        loginXML.getChildren().add(qrCheck);
                    }
                });
            }

            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);
        }).start();

    }

    public void refreshPassword(MouseEvent mouseEvent) {
        loadRefreshingPassword();
        loginXML.getChildren().add(refreshingForm);
    }

    public void startRegistration(MouseEvent mouseEvent) {
        loadRegistration();
        loginXML.getChildren().add(registration);

    }
}

