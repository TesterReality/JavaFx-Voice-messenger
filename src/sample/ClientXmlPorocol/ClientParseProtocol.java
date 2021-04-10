package sample.ClientXmlPorocol;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.ClientMsgThread;
import sample.ResizeHelper;
import sample.ThreadClientInfoSingleton;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientParseProtocol extends VacoomProtocol {
    private int howNeedString;//сколько нужно выделить строк в массиве
    private int numOfNow; // текущее положение строки
    private int randomNum;
    String[] strings1;
    ArrayList<Integer> portCheck = new ArrayList<>();

    Map<String,Integer > statesProtocol;

    public ClientParseProtocol() {
        statesProtocol = new HashMap<String,Integer>();
        statesProtocol.put("state",0);//test state
    }

    public Map parseRequest(String request) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(request);
        while (m.find()) {
            System.out.println(m.group(1));
            howNeedString++;
        }
        strings1 = new String[howNeedString];
        m.reset();
        numOfNow = 0;
        while (m.find()) {
            strings1[numOfNow++] = m.group(1);
        }
        parseCommand(strings1);
        return statesProtocol;
    }

    private void parseCommand(String[] commands) {
        switch (commands[1])//мы должны отправить или получить
        {
            case "set"://значит НАМ подали запрос, мы должны ответить
            {
                statesProtocol.put(commands[3],1);
                return;
            }

            case "result":
            {
                resultCommands(commands);
                return;
            }
        }

        statesProtocol.put(commands[3],1);
        return;
    }


    private void setCommands(String[] commands) {
        switch (commands[3])//содержит код запроса
        {
            case "default":
                break;
        }
    }
//0-ok 1-error
    private void resultCommands(String[] commands) {
        statesProtocol.clear();

        switch (commands[3])//содержит код запроса
        {
            case "authorization":
                if(commands[4].equals("error")) {
                    statesProtocol.put(commands[3],1);
                return;
                }
                if(commands[8].equals("ok"))
                {
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setCloudinaryApiKey(commands[4]);
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setCloudinaryApiSecret(commands[5]);
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setCloudinaryCloudName(commands[7]);

                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setAvatarsId(commands[6]);
                    statesProtocol.put(commands[3],0);
                    return;
                }
                switch (commands[4]) {
                    case "ok":
                        statesProtocol.put(commands[3],0);
                        break;
                    case "error":
                        statesProtocol.put(commands[3],1);
                        break;
                }
                break;

            case "checkCode":
                {
                    switch (commands[4]) {
                        case "ok":
                            statesProtocol.put(commands[3],0);
                            return;
                        case "error_code":
                            statesProtocol.put(commands[3],1);
                            return;
                    }
                    break;
                }
            case "checkCodeRefresh":
                {
                    switch (commands[4]) {
                        case "ok":
                            statesProtocol.put(commands[3],0);
                            return;
                        case "error_code":
                            statesProtocol.put(commands[3],1);
                            return;
                    }
                    break;
                }
            case "getCode":
            {
                switch (commands[4]) {
                    case "ok":
                        statesProtocol.put(commands[3],0);
                        return;
                    case "mail_error":
                        statesProtocol.put(commands[3],1);
                        return;
                    case "error":
                        statesProtocol.put(commands[3],2);
                        return;
                    case "mail_unreg":
                        statesProtocol.put(commands[3],3);
                        return;
                }
                break;
            }
            case "registration":
            {
                switch (commands[4]) {
                    case "ok":
                        statesProtocol.put(commands[3],0);
                        return;
                    case "error_user":
                        statesProtocol.put(commands[3],1);
                        return;
                }
                break;
            }
            case "getMailLogin":
            {
                switch (commands[5])
                {
                    case "ok":
                        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setUser_name(commands[4]);
                        statesProtocol.put(commands[3],0);
                        return;
                    case "error":
                        statesProtocol.put(commands[3],1);
                        return;
                }
                break;
            }
            case "changePswd":
            {
                switch (commands[4]) {
                    case "ok":
                        statesProtocol.put(commands[3],0);
                        return;
                    case "error":
                        statesProtocol.put(commands[3],1);
                        return;
                }
                break;
            }
            case "updateAvatars":
            {
                switch (commands[4]) {
                    case "ok":
                        statesProtocol.put(commands[3],0);
                        return;
                    case "error":
                        statesProtocol.put(commands[3],1);
                        return;
                }
                break;
            }
            case "getFriend": {
                if (commands[4].equals("error")) {
                    statesProtocol.put(commands[3],1);
                    return;
                }
                if (commands[4].equals("noFriend")) {
                    statesProtocol.put(commands[3],2);
                    return;
                }
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().setCommands(commands);
                statesProtocol.put(commands[3],0);
                return;
               /* FriendsInfoSingleton.getInstance().setHaveFriend(true);
                FriendsInfoSingleton.getInstance().setCommands(commands);
                FriendsInfoSingleton.getInstance().parseUser();
                return 0;*/
            }
            case "confirmFriend":
            {
                switch (commands[4]) {
                    case "ok":
                        statesProtocol.put(commands[3],0);
                        return;
                    case "error":
                        statesProtocol.put(commands[3],1);
                        return;
                }
                break;
            }
            case "cancelFriend":
            {
                switch (commands[4]) {
                    case "ok":
                        statesProtocol.put(commands[3],0);
                        return;
                    case "error":
                        statesProtocol.put(commands[3],1);
                        return;
                }
                break;
            }

        }
        statesProtocol.put(commands[3],1);
        return;
    }

}
