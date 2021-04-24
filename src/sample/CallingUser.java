package sample;

import sample.ClientXmlPorocol.VacoomProtocol;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
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

    private String ipFriend;
    private int friendPort;

    public CallingUser(String userName,Controller parent) {
        protocol = new VacoomProtocol();
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

    public void run() {
        try {
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
        String friendName = parent.dialogUsername.getText();
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.startCall("set",userName,friendName,ipAdress,String.valueOf(Port)));
        whoAmIcalling.add(parent.dialogUsername.getText());
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        boolean isAnswer = false;
        CallingAnswerSaver answer = null;
        do {
            sleep(333);

            for (int i=0;i<CallingAnswerSaver.callingAnswerSavers.size();i++)
            {
                answer=   CallingAnswerSaver.callingAnswerSavers.get(i);
                if(answer.getFriendLogin().equals(friendName))
                {
                    isAnswer=true;
                    answerFriend =  CallingAnswerSaver.callingAnswerSavers.get(i).getFrinedAnswer();
                    parseRequest(answerFriend);
                    CallingAnswerSaver.callingAnswerSavers.remove(i);
                }
            }
        }while (!isAnswer);
            System.out.println("[КЛИЕНТ] ПРИШЕЛ ОТВЕТ НА МОЙ ЗВОНОК");

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
            }


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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void parseRequest(String request) {
        int howNeedString =0;
        String[] strings1;
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(request);
        while (m.find()) {
            System.out.println(m.group(1));
            howNeedString++;
        }
        strings1 = new String[howNeedString];
        m.reset();
        int numOfNow = 0;
        while (m.find()) {
            strings1[numOfNow++] = m.group(1);
        }
        parseAnswerAccessUDP(strings1);
    }
    private void parseAnswerAccessUDP(String[] commands) {
        switch (commands[3])//содержит код запроса
        {
            case "startCall":
            {
                ipFriend=commands[5];
                ipFriend = "localhost";
                friendPort= Integer.parseInt(commands[7]);
                //new ReceivingCall(commands[6],commands[5],commands[7],commands[4]);
            }
            case "default":
                break;
        }
    }
    public void startClientServer()
    {

    }


}

