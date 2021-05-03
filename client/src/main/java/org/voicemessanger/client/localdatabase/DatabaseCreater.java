package org.voicemessanger.client.localdatabase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.voicemessanger.client.main.ResizeHelper;
import org.voicemessanger.client.main.SHA256Class;
import org.voicemessanger.client.main.VoiceCallController;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.*;

public class DatabaseCreater {
    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:" + fileName;

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
        /*
        try {
            LocalDbHandler.getInstance().checkFriend("kek");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        createNewDatabase("./clientOS.db");

            /*
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

            //DatabaseLogic db = new DatabaseLogic();
             //boolean test =   db.checkMailIsUnregister("dxyu@i.ua");

            String filePath = new File("").getAbsolutePath();
            String filePath1 = new File("").getCanonicalPath();
            String filePath2 = new File("").getPath();

            //String filePath3= Main.class.getResource("..//conf/smile.txt").toString();

          // SmileCreater smile = new SmileCreater("/conf/smile.txt");
            //SmileCreater smile = new SmileCreater(Main.class.getResource("..//conf/smile.txt").toString());

            // smile.getAllSpritePathFromConf();
            // smile.loadAllSmile();
           // smile.getAllSpritePathFromConf();
           // smile.loadAllEmoji();
*/
          //  System.out.println(filePath);

            // Удаление записи с id = 8
            //dbHandler.deleteProduct(8);

        //CallingUser callingUser = new CallingUser();
        //callingUser.start();

        /*
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                openCallWindow();
            }
        });*/
        //openCallWindow();

       // createCryptoSmile();
    }
    private static void createCryptoSmile()
    {
        String DH1SHA =new SHA256Class().getSHA256("test");
        for (int i=0; i<DH1SHA.length();i+=16)
        {

            String smileString = DH1SHA.substring(i, i+16);
            byte[] smileByte = smileString.getBytes();
            long stringToLong =  bytesToLong(smileByte);;
            stringToLong = stringToLong%(58*58);
            int x = (int) (stringToLong%58);
            int y = (int) (stringToLong/58);
            System.out.println("Число "+stringToLong);

        }
    }
    public static long bytesToLong(final byte[] b) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
    private static void openCallWindow()
    {
        FXMLLoader loader = new FXMLLoader();
        VoiceCallController voiceCallController =
                new VoiceCallController("test");
        voiceCallController.setThisNode(voiceCallController);

        loader = new FXMLLoader(
                DatabaseCreater.class.getResource(
                        "../../../../../resources/fxml/voiceCall.fxml"
                )
        );
        loader.setController(voiceCallController);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);

        //stage.setResizable(false);
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.setWidth(471);
        stage.setHeight(455);
        ResizeHelper.addResizeListener(stage);
        stage.setTitle("Hello World");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }
}
