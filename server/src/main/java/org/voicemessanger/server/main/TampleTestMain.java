package org.voicemessanger.server.main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TampleTestMain {
    public static void main(String[] args) {
        File file = new File("/img/logo125.png");
        try {
            BufferedImage logoImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(file.getAbsolutePath());
    }
}
