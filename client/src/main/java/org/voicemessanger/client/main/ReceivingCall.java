package org.voicemessanger.client.main;


import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;
import org.voicemessanger.client.localdatabase.LocalDbHandler;
import org.voicemessanger.client.voice.Mixer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Принимающий вызов. Т.е звонят именно мне
public class ReceivingCall extends Thread {
    private String loginFriend;
    private String ip;
    private String myLogin;
    private int FriendPort;
    private VacoomProtocol protocol;
    private UDPChannel udpChannel;
    private String answerFriend;

    private String myKey;
    private String friendKey;
    HashMap<String, String> protocolMsg;

    private String sha256DH1;
    private DHReceiving dhReceiving;
    int Port;
    DatagramSocket s = null;
    private int BUFFER_SIZE = 1000;
    private AudioFormat AUDIO_FORMAT = new AudioFormat(8000.0f, 16, 1, true, true);
    InetAddress addrFriend;
    private volatile FloatControl gainControl;
    private volatile TargetDataLine targetLine;
    private volatile SourceDataLine audioOutputStream;

    private SmileCreater smileCreater;
    private volatile boolean isCalling = true;
    private volatile boolean isAnswer = false;

    private Thread voiceThread;
    private Thread soundThread;
    private Thread stopWaitingThred;
    private VoiceCallController voiceCallController;

    public ReceivingCall() {
    }

    public ReceivingCall(String loginFriend, String ip, String portUserUDP, String myLogin) {
        this.loginFriend = loginFriend;
        this.ip = ip;
        this.FriendPort = Integer.parseInt(portUserUDP);
        this.myLogin = myLogin;
        protocol = new VacoomProtocol();
        protocolMsg = new HashMap<>();
        smileCreater = ThreadClientInfoSingleton.getInstance().getSmileCreater();
        this.start();
    }

    public boolean isCalling() {
        return isCalling;
    }

    public String getMyLogin() {
        return myLogin;
    }

    public void setCalling(boolean calling) {
        isCalling = calling;
    }

