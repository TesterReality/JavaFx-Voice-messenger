package org.voicemessanger.server.mail;

public class MailUser {

    private String mailServer = "vdiploma@mail.ru";
    private String mailPswd="ckj;yjghblevfnmgfhjkm1";//ckj;yjghblevfnmgfhjkm1

    public MailUser() {
    }

    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    public String getMailPswd() {
        return mailPswd;
    }

    public void setMailPswd(String mailPswd) {
        this.mailPswd = mailPswd;
    }
}
