package org.voicemessanger.server.main;

import org.voicemessanger.server.serverxmlprotocol.ParseServerVacoomProtocol;

import javax.crypto.Cipher;
import java.math.BigInteger;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends Thread {
    private String TEMPL_MSG =
            "The client '%d' sent me message : \n\t";
    private String TEMPL_CONN =
            "The client '%d' closed the connection";

    boolean isActive = true;
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
    public String USER_NAME;
    boolean haveDH = false;
    boolean isHaveSharedData = false;
    private String sha256DH1;
    private AES256Serv aes256Serv;
    private boolean isAesOk = false; //client aes == server aes?
    private ParseServerVacoomProtocol parseServerVacoomProtocol;
    private InfoUsernameFromThread infoThisUserThread;
    public Server getThisObj() {
        return thisObj;
    }
    int timePing =0;
    public void setThisObj(Server thisObj)
    {
        this.thisObj = thisObj;
        infoThisUserThread = new InfoUsernameFromThread(new RandomStringGenerator().generateString(),thisObj);
        ServerMain.usernameFromThreadArrayList.add(infoThisUserThread);

    }

    public Server() {

    }



    public void userThreadRename(String name)
    {
        USER_NAME = name;
        ServerMain.usernameFromThreadArrayList.remove(infoThisUserThread);
        infoThisUserThread = new InfoUsernameFromThread(name,thisObj);
        ServerMain.usernameFromThreadArrayList.add(infoThisUserThread);

       // InfoUsernameFromThread test = (InfoUsernameFromThread) search(ServerMain.usernameFromThreadArrayList,new InfoUsernameFromThread(name,null));
      /*
       search(ServerMain.usernameFromThreadArrayList,new InfoUsernameFromThread(name,thisObj));
        for (int i=0;i<ServerMain.usernameFromThreadArrayList.size();i++)
        {
         InfoUsernameFromThread inf = ServerMain.usernameFromThreadArrayList.get(i);
            if(inf.getThread()== thisObj)
            {
              //  ServerMain.usernameFromThreadArrayList.set(i,new InfoUsernameFromThread(USER_NAME,thisObj));
            }
        }*/
    }

    public void userThreadDel()
    {
        ServerMain.usernameFromThreadArrayList.remove(infoThisUserThread);
        /*
        for (int i=0;i<ServerMain.usernameFromThreadArrayList.size();i++)
        {
            InfoUsernameFromThread inf = ServerMain.usernameFromThreadArrayList.get(i);
            if(inf.getThread()== thisObj)
            {
                ServerMain.usernameFromThreadArrayList.remove(i);
            }
        }
        */
        System.out.println("???????????? ?? ?????????????? ????????????????????: " +ServerMain.usernameFromThreadArrayList.size() +" ??????????????" );
        for(InfoUsernameFromThread state : ServerMain.usernameFromThreadArrayList){
            System.out.println(state.getName());
        }
        /*
        for (int i=0;i<ServerMain.usernameFromThreadArrayList.size();i++)
        {
            System.out.println("[???????????? "+i+"]:"+ServerMain.usernameFromThreadArrayList.get(i).getName());
        }*/
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
       // start();
    }
/*
    public void sendMsgNow(String msg) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        outputMsg = SingletonServerCryproRSA.getInstance().encryptMsg(msg, publicKeyUser);//?????????????? ??????????
        dos.writeInt(outputMsg.length);
        dos.write(outputMsg, 0, outputMsg.length);
        System.out.println("???????????? ???????????????? ?????????????? ??????????????????");
    }*/

    public void setPorts(int port) {
        ports.add(port);
    }

    public void run() {
        try {
            // ???????????????????? ?????????????? ?? ???????????????? ???????????? ????????????
            // ?????? ???????????? ?????????????? ?? ????????????????
            sin = socket.getInputStream();
            sout = socket.getOutputStream();
            dis = new DataInputStream(sin);
            dos = new DataOutputStream(sout);


            parseServerVacoomProtocol = new ParseServerVacoomProtocol(thisObj);
            String line = null;
            //?????? ???????????? ???????????? ???????? ?????????????? ???? ?????????????????? ????????????????????????. ???? ???????????? ???????????????????? ?????? ??????????????????
            //?????????????? ?????????? ?????????????????????????? ?? ???????????????? ?????? ???? ???????????? ?? ?????? ????????????????
            System.out.println("???????????? ???????????? ???????????? ??????????????");

            outputMsg = String.valueOf("ready").getBytes(); //???????????? ???????????????????? ?????????????? ???????? ?????????????????? DH (DH2)
            dos.writeInt(outputMsg.length);
            dos.write(outputMsg, 0, outputMsg.length);

            while (isActive) {
                if (sin.available() > 0) { //???????? ???????? ?????? ??????????????????
                    System.out.println("[???????????? ???????????? ???? ??????????????]:");
                    int num = dis.readInt();
                    if (num < 1024) {
                        inputMsg = new byte[num];
                        dis.readFully(inputMsg);
                        line = new String(inputMsg);
                        System.out.println(line);

                        if(!haveDH)//???????? ?????????? ???? DH ???? ???????? ???????????????? (???????????? ??????????????????????)
                        {
                            if(!isHaveSharedData) { //???????? ???? ???? ???????????????? p g, ???? ???????????????? ????
                                String[] sharedData = line.split(":");//p:g:sha256(dh1)
                                sha256DH1 = sharedData[2];
                                System.out.println("SHA256(DH1): " + sha256DH1);
                                diffieServer = new DHServer(new BigInteger(sharedData[0]), new BigInteger(sharedData[1]));

                                System.out.println("[????????????] ???????????????? ???????? ?????????????????? ???????? (DH2) ??????????????");
                                outputMsg = String.valueOf( diffieServer.getPublicB()).getBytes(); //???????????? ???????????????????? ?????????????? ???????? ?????????????????? DH (DH2)
                                dos.writeInt(outputMsg.length);
                                dos.write(outputMsg, 0, outputMsg.length);
                                isHaveSharedData=true;
                            }else //???????? p ?? g ???? ?????? ????????????????, ???? ?????? ???? ???????????????? ?????????????????? ???????? ??????????????
                            {
                               if (diffieServer.setPublicA(new BigInteger(line),sha256DH1))
                               {
                                   haveDH = true;

                                   String halfSharedkeyBytes = diffieServer.getSHA256(diffieServer.getSharedKeyB().toString());
                                   System.out.println("[????????????] SHA256(?????????? ????????) ="+ halfSharedkeyBytes);
                                   halfSharedkeyBytes = halfSharedkeyBytes.substring(0,halfSharedkeyBytes.length()/2);
                                   System.out.println("[????????????] SHA256(?????????? ????????)/2 ="+ halfSharedkeyBytes);

                                   outputMsg = halfSharedkeyBytes.getBytes(); //???????????? ???????????????????? ?????????????? sha256(???????????? ??????????) (????????????????)
                                   System.out.println("[????????????] ?????????????????? ?????????????? "+ halfSharedkeyBytes.length()/2 +" ???????????????? ?? sha256 ???? ???????????? ??????????");
                                   dos.writeInt(outputMsg.length);
                                   dos.write(outputMsg, 0, outputMsg.length);

                                   System.out.println("[????????????] ?????????????????? ???????? ?????? AES256");
                                   aes256Serv = new AES256Serv(diffieServer.getByteSHA256(diffieServer.getSharedKeyB().toString()));
                               }
                            }
                        }
                        else
                        {
                            //?????????? ???????????????????? ??????????????
                            //?????? ?????????? ?????????????????? ?????? ?????????? ??????????????
                            try {
                                if (!isAesOk) {
                                    String clientMsg = new String(aes256Serv.makeAes(inputMsg, Cipher.DECRYPT_MODE));
                                    if (clientMsg.equals("AES-OK")) {
                                        System.out.println("[????????????] ???????????? ???????????????? AES-OK");
                                        isAesOk = true;
                                    } else//???????? ?????????? ????????????
                                    {
                                        System.out.println("[????????????] ???????????? !!????!! ???????????????? AES-OK");
                                        resetFlags();
                                    }

                                }else
                                {

                                    //???????????? ???????????? ?????????????????? ?????????????? ???? ??????????????
                                    inputMsg = aes256Serv.makeAes(inputMsg, Cipher.DECRYPT_MODE);
                                    line = new String(inputMsg);//???????????????????????????? ?????????????????? ??????
                                    line = parseServerVacoomProtocol.parseRequest(line);//?????? ?????????? ???? ??????????????
                                    if(line!=null) sendMessage(line);
                                }
                            }catch (NullPointerException nu)//???? ?????????????? ???????????????????????? ?????????????????? ???????????? 1 ???????????? - ???????? ?????? ???????? ???????? ?????????????? ????????????????????????, ???????? ???? ???????? ????????????????????????
                            {
                                nu.getMessage();
                                System.out.println("[????????????] ???????????? AES ????????????");
                                System.out.println("[????????????] ???????????? !!????!! ???????????????? AES-OK");
                                resetFlags();
                            }
                        }



                    }
                }

                Thread.sleep(300);
                timePing++;
                if(isAesOk && timePing==30)
                {
                    timePing=0;
                    sendMessage("ping") ;
                    if(USER_NAME!=null)
                    {
                        parseServerVacoomProtocol.setOnlineUser(USER_NAME);
                    }
                }

            }
        }
        catch (InterruptedException inter)
        {
            System.out.println("?????????? ?? ???????????????? ????????????????: " + inter);
            // ?????? ???????????? ?????????????? ?? ????????????????
            try {
                dis.close();
                dos.close();
                sin.close();
                sout.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        catch (SocketException e)
        {
            System.out.println("[????????????] ???????????? ????????????????????");
            userThreadDel();
            this.interrupt();
            isActive=false;
        } catch (Exception e) {
            System.out.println("?????? ?? ???????????? ?????? ?????????? : " + e);
        }
    }

    public void sendMessage(String msg)
    {
        try {
            outputMsg = aes256Serv.makeAes(msg.getBytes(), Cipher.ENCRYPT_MODE);
            dos.writeInt(outputMsg.length);
            dos.write(outputMsg, 0, outputMsg.length);
            System.out.println("???????????? ???????????????? ?????????????? ??????????");

        }
        catch (SocketException e)
        {
            System.out.println("[????????????] ???????????? ????????????????????");
            userThreadDel();
            this.interrupt();
            isActive=false;

        }
        catch (IOException e) {
            System.out.println("[????????????] ???????????? ???????????????? ??????????????????");
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
        System.out.println("???????? ?????????? ??????????????" + num);
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(msg);
        while (m.find()) {
            System.out.println(m.group(1));
        }
        String[] strings1 = p.split(msg);
    }
}