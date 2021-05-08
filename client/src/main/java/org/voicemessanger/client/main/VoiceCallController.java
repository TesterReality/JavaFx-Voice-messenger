package org.voicemessanger.client.main;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.voicemessanger.client.clientxmlporocol.VacoomProtocol;

public class VoiceCallController {
    public AnchorPane stageWindow;
    public Label tray;
    public Label full;
    public Label Exit;
    public ImageView smile0;
    public ImageView smile1;
    public ImageView smile2;
    public ImageView smile3;
    public Circle frindIsSend;
    public Circle userImgFriend;
    public Label friendName;
    public Label timeOfCalling;
    public Label headphones;
    public Label stopCall;
    public Label mute;

    private String srtingFriendName;
    private VoiceCallController thisNode;
    private CallingUser callingParent;
    private ReceivingCall receivingParent;
     private KTimer timer;
    public VoiceCallController() {
    }

    public VoiceCallController(String userName) {
        srtingFriendName = userName;
    }

    public VoiceCallController getThisNode() {
        return thisNode;
    }

    public void setThisNode(VoiceCallController thisNode) {
        this.thisNode = thisNode;
    }


    public void setParent(Object parent) {
        if(parent instanceof CallingUser)
        {
            this.callingParent =(CallingUser) parent;
        }
        if(parent instanceof ReceivingCall)
        {
            this.receivingParent = (ReceivingCall)parent;
        }
    }

    public ImageView getSmile0() {
        return smile0;
    }

    public void setSmile0(ImageView smile0) {
        this.smile0 = smile0;
    }

    public ImageView getSmile1() {
        return smile1;
    }

    public void setSmile1(ImageView smile1) {
        this.smile1 = smile1;
    }

    public ImageView getSmile2() {
        return smile2;
    }

    public void setSmile2(ImageView smile2) {
        this.smile2 = smile2;
    }

    public ImageView getSmile3() {
        return smile3;
    }

    public void setSmile3(ImageView smile3) {
        this.smile3 = smile3;
    }

    @FXML
    private void initialize()
    {
        friendName.setText(srtingFriendName);
        Image image=null;
        if(callingParent!=null) {
            image=SwingFXUtils.toFXImage(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getFriendsInfo().getLastClickAvatar(), null);
        }
        if(receivingParent!=null)
        {
            image =SwingFXUtils.toFXImage(ThreadClientInfoSingleton.getInstance().getImageUser().get(srtingFriendName), null);
        }
         userImgFriend.setFill(new ImagePattern(image));
        timer = new KTimer();
        timer.setVoiceCallController(thisNode);
        timer.startTimer(0);


    }
    public void toTray(MouseEvent mouseEvent) {
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.setIconified(true);
    }

    public void fullScreenWindow(MouseEvent mouseEvent) {
    }

    public void closeWindow(MouseEvent mouseEvent) {
        stopCall();
    }


    public void clickHeadphones(MouseEvent mouseEvent) {
    }
    public void setTimeCallee(String time)
    {
        timeOfCalling.setText(time);
    }
    private void stopCall()
    {
        Stage stage = (Stage) Exit.getScene().getWindow();
        VacoomProtocol protocol = new VacoomProtocol();
        if(callingParent!=null)
        {

            System.out.println("Отправил собще о том, что я завершил звонок другу");
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.stopCall("set",callingParent.getUserName(),srtingFriendName));

            callingParent.setCalling(false);
            callingParent.stopCalling();
        }
        if(receivingParent!=null)
        {
            System.out.println("Отправил собще о том, что я завершил звонок инициатору");
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(protocol.stopCall("set",receivingParent.getMyLogin(),srtingFriendName));
            receivingParent.setCalling(false);
            receivingParent.stopCalling();
        }
        timer.stopTimer();
        stage.close();
    }
    public void closeWin()
    {
        Stage stage = (Stage) Exit.getScene().getWindow();
        timer.stopTimer();
        stage.close();
    }
    public void clickStopCall(MouseEvent mouseEvent) {
        stopCall();
    }

    public void clickMute(MouseEvent mouseEvent) {
    }
}
