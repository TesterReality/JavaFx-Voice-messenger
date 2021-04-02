package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import sample.ClientXmlPorocol.VacoomProtocol;
import sample.qr.QrCheckController;
import sample.EmailValidator;

import java.io.IOException;

/**
 * Created by user on 27.04.2019.
 */
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
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);


        new Thread(() -> {

                        do {
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode() == -1);
                        ErrorMsg t = new ErrorMsg();
                       if( t.getCode()==0 )
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

                        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);
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
        loaderQr = new FXMLLoader(getClass().getResource("fxml/qrCheck.fxml"));
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
