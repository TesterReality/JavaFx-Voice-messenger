package sample;

import java.util.ArrayList;

public class FriendsInfo {
    private String[] commands;
    boolean haveFriend = false;
    ArrayList<String> friend_name = new ArrayList<String>();
    ArrayList<String> status = new ArrayList<String>();
    ArrayList<String> statusOnline = new ArrayList<String>();
    ArrayList<String> avatars = new ArrayList<String>();

    String[] temp;

    public FriendsInfo(){}

    public ArrayList<String> getStatusOnline() {
        return statusOnline;
    }

    public void setHaveFriend(boolean haveFriend) {
        this.haveFriend = haveFriend;
    }

    public String getStatusOnline(int index) {
        return statusOnline.get(index);
    }

    public void setStatusOnline(ArrayList<String> statusOnline) {
        this.statusOnline = statusOnline;
    }

    public boolean isHaveFriend() {
        return haveFriend;
    }

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
        parseUser();
    }

    public String getFriendName(int index)
    {
        return friend_name.get(index);
    }
    public String getFriendAvatars(int index)
    {
        return avatars.get(index);
    }
    public String getFrienStatus(int index)
    {
        return status.get(index);
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

    public void clearArray()
    {
        friend_name.clear();
        status.clear();
        statusOnline.clear();
        avatars.clear();
    }

    public int getFriendNumber()
    {
        return friend_name.size();
    }

    public void parseUser()
    {
        clearArray();
        try {
            for (int i = 4; i < commands.length - 1; i++) {
                temp = null;
                temp = commands[i].split(":");
                friend_name.add(temp[0]);
                status.add(temp[1]);
                statusOnline.add(temp[2]);
                avatars.add(temp[3]);
            }
        }catch (NullPointerException e)
        {
            System.out.println("СТОЛЬКО РЕАЛЬНЫХ ДРУЗЕЙ"+friend_name.size());
            return;
        }
    }
}
