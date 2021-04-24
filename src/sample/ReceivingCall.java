package sample;

import sample.ClientXmlPorocol.VacoomProtocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

//Принимающий вызов. Т.е звонят именно мне
public class ReceivingCall extends Thread {
    private String loginFriend;
    private String ip;
    private String myLogin;
    private int FriendPort;
    private VacoomProtocol protocol;
    private UDPChannel udpChannel;

    public ReceivingCall() {
    }

    public ReceivingCall(String loginFriend, String ip, String portUserUDP,String myLogin) {
        this.loginFriend = loginFriend;
        this.ip = ip;
        this.FriendPort =Integer.parseInt(portUserUDP);
        this.myLogin = myLogin;
        protocol = new VacoomProtocol();
        this.start();
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
        System.out.println("[КЛИЕНТ] ОТВЕТИЛ НА ЗВОНОК");

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
        }
    }
}
