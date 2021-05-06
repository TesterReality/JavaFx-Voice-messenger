package org.voicemessanger.server.mail;

import com.sun.mail.smtp.SMTPTransport;
import org.voicemessanger.server.qrcodegenerator.QRgenerate;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Mail {
    private static String mailTo = "";
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
    public String OldsendCode(String codeMsg) throws MessagingException {
        Properties props = System.getProperties();
        System.out.println("Начинаю отправлять письмо по емейлу "+mailServer);
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
        System.out.println("Письмо получилось отправить!");

        return Integer.toString(randomNum);
    }
    public String sendCode(String codeMsg) throws MessagingException {
        System.out.println("Начали отправку сообщения от "+mailServer +" к "+mailTo);
        Properties pro = new Properties();
        pro.put("mail.smtp.host", "smtp.gmail.com");
        pro.put("mail.smtp.starttls.enable", "true");
        pro.put("mail.smtp.auth", "true");
        pro.put("mail.smtp.port", "587");
        Session ss = Session.getInstance(pro, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailServer, mailPswd);
            }
        });

        try {
            Message msg = new MimeMessage(ss);
            msg.setFrom(new InternetAddress(mailServer));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
            msg.setSubject("Тест кода активации");
            msg.setSentDate(new Date());

            // Содержимое сообщения
            Multipart mmp = new MimeMultipart();
            // Текст сообщения
            MimeBodyPart bodyPart = new MimeBodyPart();

            QRgenerate qr=  new QRgenerate();
            qr.createQR(codeMsg);

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

            msg.setContent(mmp);

            Transport trans = ss.getTransport("smtp");
            Transport.send(msg);

            System.out.println("Письмо получилось отправить!");
            return Integer.toString(randomNum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Integer.toString(randomNum);
    }
}
