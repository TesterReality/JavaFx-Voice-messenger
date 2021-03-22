package sample;

import javafx.scene.control.Alert;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClientMsgThread extends Thread {
    private volatile String protocolMsg = "";
    private volatile boolean needSend = true;
    private volatile String onlineMsg;
    private InetAddress ipAddress;
    private Socket socket = null;
    private InputStream sin;
    private OutputStream sout;
    private DataInputStream in;
    private DataOutputStream out;
  //  ClientCryptoRSA cryptoRSA;
    boolean pubKeySend = false;
    byte bytemsg[];//исходящие
    byte inputMsg[];//входящие сообщение
    int len;
    String msgFromServer;
    private volatile boolean serverIsOnline = false;
  //  ClientParseProtocol parser;
    private volatile int answerGetCode = -1;
    private volatile boolean isRegistreUser = false;
    private volatile boolean userLogin = false;//пользлватель зашел?
    private volatile String user_name = "";
    private int sec = 30;// каждые 30 сек отправляем статус онлайн


    private boolean isDHFully = false; // значит что нет полной пары ключей DH
    private DH diffie;
    private boolean firstStep = false;

    public boolean isUserLogin() {
        return userLogin;
    }

    public void setUserLogin(boolean userLogin) {
        this.userLogin = userLogin;
    }

    public String getOnlineMsg() {
        return onlineMsg;
    }

    public void setOnlineMsg(String onlineMsg) {
        this.onlineMsg = onlineMsg;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isRegistreUser() {
        return isRegistreUser;
    }

    public void setRegistreUser(boolean registreUser) {
        isRegistreUser = registreUser;
    }

    public int getAnswerGetCode() {
        return answerGetCode;
    }

    public void setAnswerGetCode(int answerGetCode) {
        this.answerGetCode = answerGetCode;
    }

    public boolean isServerIsOnline() {
        return serverIsOnline;
    }

    public void setServerIsOnline(boolean serverIsOnline) {
        this.serverIsOnline = serverIsOnline;
    }

    public void setNeedSend(boolean needSend) {
        this.needSend = needSend;
    }

    public String getProtocolMsg() {
        return protocolMsg;
    }

    public void setProtocolMsg(String protocolMsg) {
        this.protocolMsg = protocolMsg;
    }

    private static final int serverPort = 5000;
    private static final String localhost = "185.156.42.223";
    public boolean starting() {
        try {
            try {
                System.out.println("Welcome to Clients side\n" +
                        "Connecting to the server\n\t" +
                        "(IP address " + localhost +
                        ", port " + serverPort + ")");
                ipAddress = InetAddress.getByName(localhost);
                socket = new Socket(ipAddress, serverPort);
                System.out.println(
                        "The connection is established.");
                System.out.println(
                        "\tLocalPort = " +
                                socket.getLocalPort() +
                                "\n\tInetAddress.HostAddress = " +
                                socket.getInetAddress()
                                        .getHostAddress() +
                                "\n\tReceiveBufferSize (SO_RCVBUF) = "
                                + socket.getReceiveBufferSize());
                // Получаем входной и выходной потоки
                // сокета для обмена сообщениями с сервером
                sin = socket.getInputStream();
                sout = socket.getOutputStream();
                in = new DataInputStream(sin);
                out = new DataOutputStream(sout);

                sendDHStartKey();
            } catch (Exception e) {
                return false;
            }
        } finally {
        }
        return true;
    }

    private void sendDHStartKey()
    {
        try {
            diffie = new DH();
            String gg = diffie.getPrimeValue().toString()+":"+diffie.getGeneratorValue().toString()+":"+diffie.getPublicSHA256();
            bytemsg = gg.getBytes();
            len = bytemsg.length;
            out.writeInt(len);
            out.write(bytemsg, 0, len); // отправляю байты
            System.out.println("Отправил p и q DH, а также SHA256");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при генерации DH ключей");
        }

    }

    private String getSHA256(String msg)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(msg.getBytes());

            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();

            for(int i=0;i<byteData.length;i++)
            {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));// ??
            }

            String getHexValue = sb.toString();
            System.out.println("SHA256("+msg+") = "+getHexValue);
            return  getHexValue;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
    private void senMsgToServer() {
      /*  try {
            if (!pubKeySend)//если свой публичный мы серваку не отправляли, то
            {
                bytemsg = cryptoRSA.getPublicKey().getEncoded();
                pubKeySend = true;
            } else {
                if (!protocolMsg.equals("")) {
                    bytemsg = cryptoRSA.encryptMsg(protocolMsg, cryptoRSA.getPublicKeyServer());
                    protocolMsg = "";
                }
            }
            len = bytemsg.length;
            out.writeInt(len);
            out.write(bytemsg, 0, len); // отправляю байты
        } catch (IOException e) {
        }*/
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(300);        //Приостановка потока
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!starting());

        do {
            if (!Thread.interrupted())    //Проверка прерывания
            {
                if (userLogin) {
                    sec--;
                    if (sec == 0) {
                        needSend = true;
                        protocolMsg = onlineMsg;
                        sec = 30;
                    }
                }
              /*  if (socket.isOutputShutdown())//если соединение есть
                {
                    serverIsOnline = false;
                    do {
                        try {
                            Thread.sleep(300);        //Приостановка потока
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (!starting());
                    serverIsOnline = true;
                }*/

                if (needSend)//если что-то нужно отравить
                {
                    senMsgToServer();
                    needSend = false;
                } else {
                    // return;
                }
                try {
                    if (sin.available() > 0)//если что-то нам пришло
                    {
                        int num = in.readInt();

                        inputMsg = new byte[num];
                        in.readFully(inputMsg);
                        msgFromServer = new String(inputMsg);

                        System.out.println("[Клиент получил такое сообщение]:");
                        System.out.print(msgFromServer);

                        if(!isDHFully)//если ключи не были получены от сервера
                        {
                            if(!firstStep) { //якобы первый шаг. Означает что отправили хеш DH, но не отправили сам ключ
                              //  isDHFully = true;
                                diffie.setPublicB(new BigInteger(msgFromServer));

                                //ниже отправляю свой публичный (ранее отправлял его хеш)
                                bytemsg = diffie.getPublicA().toString().getBytes();
                                len = bytemsg.length;
                                out.writeInt(len);
                                out.write(bytemsg, 0, len); // отправляю байты
                                System.out.println("[Клиент] Отправил свой публичный DH1");
                                firstStep=true;
                            }else
                            {
                                isDHFully=true;
                                String halfSha256SharedKey = getSHA256(diffie.getSharedKeyA().toString());
                                halfSha256SharedKey = halfSha256SharedKey.substring(0,halfSha256SharedKey.length()/2);
                                if(halfSha256SharedKey.equals(msgFromServer))
                                {
                                    System.out.println("[Клиент] Общие секретные ключи совпали!");
                                }else
                                {
                                    System.out.println("[Клиент] Общие секретные НЕ ключи совпали!!");
                                }
                            }
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;        //Завершение потока
            }
            try {
                Thread.sleep(1000);        //Приостановка потока на 1 сек.
            } catch (InterruptedException e) {
                return;    //Завершение потока после прерывания
            }
        }
        while (true);

        /*
        try {
            cryptoRSA = new ClientCryptoRSA();
            cryptoRSA.generateKeys();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        do {
            try {
                Thread.sleep(300);        //Приостановка потока
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!starting());
        serverIsOnline = true;
        parser = new ClientParseProtocol();
        do {
            if (!Thread.interrupted())    //Проверка прерывания
            {
                if (userLogin) {
                    sec--;
                    if (sec == 0) {
                        needSend = true;
                        protocolMsg = onlineMsg;
                        sec = 30;
                    }
                }
                if (socket.isOutputShutdown())//если соединение есть
                {
                    serverIsOnline = false;
                    do {
                        try {
                            Thread.sleep(300);        //Приостановка потока
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (!starting());
                    serverIsOnline = true;
                }
                if (cryptoRSA.getPublicKeyServer() != null) {

                }
                if (needSend)//если что-то нужно отравить
                {
                    senMsgToServer();
                    needSend = false;
                } else {
                    // return;
                }
                try {
                    if (sin.available() > 0)//если что-то нам пришло
                    {
                        int num = in.readInt();

                        inputMsg = new byte[num];
                        in.readFully(inputMsg);
                        if (cryptoRSA.getPublicKeyServer() == null) {
                            X509EncodedKeySpec spec =
                                    new X509EncodedKeySpec(inputMsg);
                            KeyFactory kf = KeyFactory.getInstance("RSA");
                            cryptoRSA.setPublicKeyServer(kf.generatePublic(spec));

                            String gg = com.sun.org.apache.xml.internal.security.utils.Base64.encode(cryptoRSA.getPublicKeyServer().getEncoded());
                            System.out.println("Пришел серверный публичный ключ:");
                            System.out.println(gg);
                        } else {
                            //если шо тут потока не было
                            try {
                                inputMsg = cryptoRSA.decryptMsg(inputMsg);
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            }
                            msgFromServer = new String(inputMsg);
                            answerGetCode = parser.switchAnswerFromServer(parser.parseRequest(msgFromServer));
                            System.out.println("Клиент получил такое сообщение:");
                            System.out.print(msgFromServer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;        //Завершение потока
            }
            try {
                Thread.sleep(1000);        //Приостановка потока на 1 сек.
            } catch (InterruptedException e) {
                return;    //Завершение потока после прерывания
            }
        }
        while (true);
        */
    }
}