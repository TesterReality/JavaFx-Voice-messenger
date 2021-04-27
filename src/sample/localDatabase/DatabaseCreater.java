package sample.localDatabase;
import sample.CallingUser;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseCreater {
    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:src/resource/localDatabase/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        //createNewDatabase("client.db");
        try {
            // Создаем экземпляр по работе с БД
            LocalDbHandler dbHandler = LocalDbHandler.getInstance();
            // Добавляем запись
            //dbHandler.addProduct(new Product("Музей", 200, "Развлечения"));
            // Получаем все записи и выводим их на консоль
            List<UserVoiceKey> products = dbHandler.getAllProducts();
            for (UserVoiceKey product : products) {
                System.out.println(product.toString());
            }
           // dbHandler.addVoiceUser("memes");
            //dbHandler.addVoiceKey("hui","hui","hui","hui","test");
            //dbHandler.addNewSecretKey("eobana","test");
            dbHandler.getFriendContact("test");
            dbHandler.getFriendContact("hui");
            dbHandler.updateFriendContact("test",777);
          String sck=  dbHandler.getSecretKeyOne("kekich");
            System.out.println(sck);
            // Удаление записи с id = 8
            //dbHandler.deleteProduct(8);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //CallingUser callingUser = new CallingUser();
        //callingUser.start();

    }
}
