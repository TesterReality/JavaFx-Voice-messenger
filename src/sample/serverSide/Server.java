package sample.serverSide;

import sample.serverSide.ServerXmlProtocol.ParseServerVacoomProtocol;

import javax.crypto.Cipher;
import java.math.BigInteger;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends Thread {
    private String TEMPL_MSG =
            "The client '%d' sent me message : \n\t";
    private String TEMPL_CONN =
            "The client '%d' closed the connection";

    private Socket socket;
    private static int num;
    private byte[] inputMsg;
    private byte[] outputMsg;
    public PublicKey publicKeyUser;
    private boolean havePublicKeyUser = false;
    private boolean clientHaveMyPublicKey = false;
   // private ParseProtocol parser;
    private Server thisObj;
    private ArrayList<Integer> ports = new ArrayList<>();
    InputStream sin;
    OutputStream sout;
    DataInputStream dis;
    DataOutputStream dos;
    DHServer diffieServer;

    boolean haveDH = false;
    boolean isHaveSharedData = false;
    private String sha256DH1;
    private AES256Serv aes256Serv;
    private boolean isAesOk = false; //client aes == server aes?
    private ParseServerVacoomProtocol parseServerVacoomProtocol;
    public Server getThisObj() {
        return thisObj;
    }

    public void setThisObj(Server thisObj) {
        this.thisObj = thisObj;
    }

    public Server() {
    }

    public AES256Serv getAes256Serv() {
        return aes256Serv;
    }

    public void setAes256Serv(AES256Serv aes256Serv) {
        this.aes256Serv = aes256Serv;
    }

    public void setSocket(int num, Socket socket) {
        this.num = num;
        this.socket = socket;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }
/*
    public void sendMsgNow(String msg) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        outputMsg = SingletonServerCryproRSA.getInstance().encryptMsg(msg, publicKeyUser);//шифруем ответ
        dos.writeInt(outputMsg.length);
        dos.write(outputMsg, 0, outputMsg.length);
        System.out.println("Сервер отправил клиенту сообщение");
    }*/

    public void setPorts(int port) {
        ports.add(port);
    }

    public void run() {
        try {
            // Определяем входной и выходной потоки сокета
            // для обмена данными с клиентом
            sin = socket.getInputStream();
            sout = socket.getOutputStream();
            dis = new DataInputStream(sin);
            dos = new DataOutputStream(sout);


            parseServerVacoomProtocol = new ParseServerVacoomProtocol(thisObj);
            String line = null;
            while (true) {
                if (sin.available() > 0) { //если есть что считывать
                    System.out.println("[Пришли данные от клиента]:");
                    int num = dis.readInt();
                    if (num < 1024) {
                        inputMsg = new byte[num];
                        dis.readFully(inputMsg);
                        line = new String(inputMsg);
                        System.out.println(line);

                        if(!haveDH)//Еслю ключи по DH не были получены (первое подключение)
                        {
                            if(!isHaveSharedData) { //если мы не получили p g, то получаем их
                                String[] sharedData = line.split(":");//p:g:sha256(dh1)
                                sha256DH1 = sharedData[2];
                                System.out.println("SHA256(DH1): " + sha256DH1);
                                diffieServer = new DHServer(new BigInteger(sharedData[0]), new BigInteger(sharedData[1]));

                                System.out.println("[Сервер] Отправил свой публичный ключ (DH2) клиенту");
                                outputMsg = String.valueOf( diffieServer.getPublicB()).getBytes(); //Сервер отправляет клиенту свой публичный DH (DH2)
                                dos.writeInt(outputMsg.length);
                                dos.write(outputMsg, 0, outputMsg.length);
                                isHaveSharedData=true;
                            }else //если p и g мы уже получили, но еще не получили публичный ключ клиента
                            {
                               if (diffieServer.setPublicA(new BigInteger(line),sha256DH1))
                               {
                                   haveDH = true;

                                   String halfSharedkeyBytes = diffieServer.getSHA256(diffieServer.getSharedKeyB().toString());
                                   System.out.println("[Сервер] SHA256(общий ключ) ="+ halfSharedkeyBytes);
                                   halfSharedkeyBytes = halfSharedkeyBytes.substring(0,halfSharedkeyBytes.length()/2);
                                   System.out.println("[Сервер] SHA256(общий ключ)/2 ="+ halfSharedkeyBytes);

                                   outputMsg = halfSharedkeyBytes.getBytes(); //Сервер отправляет клиенту sha256(общего ключа) (половину)
                                   System.out.println("[Сервер] Отправили клиенту "+ halfSharedkeyBytes.length()/2 +" символов в sha256 от общего ключа");
                                   dos.writeInt(outputMsg.length);
                                   dos.write(outputMsg, 0, outputMsg.length);

                                   System.out.println("[Сервер] Сохранили ключ для AES256");
                                   aes256Serv = new AES256Serv(diffieServer.getByteSHA256(diffieServer.getSharedKeyB().toString()));
                               }
                            }
                        }
                        else
                        {
                            //Когда обменялись ключами
                            //Нам нужно убедиться что ключи сошлись
                            try {
                                if (!isAesOk) {
                                    String clientMsg = new String(aes256Serv.makeAes(inputMsg, Cipher.DECRYPT_MODE));
                                    if (clientMsg.equals("AES-OK")) {
                                        System.out.println("[Сервер] Клиент отправил AES-OK");
                                        isAesOk = true;
                                    } else//если ключи разные
                                    {
                                        System.out.println("[Сервер] Клиент !!НЕ!! отправил AES-OK");
                                        resetFlags();
                                    }

                                }else
                                {

                                    //Теперь Просто принимаем команды от сервера
                                    inputMsg = aes256Serv.makeAes(inputMsg, Cipher.DECRYPT_MODE);
                                    line = new String(inputMsg);//расшифрованное сообщение тут
                                    line = parseServerVacoomProtocol.parseRequest(line);//тут ответ от сервера
                                    if(line!=null) sendMessage(line);
                                }
                            }catch (NullPointerException nu)//не удастся расшифровать сообщение только 1 случае - если оно были либо неверно зашифрованно, либо не было зашифрованно
                            {
                                System.out.println("[СЕРВЕР] ОШИБКА AES ключей");
                                System.out.println("[Сервер] Клиент !!НЕ!! отправил AES-OK");
                                resetFlags();
                            }
                        }



                    }
                }

                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("ЭТО В ПОТОКЕ ГДЕ ЧИТАЮ : " + e);
        }
    }

    private void sendMessage(String msg)
    {
        try {
            outputMsg = aes256Serv.makeAes(msg.getBytes(), Cipher.ENCRYPT_MODE);
            dos.writeInt(outputMsg.length);
            dos.write(outputMsg, 0, outputMsg.length);
            System.out.println("Сервер отправил клиенту ответ");

        }catch (IOException e) {
            System.out.println("[СЕРВЕР] Ошибка отправки сообщения");
            e.printStackTrace();
        }
    }
    private void resetFlags()
    {
         haveDH = false;
         isHaveSharedData = false;
         isAesOk = false;
    }
/*
    private byte[] decrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        Cipher gg = SingletonServerCryproRSA.getInstance().getCipher();
        try {
            gg.init(Cipher.DECRYPT_MODE, SingletonServerCryproRSA.getInstance().getPrivateKey());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] y = gg.doFinal(inputMsg);

        return y;
    }*/

    private void parseProtocol(String msg) {
        System.out.println("ЭТОТ НОМЕР КЛИЕНТА" + num);
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(msg);
        while (m.find()) {
            System.out.println(m.group(1));
        }
        String[] strings1 = p.split(msg);
    }
}