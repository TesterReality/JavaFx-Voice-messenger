package sample;

import sample.ClientXmlPorocol.VacoomProtocol;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.SocketException;
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
        String myIp = "localhost";
        int Port;
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
            case "default":
                break;
        }
    }
}
