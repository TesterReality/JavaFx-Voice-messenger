package org.voicemessanger.client.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;
import org.voicemessanger.client.qr.QrCheckController;


import java.io.IOException;

public class RegistrationController  extends VacoomProtocol {
    public volatile AnchorPane rgistrationXML; // сама форма
    public TextField email; // ввод мейла
    public AnchorPane QRcodeAnchor = null;
    public ProgressBar progressCode;
    EmailValidator checkMail = null;
    public int num;
    public RegistrationController() {
    }

    @FXML
    private void initialize() throws IOException {
        loadCode();
        checkMail = new EmailValidator();
        progressCode.setVisible(false);
    }

    LoginController parents;
    RegistrationController thisNode;

    public void setParent(LoginController parents)
    {
        this.parents = parents;
    }

    public void setThisNode (RegistrationController rg)
    {
        thisNode = rg;
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

        progressCode.setVisible(true);
        progressCode.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
       // ThreadClientInfoSingleton.getInstance().getCleintMsgThread().

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setRegistreUser(false);
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(getCodeMsg(email.getText(),true));
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
                                   progressCode.setProgress(0);
                                   thisNode.progressCode.setVisible(false);
                                   thisNode.addQrForms();

                               }
                           });
                       }
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("getCode");

        }).start();

    }

    public void back()
    {
        rgistrationXML.getChildren().remove(rgistrationXML.getChildren().size()-1);

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
    public void addQrForms()
    {
        rgistrationXML.getChildren().add(QRcodeAnchor);
    }

    public void returnToStart(MouseEvent mouseEvent) {
        toLoginPage();
    }

    public void toLoginPage()
    {
        if(rgistrationXML.getChildren().size()>1)
        {
            rgistrationXML.getChildren().remove(rgistrationXML.getChildren().size()-1);

        }

        parents.backToLogin();
    }

    public void loadCode()
    {/*
        FXMLLoader loader = new FXMLLoader();
        codeController code =
                new codeController();
        code.setParent(thisNode);
        code.setThisNode(code);
        loader = new FXMLLoader(
                getClass().getResource(
                        "fxmls/code.fxml"
                )
        );
        loader.setController(code);
        try {
            codeAnchor = (AnchorPane) loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
