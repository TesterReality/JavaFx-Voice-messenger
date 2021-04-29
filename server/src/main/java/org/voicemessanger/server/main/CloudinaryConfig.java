package org.voicemessanger.server.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CloudinaryConfig {

    private String cloud_name = "";
    private String api_key="";
    private String api_secret="";

    public CloudinaryConfig() {
        loadConfig();
    }
    public void loadConfig()
    {
        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream("/conf/config.properties");
            property.load(fis);

            cloud_name = property.getProperty("cloud.name");
            api_key = property.getProperty("api.key");
            api_secret = property.getProperty("api.secret");
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
    }
    public String getCloud_name() {
        return cloud_name;
    }

    public void setCloud_name(String cloud_name) {
        this.cloud_name = cloud_name;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getApi_secret() {
        return api_secret;
    }

    public void setApi_secret(String api_secret) {
        this.api_secret = api_secret;
    }
}
