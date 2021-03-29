package sample.serverSide.ServerXmlProtocol;

import sample.serverSide.Database.DatabaseLogic;
import sample.serverSide.Mail.Mail;
import sample.serverSide.RandomStringGenerator;
import sample.serverSide.Server;
import sample.serverSide.ServerXmlProtocol.ServerVacoomProtocol;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseServerVacoomProtocol extends DatabaseLogic {

    private int howNeedString;//сколько нужно выделить строк в массиве
    private int numOfNow; // текущее положение строки

    private Server objServer;
    private Mail mail;
    private ServerVacoomProtocol serverVacoomProtocol;

    public ParseServerVacoomProtocol(Server objServer) {
        this.objServer = objServer;
        mail = new Mail();
        serverVacoomProtocol = new ServerVacoomProtocol();
    }

    public String parseRequest(String request) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(request);
        while (m.find()) {
            System.out.println(m.group(1));
            howNeedString++;
        }
        String[] strings1 = new String[howNeedString];
        m.reset();
        numOfNow = 0;
        while (m.find()) {
            strings1[numOfNow++] = m.group(1);
        }
        return parseCommand(strings1);
    }

    private String parseCommand(String[] commands) {
        switch (commands[1])//мы должны отправить или получить
        {
            case "set"://значит НАМ подали запрос, мы должны ответить
                return setCommads(commands);
            case "result":
                System.out.print("result от клиента");
                break;//значит НАМ подали ответ
        }
        return null;
    }

    private String setCommads(String[] commands) {

        switch (commands[3])//содержит код запроса
        {
            case "authorization": {
                if (checkUser(commands[4], commands[5])) {
                    String mailUser;
                    try {
                        mailUser = checkLogin(commands[4]);
                        mail.setMailTo(mailUser);//это мейл

                        String random = new RandomStringGenerator().generateString();
                        byte[] aesByte = objServer.getAes256Serv().makeAes(random.getBytes(), Cipher.ENCRYPT_MODE);
                        byte[] encoded = Base64.getEncoder().encode(aesByte);

                        mail.sendCode(new String(encoded));
                        upadteCodeActivated(mailUser, random);

                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                    /*
                    try {
                        SingletonUserConnect.getInstance().setUsers(commands[4], objServer);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    }*/

                    return sendAnswer(commands[3], "ok");
                } else
                    return sendAnswer(commands[3], "error");
            }

        }
        return null;
    }

    private String sendAnswer(String action, String status) {
        return serverVacoomProtocol.sendAnswer(action, status);//вернет строку
    }
}
