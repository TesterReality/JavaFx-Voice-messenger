package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public class StartWindowController {

    public Label Exit;
    public AnchorPane test_change;
    public Button testClick;
    public TextField input;
    public ImageView LogoImage;
    public AnchorPane firstWindow;
    public Label full;
    public Text serverStatus;
    Stage stage;
    AnchorPane p;
    Node node;
    StartWindowController controller;
    Thread myThread;
    public StartWindowController() {
    }


    public  void setController(StartWindowController ctr)
    {
        controller = ctr;
    }

    public StartWindowController getController()
    {
        return controller;
    }
    @FXML
    private void initialize()
    {
        LogoImage.setImage(new Image("resource/img/t1.png"));
        loadLogin();

        Runnable r = ()->{
            try {

                if (!Thread.currentThread().isInterrupted()) {

                    do {
                        do {
                            if (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().isServerIsOnline()) {
                                serverStatus.setText("Недоступен");
                                serverStatus.setStyle("-fx-fill: #b22222");
                            }

                                Thread.sleep(1000);

                        } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().isServerIsOnline());
                        // System.out.println(ThreadClientInfoSingleton.getInstance().getClientMsgThread().isServerIsOnline());
                        serverStatus.setText("Доступен");
                        serverStatus.setStyle("-fx-fill: #00a896");
                    } while (true);
                }

            }catch (InterruptedException ie)
            {
                ie.printStackTrace();
                System.out.println("Поток окна состояния серва закрыт");
                Thread.currentThread().interrupt();
            }
        };
         myThread = new Thread(r,"MyThread");
        myThread.start();
    }


    public void toTary(MouseEvent mouseEvent) {
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.setIconified(true);

    }

    public void loadLogin()
    {
        FXMLLoader loader = new FXMLLoader();
        LoginController loginController = new LoginController();
        loginController.setParent(controller);
        loginController.setNode(loginController);
        loader = new FXMLLoader(getClass().getResource("fxml/login.fxml"));
        AnchorPane gg = null;
        loader.setController(loginController);
        try {
            gg = (AnchorPane) loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (!test_change.getChildren().isEmpty())
                test_change.getChildren().clear();
        }catch (NullPointerException e)
        {
            Stage stage = (Stage) input.getScene().getWindow();
        }

        test_change.getChildren().add(gg);
    }

    public  void test1()
    {

        if (!test_change.getChildren().isEmpty())
            test_change.getChildren().clear();

    }

    public void fullscreen(MouseEvent mouseEvent) {
        stage = (Stage) Exit.getScene().getWindow();
        if(!stage.isMaximized())
            stage.setMaximized(true);
        else
        {
            stage.setMaximized(false);
            stage.setWidth(600);
            stage.setHeight(400);
        }
    }

    public void Close(MouseEvent mouseEvent) {
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().interrupt();
        myThread.interrupt();
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.close();

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println(threadSet.size());
        //return;
    }

    public void test(MouseEvent mouseEvent) {
    }

    public void loadWorkrArea(String user_name) throws IOException {
      /*  Stage stage = (Stage) Exit.getScene().getWindow();
        firstWindow.getChildren().clear();
        stage.close();

        FXMLLoader loader = new FXMLLoader();
        WorkAreaController workinArea =
                new WorkAreaController();
        workinArea.setParent(controller);
        workinArea.setThisNode(workinArea);
        loader = new FXMLLoader(
                getClass().getResource(
                        "fxml/workArea.fxml"
                )
        );
        workinArea.setUser_name(user_name);
        loader.setController(workinArea);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setWidth(800);
        stage.setHeight(500);
        ResizeHelper.addResizeListener(stage);
        stage.show();*/

    }


}
