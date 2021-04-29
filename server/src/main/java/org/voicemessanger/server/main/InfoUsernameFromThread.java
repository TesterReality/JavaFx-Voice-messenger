package org.voicemessanger.server.main;
public class InfoUsernameFromThread {
    String name ="";
    Server thread = null;

    public InfoUsernameFromThread() {
    }

    public InfoUsernameFromThread(String name, Server thread) {
        this.name = name;
        this.thread = thread;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Server getThread() {
        return thread;
    }

    public void setThread(Server thread) {
        this.thread = thread;
    }
}
