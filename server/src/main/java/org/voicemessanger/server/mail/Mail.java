package org.voicemessanger.server.mail;

import com.sun.mail.smtp.SMTPTransport;
import org.voicemessanger.server.qrcodegenerator.QRgenerate;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Mail {
    private static String mailTo = "testerreality@gmail.com";
    private  String mailServer = "";
    private  String mailPswd="";
    private static int randomNum;
    MailUser mailUser;
    public Mail() {
         mailUser = new MailUser();
        this.mailServer = mailUser.getMailServer();
        this.mailPswd = mailUser.getMailPswd();
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }
/*
    public static void main(String[] args) {
        try {
            sendNumber("ss");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }*/
    public String sendCode(String codeMsg) throws MessagingException {
        Properties props = System.getProperties();
        props.put("mail.smtps.host", "smtp.mail.ru");
        props.put("mail.smtps.auth","true");
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(mailUser.getMailServer()));
        msg.setSubject("Код активации");

        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(mailTo, false));
        msg.setSentDate(new Date());
        // Содержимое сообщения
        Multipart mmp = new MimeMultipart();
        // Текст сообщения
        MimeBodyPart bodyPart = new MimeBodyPart();
        //  randomNum =  (int) ( Math.random() * 999999 );



        QRgenerate qr=  new QRgenerate();
        qr.createQR(codeMsg);//внутри преобразуется в base64
    /*  String  htmlMsg = "<img width=\"400\" height=\"400\" "
                + "alt=\"View of the object.\" src=\"data:image/png;base64,"
                + new QRgenerate().createQR("dd") + "\">";*/

        BufferedImage img = qr.getQrCode();

        byte[] imageBytes=null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
            baos.flush();
            imageBytes= baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ByteArrayDataSource bds = new ByteArrayDataSource(imageBytes, "image/png");
        bodyPart.setDataHandler(new DataHandler(bds));
        bodyPart.setFileName("code.png");
        bodyPart.setHeader("Content-ID", "<image>");
        mmp.addBodyPart(bodyPart);

       //bodyPart.setContent("тест", "text/html; charset=utf-8");

        msg.setContent(mmp);
        SMTPTransport t =
                (SMTPTransport)session.getTransport("smtps");
        t.connect("smtp.mail.ru", mailServer,mailPswd);
        t.sendMessage(msg,msg.getAllRecipients());

        return Integer.toString(randomNum);
    }
}
