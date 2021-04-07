package sample;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class MyFriendController {

    public AnchorPane friend_AnchorPane;
    public Circle frined_avatar;
    public Label friendOnline;
    public Label friend_login;
    public Label friend_last_msg;
    public Label friend_date;
    public Circle friend_is_reding;

    Controller parent;
    MyFriendController thisNode;

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
}
