package org.voicemessanger.client.main;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoadingController {
    public AnchorPane stageWindow;
    public Label loadingLable;
    public ImageView gifImage;

    public LoadingController() {
    }

    @FXML
    private void initialize() {
       // InputStream resourceStream = getClass().getResourceAsStream("/img/line.png");
        //  ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/img/loading.gif")));
        // simple displays ImageView the image as is
        gifImage.setImage(new Image(getClass().getResourceAsStream("/img/loading500.gif")));


    }


}
