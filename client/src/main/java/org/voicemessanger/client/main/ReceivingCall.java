package org.voicemessanger.client.main;



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
    private  int BUFFER_SIZE = 1000;
    private AudioFormat AUDIO_FORMAT = new AudioFormat(8000.0f, 16, 1, true, true);
    InetAddress addrFriend;
    private volatile FloatControl gainControl;
    private volatile TargetDataLine targetLine;
    private volatile SourceDataLine audioOutputStream;

    public ReceivingCall() {
    }

    public ReceivingCall(String loginFriend, String ip, String portUserUDP,String myLogin) {
        this.loginFriend = loginFriend;
        this.ip = ip;
        this.FriendPort =Integer.parseInt(portUserUDP);
        this.myLogin = myLogin;
        protocol = new VacoomProtocol();
        protocolMsg = new HashMap<>();

        this.start();
    }

    private void waitAnswer()
    {
        boolean isAnswer = false;
        try {
            CallingAnswerSaver answer = null;
            do {
                sleep(333);

                System.out.println("СООБЩЕНИЕ ОТ ДРУГА РАЗМЕР:"+CallingAnswerSaver.callingAnswerSavers.size());

                for (int i = 0; i < CallingAnswerSaver.callingAnswerSavers.size(); i++) {
                    answer = CallingAnswerSaver.callingAnswerSavers.get(i);
                    System.out.println("КТО ПРИСЛАЛ СООБЩЕНИЕ:" + answer.getFriendLogin());
                    System.out.println("Кого ждем :" + loginFriend);

                    if (answer.getFriendLogin().equals(loginFriend)) {
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

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.startCall("result", myLogin, loginFriend, myIp, String.valueOf(Port)));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        System.out.println("[КЛИЕНТ] ОТВЕТИЛ на startCall");
        waitAnswer();//ждм пока нам придет sendKey

        waitAnswer();//Ждем начальный DH

        waitAnswer();//Ждем публичный ключ DH

        waitAnswer();// Ждем подтверждения DH

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
            case "sendKey":
            {
                System.out.println("[КЛИЕНТ] ОТВЕТИЛ на sendKey");
                myKey = new RandomString().randomString(12);
                friendKey = protocolMsg.get("key");
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.sendKeyFriend("result",myLogin,loginFriend,myKey));
                break;
            }
            case "firstDH":
            {
                System.out.println("Иниициатор прислал ключи firstDH");
                sha256DH1 = protocolMsg.get("hash");
                System.out.println("SHA256(DH1): " + sha256DH1);

                try {
                    dhReceiving = new DHReceiving(new BigInteger(protocolMsg.get("p")), new BigInteger(protocolMsg.get("g")));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Ошибка ключей DH принимающего. Невозможно установить присланные ключи");
                }
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.clientDHstart("result",myLogin,loginFriend,null,null,null,String.valueOf( dhReceiving.getPublicB())));
                break;
            }
            case "publicDH":
            {
                System.out.println("publicDH Проверяет подлинность DH1 ");
                if (dhReceiving.setPublicA(new BigInteger(protocolMsg.get("public")),sha256DH1))
                {
                    String halfSharedkeyBytes = dhReceiving.getSHA256(dhReceiving.getSharedKeyB().toString());
                    System.out.println("[КЛИЕНТ] SHA256(общий ключ) ="+ halfSharedkeyBytes);
                    halfSharedkeyBytes = halfSharedkeyBytes.substring(0,halfSharedkeyBytes.length()/2);
                    System.out.println("[КЛИЕНТ] SHA256(общий ключ)/2 ="+ halfSharedkeyBytes);
                    System.out.println("Отправили половину общего секрета");
                    ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.halfDHhash("set",myLogin,loginFriend,halfSharedkeyBytes));

                }
                break;
            }
            case "DHstatus":
            {
                String status = protocolMsg.get("status");
                switch (status)
                {
                    case "ok":
                    {
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
            case "default":
                break;
        }
    }

    public void startVoiceUDP()
    {
        String voiceKey =keyManipulation();
        startVoice();
    }

    public String keyManipulation()
    {
        String secretKey1 = "NaN";
        try {
            //добавлю ключи (keyBob keyAlice) в локальную бд
            LocalDbHandler.getInstance().addVoiceKey(myKey,friendKey,null,null,loginFriend);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String newSercertKey = new StringXORer().encode(new StringXORer().encode(dhReceiving.getSharedKeyB().toString(),friendKey),myKey);
        String newSercertKeyHash = new SHA256Class().getSHA256(newSercertKey);
        try {
            //добавлю новый секретный ключ в бд для обеспечения непрерывности ключегового материала
            LocalDbHandler.getInstance().addNewSecretKey(newSercertKeyHash,loginFriend);
            secretKey1 = LocalDbHandler.getInstance().getSecretKeyOne(loginFriend);
            if(secretKey1==null)
            {
                secretKey1 = " ";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String newCryptoVoiceKey = new StringXORer().encode(new StringXORer().encode(dhReceiving.getSharedKeyB().toString(),secretKey1),newSercertKeyHash);
        String voiceKey = new SHA256Class().getSHA256(newCryptoVoiceKey);
        System.out.println("VOICE KEY " + voiceKey);
        return voiceKey;

    }
    private void startVoice()
    {
       // InetAddress addrFriend;
        try {
            addrFriend = InetAddress.getByName(ip);
            clientConnection(addrFriend);

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
    private void clientConnection(InetAddress addr) throws IOException {
        try {
            s = new DatagramSocket(Port);
            s.send(new DatagramPacket(new byte[0], 0, addr, FriendPort));
        } catch(SocketException e) {
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
                    s.send(new DatagramPacket(buffer, 0, total, addrFriend, FriendPort));
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

}
