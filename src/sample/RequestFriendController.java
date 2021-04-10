package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import sample.ClientXmlPorocol.VacoomProtocol;

import java.awt.image.BufferedImage;

public class RequestFriendController extends VacoomProtocol {

    public Circle frined_avatar;
    public Label friendOnline;
    public Label friend_login;
    public Label friend_last_msg;
    public Label cancelFriendButton;
    public Label acceptFriendButton;

    String userName;
    String online;
    BufferedImage avatars;
    String last_msg;
    String parentUserName;

    Controller parent;
    RequestFriendController thisNode;

    RequestFriendController()
    {

    }

    RequestFriendController(String userName, String online, BufferedImage avatars, String last_msg,String parentUserName)
    {
        this.userName = userName;
        this.online = online;
        this.avatars = avatars;
        this.last_msg = last_msg;
        this.parentUserName = parentUserName;
    }

    private void disableButton()
    {
        cancelFriendButton.setDisable(false);
        acceptFriendButton.setDisable(false);
    }
    @FXML
    private void initialize(){
        friend_login.setText(userName);
        friendOnline.getStyleClass().clear();
        switch (online)
        {
            case "true":
                friendOnline.getStyleClass().add("isOnline");
                break;
            case "false":
                friendOnline.getStyleClass().add("isOffline");
                break;
        }

        Image image = SwingFXUtils.toFXImage(avatars, null);
        frined_avatar.setFill(new ImagePattern(image));

        friend_last_msg.setText(last_msg);
    }

    public void cancelFriend(MouseEvent mouseEvent) {

        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(cancelFriend(parentUserName,userName));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        disableButton();
        new Thread(() -> {
            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("cancelFriend"));
            ErrorMsg t = new ErrorMsg();
            if( t.cancelFriend()==0 )
            {
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("cancelFriend");
                parent.getFriendThread();
            }

        }).start();

        System.out.println("Отсылаем друга");
    }

    public void acceptFriend(MouseEvent mouseEvent) {
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(confirmFriend(parentUserName,userName));
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
        disableButton();

        new Thread(() -> {
            do {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("confirmFriend"));
            ErrorMsg t = new ErrorMsg();
            if( t.confirmFriend()==0 )
            {
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("confirmFriend");
                parent.getFriendThread();
            }

        }).start();

        System.out.println("Принимаем друга");
    }

    public Circle getFrined_avatar() {
        return frined_avatar;
    }

    public void setFrined_avatar(Circle frined_avatar) {
        this.frined_avatar = frined_avatar;
    }

    public Label getFriendOnline() {
        return friendOnline;
    }

    public void setFriendOnline(Label friendOnline) {
        this.friendOnline = friendOnline;
    }

    public Label getFriend_login() {
        return friend_login;
    }

    public void setFriend_login(Label friend_login) {
        this.friend_login = friend_login;
    }

    public Label getFriend_last_msg() {
        return friend_last_msg;
    }

    public void setFriend_last_msg(Label friend_last_msg) {
        this.friend_last_msg = friend_last_msg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public BufferedImage getAvatars() {
        return avatars;
    }

    public void setAvatars(BufferedImage avatars) {
        this.avatars = avatars;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public Controller getParent() {
        return parent;
    }

    public void setParent(Controller parent) {
        this.parent = parent;
    }

    public RequestFriendController getThisNode() {
        return thisNode;
    }

    public void setThisNode(RequestFriendController thisNode) {
        this.thisNode = thisNode;
    }
}
