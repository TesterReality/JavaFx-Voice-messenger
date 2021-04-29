package org.voicemessanger.server.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseUser {
   // private  String USER = "postgres";
   // private  String PASS = "";

    private  String USER = "";
    private  String PASS = "";
    private  String DB_URL ="";

    public DatabaseUser() {
        loadConfig();
    }

    public void loadConfig()
    {
        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream("/conf/config.properties");
            property.load(fis);

            USER = property.getProperty("db.login");
            PASS = property.getProperty("db.password");
            DB_URL = property.getProperty("db.url");
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
    }

    public String getDB_URL() {
        return DB_URL;
    }

    public void setDB_URL(String DB_URL) {
        this.DB_URL = DB_URL;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }
}
