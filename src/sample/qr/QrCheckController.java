package sample.qr;

import javafx.application.Platform;
import javafx.fxml.FXML;
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
import sample.ClientXmlPorocol.VacoomProtocol;
import sample.ErrorMsg;
import sample.LoginController;
import sample.StartWindowController;
import sample.ThreadClientInfoSingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class QrCheckController extends VacoomProtocol {


    public Rectangle drugs;
    public ImageView qrInside;
    public Label dropHereQrLabel;
    public Label YourQrLable;
    public Button nextPage;
    public TextField codeInput;
    public AnchorPane thisAnchorPane;

    Image imgOk = new Image("resource/img/Qr/qrIMGok.png");
    Image imgErr = new Image("resource/img/Qr/qrIMGErr.png");
    Image imgLoad = new Image("resource/img/Qr/qrIMGload.png");
    Image imgDefault = new Image("resource/img/Qr/qrIMG.png");

    ImageChooser chooser;
    BorderPane root;
    LoginController parents;
    QrCheckController thisNode;
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
       // Image img = new Image("resource/img/Qr/qrIMG.png");
        drugs.setFill(new ImagePattern(imgDefault));
    }

    private void openQrImage(File img) {
        try {
            InputStream isImage = (InputStream) new FileInputStream(img);
            qrInside.setImage(new Image(isImage));
            visibleQrLabel(false);
        } catch (FileNotFoundException e) {
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
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(checkCode(parents.input.getText(),code,false));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);

        new Thread(() -> {

            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAnswerGetCode() == -1);
            ErrorMsg t = new ErrorMsg();
            if( t.checkCode()==0 )
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // ThreadClientInfoSingleton.getInstance().getClientMsgThread().setUserLogin(true);
                        // parents.loadWorkrArea(input.getText());
                        //loadQrCheck();
                       // loginXML.getChildren().add(qrCheck);
                        System.out.println("[Клиент] Все прошло хорошо. QR проверен. Загрузка основной формы");
                    }
                });
            }

            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAnswerGetCode(-1);
        }).start();

    }

    public void toLoginPage(MouseEvent mouseEvent) {
        /*
        if(thisAnchorPane.getChildren().size()>1)
        {
            thisAnchorPane.getChildren().remove(thisAnchorPane.getChildren().size()-1);
        }*/

        parents.backToLogin();
    }
}
