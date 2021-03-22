package sample.serverSide;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class ServerMain {
    // открываемый порт сервера
    private static final int port = 5000;

    public static void main(String[] ar) {
        ServerSocket srvSocket = null;
        try {
          /*  SingletonServerCryproRSA.getInstance().firstRun();
            SingletonServerCryproRSA.getInstance().generateKeys();
            try {
                SingletonDatabaseConnection.getInstance().getDBConnection();
            } catch (Exception e) {
                e.printStackTrace();

                System.exit(0);
            }*/
            try {
                int i = 0; // Счётчик подключений
                // Подключение сокета к localhost
                InetAddress ia;
                ia = InetAddress.getByName("localhost");
                srvSocket = new ServerSocket(port, 0, ia);
                System.out.println("Server started\n\n");
                while (true) {
                    // ожидание подключения
                    Socket socket = srvSocket.accept();
                    System.err.println("Clients accepted");
                    System.out.println("Clients ЗДЕСЬ НОВЫЙ");

                    // Стартуем обработку клиента
                    // в отдельном потоке
                    Server serv = new Server();
                    serv.setThisObj(serv);
                    serv.setSocket(i++, socket);
                }
            } catch (Exception e) {
                System.out.println("Exception : " + e);
            }
        } finally {
            try {
                if (srvSocket != null)
                    srvSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}