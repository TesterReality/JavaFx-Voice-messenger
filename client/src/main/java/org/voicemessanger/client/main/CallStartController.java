package org.voicemessanger.client.main;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;

import java.awt.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class CallStartController {
    public AnchorPane stageWindow;
    public Label tray;
    public Label Exit;
    public Circle userImgFriend;
    public Label friendName;
    public Label statusStr;
    public Label acceptButtom;
    public Label cancelButton;
    private VacoomProtocol protocol;
    HashMap<String, String> protocolMsg;

    private String friendNameStr;
    private String myNameStr;
    private boolean isICall;
    private SmileCreater smile;
    private  Controller parent;
    private volatile CallStartController thisClass;
    public CallStartController(String friendNameStr,String myNameStr,boolean isICall, SmileCreater smile, Controller parent ) {
        this.friendNameStr = friendNameStr;
        this.myNameStr = myNameStr;
        this.isICall = isICall;
        protocol = new VacoomProtocol();
        protocolMsg = new HashMap<>();
        this.smile = smile;
        this.parent = parent;
        thisClass = this;
    }
    private void waitAnswer()//НЕ ЗАХОДИТ СЮДА????
    {
        System.err.println("Запустил поток ожидание ответа звонка");
        boolean isAnswer = false;
        try {
            CallingAnswerSaver answer = null;
            do {
                sleep(333);

                for (int i = 0; i < CallingAnswerSaver.callingAnswerSavers.size(); i++) {
                    answer = CallingAnswerSaver.callingAnswerSavers.get(i);
                    if (answer.getFriendLogin().equals(friendNameStr)) {
                        System.err.println("Тут");

                        isAnswer = true;
                        String answerFriend = CallingAnswerSaver.callingAnswerSavers.get(i).getFrinedAnswer();
                        System.err.println(answerFriend);

                        parseRequest(answerFriend);
                        CallingAnswerSaver.callingAnswerSavers.remove(i);
                    }
                }
            } while (!isAnswer);
        }catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }
    @FXML
    private void initialize()
    {

        friendName.setText(friendNameStr);

        System.out.println("размер друганов с картинками: "+ThreadClientInfoSingleton.getInstance().getImageUser().size());
        //мейби тут ошибка? обработать??
        Image image =SwingFXUtils.toFXImage(ThreadClientInfoSingleton.getInstance().getImageUser().get(friendNameStr), null);

        userImgFriend.setFill(new ImagePattern(image));
        if(isICall)
        {
            //Значит звоню Я
            statusStr.setText("Звоним пользователю...");
            acceptButtom.setVisible(false);
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.firstCall("set",myNameStr,friendNameStr,"set"));


            Thread t = new Thread( this::waitAnswer,"waitCallAnswer");
            t.start();
        }else
        {
            statusStr.setText("Входящий звонок");
        }

    }


    public void toTray(MouseEvent mouseEvent) {
    }

    public void fullScreenWindow(MouseEvent mouseEvent) {
    }

    public void closeWindow(MouseEvent mouseEvent) {
    }

    public void clickAccept(MouseEvent mouseEvent) {
        if(!isICall)
        {
            Stage stage1 = (Stage) Exit.getScene().getWindow();
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.firstCall("result",myNameStr,friendNameStr,"ok"));
            stage1.close();
        }
    }

    public void clickCancel(MouseEvent mouseEvent) {
        if(!isICall)//если отвечающий на звонок
        {
            Stage stage1 = (Stage) Exit.getScene().getWindow();
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.firstCall("result",myNameStr,friendNameStr,"cancel"));
            stage1.close();
        }else
        {
            cancelButton.setDisable(true);
        }
    }
    public void parseRequest(String request) {
        int howNeedString =0;
        int howNeeds =0;

        String[] strings1;
        String[] strings2;

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
        strings2 = new String[howNeeds];

        m.reset();
        m1.reset();


        int numOfNow = 0;
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

        parseAnswerAccessUDP(strings1);
    }

    private void parseAnswerAccessUDP(String[] commands) {
        boolean isParse = false;
        switch (protocolMsg.get("actionClient"))//содержит код запроса
        {
            case "firstCall":
            {
                isParse=true;
                String answer = protocolMsg.get("status");
                System.out.println("[КЛИЕНТ] Получил ответ firstCall");
                switch (answer)
                {
                    case "ok":
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                Stage stage = (Stage) thisClass.Exit.getScene().getWindow();
                                CallingUser callingUser = new CallingUser(myNameStr,parent,smile);
                                stage.close();
                            }
                        });

                        break;
                    case "cancel":
                        isParse=true;
                        Stage stage1 = (Stage) Exit.getScene().getWindow();
                        stage1.close();
                        break;
                }
                break;
            }
            case "default":
                break;
        }
        if(!isParse) {
            CallingAnswerSaver.callingAnswerSavers.remove(CallingAnswerSaver.callingAnswerSavers.size() - 1);
            waitAnswer();
        }

    }
}
