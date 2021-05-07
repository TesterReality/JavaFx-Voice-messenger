package org.voicemessanger.client.qr;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;
import org.voicemessanger.client.main.*;


import java.io.*;

public class QrCheckController extends VacoomProtocol {


    public Rectangle drugs;
    public ImageView qrInside;
    public Label dropHereQrLabel;
    public Label YourQrLable;
    public Button nextPage;
    public TextField codeInput;
    public AnchorPane thisAnchorPane;

    Image imgOk = new Image("/img/Qr/qrIMGok.png");
    Image imgErr = new Image("/img/Qr/qrIMGErr.png");
    Image imgLoad = new Image("/img/Qr/qrIMGload.png");
    Image imgDefault = new Image("/img/Qr/qrIMG.png");

    ImageChooser chooser;
    BorderPane root;
    Object parents;
   // LoginController parents;

    QrCheckController thisNode;
    AnchorPane regUser=null;
    AnchorPane changePass = null;
    QRscanner qRscanner;
    String code;

    private void visibleQrLabel(boolean visible)
    {
        dropHereQrLabel.setVisible(visible);
        YourQrLable.setVisible(visible);
    }
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public void setParent  (LoginController login)
    {
        parents = login;
    }
    public void setParent  (RegistrationController register)
    {
        parents = register;
    }
    public void setParent  (RefreshingPasswordController refreshing)
    {
        parents = refreshing;
    }

    public Object getParents() {
        return parents;
    }

    public void setNode (QrCheckController qr)
    {
        thisNode = qr;
    }

    @FXML
    public void initialize() {
        qRscanner = new QRscanner();
        chooser = new ImageChooser();
        chooser.setAvailableFormats("*.png"); // Указываем форматы для FileChooser.
        drugs.setOnDragEntered(
                (DragEvent event) -> {
                    if(event.getSource() == drugs && event.getDragboard().hasFiles()) {
                        drugs.setFill(new ImagePattern(imgLoad));
                        qrInside.setImage(null);
                        visibleQrLabel(true);
                    }

                    event.consume();
                }
        );

        drugs.setOnDragExited(
                (DragEvent event) -> {
                   // drugs.setFill(Color.LIGHTGREEN);
                    //drugs.setFill(new ImagePattern(imgOk));
                    //drugs.setFill(new ImagePattern(imgDefault));

                    event.consume();
                }
        );

        drugs.setOnDragOver(
                (DragEvent event) -> {
                    if(event.getDragboard().hasFiles()) {
                        for (File f: event.getDragboard().getFiles()) {
                            event.acceptTransferModes(TransferMode.COPY);
                        }
                    }
                }
        );

        drugs.setOnDragDropped(
                (DragEvent event) -> {
                    if(event.getDragboard().hasFiles()) {
                        for (File f: event.getDragboard().getFiles()) {
                            if (getFileExtension(f).equals("png")) {
                                System.out.println(f.getPath());
                                String pathLoadingImg = f.getPath();
                                File img = new File(pathLoadingImg);
                                openQrImage(img);
                                String qrAnswer = qRscanner.scanQr(f.getPath());
                                if(qrAnswer == null)
                                {
                                    System.out.println("[КЛИЕНТ] Неподходящее изображение");
                                    drugs.setFill(new ImagePattern(imgErr));

                                }else
                                {
                                    code =  ThreadClientInfoSingleton.getInstance().getClientMsgThread().decryQr(qrAnswer);
                                    if(code!=null)
                                    {
                                        System.out.println("[КЛИЕНТ] Расшифрованный текст из qr = ["+code+"]");
                                        drugs.setFill(new ImagePattern(imgOk));
                                        nextPage.setDisable(false);
                                        codeInput.setText(code);
                                    }
                                    else
                                    {
                                        System.out.println("[КЛИЕНТ] Это был QR, но расшифровать его не удалось");
                                        drugs.setFill(new ImagePattern(imgErr));
                                    }

                                }
                                // qrInside.setImage(new Image("file://"+f.getPath()));
                            }
                        }
                        event.setDropCompleted(true);
                    }
                    event.consume();
                }
        );
       // Image img = new Image("/img/Qr/qrIMG.png");
        drugs.setFill(new ImagePattern(imgDefault));
    }

