package org.voicemessanger.client.main;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.awt.image.BufferedImage;

public class MyFriendController {

    public AnchorPane friend_AnchorPane;
    public Circle frined_avatar;
    public Label friendOnline;
    public Label friend_login;
    public Label friend_last_msg;
    public Label friend_date;
    public Circle friend_is_reding;


    String userName;
    String online;
    BufferedImage avatars;
    String last_msg;
    String friend_dates;

    Controller parent;
    MyFriendController thisNode;

    MyFriendController()
    {

    }

    public MyFriendController(String userName, String online, BufferedImage avatars, String last_msg, String friend_dates) {
        this.userName = userName;
        this.online = online;
        this.avatars = avatars;
        this.last_msg = last_msg;
        this.friend_dates = friend_dates;
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
        this.friend_date.setText(friend_dates);
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        friend_login.setText(userName);
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

    public String getFriend_dates() {
        return friend_dates;
    }

    public void setFriend_dates(String friend_dates) {
        this.friend_dates = friend_dates;
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

    public Label getFriend_date() {
        return friend_date;
    }

    public void setFriend_date(Label friend_date) {
        this.friend_date = friend_date;
    }

    public Circle getFriend_is_reding() {
        return friend_is_reding;
    }

    public void setFriend_is_reding(Circle friend_is_reding) {
        this.friend_is_reding = friend_is_reding;
    }

    public Controller getParent() {
        return parent;
    }

    public void setParent(Controller parent) {
        this.parent = parent;
    }

    public MyFriendController getThisNode() {
        return thisNode;
    }

    public void setThisNode(MyFriendController thisNode) {
        this.thisNode = thisNode;
    }

    public void userFriendClick(MouseEvent mouseEvent) {
        parent.dialogUsername.setText(userName);
        switch (online)
        {
            case "true":
                parent.startCallButton.setDisable(false);
                break;
            case "false":
                parent.startCallButton.setDisable(true);
                break;
        }
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().setLastClickAvatar(avatars);
    }
}
