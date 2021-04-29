package org.voicemessanger.client.main;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.PopupWindow;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SmileElemController {

    String categoryInfo[] = new String[3];
    @FXML
    private ImageView oneSmile;

    @FXML
    private ImageView twoSmile;

    @FXML
    private ImageView threeSmile;

    @FXML
    private ImageView fourSmile;

    @FXML
    private ImageView fiveSmile;

    @FXML
    private ImageView sixSmile;

    @FXML
    private ImageView sevenSmile;

    @FXML
    private ImageView eightSmile;

    @FXML
    private ImageView neinSmile;

    @FXML
    private ImageView temSmile;

    @FXML
    private ImageView elfSmile;
    @FXML
    private HBox hBoxSmile;

    private ArrayList<BufferedImage> allSmile = new ArrayList<BufferedImage>();
    private ArrayList<String> allSmileInfo = new ArrayList<>();
    private ArrayList<ImageView> allImageView = new ArrayList<>();
    private FlowPane flowWhereText;
    private FlowFieldPositionHelper flowPosition;

    public SmileElemController(FlowPane flowWhereText, FlowFieldPositionHelper flowPosition) {
        this.flowWhereText = flowWhereText;
        this.flowPosition = flowPosition;
    }


    @FXML
    private void initialize()
    {
        setAllSmile();
    }

    public void setSmile(BufferedImage smilik)
    {
        allSmile.add(smilik);
    }
    public void setSmileInfo(String info){allSmileInfo.add(info);}
    public void setAllSmile()
    {
        setListImageView();

        try {
            for (int i = 0; i < allImageView.size(); i++) {

                    Image image = SwingFXUtils.toFXImage(allSmile.get(i), null);
                    //ImageView test = allImageView.get(i);
                    allImageView.get(i).setImage(image);
                    categoryInfo = allSmileInfo.get(i).split(":");
                    Tooltip.install(allImageView.get(i), makeBubble(new Tooltip(":" + categoryInfo[0] + ":")));

            }
        }catch (NullPointerException n)
        {
            System.out.println("Смайлы не вивелись?");
        }
        catch (IndexOutOfBoundsException inde)
        {
            /*Все элементы, которые не заполнены смайлами, но присутствуют, делаем невидимыми*/
          int index=  allSmile.size();
          for(int i=index;i<allImageView.size();i++)
            hBoxSmile.getChildren().get(i).setVisible(false);

        }
    }

    private void setListImageView()
    {
        allImageView.add(oneSmile);
        allImageView.add(twoSmile);
        allImageView.add(threeSmile);
        allImageView.add(fourSmile);
        allImageView.add(fiveSmile);
        allImageView.add(sixSmile);
        allImageView.add(sevenSmile);
    }

    private Tooltip makeBubble(Tooltip tooltip) {
        tooltip.setStyle("-fx-font-size: 12px;");
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

        return tooltip;
    }

    private void addSmileToTextField(TextField test,int numElem)
    {
        StringBuilder sb = new StringBuilder(test.getText());
        categoryInfo = allSmileInfo.get(numElem).split(":");

            sb.insert(flowPosition.getCaretPosition(), ":" + categoryInfo[0] + ":");
            test.setText(sb.toString());


    }
    public void fMouseClick(MouseEvent mouseEvent) {

        TextField test = (TextField) flowWhereText.getChildren().get(flowPosition.getPosition());
        addSmileToTextField(test,0);
    }

    public void twoMouseClick(MouseEvent mouseEvent) {
        TextField test = (TextField) flowWhereText.getChildren().get(flowPosition.getPosition());
        addSmileToTextField(test,1);
    }

    public void threeMouseClick(MouseEvent mouseEvent) {
        TextField test = (TextField) flowWhereText.getChildren().get(flowPosition.getPosition());
        addSmileToTextField(test,2);
    }

    public void fourMouseClick(MouseEvent mouseEvent) {
        TextField test = (TextField) flowWhereText.getChildren().get(flowPosition.getPosition());
        addSmileToTextField(test,3);
    }

    public void fiveMouseClick(MouseEvent mouseEvent) {
        TextField test = (TextField) flowWhereText.getChildren().get(flowPosition.getPosition());
        addSmileToTextField(test,4);
    }

    public void sixMouseClick(MouseEvent mouseEvent) {
        TextField test = (TextField) flowWhereText.getChildren().get(flowPosition.getPosition());
        addSmileToTextField(test,5);
    }

    public void sevenMouseClick(MouseEvent mouseEvent) {
        TextField test = (TextField) flowWhereText.getChildren().get(flowPosition.getPosition());
        addSmileToTextField(test,6);
    }
}
