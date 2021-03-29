package sample.qr;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import sample.LoginController;
import sample.StartWindowController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class QrCheckController {


    public Rectangle drugs;
    public ImageView qrInside;
    public Label dropHereQrLabel;
    public Label YourQrLable;
    Image imgOk = new Image("resource/img/Qr/qrIMGok.png");
    Image imgLoad = new Image("resource/img/Qr/qrIMGload.png");
    ImageChooser chooser;
    BorderPane root;
    LoginController parents;
    QrCheckController thisNode;

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
//        FileDrag fd = new FileDrag();
       // drugs = fd;
        chooser = new ImageChooser();
        chooser.setAvailableFormats("*.png"); // Указываем форматы для FileChooser.






        drugs.setOnDragEntered(
                (DragEvent event) -> {
                    if(event.getSource() == drugs && event.getDragboard().hasFiles()) {
                       // drugs.setFill(Color.DARKGREEN);

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
                    drugs.setFill(new ImagePattern(imgOk));

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
                                // qrInside.setImage(new Image("file://"+f.getPath()));
                            }
                        }
                        event.setDropCompleted(true);
                    }
                    event.consume();
                }
        );
        Image img = new Image("resource/img/Qr/qrIMG.png");
        drugs.setFill(new ImagePattern(img));
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
        }


    }
}
