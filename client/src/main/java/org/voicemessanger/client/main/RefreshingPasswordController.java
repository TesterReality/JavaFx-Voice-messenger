package org.voicemessanger.client.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;
import org.voicemessanger.client.qr.QrCheckController;


import java.io.IOException;

public class RefreshingPasswordController extends VacoomProtocol {

    public AnchorPane thisXML;

    public LoginController parent;

    public RefreshingPasswordController thisNode;
    public TextField email;
    EmailValidator checkMail = null;
    public AnchorPane QRcodeAnchor = null;


    public RefreshingPasswordController getThisNode() {
        return thisNode;
    }

    public void setThisNode(RefreshingPasswordController thisNode) {
        this.thisNode = thisNode;
    }



    public LoginController getParent() {
        return parent;
    }

    public void setParent(LoginController parent) {
        this.parent = parent;
    }

    public RefreshingPasswordController() {
    }


    @FXML
    private void initialize() throws IOException {
      //  loadCode();
        checkMail = new EmailValidator();

    }

    public void getCode(MouseEvent mouseEvent) {

        if(!checkMail.validate(email.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка Ввода");
            alert.setHeaderText("Неверный email");
            alert.setContentText("Проверьте пожалуйста правильность ввода email'a");
            alert.showAndWait();
            return;
        }

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setRegistreUser(true);
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(getCodeMsg(email.getText(),false));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);

        new Thread(() -> {

            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("getCode"));
            ErrorMsg t = new ErrorMsg();
            if( t.getCode(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("getCode"))==0 )
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadQrCheck();
                        thisXML.getChildren().add(QRcodeAnchor);
                    }
                });
            }
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("getCode");

        }).start();

    }

    public void returnToStart(MouseEvent mouseEvent) {
        thisXML.getChildren().remove(thisXML.getChildren().size()-1);
        parent.backToLogin();
    }
    public void loadQrCheck()
    {
        FXMLLoader loaderQr = new FXMLLoader();
        QrCheckController refresh = new QrCheckController();
        refresh.setParent(thisNode);
        refresh.setNode(refresh);
        loaderQr = new FXMLLoader(getClass().getResource("/fxml/qrCheck.fxml"));
        loaderQr.setController(refresh);

        try {
            QRcodeAnchor= (AnchorPane) loaderQr.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back()
    {
        thisXML.getChildren().remove(thisXML.getChildren().size()-1);

    }

    public void toLoginPage()
    {
        if(thisXML.getChildren().size()>1)
        {
            thisXML.getChildren().remove(thisXML.getChildren().size()-1);

        }

        parent.backToLogin();
    }
}