    private void openQrImage(File img) {
        try {
            InputStream isImage = (InputStream) new FileInputStream(img);
            qrInside.setImage(new Image(isImage));
            visibleQrLabel(false);
            isImage.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadImageQr(MouseEvent mouseEvent) {

        String image = chooser.openImage(); // Выбираем изображение.
        if (image != null) {
        File img = new File(image);
        openQrImage(img);
        String qrAnswer = qRscanner.scanQr(img.getPath());
            if(qrAnswer == null)
            {
                drugs.setFill(new ImagePattern(imgErr));

            }else
            {
                drugs.setFill(new ImagePattern(imgOk));

            }
        }


    }

    public void toMainUserPage(MouseEvent mouseEvent) {
        if(parents instanceof LoginController) {
            LoginController login = (LoginController) parents;
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(checkCode(login.input.getText(), code, false));
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);

            new Thread(() -> {

                do {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("checkCode"));
                ErrorMsg t = new ErrorMsg();
                if (t.checkCode(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("checkCode"),false) == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                             //ThreadClientInfoSingleton.getInstance().getClientMsgThread().setUserLogin(true);
                            //login. loadWorkrArea(input.getText());
                            try {
                                System.out.println("[Клиент] Все прошло хорошо. QR проверен. Загрузка основной формы");
                                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("checkCode");

                                login.getParents().loadWorkArea(login.input.getText(),false);
                            } catch (IOException e) {
                                System.out.println("[Клиент] Ошибка загрузка основной формы");
                                e.printStackTrace();
                            }
                            //loadQrCheck();
                            // loginXML.getChildren().add(qrCheck);
                        }
                    });
                }


            }).start();
        }
        if(parents instanceof RegistrationController)
        {
            RegistrationController registrationController = (RegistrationController) parents;

            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(checkCode(registrationController.email.getText(), code, true));
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);

            new Thread(() -> {

                do {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("checkCode"));
                ErrorMsg t = new ErrorMsg();
                if (t.checkCode(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("checkCode"),true) == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("[Клиент] Все прошло хорошо. QR проверен. Переходим к регистрации");
                            loadRegistrationUser();
                            thisAnchorPane.getChildren().add(regUser);

                        }
                    });
                }
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("checkCode");
            }).start();

            System.out.println("Мы подтверждает неактивированный мейл");
        }
        if(parents instanceof RefreshingPasswordController)
        {
            RefreshingPasswordController RefreshingPasswordController = (RefreshingPasswordController) parents;

            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(checkCodeRefresh(RefreshingPasswordController.email.getText(), code));
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);

            new Thread(() -> {

                do {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("checkCodeRefresh"));
                ErrorMsg t = new ErrorMsg();
                if (t.checkCode(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().get("checkCodeRefresh"),true) == 0) {
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("checkCodeRefresh");

                    //Если Qr подошел, возьмем имя пользователя
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(getUserLoginFromMail(((RefreshingPasswordController) parents).email.getText()));
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);

                    do {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("getMailLogin"));
                    String userName =  ThreadClientInfoSingleton.getInstance().getClientMsgThread().getUser_name();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("[Клиент] Все прошло хорошо. QR проверен. Переходим к ввостановлению пароля");
                            System.out.println("[Клиент] Имя пользователя:"+userName);
                            loadChangePswd(userName);
                            thisAnchorPane.getChildren().add(changePass);
                          //  loadRegistrationUser();
                           // ss
                          //  thisAnchorPane.getChildren().add(regUser);

                        }
                    });
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("getMailLogin");

                }

            }).start();

            System.out.println("Мы подтверждает неактивированный мейл");
        }

    }

    private void loadChangePswd(String username)
    {
        FXMLLoader loader = new FXMLLoader();
        ChangePasswordController changePasswordController =
                new ChangePasswordController(username);
        changePasswordController.setParent(thisNode);
        changePasswordController.setThisNode(changePasswordController);
        loader = new FXMLLoader(getClass().getResource("/fxml/changePassword.fxml"));
        loader.setController(changePasswordController);

        try {
            changePass = (AnchorPane) loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadRegistrationUser()
    {
        FXMLLoader loader = new FXMLLoader();
        RegistrationUserController register =
                new RegistrationUserController();
        register.setParent(thisNode);
        register.setThisNode(register);
        loader = new FXMLLoader(getClass().getResource("/fxml/registrationUser.fxml"));
        loader.setController(register);

        try {
            regUser = (AnchorPane) loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toLoginPage(MouseEvent mouseEvent) {
        /*
        if(thisAnchorPane.getChildren().size()>1)
        {
            thisAnchorPane.getChildren().remove(thisAnchorPane.getChildren().size()-1);
        }*/
        if(parents instanceof LoginController)
        {
            LoginController login = (LoginController) parents;
            login.backToLogin();

        }
        if(parents instanceof RegistrationController)
        {
            RegistrationController registrationController = (RegistrationController) parents;
            registrationController.back();

        }
        if(parents instanceof RefreshingPasswordController)
        {
            RefreshingPasswordController refreshingPasswordController = (RefreshingPasswordController) parents;
            refreshingPasswordController.back();
        }

    }

    public void toStartPage()
    {
        thisAnchorPane.getChildren().remove(thisAnchorPane.getChildren().size()-1);
        if(parents instanceof RefreshingPasswordController) {

            RefreshingPasswordController refreshingPasswordController = (RefreshingPasswordController) parents;
            refreshingPasswordController.toLoginPage();
        }
        if(parents instanceof RegistrationController) {

            RegistrationController registrationController = (RegistrationController) parents;
            registrationController.toLoginPage();
        }
    }
}
