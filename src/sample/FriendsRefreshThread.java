package sample;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FriendsRefreshThread extends Thread {


    private String USER_NAME;
    private Controller parent;
    private String urlPathToImg = "https://res.cloudinary.com/diplomaimgdpi/image/upload/";

    FriendsRefreshThread(String USER_NAME,Controller parent) {
        this.USER_NAME = USER_NAME;
        this.parent =parent;
        start(); // Запускаем поток
    }
    public String getFriend(String userLogin) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "getFriend")
                            .attr("login", userLogin)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        byte[] gg = xml.getBytes();
        int index = xml.indexOf(">");
        xml = xml.substring(index + 3, xml.length());
        System.out.print(xml);
        return xml;
    }

    public void run() {
        try {
            do {
                Thread.sleep(5000);

                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(getFriend(USER_NAME));
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
                do {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("getFriend"));
                ErrorMsg err = new ErrorMsg();
                if (err.checkFriend() == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                           parent.users_list.getChildren().clear();

                            int numOfFriend = ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getFriendNumber();
                            for(int i=0;i<numOfFriend;i++)
                            {
                                switch (ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getFrienStatus(i)) {
                                    case "1":
                                        setFriend(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getFriendName(i),
                                                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getStatusOnline(i),
                                                getImgFromUrl(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getFriendAvatars(i)),
                                                "Друг",
                                                "" );
                                        break;//друг
                                    case "3":
                                        setNewFriendRequest(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getFriendName(i),
                                                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getStatusOnline(i),
                                                getImgFromUrl(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getFriendAvatars(i)),
                                                "Запрос в друзья");
                                        //   setNewUser(FriendsInfoSingleton.getInstance().getFriendName(i), "Запрос в друзья", Boolean.parseBoolean(FriendsInfoSingleton.getInstance().getStatusOnline(i)));
                                        break;//запрос дал мне
                                }
                            }
                            // ThreadClientInfoSingleton.getInstance().getClientMsgThread().setUserLogin(true);
                            // parents.loadWorkrArea(input.getText());

                        }
                    });
                }

                if (err.checkFriend() == 2) {
                    System.out.println("Нет друзей :(");
                }
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("getFriend");
            } while (true);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setNewFriendRequest(String friendName, String online, BufferedImage avatars, String last_msg)
    {
        FXMLLoader loader = new FXMLLoader();
        RequestFriendController myNewfriend =
                new RequestFriendController(friendName,online,avatars,last_msg,USER_NAME);
        myNewfriend.setParent(parent.thisNode);
        myNewfriend.setThisNode(myNewfriend);
        loader = new FXMLLoader(
                getClass().getResource(
                        "fxml/requestFriend.fxml"
                )
        );
        loader.setController(myNewfriend);
        AnchorPane newUsers =null;

        try {
            newUsers = (AnchorPane) loader.load();
            parent.users_list.getChildren().add(newUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void setFriend(String userName,String online,BufferedImage avatars,String last_msg,String friend_date)
    {
        FXMLLoader loader = new FXMLLoader();
        MyFriendController myfriend =
                new MyFriendController(userName,online,avatars,last_msg,friend_date);
        myfriend.setParent(parent.thisNode);
        myfriend.setThisNode(myfriend);
        loader = new FXMLLoader(
                getClass().getResource(
                        "fxml/myFriend.fxml"
                )
        );
        loader.setController(myfriend);
        AnchorPane newUsers =null;

        try {
            newUsers = (AnchorPane) loader.load();
            parent.users_list.getChildren().add(newUsers);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private BufferedImage getImgFromUrl(String urlStr)
    {
        URLConnection uc;
        String tmpUrl = urlPathToImg +urlStr+".png";
        BufferedImage imageAvatars =null;
        try {
            URL url = new URL(tmpUrl);
            uc = url.openConnection();
            uc.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            uc.connect();

            InputStream urlStream = uc.getInputStream();
            imageAvatars = ImageIO.read(urlStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageAvatars;
    }
}
