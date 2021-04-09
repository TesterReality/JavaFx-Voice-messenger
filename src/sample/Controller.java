package sample;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import javafx.scene.image.Image;
import sample.ClientXmlPorocol.VacoomProtocol;
import sample.qr.ImageChooser;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;

public class Controller extends VacoomProtocol {
    public TextArea testTextArea;
    public AnchorPane containerTextArea;
    public AnchorPane smileDialog;
    public FlowPane flow;
    public AnchorPane stageWindow;
    public Circle userImg;
    public Label isNewNews;
    public Label userName;
    public Label Exit;
    public Circle newAvatars;
    public VBox users_list;

    private String userNameString;
    private Map configImg;
    private CloudinaryConfig cloudinaryConfig;
    Stage stage;
    int index =0;
    Map<String, BufferedImage> hashMap = new HashMap<String, BufferedImage>();
    FlowFieldPositionHelper flowPosition;
    private String urlPathToImg = "https://res.cloudinary.com/diplomaimgdpi/image/upload/";
    ImageChooser chooser;
    BufferedImage newImage = null;
    Controller thisNode;

    public Controller getThisNode() {
        return thisNode;
    }

    public void setThisNode(Controller thisNode) {
        this.thisNode = thisNode;
    }

    public Controller(String username) {
        this.userNameString = username;
    }
    public String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }


    public BufferedImage scale(BufferedImage img, int targetWidth, int targetHeight) {

        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        int w = img.getWidth();
        int h = img.getHeight();

        int prevW = w;
        int prevH = h;

        do {
            if (w > targetWidth) {
                w /= 2;
                w = (w < targetWidth) ? targetWidth : w;
            }

            if (h > targetHeight) {
                h /= 2;
                h = (h < targetHeight) ? targetHeight : h;
            }

            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);

            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);

        if (g2 != null) {
            g2.dispose();
        }

        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return ret;

    }

    public String changeAvatar(BufferedImage buffImg)
    {
        Cloudinary cloudinary = new Cloudinary(configImg);
        Map uploadInfo =null;
        try {
            BufferedImage in = buffImg;
            in = scale(in, 50, 50);
            File outputfile = new File("src/resource/img/temp.png");
            ImageIO.write(in, "png", outputfile);
            uploadInfo = cloudinary.uploader().upload(outputfile.getPath(), ObjectUtils.emptyMap());
            outputfile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (String) uploadInfo.get("public_id");
    }

    private BufferedImage getImgFromUrl(String urlStr)
    {
        URLConnection uc;
        String tmpUrl = urlPathToImg +urlStr+".png";
        BufferedImage imageAvatars =null;
        try {
            URL url = new URL(tmpUrl);
            uc = url.openConnection();
            uc.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            uc.connect();

            InputStream urlStream = uc.getInputStream();
            imageAvatars = ImageIO.read(urlStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageAvatars;
    }
    @FXML
    private void initialize() throws IOException {


        userName.setText(userNameString);
/*
         URL url = new URL("https://api.multiavatar.com/"+userNameString+".png");
        uc = url.openConnection();
        uc.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        uc.connect();

        InputStream urlStream = uc.getInputStream();
       BufferedImage image1 = ImageIO.read(urlStream);
       image1 = scale(image1,50,50);*/
       // System.out.println(encodeToString(image1,"png"));

        configImg = new HashMap();

        //https://res.cloudinary.com/diplomaimgdpi/image/upload/k2tx9q47isouxxfnvdbr.png







        // Map testUrl =cloudinary.uploader().upload("src/resource/img/lady1.png", ObjectUtils.emptyMap());
        //System.out.println(testUrl.get("url"));
       // System.out.println(testUrl.get("public_id"));


        //image1 = scale(image1,50,50);

        //cloudinary.uploader().upload("src/resource/img/lady1.png",ObjectUtils.asMap("name", "sample_id"));






        //URL url = new URL("http://www.avajava.com/images/avajavalogo.jpg");
      //  InputStream input = uc.getInputStream();

      //  BufferedImage img = ImageIO.read(uc.getInputStream());
        do {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("authorization"));
        ErrorMsg t = new ErrorMsg();
        if( t.checkLogin()!=0 )
        {
            System.out.println("error");
        }
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("authorization");


        Image image = SwingFXUtils.toFXImage(getImgFromUrl(ThreadClientInfoSingleton.getInstance().getClientMsgThread().getAvatarsId()), null);
        userImg.setFill(new ImagePattern(image));


        flowPosition = new FlowFieldPositionHelper();
        FXMLLoader loader = new FXMLLoader();
        SmileController smileController = new SmileController();
        loader = new FXMLLoader(
                getClass().getResource(
                        "fxml/smile.fxml"
                )
        );
        loader.setController(smileController);

        AnchorPane codeAnchor = (AnchorPane) loader.load();
        smileDialog.getChildren().add(codeAnchor);
        smileDialog.setVisible(false);


        SmileCreater smile = new SmileCreater("src/resource/conf/smile.txt");
        // smile.getAllSpritePathFromConf();
        // smile.loadAllSmile();
        smile.getAllSpritePathFromConf();
        smile.loadAllEmoji();
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader("src/resource/conf/emojiNew.json"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jo = (JSONObject) obj;

        String firstName = (String) jo.get("image");
        String lastName = (String) jo.get("short_name");
        System.out.println("path: " + firstName + " short_name " + lastName);
// Достаем массив номеров
        JSONArray phoneNumbersArr = (JSONArray) jo.get("test");
        Iterator phonesItr = phoneNumbersArr.iterator();
        System.out.println("phoneNumbers:");
// Выводим в цикле данные массива
        String emojiInfo = "";
        while (phonesItr.hasNext()) {
            JSONObject test = (JSONObject) phonesItr.next();
            System.out.println("- img: " + test.get("image") + " " + test.get("short_name") + "\n");
            //smile.addEmojiToList(test.get("image").toString());
            emojiInfo = test.get("short_name").toString() + ":" + test.get("unified").toString() + ":" + test.get("category").toString();
            smile.setEmojiInfo(Integer.parseInt(test.get("sheet_x").toString()), Integer.parseInt(test.get("sheet_y").toString()), emojiInfo);
            JSONObject phoneNumbersArr1 = (JSONObject) test.get("skin_variations");
            if (phoneNumbersArr1 != null) {

                System.out.println("Есть вложения!");
                for (int i = 0; i < phoneNumbersArr1.size(); i++) {
                    String insideEmoji1 = (String) phoneNumbersArr1.toJSONString();
                    Object pp = null;
                    try {
                        pp = new JSONParser().parse(insideEmoji1.trim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    JSONObject jof = (JSONObject) pp;

                    jof.keySet().forEach(keyStr ->
                    {
                        Object keyvalue = jof.get(keyStr);
                        JSONObject test5 = (JSONObject) keyvalue;
                        System.out.println("key: " + keyStr + " value: " + test5.get("image"));
                        //   smile.addEmojiToList(test5.get("image").toString());
                    });

                }
            }
        }

        String category = "";
        String tempCategory = "";
        String[] categoryInfo = new String[3];
        ArrayList<String> allCategory = new ArrayList<String>();
        boolean find = false;


        //выясняем какие категории у нас есть
        for (int i = 0; i < 57; i++) {
            for (int j = 0; j < 57; j++) {
                category = smile.getInfo(i, j);
                System.out.println("index: " + i + " " + j + ":");
                System.out.println(category);
                if (category == null) continue;

                categoryInfo = category.split(":");

                find = false;
                for (int n = 0; n < allCategory.size(); n++) {
                    if (allCategory.get(n).equals(categoryInfo[2])) {
                        find = true;
                    }
                }
                if (!find) {
                    allCategory.add(categoryInfo[2]);
                }

            }
        }


        loader = new FXMLLoader(
                getClass().getResource(
                        "fxml/smileElem.fxml"
                )
        );
        int temp = 0;
        SmileElemController smileElemController = null;
        FXMLLoader loader1 = null;

        for (int k = allCategory.size() - 1; k >= 0; k--) {
            Label label = new Label(allCategory.get(k));
            label.setStyle("-fx-background-color: transparent;\n" +
                    "    -fx-background-radius: 5px;\n" +
                    "    -fx-text-fill: white;\n" +
                    "    -fx-background-position: center;\n" +
                    "    -fx-padding: 15px 5px 5px 13 px;\n" +
                    "    -fx-font-family: Roboto;");
            if(temp!=0)
            {
               // smileElemController.setAllSmile();
                loader1.setController(smileElemController);

                HBox vbox = (HBox) loader1.load();
                smileController.smileArea.getChildren().add(vbox);
            }
            smileController.smileArea.getChildren().add(label);
            temp = 0;
            for (int i = 0; i < 57; i++) {
                for (int j = 0; j < 57; j++) {

                    category = smile.getInfo(i, j);
                    if (category == null) continue;
                    categoryInfo = category.split(":");

                    if (categoryInfo[2].equals(allCategory.get(k))) {

                        if (temp % 7 == 0) {
                            if (temp != 0) {
                               // smileElemController.setAllSmile();
                                loader1.setController(smileElemController);

                                HBox vbox = (HBox) loader1.load();
                                smileController.smileArea.getChildren().add(vbox);
                            }
                            loader1 = new FXMLLoader();
                            loader1 = new FXMLLoader(
                                    getClass().getResource(
                                            "fxml/smileElem.fxml"
                                    )
                            );
                            smileElemController = new SmileElemController(flow,flowPosition);

                            temp = 0;
                        }
                        smileElemController.setSmile(smile.getEmojiFromIndex(i, j));
                        smileElemController.setSmileInfo(smile.getInfo(i, j));

                        String caregorys[] = new String[3];
                        caregorys =  smile.getInfo(i, j).split(":");
                        hashMap.put(":"+caregorys[0]+":", smile.getEmojiFromIndex(i, j));

                        temp++;
                    }
                }
            }
        }
        /*
        for (int i=0;i< smile.getAllSmile().size(); i+=11) {
            FXMLLoader loader1 = new FXMLLoader();
            loader1 = new FXMLLoader(
                    getClass().getResource(
                            "smileElem.fxml"
                    )
            );
            SmileElemController smileElemController = new SmileElemController();


            for(int j=i; j<i+11;j++) {
                try {
                    smileElemController.setSmile(smile.getSmileFromIndex(j));
                }catch (IndexOutOfBoundsException e)
                {

                }
            }

            //smileElemController.setAllSmile();


            loader1.setController(smileElemController);

            HBox vbox = (HBox) loader1.load();
            smileController.smileArea.getChildren().add(vbox);
        }
*/



/*
        testTextArea.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                double howStr = testTextArea.getText().length();
                double dd = howStr/40.0;
                int value = (int)Math.round(dd);

                if(testTextArea.getPrefHeight() != testTextArea.getMinHeight()*value)
                {
                    containerTextArea.setPrefHeight(testTextArea.getMinHeight() + 10*value);
                    testTextArea.setPrefHeight(testTextArea.getMinHeight() + 10* value);
                    System.out.println("Должно работать");
                }

                if(testTextArea.getText().length()%40.0==0) {
                    containerTextArea.setPrefHeight(testTextArea.getPrefHeight() + 10);
                    testTextArea.setPrefHeight(testTextArea.getPrefHeight() + 10);
                    System.out.println("Должно работать");
                }
            }
        });*/
        addNewPole(index++,"",true,-1);

    }


    public void writeTextMsg1(KeyEvent keyEvent) {
        double howStr = testTextArea.getText().length();
        double dd = howStr / 40.0;
        int value = (int) Math.round(dd);

        if (testTextArea.getPrefHeight() != testTextArea.getMinHeight() + (value + 1) * 10 &&
                testTextArea.getMinHeight() + (value) * 10 <= testTextArea.getMaxHeight()) {
            containerTextArea.setPrefHeight(testTextArea.getMinHeight() + 10 * value);
            testTextArea.setPrefHeight(testTextArea.getMinHeight() + 10 * value);
            System.out.println("Должно работать");
            System.out.println("Сейчас стало:" + testTextArea.getPrefHeight());
            System.out.println("Максимум:" + testTextArea.getMaxHeight());


            VBox.setMargin(smileDialog, new Insets(-60 + (-1 * (10 * value)), 0, 5, 163));
        }

    }

    private void textFieldSetPosition(TextField textField)
    {

            for (int i = 0; i < flow.getChildren().size(); i++) {
                try {
                    if (flow.getChildren().get(i).getClass() == TextField.class) {
                        TextField test = (TextField) flow.getChildren().get(i);
                        if (test.equals(textField)) {
                            flowPosition.setPosition(i);
                            flowPosition.setCaretPosition(test.getCaretPosition());
                            System.out.println("textFieldSetPosition Всего букв: " + test.getText().length());
                            System.out.println("Позиция: " + test.getCaretPosition());

                            break;
                        }
                    } else {
                        System.out.println("НЕ Текстовое поле");
                    }

                } catch (Exception q) {
                    //Надобы норм исключение кидать
                }
            }

    }
    private TextField addNewPole(int ind,String msg, boolean isFirst, int indexToReplace)
    {
        TextField text = new TextField();
        text.setStyle("-fx-background-color: transparent; -fx-padding: 0px 0px 0px 0px;");
        text.setText(msg);
        if(isFirst) {
            text.setPrefWidth(flow.getPrefWidth());
        }else
        {
            text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(),
                    text.getText(), 0.0D) + 1);
        }
        flowPosition.setCaretPosition(0);
        flowPosition.setPosition(flow.getChildren().size());

        text.setOnKeyReleased(e -> {
            textFieldSetPosition(text);
            if (e.getCode() == KeyCode.BACK_SPACE){
                boolean isAccept = false;
                if(text.getCaretPosition()==0)
                {
                    for (int i = 0; i < flow.getChildren().size(); i++) {
                        try {
                            if (flow.getChildren().get(i).getClass() == TextField.class) {
                                TextField test = (TextField) flow.getChildren().get(i);
                                if (test.equals(text)) {
                                    if(flow.getChildren().get(i-1).getClass() == ImageViewHelper.class)
                                    {
                                       // flow.getChildren().remove(i-1);
                                        if(flow.getChildren().get(i-2).getClass() == TextField.class)
                                        {
                                            TextField textSecond = (TextField) flow.getChildren().get(i-2);
                                            textSecond.requestFocus();
                                            int lastMaxCaretPosition = textSecond.getText().length();
                                            textSecond.setText(textSecond.getText()+text.getText());
                                            textSecond.positionCaret(lastMaxCaretPosition);

                                            flow.getChildren().remove(text);
                                            isAccept=true;

                                        }
                                        flow.getChildren().remove(i-1);

                                    }
                                    break;
                                }
                            } else {
                                System.out.println("НЕ Текстовое поле");
                            }

                        } catch (Exception q) {
                            System.out.println("Ошибонька");
                            //Надобы норм исключение кидать
                        }
                    }
                }

                /*ЭТОТ БЛОК ПОЛНОСТЬЮ ПЕРЕПИСАТЬ!*/
                if(text.getCaretPosition()==0 && isAccept==false) {

                    if(flow.getChildren().get(flowPosition.getPosition()-1).getClass()==TextField.class) {
                        TextField pastField = (TextField) flow.getChildren().get(flowPosition.getPosition()-1);
                        if(pastField.getText().equals("") || pastField.getText().equals(" "))
                        {
                            flow.getChildren().remove(flowPosition.getPosition() - 1);
                        }
                    }

                    if(flow.getChildren().get(flowPosition.getPosition()-1).getClass()==ImageViewHelper.class) {
                            flow.getChildren().remove(flowPosition.getPosition() - 1);
                    }

                    if(flow.getChildren().get(flowPosition.getPosition()-1).getClass()==TextField.class) {
                        if(text.getText().equals("")||text.getText().equals(" "))
                        {
                            flow.getChildren().remove(flowPosition.getPosition());
                        }
                    }
                }

            }
        });

        text.setOnMouseClicked(e -> {
            textFieldSetPosition(text);
        });

        text.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(),
                        newValue, 0.0D) + 1);
                int indexTextField =0;
                for (int i=0;i<flow.getChildren().size();i++)
                {
                    try {
                        if( flow.getChildren().get(i).getClass()==TextField.class)
                        {
                            if(text.equals(flow.getChildren().get(i)))
                            {
                                indexTextField = i;
                            }
                            System.out.println("Текстовое поле");
                        }else
                        {
                            System.out.println("НЕ Текстовое поле");

                        }

                    }catch (ClassCastException cl)
                    {
                        System.out.println("Попытка текстовому полю присвоить что-то другое");
                    }
                }

                findSmile(text,flow,indexTextField);

                /*Если размер текстового поля больше, чем размер поля в котором оно, то разделяем*/
                for (int i=0;i<flow.getChildren().size();i++)
                {
                    try {
                        if( flow.getChildren().get(i).getClass()==TextField.class)
                        {
                            System.out.println("Текстовое поле");
                        }else
                        {
                            System.out.println("НЕ Текстовое поле");

                        }

                    }catch (ClassCastException cl)
                    {
                        System.out.println("Попытка текстовому полю присвоить что-то другое");
                    }
                }
                //
               // if(newValue.length() >25)
               // {
                 //   addNewPole(index++);
               // }

                if(newValue.length() < oldValue.length())
                {
                    for (int i=0;i<flow.getChildren().size();i++)
                    {
                        try {
                            TextField test = (TextField) flow.getChildren().get(i);
                            if (test.equals(text)) {
                                TextField test1;
                                try {
                                    test1 = (TextField) flow.getChildren().get(i + 1);
                                    String text = test1.getText();
                                    String letter = Character.toString(text.charAt(0));
                                    test.setText(test.getText() + letter);

                                    String result = text.substring(0, 0) + text.substring(1);

                                    test1.setText(result);
                                } catch (Exception e) {

                                }

                            }
                        }catch (ClassCastException cl)
                        {
                            System.out.println("Попытка текстовому полю присвоить что-то другое");
                        }
                    }
                }

                if(newValue.length()==0)
                {
                    for (int i=0;i<flow.getChildren().size();i++)
                    {
                        TextField test = (TextField) flow.getChildren().get(i);
                        if(test.equals(text))
                        {
                            TextField test1 =  (TextField)flow.getChildren().get(i-1);
                            test1.requestFocus();
                            test1.deselect();
                            test1.end();

                            flow.getChildren().remove(i);
                        }

                    }
                }
                System.out.println("Всего букв: "+text.getText().length());
                System.out.println("Позиция: "+ text.getCaretPosition());
                flowPosition.setCaretPosition(text.getCaretPosition()+1);


            }
        });

        if(indexToReplace!=-1)
        {
            flow.getChildren().add(indexToReplace,text);
        }else {
            flow.getChildren().add(text);
        }
        text.requestFocus();

        return text;
    }

    public void findSmile(TextField text, FlowPane flow, int indexTextField)
    {
        int colon =0;
        for(int i=0;i<text.getText().length();i++)
        {
          if( text.getText().charAt(i) == ':')
          {
              colon++;
          }
        }
        if(colon<2) return;

        String caregorys[] = text.getText().split(":");
        String smileNameFromImageView ="";
        BufferedImage smileImage = null;
        for(int i=0;i<caregorys.length;i++)
        {
            smileImage=  hashMap.get(":"+caregorys[i]+":");
            smileNameFromImageView = ":"+caregorys[i]+":";
            if(smileImage != null) break; //значит нашли картинку
        }

        if(smileImage==null) return;

        Image image = SwingFXUtils.toFXImage(smileImage, null);
        ImageViewHelper imageView = new ImageViewHelper(image,smileNameFromImageView);
        System.out.println(text.getHeight());
        imageView.setFitHeight(text.getHeight());
        imageView.setFitWidth(text.getHeight());
        imageView.prefWidth(text.getHeight());
        imageView.prefHeight(text.getHeight());
        imageView.maxHeight(text.getHeight());
        imageView.maxWidth(text.getHeight());

        if (indexTextField==0 && flowPosition.getCaretPosition()==0 || flowPosition.getCaretPosition()==0)
        {
            flow.getChildren().add(indexTextField,imageView);
        }else {
            flow.getChildren().add(indexTextField + 1, imageView);
            indexTextField = indexTextField+1;
        }
      //  flow.getChildren().add(imageView);// я должен вставлять не в конец а в нужное место!

        String textInTextField = text.getText();

        textInTextField = textInTextField.replace (smileNameFromImageView, "");
        String msg[] = text.getText().split(smileNameFromImageView);




        if(!msg[0].equals("")) {

            if (msg.length > 1) {
                addNewPole(index++, msg[1], false,indexTextField+1);
            } else {
                addNewPole(index++, " ", false,indexTextField+1);
            }
            text.setText(msg[0]);
        }else
        {
            addNewPole(index++, " ", false,-1);
            text.setText(msg[1]);
        }

    }

    public void openSmileDialog(MouseEvent mouseEvent) {

        /*
        FXMLLoader loader = new FXMLLoader();

        loader = new FXMLLoader(
                getClass().getResource(
                        "smile.fxml"
                )
        );
        try {
            if(smileDialog.getChildren().size()==0) {
                System.out.println("ВНУТРИ СМАЙЛОВ:" + smileDialog.getChildren().size());

                AnchorPane codeAnchor = (AnchorPane) loader.load();
                smileDialog.getChildren().add(codeAnchor);
                smileDialog.setVisible(true);
                System.out.println("ДОБАВИЛИ СМАЙЛОВ:" + smileDialog.getChildren().size());
            }
            if(smileDialog.isVisible())
            {
                smileDialog.setVisible(false);
            }else
            {
                smileDialog.setVisible(true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    public void openSmileDialogAll(MouseEvent mouseEvent) {
        if (smileDialog.isVisible()) {
            smileDialog.setVisible(false);
        } else {
            smileDialog.setVisible(true);
        }

    }

    public void toTray(MouseEvent mouseEvent) {
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.setIconified(true);
    }

    public void fullScreenWindow(MouseEvent mouseEvent) {
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

    public void closeWindow(MouseEvent mouseEvent) {
        ThreadClientInfoSingleton.getInstance().getClientMsgThread().interrupt();
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.close();
    }

    public void mouseInsideAvatar(MouseEvent mouseEvent) {
        Image imgLoad = new Image("resource/img/Qr/qrIMGload.png");
        newAvatars.setFill(new ImagePattern(imgLoad));
        newAvatars.setOpacity(0.6);

    }

    public void mouseLeaveAvatar(MouseEvent mouseEvent) {
        newAvatars.setOpacity(0);
        newAvatars.setFill(Color.RED);

    }

    public void loadNewAvatarClick(MouseEvent mouseEvent) {
        configImg.put("cloud_name", ThreadClientInfoSingleton.getInstance().getClientMsgThread().getCloudinaryConfig().getCloud_name());
        configImg.put("api_key", ThreadClientInfoSingleton.getInstance().getClientMsgThread().getCloudinaryConfig().getApi_key());
        configImg.put("api_secret", ThreadClientInfoSingleton.getInstance().getClientMsgThread().getCloudinaryConfig().getApi_secret());

        chooser = new ImageChooser();
        chooser.setAvailableFormats("*.png"); // Указываем форматы для FileChooser.

        String image = chooser.openImage(); // Выбираем изображение.
        if (image != null) {
            File img = new File(image);
            newImage=null;
            try {
                newImage= ImageIO.read(img);
            } catch (IOException e) {
                e.printStackTrace();
            }

            newImage=scale(newImage,50,50);

            String avatrId=changeAvatar(newImage);
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setProtocolMsg(updateAvatars(userNameString,avatrId));
            ThreadClientInfoSingleton.getInstance().getClientMsgThread().setNeedSend(true);
            new Thread(() -> {

                do {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().containsKey("updateAvatars"));
                ErrorMsg t = new ErrorMsg();
                if( t.chnageAvatar()==0 )
                {
                    Image image1 = SwingFXUtils.toFXImage(newImage, null);
                    userImg.setFill(new ImagePattern(image1));
                }
                ThreadClientInfoSingleton.getInstance().getClientMsgThread().getStatesProtocol().remove("updateAvatars");


            }).start();
        }else
        {
            setFriend("ff",false);
        }
    }

    public void setFriend(String user_name,boolean online)
    {
        FXMLLoader loader = new FXMLLoader();
        MyFriendController myfriend =
                new MyFriendController();
        myfriend.setParent(thisNode);
        myfriend.setThisNode(myfriend);
        loader = new FXMLLoader(
                getClass().getResource(
                        "fxml/myFriend.fxml"
                )
        );
        loader.setController(myfriend);
        AnchorPane newUsers =null;


        try {
            newUsers = (AnchorPane) loader.load();
            users_list.getChildren().add(newUsers);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
