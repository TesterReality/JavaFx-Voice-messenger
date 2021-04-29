package org.voicemessanger.client.localdatabase;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class LocalDbHandler {

    // Константа, в которой хранится адрес подключения
    private static final String CON_STR = "jdbc:sqlite:/localdatabase/client.db";

    // Используем шаблон одиночка, чтобы не плодить множество
    // экземпляров класса DbHandler
    private static LocalDbHandler instance = null;

    public static synchronized LocalDbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new LocalDbHandler();
        return instance;
    }

    // Объект, в котором будет храниться соединение с БД
    private Connection connection;

    private LocalDbHandler() throws SQLException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
        DriverManager.registerDriver(new JDBC());
        // Выполняем подключение к базе данных
        this.connection = DriverManager.getConnection(CON_STR);
    }

    public List<UserVoiceKey> getAllProducts() {

        // Statement используется для того, чтобы выполнить sql-запрос
        try (Statement statement = this.connection.createStatement()) {
            // В данный список будем загружать наши продукты, полученные из БД
            List<UserVoiceKey> userVoiceKeys = new ArrayList<UserVoiceKey>();
            // В resultSet будет храниться результат нашего запроса,
            // который выполняется командой statement.executeQuery()
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_voice_key");
            // Проходимся по нашему resultSet и заносим данные в products
            while (resultSet.next()) {
                userVoiceKeys.add(new UserVoiceKey(resultSet.getInt("id_voice"),
                        resultSet.getInt("id_friend"),
                        resultSet.getString("key_my"),
                        resultSet.getString("key_friend"),
                        resultSet.getString("secret_key1"),
                        resultSet.getString("secret_key2")));
            }
            // Возвращаем наш список
            return userVoiceKeys;

        } catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return Collections.emptyList();
        }
    }

    public void addNewSecretKey(String newSecretKey,String username)
    {

        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("UPDATE user_voice_key SET secret_key1 = secret_key2, secret_key2=? WHERE id_friend = ((SELECT id_user_friend FROM user_friend WHERE user_name = ?))");
            cstmt.setString(1, newSecretKey);
            cstmt.setString(2, username);

            int rows = cstmt.executeUpdate();
            System.out.printf("%d rows added", rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addVoiceKey(String key_my, String key_friend, String secret_key1, String secret_key2,String username)
    {

        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("UPDATE user_voice_key SET key_my = ?,key_friend = ? WHERE id_friend = ((SELECT id_user_friend FROM user_friend WHERE user_name = ?))");
            cstmt.setString(1, key_my);
            cstmt.setString(2, key_friend);
            cstmt.setString(3, username);

            int rows = cstmt.executeUpdate();
            System.out.printf("%d rows added", rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addVoice(String username)
    {

        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("INSERT INTO user_voice_key (id_friend) VALUES( (SELECT id_user_friend FROM user_friend WHERE user_name = ?))");
            cstmt.setString(1, username);

            int rows = cstmt.executeUpdate();
            System.out.printf("%d rows added", rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFriendContact(String username, int contactNew)
    {
        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("UPDATE user_friend SET user_contact=? WHERE user_name = ?");
            cstmt.setInt(1, contactNew);
            cstmt.setString(2, username);

            int rows = cstmt.executeUpdate();
            System.out.printf("%d rows updates", rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getSecretKeyOne(String username)
    {
        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("SELECT secret_key1 FROM user_voice_key WHERE id_friend = (SELECT id_user_friend FROM user_friend WHERE user_name = ? )");
            cstmt.setString(1, username);

            ResultSet resultSet = cstmt.executeQuery();
            while (resultSet.next()) {
                String secretKey1 = resultSet.getString("secret_key1");
                return secretKey1;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NaN";
    }
    public int getFriendContact(String username)
    {
        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("SELECT user_contact FROM user_friend WHERE user_name = ?");
            cstmt.setString(1, username);

            ResultSet resultSet = cstmt.executeQuery();
            while (resultSet.next()) {
              int contact_int = resultSet.getInt("user_contact");
              return contact_int;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // такого друга в локальной бд нет
    }
    public boolean checkFriend(String username)
    {
        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("SELECT id_user_friend FROM user_friend WHERE user_name = ?");
            cstmt.setString(1, username);

            ResultSet resultSet = cstmt.executeQuery();
            if (!resultSet.isBeforeFirst() ) {
                return false; // такого друга в локальной бд нет
            }else
            {
                return true; //такой друг есть
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // такого друга в локальной бд нет
    }
    public void addFriend(String username,int contact)
    {

        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("INSERT INTO user_friend (user_name, user_contact) VALUES (?,?)");
            cstmt.setString(1, username);
            cstmt.setInt(2, contact);

            int rows = cstmt.executeUpdate();
            System.out.printf("%d rows added", rows);
            addVoice(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addVoiceUser(String username)
    {
        PreparedStatement cstmt = null;

        try {
            cstmt= connection.prepareStatement("SELECT id_voice FROM user_voice_key WHERE id_friend = (SELECT id_user_friend FROM user_friend WHERE user_name = ? )");
            cstmt.setString(1, username);

            ResultSet resultSet = cstmt.executeQuery();
            if (!resultSet.isBeforeFirst() ) {
                System.out.println("No data");
                addFriend(username,1);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}