    private void waitAnswer() {
        try {
            CallingAnswerSaver answer = null;
            do {
                sleep(333);

                if (!isAnswer) {
                    System.out.println("СООБЩЕНИЕ ОТ ДРУГА РАЗМЕР:" + CallingAnswerSaver.callingAnswerSavers.size());

                    for (int i = 0; i < CallingAnswerSaver.callingAnswerSavers.size(); i++) {
                        answer = CallingAnswerSaver.callingAnswerSavers.get(i);
                        System.out.println("КТО ПРИСЛАЛ СООБЩЕНИЕ:" + answer.getFriendLogin());
                        System.out.println("Кого ждем :" + loginFriend);

                        if (answer.getFriendLogin().equals(loginFriend)) {

                            answerFriend = CallingAnswerSaver.callingAnswerSavers.get(i).getFrinedAnswer();
                            parseRequest(answerFriend,answer);

                            if(!isAnswer)
                            CallingAnswerSaver.callingAnswerSavers.remove(i);

                            isAnswer = true;
                        }
                    }
                }
            } while (!isAnswer);
            isAnswer=false;
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Нам позвонили");

        ClientUDPAccess clientUDPAccess = new ClientUDPAccess();
        String myIp = "";

        try {
            myIp = clientUDPAccess.getIpAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        do {
            Port = clientUDPAccess.getPorts();
        } while (Port == -1);

        myIp = "localhost";
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.startCall("result", myLogin, loginFriend, myIp, String.valueOf(Port)));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        System.out.println("[КЛИЕНТ] ОТВЕТИЛ на startCall");
        waitAnswer();//ждм пока нам придет sendKey

        waitAnswer();//Ждем начальный DH

        waitAnswer();//Ждем публичный ключ DH

        waitAnswer();// Ждем подтверждения DH
        waitAnswer();// Ожидаем сообщение о том, что все верно

        stopWaitingThred = new Thread(
                this::waitAnswer,
                "waitStopThread");//ждем ответ о том, что другой завершил звонок
        stopWaitingThred.start();


/*
        try {
            udpChannel = new UDPChannel();
            System.out.println("Мой порт UDP:"+ Port);
            udpChannel.bind(Port);
            udpChannel.start();//Начали прослушивать порт
        } catch (SocketException e) {
            e.printStackTrace();
        }

        ip = "localhost";
        System.out.println("Порт друга UDP:"+ FriendPort);

        InetSocketAddress newAddress = new InetSocketAddress(ip, FriendPort);
        try {
            for( int i=0;i<5;i++)
            udpChannel.sendTo(newAddress , "testMSG");
            System.out.println("Отправил собщение по UDP");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void parseRequest(String request, CallingAnswerSaver answer) {
        int howNeedString = 0;
        int howNeeds = 0;

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

        if (protocolMsg.size() > 0)
            protocolMsg.clear();

        for (int i = 0; i < strings1.length; i++) {
            protocolMsg.put(strings2[i], strings1[i]);
        }

        parseAnswerAccessUDP(strings1,answer);
    }

    private void parseAnswerAccessUDP(String[] commands,CallingAnswerSaver answer) {
        switch (protocolMsg.get("actionClient"))//содержит код запроса
        {
            case "sendKey": {
                System.out.println("[КЛИЕНТ] ОТВЕТИЛ на sendKey");
                myKey = new RandomString().randomString(12);
                friendKey = protocolMsg.get("key");
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.sendKeyFriend("result", myLogin, loginFriend, myKey));
                break;
            }
            case "firstDH": {
                System.out.println("Иниициатор прислал ключи firstDH");
                sha256DH1 = protocolMsg.get("hash");
                System.out.println("SHA256(DH1): " + sha256DH1);

                try {
                    dhReceiving = new DHReceiving(new BigInteger(protocolMsg.get("p")), new BigInteger(protocolMsg.get("g")));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Ошибка ключей DH принимающего. Невозможно установить присланные ключи");
                }
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.clientDHstart("result", myLogin, loginFriend, null, null, null, String.valueOf(dhReceiving.getPublicB())));
                break;
            }
            case "publicDH": {
                System.out.println("publicDH Проверяет подлинность DH1 ");
                if (dhReceiving.setPublicA(new BigInteger(protocolMsg.get("public")), sha256DH1)) {
                    String halfSharedkeyBytes = dhReceiving.getSHA256(dhReceiving.getSharedKeyB().toString());
                    System.out.println("[КЛИЕНТ] SHA256(общий ключ) =" + halfSharedkeyBytes);
                    halfSharedkeyBytes = halfSharedkeyBytes.substring(0, halfSharedkeyBytes.length() / 2);
                    System.out.println("[КЛИЕНТ] SHA256(общий ключ)/2 =" + halfSharedkeyBytes);
                    System.out.println("Отправили половину общего секрета");
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.halfDHhash("set", myLogin, loginFriend, halfSharedkeyBytes));

                }
                break;
            }
            case "DHstatus": {
                System.out.println("Распознал DHstatus");
                String status = protocolMsg.get("status");
                System.out.println(status);
                switch (status) {
                    case "ok": {
                        startVoiceUDP();
                        break;
                    }
                    case "error": // ключ был кем-то изменен
                    {
                        break;
                    }
                }

                break;
            }
            case "stopCall": {
                if(s==null)
                {
                    if (isAnswer)
                    {
                       // CallingAnswerSaver.callingAnswerSavers.remove( CallingAnswerSaver.callingAnswerSavers.size()-2);
                        CallingAnswerSaver.callingAnswerSavers.remove( answer);

                    }
                    break;
                }
                System.out.println("Завершаем звонок");
                try {
                    stopWaitingThred.interrupt();
                }catch (NullPointerException npe)
                {
                    npe.getMessage();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        voiceCallController.closeWin();
                    }
                });

                setCalling(false);
                stopCalling();
                this.interrupt();
                break;
            }
            case "default":
                break;
        }
    }

    public void startVoiceUDP() {

        String voiceKey = keyManipulation();
        startVoice();
    }

    public String keyManipulation() {
        String secretKey1 = "NaN";
        try {
            //добавлю ключи (keyBob keyAlice) в локальную бд
            LocalDbHandler.getInstance().addVoiceKey(myKey, friendKey, null, null, loginFriend);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String newSercertKey = new StringXORer().encode(new StringXORer().encode(dhReceiving.getSharedKeyB().toString(), friendKey), myKey);
        String newSercertKeyHash = new SHA256Class().getSHA256(newSercertKey);
        try {
            //добавлю новый секретный ключ в бд для обеспечения непрерывности ключегового материала
            LocalDbHandler.getInstance().addNewSecretKey(newSercertKeyHash, loginFriend);
            secretKey1 = LocalDbHandler.getInstance().getSecretKeyOne(loginFriend);
            if (secretKey1 == null) {
                secretKey1 = " ";
            }
            if (secretKey1.equals("")) {
                secretKey1 = " ";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String newCryptoVoiceKey = new StringXORer().encode(new StringXORer().encode(dhReceiving.getSharedKeyB().toString(), secretKey1), newSercertKeyHash);
        String voiceKey = new SHA256Class().getSHA256(newCryptoVoiceKey);
        System.out.println("VOICE KEY " + voiceKey);
        return voiceKey;

    }

    public void stopCalling() {
        try {
            voiceThread.interrupt();
            soundThread.interrupt();
        }catch (NullPointerException npe)
        {
            npe.getMessage();
        }
        s.disconnect();
        s.close();
        isAnswer=true;
        System.out.println("Все порты закрыты. Звонок прекращен");
    }

    private void startVoice() {
        // InetAddress addrFriend;
        System.out.println("Открываю окно звонка");
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    openCallWindow();
                }
            });

            addrFriend = InetAddress.getByName(ip);
            clientConnection(addrFriend);

            Mixer mixer = Mixer.createDefault();
            String[] inputList = mixer.getInputNameList();
            String[] outputList = mixer.getOutputNameList();

            setupAudio(inputList[0], mixer, outputList[1]);

            voiceThread = new Thread(this::inputLoop, "Input loop");
            voiceThread.start();
            soundThread = new Thread(this::outputLoop, "Output loop");
            soundThread.start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void clientConnection(InetAddress addr) throws IOException {
        try {
            s = new DatagramSocket(Port);
            s.send(new DatagramPacket(new byte[0], 0, addr, FriendPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void setupAudio(String input, Mixer mixer, String output) {
        setupInput(input, mixer);
        setupOutput(output, mixer);
    }

    private void setupInput(String inputItem, Mixer mixer) {

        Line.Info input;
        String inputSelectedName = inputItem;
        input = mixer.getInputByName(inputSelectedName);

        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(input);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void openCallWindow() {
        FXMLLoader loader = new FXMLLoader();
        voiceCallController =
                new VoiceCallController(loginFriend);
        voiceCallController.setThisNode(voiceCallController);
        voiceCallController.setParent(this);
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
        createCryptoSmile(voiceCallController);
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

    private void createCryptoSmile(VoiceCallController voiceCallController) {
        String DH1SHA = new SHA256Class().getSHA256(dhReceiving.getPublicA().toString());
        for (int i = 0; i < DH1SHA.length(); i += 16) {
            String smileString = DH1SHA.substring(i, i + 16);
            byte[] smileByte = smileString.getBytes();
            long stringToLong = bytesToLong(smileByte);
            ;
            stringToLong = stringToLong % (58 * 58);
            int x = (int) (stringToLong % 58);
            int y = (int) (stringToLong / 58);
            System.out.println("Число " + stringToLong + " x=" + x + " y=" + y);
            Image image;
            try {
                image = SwingFXUtils.toFXImage(smileCreater.getEmojiFromIndex(x, y), null);
            } catch (NullPointerException npe) {
                image = SwingFXUtils.toFXImage(smileCreater.getEmojiFromIndex(33, 33), null);
            }

            switch (i) {
                case 0:
                    voiceCallController.smile0.setImage(image);
                    break;
                case 16:
                    voiceCallController.smile1.setImage(image);
                    break;
                case 32:
                    voiceCallController.smile2.setImage(image);
                    break;
                case 48:
                    voiceCallController.smile3.setImage(image);
                    break;
            }
        }
    }

    public long bytesToLong(final byte[] b) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    private void setupOutput(String outputItem, Mixer mixer) {

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
        try {
            TargetDataLine currentLine = null;
            byte[] buffer = new byte[BUFFER_SIZE];
            boolean wasMuted = false;

            while (isCalling) {
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
                        s.send(new DatagramPacket(buffer, 0, total, addrFriend, FriendPort));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Теперь закрываю каналы считывания голоса");
            currentLine.stop();
            currentLine.drain();
            currentLine.close();
        } catch (Exception e) {
            System.out.println("Ошибка в потоке голоса");
            e.printStackTrace();
        }
    }

    private void outputLoop() {
        SourceDataLine currentOutput = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (isCalling) {
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
        System.out.println("Закрываю каналы прослушивания голоса");
        currentOutput.drain();
        currentOutput.close();
    }

}
