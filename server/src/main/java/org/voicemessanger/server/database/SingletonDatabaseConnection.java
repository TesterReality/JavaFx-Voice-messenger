package org.voicemessanger.server.database;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by user on 10.05.2019.
 */
public class SingletonDatabaseConnection {
   // static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/vacoommessanger";
    static String DB_URL = "";

    static String USER = "";
    static String PASS = "";
    Connection connection = null;
    private static SingletonDatabaseConnection instance;

    public  Connection getConnection() {
        return connection;
    }

    public  void setConnection(Connection connection) {
        this.connection = connection;
    }

    public static SingletonDatabaseConnection getInstance() throws NoSuchAlgorithmException,
            NoSuchPaddingException {
        if(instance == null){

            instance = new SingletonDatabaseConnection();
            DatabaseUser databaseUser = new DatabaseUser();
            USER = databaseUser.getUSER();
            PASS = databaseUser.getPASS();
            DB_URL=databaseUser.getDB_URL();
        }
        return instance;
    }

    public Connection getDBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            return connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return connection;
    }

    public void closeConncetion()
    {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
