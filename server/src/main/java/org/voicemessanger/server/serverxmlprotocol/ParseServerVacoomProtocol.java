
package org.voicemessanger.server.serverxmlprotocol;

import org.voicemessanger.server.database.DatabaseLogic;
import org.voicemessanger.server.mail.Mail;
import org.voicemessanger.server.main.*;
import org.voicemessanger.server.serverxmlprotocol.ServerVacoomProtocol;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseServerVacoomProtocol extends DatabaseLogic {

    private int howNeedString;//сколько нужно выделить строк в массиве
    private int numOfNow; // текущее положение строки
    private Server objServer;
    private Mail mail;
    private ServerVacoomProtocol serverVacoomProtocol;
    private String USER_NAME;
    private String lastMsg= null;
    HashMap<String, String> protocolMsg;
    public ParseServerVacoomProtocol(Server objServer) {
        this.objServer = objServer;
        mail = new Mail();
        protocolMsg = new HashMap<>();

        serverVacoomProtocol = new ServerVacoomProtocol();
    }

    public String parseRequest(String request ) {
        howNeedString=0;

        lastMsg = request;
        Pattern p = Pattern.compile("\"([^\"]*)\""); //Результаты тегов, например: <from to="client".. будет client
        Pattern p1 = Pattern.compile("\\w+(?=\\=)");//Сами теги, например: <from to="client".. будет to
        int howNeeds=0;
        Matcher m = p.matcher(request);
        Matcher m1 = p1.matcher(request);

        while (m.find()) {
            System.out.println(m.group(1));
            howNeedString++;
        }
        System.out.println("------------");
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
        if(protocolMsg.size()>0)
        protocolMsg.clear();
        for (int i=0; i<strings1.length;i++)
        {
            protocolMsg.put(strings2[i],strings1[i]);
        }
        return parseCommand(strings1,strings2);
    }

    private String parseCommand(String[] commands,String[] suffix) {
        switch (commands[1])//мы должны отправить или получить
        {
            case "set"://значит НАМ подали запрос, мы должны ответить
                return setCommads(commands,suffix);
            case "result"://значит НАМ подали ответ
                System.out.print("result от клиента");
                return resultCommads(commands,suffix);
        }
        return null;
    }

    private String sendQR()  {
        String random = new RandomStringGenerator().generateString();
        byte[] aesByte = objServer.getAes256Serv().makeAes(random.getBytes(), Cipher.ENCRYPT_MODE);
        byte[] encoded = Base64.getEncoder().encode(aesByte);

        /*
        try {
            mail.sendCode(new String(encoded));
        } catch (MessagingException e) {
            e.printStackTrace();
        }*/

        return random;
    }
    private String resultCommads(String[] commands,String[] suffix) {

        switch (protocolMsg.get("action"))//содержит код
        {
            case "relay":
            {
                relayRequest(commands);
                return null;
            }
            case "sendKey":
            {
                relayRequest(commands);
                return null;
            }
        }
        return sendAnswer(commands[3], "error");//в любой непонятной ситуации отвечаем ошибкой

    }
    private String setCommads(String[] commands,String[] suffix) {
        System.out.println("Начинаем распознавать команду");
        switch (protocolMsg.get("action"))//содержит код запроса
        {
            case "authorization": {
                if (checkUser(commands[4], commands[5])) {
                    String mailUser;
                    try {
                        USER_NAME = commands[4];
                        System.out.println("На сервере теперь "+ USER_NAME);;
                        objServer.userThreadRename(USER_NAME);

                        mailUser = checkLogin(commands[4]);
                        mail.setMailTo(mailUser);//это мейл

                        upadteCodeActivated(mailUser,  sendQR());

                    } catch (Exception e) {
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

                    return serverVacoomProtocol.sendOkAnswerAuthorization(getAvatarFromUsername(commands[4]));
                  //  return sendAnswer(commands[3], "ok");
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
            case "checkCodeRefresh":
            {
                if(checkUserActivatedMailCode(commands[4],commands[5]))
                {
                    return sendAnswer(commands[3],"ok");
                }else
                {
                    return  sendAnswer(commands[3],"error_code");
                }
            }
            case "getCode":
            {
                switch (commands[5])
                {
                    case "registration": {
                        mail.setMailTo(commands[4]);//установить емейл
                        try {

                            if (checkMailIsUnregister(commands[4])==false)// если на емейл зарегистрирован человек
                            {
                                return sendAnswer(commands[3], "mail_error");//если мейл зареган
                            }

                            addCodeAnonymusDatabase(sendQR(), commands[4]);
                            return sendAnswer(commands[3], "ok");//все хорошо
                        } catch (Exception e) {
                            e.printStackTrace();
                            return sendAnswer(commands[3], "error");//если что-то пошло не так с сообщением
                        }
                    }
                    case "refresh": {
                        mail.setMailTo(commands[4]);//установить емейл
                        try {
                            if (checkMailIsUnregister(commands[4])==true)// если на емейл НЕ зарегистрирован человек
                            {
                                return sendAnswer(commands[3], "mail_unreg");//если мейл НЕ зареган
                            }
                            upadteCodeActivated(commands[4],  sendQR());
                            return sendAnswer(commands[3], "ok");//все хорошо

                        } catch (Exception e) {
                            e.printStackTrace();
                            return sendAnswer(commands[3], "error");//если что-то пошло не так с сообщением

                        }
                    }
                }

            }
            case"registration":
            {
                if (registrationUser(commands[4], commands[5], commands[6])) {
                    //USER_NAME =  commands[5];
                    //System.out.println("На сервере теперь "+ USER_NAME);;
                    return sendAnswer(commands[3], "ok");
                }
                else
                    return sendAnswer(commands[3], "error_user");
            }
            case "getMailLogin":
            {
                try {
                    String userLogin= getUserLoginFromMail(commands[4]);
                    return sendAnswer(commands[3], "ok:"+userLogin);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            case "changePswd":
            {
                if (changePswd(commands[4], commands[5]))
                    return sendAnswer(commands[3], "ok");
                else
                    return sendAnswer(commands[3], "error");
            }
            case "updateAvatars":
            {
                //если подменили ник
                if(!commands[5].equals(USER_NAME))return sendAnswer(commands[3], "error");
                if (changeAvatar(commands[5],commands[4]))
                    return sendAnswer(commands[3], "ok");
                else
                    return sendAnswer(commands[3], "error");
            }
            case"getFriend":
            {
                System.out.println("[Сервер] Запрос друзей у сервера" + USER_NAME);
                if(!commands[4].equals(USER_NAME)) return sendAnswer(commands[3], "error");
                FriendsHelper friend = new FriendsHelper();
                System.out.println("[Сервер] Начинаю поиск друзей");
                if (getFriend(commands[4], friend)) {
                    if(friend.getFriend_name()==null||
                            friend.getFriend_name().size()==0)
                        return sendAnswer(commands[3], "noFriend");
                    return serverVacoomProtocol.sendAnswerFriends( friend.getFriend_name(), friend.getStatus(), friend.getStatusOnline(),friend.getAvatars());
                } else return sendAnswer(commands[3], "error");
            }
            case "confirmFriend":
            {
                if(!commands[5].equals(USER_NAME)) return sendAnswer(commands[3], "error");
                if (friendManipulator(commands[5], commands[4], (byte) 1))
                    return sendAnswer(commands[3], "ok");
                else return sendAnswer( commands[3], "error");
            }
            case "cancelFriend":
            {
                if (friendManipulator(commands[5], commands[4], (byte) 0))
                    return sendAnswer(commands[3], "ok");
                else return sendAnswer(commands[3], "error");
            }
            case "relay":
            {
                relayRequest(commands);
                return null;
            }
            /*
            case "sendKey":
            {
                relayRequest(commands);
                return null;
            }*/

        }
        return sendAnswer(commands[3], "error");//в любой непонятной ситуации отвечаем ошибкой
    }
    Object search(TreeSet treeset, Object key) {
        Object ceil  = treeset.ceiling(key); // least elt >= key
        Object floor = treeset.floor(key);   // highest elt <= key
        return ceil == floor? ceil : null;
    }
    private void relayRequest (String[] commands)
    {
        try {
            InfoUsernameFromThread friend = (InfoUsernameFromThread) search(ServerMain.usernameFromThreadArrayList, new InfoUsernameFromThread(protocolMsg.get("friend"), null));
            Server friendThread = friend.getThread();
            friendThread.sendMessage(lastMsg);
        }catch (Exception e)
        {
            System.out.println("[СЕРВЕР] Не удалось отправить сообщение другу");
        }
        /*
        for (int i = 0; i< ServerMain.usernameFromThreadArrayList.size(); i++)
        {
            InfoUsernameFromThread inf = ServerMain.usernameFromThreadArrayList.get(i);
            if(inf.getName().equals(commands[4]))
            {
                Server friend = inf.getThread();
                friend.sendMessage(lastMsg);
            }
        }*/
    }
    private String sendAnswer(String action, String status) {
        return serverVacoomProtocol.sendAnswer(action, status);//вернет строку
    }

    public void setOnlineUser(String username)
    {
        if(username.equals(USER_NAME))
        setOnline(username);
    }
}
