package org.voicemessanger.client.main;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;


public class KTimer {

    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:S");
    private String[] split;
    private SimpleStringProperty sspTime;
    private long time;
    private Timer t = new Timer("Metronome", true);
    private TimerTask tt;
    boolean timing = false;
    private VoiceCallController voiceCallController;

    public VoiceCallController getVoiceCallController() {
        return voiceCallController;
    }

    public void setVoiceCallController(VoiceCallController voiceCallController) {
        this.voiceCallController = voiceCallController;
    }

    public KTimer() {
        sspTime = new SimpleStringProperty("00:00:00");
    }

    public void startTimer(final long time) {
        this.time = time;
        timing = true;
        tt = new TimerTask() {

            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!timing) {
                            try {
                                tt.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            updateTime();
                        }
                    }
                });

            }
        };
        t.scheduleAtFixedRate(tt, 10, 10);
    }

    public synchronized void stopTimer() {
        timing = false;
    }

    public synchronized void updateTime() {
        this.time = this.time + 10;
        split = sdf.format(new Date(this.time)).split(":");
       // sspTime.set(split[0] + ":" + split[1] + ":" + (split[2].length() == 1 ? "0" + split[2] : split[2].substring(0, 2)));
        sspTime.set(split[0] + ":" + split[1]);

        voiceCallController.setTimeCallee(sspTime.get());
    }

    public synchronized void moveToTime(long time) {
        stopTimer();
        this.time = time;
        split = sdf.format(new Date(time)).split(":");
        sspTime.set(split[0] + ":" + split[1] + ":" + (split[2].length() == 1 ? "0" + split[2] : split[2].substring(0, 2)));
    }

    public synchronized long getTime() {
        return time;
    }

    public synchronized SimpleStringProperty getSspTime() {
        return sspTime;
    }
}
