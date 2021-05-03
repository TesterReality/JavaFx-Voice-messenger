package org.voicemessanger.server.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailUser {

    private String mailServer;
    private String mailPswd;

    public MailUser() {
        loadConfig();
    }

    private void loadConfig() {
        Properties property = new Properties();

        try {
            InputStream resourceStream = getClass().getResourceAsStream("/conf/config.properties");
            property.load(resourceStream);

            mailServer = property.getProperty("mail.server");
            mailPswd = property.getProperty("mail.pswd");
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
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
