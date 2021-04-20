package sample.localDatabase;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class LocalDbHandler {

    // Константа, в которой хранится адрес подключения
    private static final String CON_STR = "jdbc:sqlite:src/resource/localDatabase/client.db";

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
                userVoiceKeys.add(new UserVoiceKey(resultSet.getInt("id"),
                        resultSet.getString("friend_name"),
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

}