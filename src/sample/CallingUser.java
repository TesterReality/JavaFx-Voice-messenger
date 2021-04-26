package sample;

import sample.ClientXmlPorocol.VacoomProtocol;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
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
        waitAnswer();



        //  System.out.println("[КЛИЕНТ] ПРИШЕЛ ОТВЕТ НА МОЙ ЗВОНОК");


        /*
        try {
            udpChannel = new UDPChannel();
            System.out.println("Мой порт UDP:"+ Port);
            udpChannel.bind(Port);
            udpChannel.start();//Начали прослушивать порт
        } catch (SocketException e) {
            e.printStackTrace();
        }

        System.out.println("Порт друга UDP:"+ friendPort);
        InetSocketAddress newAddress = new InetSocketAddress(ipFriend, friendPort);
        try {
            udpChannel.sendTo(newAddress , "testMSG");
            System.out.println("Отправил собщение по UDP");
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        /*
        do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("updateAvatars"));
            ErrorMsg t = new ErrorMsg();
            if( t.chnageAvatar()==0 )
            {

            }
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("updateAvatars");



        try {
            InetAddress ia;
            ia = InetAddress.getByName("0.0.0.0");
            ServerSocket ss = new ServerSocket(Port,0,ia); // создаем сокет сервера и привязываем его к вышеуказанному порту
            System.out.println("Ожидаем другого клиента...");
            Socket socket = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
            System.out.println("К нам кто-то подключился!");
            System.out.println();

            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            String line = null;
           // while(true) {
                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                System.out.println("Нам прислали : " + line);

               // System.out.println("I'm sending it back...");
               // out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
                out.flush(); // заставляем поток закончить передачу данных.
               // System.out.println("Waiting for the next line...");
               // System.out.println();
            in.close();
            out.close();
            sin.close();
            sout.close();
           // }
        } catch(Exception x) { x.printStackTrace(); }
        */
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

                if (halfSha256SharedKey.equals(protocolMsg.get("hashSharedKey")))
                {
                    System.out.println("ОХУЕТЬ КЛЮЧИ РАБОТАЮТ");
                }
                break;
            }
            case "default":
                break;
        }
    }
    public void startClientServer()
    {

    }


}

