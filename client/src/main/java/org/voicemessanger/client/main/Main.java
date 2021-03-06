package org.voicemessanger.client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    Stage testStage;
    static ClientMsgThread clientMsgThread;	//Объект побочного потока
    @Override
    public void start(Stage primaryStage) throws Exception{

        clientMsgThread = new ClientMsgThread();
        clientMsgThread.setDaemon(true);
        ThreadClientInfoSingleton.getInstance().setClientMsgThread(clientMsgThread);

        clientMsgThread.start();

        /*
        System.out.println( getClass().getClassLoader().getResource(
                "/fxml/startWindow.fxml"
        ));*/
        FXMLLoader loader = new FXMLLoader();
        StartWindowController startWindowController =
                new StartWindowController();
        startWindowController.setController(startWindowController);
        /*
        loader = new FXMLLoader(
                getClass().getClassLoader().getResource(
                        "/fxml/startWindow.fxml"
                )
        );*/
        System.out.println(getClass().getResource("/fxml/startWindow.fxml"));
        loader = new FXMLLoader(getClass().getResource("/fxml/startWindow.fxml"));

        loader.setController(startWindowController);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image("/img/TrayLogo.png"));
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(250);
        ResizeHelper.addResizeListener(primaryStage);
       // ThreadClientInfoSingleton.getInstance().setClientMsgThread(clientMsgThread);
        primaryStage.show();

        /*Scene mainScene;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sample.fxml"));
        primaryStage.setTitle("Hello World");
        mainScene = new Scene(root,800,600);*/

        /*//////
        SmileCreater smile = new SmileCreater("/conf/smile.txt");
        smile.getAllSpritePathFromConf();
        smile.loadAllSmile();*/
/*
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();

        testStage = primaryStage;*/
        System.out.println("Конец");
       // openCallWindow();
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


    private static void openCallWindow()
    {
        FXMLLoader loader = new FXMLLoader();
        LoadingController loadingController =
                new LoadingController();

        loader = new FXMLLoader(
                Main.class.getResource(
                        "/fxml/loading.fxml"
                )
        );
        loader.setController(loadingController);

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
        stage.setMinWidth(300);
        stage.setMinHeight(400);
        stage.setWidth(351);
        stage.setHeight(455);
        ResizeHelper.addResizeListener(stage);
        stage.setTitle("Hello World");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

}
