package org.voicemessanger.server.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseUser {

    private String USER;
    private String PASS;
    private String DB_URL;

    public DatabaseUser() {
        loadConfig();
    }

    private void loadConfig() {
        Properties property = new Properties();

        try {
            InputStream resourceStream = getClass().getResourceAsStream("/conf/config.properties");
            property.load(resourceStream);

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

    public String getUSER() {
        return USER;
    }

    public String getPASS() {
        return PASS;
    }

}
