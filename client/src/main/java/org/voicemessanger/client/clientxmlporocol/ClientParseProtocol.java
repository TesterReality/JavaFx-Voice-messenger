package org.voicemessanger.client.clientxmlporocol;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.voicemessanger.client.main.*;

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
    private int howNeeds;

    private int numOfNow; // текущее положение строки
    private int randomNum;
    String[] strings1;
    ArrayList<Integer> portCheck = new ArrayList<>();
    private String lastRequest;
    Map<String,Integer > statesProtocol;
    HashMap<String, String> protocolMsg;

    public ClientParseProtocol() {
        statesProtocol = new HashMap<String,Integer>();
        statesProtocol.put("state",0);//test state
        protocolMsg = new HashMap<>();
    }

    public Map parseRequest(String request) {
        lastRequest = request;
        howNeedString=0;
        howNeeds =0;
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Pattern p1 = Pattern.compile("\\w+(?=\\=)");//Сами теги, например: <from to="client".. будет to

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

        strings1 = new String[howNeedString];
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

        parseCommand(strings1);
        return statesProtocol;
    }

    private void parseCommand(String[] commands) {
        switch (commands[1])//мы должны отправить или получить
        {
            case "set"://значит НАМ подали запрос, мы должны ответить
            {
                //statesProtocol.put(commands[3],1);
                setCommands(commands);
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
        switch (protocolMsg.get("actionClient"))//содержит код запроса
        {
            case "startCall":
            {
                new ReceivingCall(protocolMsg.get("login"),protocolMsg.get("ipUser"),protocolMsg.get("port"),protocolMsg.get("friend"));
                break;
            }
            case "sendKey":
            {
                CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                break;
            }
            case "firstDH":
            {
                CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                break;
            }
            case "publicDH":
            {
                CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                break;
            }
            case "halfDH":
            {
                CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                break;
            }
            case "DHstatus":
            {
                System.out.println("При приеме протокола поняли что это DHstatus");
                CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                break;
            }
            case "firstCall":
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                       newCall(protocolMsg.get("friend"),protocolMsg.get("login"),false);
                    }
                });
            }
            case "stopCall":
            {
                CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                break;
            }
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
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().setLastFriendStr(lastRequest);
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

            case "default":
                break;

        }
        if (protocolMsg.containsKey("actionClient"))
        {
            CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
            CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
            return;
            /*
            switch (protocolMsg.get("actionClient"))
            {

                case "startCall":
                {
                    CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                    CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                    return;
                }
                case "sendKey":
                {
                    CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                    CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                    return;
                }
                case "firstDH":
                {
                    CallingAnswerSaver callingAnswer = new CallingAnswerSaver(protocolMsg.get("login"),lastRequest);
                    CallingAnswerSaver.callingAnswerSavers.add(callingAnswer);
                    return;
                }

            }*/

        }
        statesProtocol.put(commands[3],1);
        return;
    }

    private void newCall(String myLogin,String friendLogin,boolean status)
    {
        FXMLLoader loader = new FXMLLoader();
        CallStartController callStartController =
                new CallStartController(friendLogin,myLogin,status,ThreadClientInfoSingleton.getInstance().getSmileCreater(),null);//установили имя друга,мое,Звоню я?(true-да, false - мне)

        loader = new FXMLLoader(
                getClass().getResource(
                        "/fxml/callStart.fxml"
                )
        );

        loader.setController(callStartController);

        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);

        //stage.setResizable(false);
        stage.setMinWidth(300);
        stage.setMinHeight(400);
        stage.setWidth(351);
        stage.setHeight(455);
        ResizeHelper.addResizeListener(stage);
        stage.setTitle("Звонок");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

}
