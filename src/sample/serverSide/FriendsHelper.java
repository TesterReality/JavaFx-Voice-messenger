package sample.serverSide;

import java.util.ArrayList;

public class FriendsHelper {

    ArrayList<String> friend_name;
    ArrayList<String> status;
    ArrayList<Boolean> statusOnline;
    ArrayList<String> avatars;



    public FriendsHelper() {
    }

    public ArrayList<Boolean> getStatusOnline() {
        return statusOnline;
    }

    public void setStatusOnline(ArrayList<Boolean> statusOnline) {
        this.statusOnline = statusOnline;
    }

    public ArrayList<String> getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(ArrayList<String> friend_name) {
        this.friend_name = friend_name;
    }

    public ArrayList<String> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }

    public ArrayList<String> getAvatars() {
        return avatars;
    }

    public void setAvatars(ArrayList<String> avatars) {
        this.avatars = avatars;
    }
}
