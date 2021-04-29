package org.voicemessanger.client.main;

import java.util.ArrayList;

public class CallingAnswerSaver {
    private String friendLogin;
    private String frinedAnswer;
    public static ArrayList<CallingAnswerSaver> callingAnswerSavers = new ArrayList<CallingAnswerSaver>();
    public CallingAnswerSaver() {
    }

    public CallingAnswerSaver(String friendLogin, String frinedAnswer) {
        this.friendLogin = friendLogin;
        this.frinedAnswer = frinedAnswer;
    }

    public String getFriendLogin() {
        return friendLogin;
    }

    public void setFriendLogin(String friendLogin) {
        this.friendLogin = friendLogin;
    }

    public String getFrinedAnswer() {
        return frinedAnswer;
    }

    public void setFrinedAnswer(String frinedAnswer) {
        this.frinedAnswer = frinedAnswer;
    }
}
