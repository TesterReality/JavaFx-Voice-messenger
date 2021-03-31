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
        Pattern p = Pattern.compile("\"([^\"]*)\""); //Результаты тегов, например: <from to="client".. будет client
        Pattern p1 = Pattern.compile("\\w+(?=\\=)");//Сами теги, например: <from to="client".. будет to
        int howNeeds=0;
        Matcher m = p.matcher(request);
        Matcher m1 = p1.matcher(request);

        while (m.find()) {
            System.out.println(m.group(1));
            howNeedString++;
        }
        while (m1.find()) {
            System.out.println(m1.group(0));
            howNeeds++;
        }
        String[] strings1 = new String[howNeedString];
        String[] strings2 = new String[howNeeds];

        m.reset();
        m1.reset();

        numOfNow = 0;
       int numOfNow1 = 0;

        while (m.find()) {
            strings1[numOfNow++] = m.group(1);
        }
        while (m1.find()) {
            strings2[numOfNow1++] = m1.group(0);
        }
        return parseCommand(strings1,strings2);
    }

    private String parseCommand(String[] commands,String[] suffix) {
        switch (commands[1])//мы должны отправить или получить
        {
            case "set"://значит НАМ подали запрос, мы должны ответить
                return setCommads(commands,suffix);
            case "result":
                System.out.print("result от клиента");
                break;//значит НАМ подали ответ
        }
        return null;
    }

    private String sendQR() throws MessagingException {
        String random = new RandomStringGenerator().generateString();
        byte[] aesByte = objServer.getAes256Serv().makeAes(random.getBytes(), Cipher.ENCRYPT_MODE);
        byte[] encoded = Base64.getEncoder().encode(aesByte);
        mail.sendCode(new String(encoded));

        return random;
    }
    private String setCommads(String[] commands,String[] suffix) {

        switch (commands[3])//содержит код запроса
        {
            case "authorization": {
                if (checkUser(commands[4], commands[5])) {
                    String mailUser;
                    try {
                        mailUser = checkLogin(commands[4]);
                        mail.setMailTo(mailUser);//это мейл

                        upadteCodeActivated(mailUser,  sendQR());

                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (MessagingException e) {
                        return sendAnswer(commands[3], "error");
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
            case "checkCode": {
                if(suffix[5].equals("login"))//Значит входим в ЛК
                {
                    if(checkUserActivatedCode(commands[4],commands[5]))
                    {
                        return sendAnswer(commands[3],"ok");
                    }else
                    {
                        return  sendAnswer(commands[3],"error_code");
                    }
                }
                if(suffix[5].equals("email"))//Значит регистрируемся
                {
                    if(checkUserUnregisterCode(commands[4],commands[5]))
                    {
                        return sendAnswer(commands[3],"ok");
                    }else
                    {
                        return  sendAnswer(commands[3],"error_code");
                    }
                }
                break;
            }
            case "getCode":
            {
                mail.setMailTo(commands[4]);//установить емейл
                try {

                    if (checkMailIsUnregister(commands[4])==false)// если на емейл зарегистрирован человек
                    {
                        return sendAnswer(commands[3], "mail_error");//если мейл зареган
                    }

                    addCodeAnonymusDatabase(sendQR(), commands[4]);
                    return sendAnswer(commands[3], "ok");//все хорошо
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return sendAnswer(commands[3], "error");//если что-то пошло не так с сообщением
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            }
            case"registration":
            {
                if (registrationUser(commands[4], commands[5], commands[6]))
                    return sendAnswer(commands[3], "ok");
                else
                    return sendAnswer(commands[3], "error_user");
            }

        }
        return sendAnswer(commands[3], "error");//в любой непонятной ситуации отвечаем ошибкой
    }

    private String sendAnswer(String action, String status) {
        return serverVacoomProtocol.sendAnswer(action, status);//вернет строку
    }
}
