package org.voicemessanger.server.qrcodegenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QRgenerate {

    private BufferedImage qrCode;
    public QRgenerate() {
    }

    public BufferedImage getQrCode() {
        return qrCode;
    }

    public void setQrCode(BufferedImage qrCode) {
        this.qrCode = qrCode;
    }

    private String decodeBase64(String encoded)//расшифровать из base64
    {
        byte [] barr = Base64.getDecoder().decode(encoded);
        return new String(barr);
    }

    public void createQR(String msg)
    {
       // String base64msg = Base64.getEncoder().encodeToString(msg.getBytes());
        // encode without padding
        // String encoded = Base64.getEncoder().withoutPadding().encodeToString(msg.getBytes());
        generateQR(msg);
       // return imgToBase64String(base64msg);
    }

    public String imgToBase64String(String base64msg)
    {
        generateQR(base64msg);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            RenderedImage img = qrCode;
            ImageIO.write(qrCode, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = out.toByteArray();

        //String base64bytes =  new String(Base64.getEncoder().encode(bytes));
        String result = Base64.getEncoder().encodeToString(out.toByteArray());

        return result;
    }

    private void generateQR(String base64msg)
    {
        System.out.println("[СЕРВЕР] Код в qr в base64 = ["+base64msg+"]");
        Map hints = new HashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // Create a qr code with the url as content and a size of 250x250 px
            //  bitMatrix = writer.encode("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam scelerisque dictum ipsum, mollis faucibus neque. Vestibulum suscipit eu urna eget lobortis.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam scelerisque dictum ipsum, mollis faucibus neque. Vestibulum suscipit eu urna eget lobortis.",
            bitMatrix = writer.encode(base64msg,

                    BarcodeFormat.QR_CODE, 250, 250, hints);
            MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
            // Load QR image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
            // Load logo image
           // File file = new File("/img/logo125.png");
            //System.out.println("QR-logo png is" + file.getName()+ "full path: " +file.getAbsolutePath());
           // BufferedImage logoImage = ImageIO.read(file);

            BufferedImage logoImage = ImageIO.read(getClass().getResourceAsStream("/img/logo125.png"));

            // Calculate the delta height and width between QR code and logo
            int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
            int deltaWidth = qrImage.getWidth() - logoImage.getWidth();
            // Initialize combined image
            BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();
            // Write QR code to new image at position 0/0
            g.drawImage(qrImage, 0, 0, null);
            //последний парамтр прозрачность
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            // Write logo into combine image at position (deltaWidth / 2) and
            // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
            // the same space for the logo to be centered
            g.drawImage(logoImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
            // Write combined image as PNG to OutputStream
            qrCode=combined;
           // ImageIO.write(combined, "png", new File("QR.png"));
            System.out.println("GENERATED QR-CODE");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
