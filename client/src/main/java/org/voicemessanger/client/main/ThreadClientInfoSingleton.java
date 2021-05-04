package org.voicemessanger.client.main;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Created by user on 06.05.2019.
 */
public class ThreadClientInfoSingleton {

    private ClientMsgThread clientMsgThread;
    private String user_name;
    private static ThreadClientInfoSingleton instance;
    private SmileCreater smileCreater;
    private HashMap<String, BufferedImage> imageUser = new HashMap<>();

    /*
    private WorkAreaController area;
    private CallingInWindowController callingWindow;
    private ClientCaller clientCaller;*/


    private ThreadClientInfoSingleton(){}

    public ClientMsgThread getClientMsgThread() {
        return clientMsgThread;
    }

    public  void setClientMsgThread(ClientMsgThread clientMsgThread) {
        this.clientMsgThread = clientMsgThread;
    }

    public HashMap<String, BufferedImage> getImageUser() {
        return imageUser;
    }

    public void setImageUser(HashMap<String, BufferedImage> imageUser) {
        this.imageUser = imageUser;
    }

    /*
        public ClientCaller getClientCaller() {
            return clientCaller;
        }

        public void setClientCaller(ClientCaller clientCaller) {
            this.clientCaller = clientCaller;
        }

        public CallingInWindowController getCallingWindow() {
            return callingWindow;
        }

        public void setCallingWindow(CallingInWindowController callingWindow) {
            this.callingWindow = callingWindow;
        }

        public WorkAreaController getArea() {
            return area;
        }

        public void setArea(WorkAreaController area) {
            this.area = area;
        }
    */
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public SmileCreater getSmileCreater() {
        return smileCreater;
    }

    public void setSmileCreater(SmileCreater smileCreater) {
        this.smileCreater = smileCreater;
    }

    public static ThreadClientInfoSingleton getInstance(){
        if(instance == null){
            instance = new ThreadClientInfoSingleton();
        }
        return instance;
    }
}
