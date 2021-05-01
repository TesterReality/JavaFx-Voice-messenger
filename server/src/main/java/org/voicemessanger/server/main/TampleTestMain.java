package org.voicemessanger.server.main;

import org.voicemessanger.server.database.DatabaseLogic;
import org.voicemessanger.server.database.DatabaseUser;
import org.voicemessanger.server.qrcodegenerator.QRgenerate;

import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public class TampleTestMain {
    public static void main(String[] args) {
        /*
        File file = new File("/img/logo125.png");
        try {
            BufferedImage logoImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

       // System.out.println(file.getAbsolutePath());
        /*
        DatabaseLogic dbl = new DatabaseLogic();

        try {
            dbl.addCodeAnonymusDatabase("fdfdfdfd","dxyu98@i.ua");
            dbl.addCodeAnonymusDatabase("fdfdfd1fd","dxyu98@i.ua");
            dbl.addCodeAnonymusDatabase("fdfdfd1fd","dxyu98@i.ua");
            dbl.addCodeAnonymusDatabase("fd1fdfdfd","dxyu98@i.ua");

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("ok");*/
        File file = new File("/img/logo125.png");
        System.out.println("QR-logo png is" + file.getName()+ "full path: " +file.getAbsolutePath());
        InputStream is = TampleTestMain.class.getResourceAsStream("/img/logo125.png");
        QRgenerate qr = new QRgenerate();
        try {
            qr.createQR("hui");
            Image image = ImageIO.read(is);

            BufferedImage image1 = ImageIO.read(TampleTestMain.class.getResourceAsStream("/img/logo125.png"));
            System.out.println("mem");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
