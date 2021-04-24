package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;

public class ClientUDPAccess {
    private int Port;


    public ClientUDPAccess() {
    }

    public String getIpAddress() throws MalformedURLException, IOException {
        try {
            URL myIP = new URL("http://checkip.amazonaws.com/");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(myIP.openStream())
            );
            return in.readLine();

        }catch (Exception e)
        {
            URL myIP = new URL("http://icanhazip.com/");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(myIP.openStream())
            );
            return in.readLine();
        }
    }

    public int getPorts()
    {
        try {
            ServerSocket test = new ServerSocket(0);
            Port = test.getLocalPort();
            System.out.println( Port);
            test.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean portFree;
        try (ServerSocket ignored = new ServerSocket(Port)) {
            portFree = true;
            ignored.close();
            System.out.println(portFree);
            return Port;
        } catch (IOException e) {
            portFree = false;
            Port =-1;
        }
        return -1;
    }
}
