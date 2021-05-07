package org.voicemessanger.server.database;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;


public class DatabaseConnection {
   // static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/vacoommessanger";
    static String DB_URL = "";

    static String USER = "";
    static String PASS = "";
    Connection connection = null;


    public  Connection getConnection() {
        return connection;
    }

    public DatabaseConnection() {
        DatabaseUser databaseUser = new DatabaseUser();
        USER = databaseUser.getUSER();
        PASS = databaseUser.getPASS();
        DB_URL=databaseUser.getDB_URL();

    }

    public  void setConnection(Connection connection) {
        this.connection = connection;
    }


    public Connection getDBConnection() {
        if(connection !=null)
        {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            //connection.setNetworkTimeout();
            connection.setNetworkTimeout(Executors.newFixedThreadPool(10), 60*1000);

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
