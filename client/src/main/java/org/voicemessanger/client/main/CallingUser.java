package org.voicemessanger.client.main;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;
import org.voicemessanger.client.localdatabase.LocalDbHandler;
import org.voicemessanger.client.voice.Mixer;

import javax.sound.sampled.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CallingUser extends Thread{

    public static ArrayList<String> whoAmIcalling;
    private int Port =-1;
    private VacoomProtocol protocol;
    private String userName;
    private Controller parent;
    private UDPChannel udpChannel;
    private String answerFriend;
    private String friendName;
    private String ipFriend;
    private int friendPort;

    private String myKey;
    private String friendKey;
    HashMap<String, String> protocolMsg;

    private DH diffie;
    private volatile TargetDataLine targetLine;
    private volatile SourceDataLine audioOutputStream;
    DatagramSocket s = null;
    private  int BUFFER_SIZE = 1000;
    private AudioFormat AUDIO_FORMAT = new AudioFormat(8000.0f, 16, 1, true, true);
    InetAddress addrFriend;
    private volatile FloatControl gainControl;

    public CallingUser(String userName,Controller parent) {
        protocol = new VacoomProtocol();
        protocolMsg = new HashMap<>();

        this.userName = userName;
        this.parent = parent;
        this.start();
    }

    public void setPort(int port) {
        Port = port;
    }

    public int getPort() {
        return Port;
    }

    private void waitAnswer()
    {
        boolean isAnswer = false;
        try {
            CallingAnswerSaver answer = null;
            do {
                sleep(333);

                for (int i = 0; i < CallingAnswerSaver.callingAnswerSavers.size(); i++) {
                    answer = CallingAnswerSaver.callingAnswerSavers.get(i);
                    if (answer.getFriendLogin().equals(friendName)) {
                        isAnswer = true;
                        answerFriend = CallingAnswerSaver.callingAnswerSavers.get(i).getFrinedAnswer();
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
    public void run() {
        ClientUDPAccess clientUDPAccess = new ClientUDPAccess();
        String ipAdress="";

        System.out.println("Поток согласовнию ключей с другим клиентом запущен");
        do {
           Port = clientUDPAccess.getPorts();
        }while (Port==-1);
        try {
            ipAdress = clientUDPAccess.getIpAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        friendName = parent.dialogUsername.getText();
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.startCall("set",userName,friendName,ipAdress,String.valueOf(Port)));
        whoAmIcalling.add(parent.dialogUsername.getText());
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        System.out.println("[КЛИЕНТ] Отправил startCall");

        waitAnswer();

        myKey = new RandomString().randomString(12);
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.sendKeyFriend("set",userName,friendName,myKey));
        waitAnswer();

        sendDHStartKey();//отправили p g sha
        waitAnswer();

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.sendDHpublic("set",userName,friendName,diffie.getPublicA().toString()));
        waitAnswer();// жлем хеш общего секрета

       // waitAnswer();// ????

    }
    private void sendDHStartKey() {
        try {
            diffie = new DH();
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.clientDHstart("set",userName,friendName,diffie.getPrimeValue().toString(),diffie.getGeneratorValue().toString(),diffie.getPublicSHA256(),null));
            System.out.println("Отправил firstDH другу");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при генерации DH ключей");
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
        switch (protocolMsg.get("actionClient"))//содержит код запроса
        {
            case "startCall":
            {
                ipFriend=protocolMsg.get("ipUser");
                ipFriend = "localhost";
                friendPort= Integer.parseInt(protocolMsg.get("port"));
                System.out.println("[КЛИЕНТ] Получил ответ startCall");

                break;
                //new ReceivingCall(commands[6],commands[5],commands[7],commands[4]);
            }
            case "sendKey":
            {
                friendKey = protocolMsg.get("key");
                System.out.println("[КЛИЕНТ] Получил ответ sendKey" + friendKey);
                break;
            }
            case "firstDH":
            {
                System.out.println("[КЛИЕНТ] Получил ответ firstDH. Пришел публичный ключ 2го клиента. Отправляем ему свой публичный");
                diffie.setPublicB(new BigInteger(protocolMsg.get("public")));
                break;
            }
            case "halfDH":
            {
                System.out.println("[КЛИЕНТ] инициатор halfDH получил половину общего ключа");
                String halfSha256SharedKey = new SHA256Class().getSHA256(diffie.getSharedKeyA().toString());
                halfSha256SharedKey = halfSha256SharedKey.substring(0, halfSha256SharedKey.length() / 2);

                if (halfSha256SharedKey.equals(protocolMsg.get("hashSharedKey")))//Если общий секрет одинаковый
                {
                    System.out.println("Ключи совпали. Подтверждаем");
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.sharedKeyDHstatus("set",userName,friendName,"ok"));
                    startVoiceUDP();
                }else
                {
                    System.out.println("Ключи НЕ совпали. Сообщаем об этом");
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.sharedKeyDHstatus("set",userName,friendName,"error"));
                }
                break;
            }
            case "default":
                break;
        }
    }
    public void startVoiceUDP()
    {
        keyManipulation();
        startVoice();
    }

    public String keyManipulation()
    {
        String secretKey1 = "NaN";
        try {
            //добавлю ключи (keyBob keyAlice) в локальную бд
            LocalDbHandler.getInstance().addVoiceKey(myKey,friendKey,null,null,friendName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String newSercertKey = new StringXORer().encode(new StringXORer().encode(diffie.getSharedKeyA().toString(),myKey),friendKey);
        String newSercertKeyHash = new SHA256Class().getSHA256(newSercertKey);
        try {
            //добавлю новый секретный ключ в бд для обеспечения непрерывности ключегового материала
            LocalDbHandler.getInstance().addNewSecretKey(newSercertKeyHash,friendName);
            secretKey1 = LocalDbHandler.getInstance().getSecretKeyOne(friendName);
            if(secretKey1==null)
            {
                secretKey1 = " ";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String newCryptoVoiceKey = new StringXORer().encode(new StringXORer().encode(diffie.getSharedKeyA().toString(),secretKey1),newSercertKeyHash);
        String voiceKey = new SHA256Class().getSHA256(newCryptoVoiceKey);
        return voiceKey;

    }
    private void startVoice()
    {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    openCallWindow();
                }
            });


            addrFriend = InetAddress.getByName(ipFriend);
            serverConnection(addrFriend);
            Mixer mixer = Mixer.createDefault();
            String[] inputList= mixer.getInputNameList();
            String[] outputList= mixer.getOutputNameList();

            setupAudio(inputList[0],mixer,outputList[1]);

            new Thread(this::inputLoop, "Input loop").start();
            new Thread(this::outputLoop, "Output loop").start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void serverConnection(InetAddress addr) throws IOException {

        try {
            s = new DatagramSocket(Port);
            s.send(new DatagramPacket(new byte[0], 0, addr, friendPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    private void setupAudio(String input,Mixer mixer,String output) {
        setupInput(input,mixer);
        setupOutput(output,mixer);
    }
    private void setupInput(String inputItem,Mixer mixer) {

        Line.Info input;
            String inputSelectedName = inputItem;
            input = mixer.getInputByName(inputSelectedName);

        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(input);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void setupOutput(String outputItem,Mixer mixer) {

        Line.Info output;
            String outputSelectedName = outputItem;
            output = mixer.getOutputByName(outputSelectedName);
        try {
            audioOutputStream = (SourceDataLine) AudioSystem.getLine(output);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void inputLoop() {
        TargetDataLine currentLine = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        boolean wasMuted = false;
        while (true) {
            if (currentLine != targetLine) {
                if (currentLine != null) {
                    currentLine.stop();
                    currentLine.drain();
                    currentLine.close();
                }
                currentLine = targetLine;
                if (currentLine != null) {
                    try {
                        currentLine.open(AUDIO_FORMAT);
                        currentLine.start();
                    } catch (LineUnavailableException e) {
                        currentLine = null;
                        e.printStackTrace();
                    }
                }
            }
            /*
            if (wasMuted != muted) {
                wasMuted = muted;
                if (currentLine != null) {
                    if (muted) {
                        currentLine.stop();
                        currentLine.drain();
                    } else {
                        currentLine.start();
                    }
                }
            }*/
            if (currentLine == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (!wasMuted) {
                try {
                    int total = currentLine.read(buffer, 0, buffer.length);
                    s.send(new DatagramPacket(buffer, 0, total, addrFriend, friendPort));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void outputLoop() {
        SourceDataLine currentOutput = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            if (currentOutput != audioOutputStream) {
                if (currentOutput != null) {
                    currentOutput.drain();
                    currentOutput.close();
                }
                gainControl = null;
                currentOutput = audioOutputStream;
                if (currentOutput != null) {
                    try {
                        currentOutput.open(AUDIO_FORMAT);
                        currentOutput.start();
                        gainControl = (FloatControl) currentOutput.getControl(FloatControl.Type.MASTER_GAIN);
                        //updateSliderGain();
                    } catch (LineUnavailableException e) {
                        currentOutput = null;
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                       // updateSliderGain();
                        e.printStackTrace();
                    }
                }
            }
            if (currentOutput == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    s.receive(packet);
                    currentOutput.write(buffer, 0, packet.getLength());
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void openCallWindow()
    {
        FXMLLoader loader = new FXMLLoader();
        VoiceCallController voiceCallController =
                new VoiceCallController(friendName);
        voiceCallController.setThisNode(voiceCallController);
        loader = new FXMLLoader(
                getClass().getResource(
                        "/fxml/voiceCall.fxml"
                )
        );
        loader.setController(voiceCallController);
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
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.setWidth(471);
        stage.setHeight(455);
        ResizeHelper.addResizeListener(stage);
        stage.setTitle("Hello World");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

}

