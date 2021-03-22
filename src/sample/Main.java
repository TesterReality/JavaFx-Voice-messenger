package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    Stage testStage;
    static ClientMsgThread clientMsgThread;	//Объект побочного потока

    @Override
    public void start(Stage primaryStage) throws Exception{

        clientMsgThread = new ClientMsgThread();
        clientMsgThread.setDaemon(true);
        clientMsgThread.start();


        /*Scene mainScene;
        Parent root = FXMLLoader.load(getClass().getResource("fxml/sample.fxml"));
        primaryStage.setTitle("Hello World");
        mainScene = new Scene(root,800,600);*/

        /*//////
        SmileCreater smile = new SmileCreater("src/resource/conf/smile.txt");
        smile.getAllSpritePathFromConf();
        smile.loadAllSmile();*/
/*
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();

        testStage = primaryStage;*/
        System.out.println();
    }

    public Stage getTestStage() {
        return testStage;
    }

    public void setTestStage(Stage testStage) {
        this.testStage = testStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
