package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SmileCreater {

    private BufferedImage smile;
    private ArrayList<BufferedImage> allSmile = new ArrayList<BufferedImage>();
    private ArrayList<String> allSmilePath = new ArrayList<String>();

    private String smileConfPath;
    private BufferedImage[][] emoji = new BufferedImage[58][58];
    private String[][] emojiInfo = new String[58][58];

    // ../resource/conf/smile.txt

    public SmileCreater(String smileConfPath) {
       this.smileConfPath = smileConfPath;
    }

    public void getAllSpritePathFromConf() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(smileConfPath));
        String path;
        while(sc.hasNext()){
            path = sc.nextLine();
            allSmilePath.add(path);
            System.out.println(path);
        }
    }

    public void loadAllSmile() throws IOException {

         BufferedImage temp;
        int test =0;
        for(int i=0;i<allSmilePath.size();i++)
        {


            int x=0;
            int y=0;
            temp = ImageIO.read(new File(allSmilePath.get(i)));

            for(int n=0;n<temp.getHeight();n+=56)
            {
                for(int m=0;m<temp.getWidth();m+=56)
                {
                    cropImage(temp,56,56,m,n);
                }
            }
        }
    }

    public void loadAllEmoji() throws IOException {

        BufferedImage temp;
        int test =0;
        for(int i=0;i<allSmilePath.size();i++)
        {
            int x=0;
            int y=0;
            temp = ImageIO.read(new File(allSmilePath.get(i)));
            int indexX=0;
            int indexY=0;
            for(int n=0;n<temp.getHeight();n+=34,indexY++)
            {
                indexX=0;
                for(int m=0;m<temp.getWidth();m+=34,indexX++)
                {
                    cropEmoji(temp,34,34,m,n,indexX,indexY);
                }
            }
        }
    }

    private void cropEmoji(BufferedImage src, int width, int height,int x,int y,int indexX, int indexY) {
        BufferedImage dest = src.getSubimage(x, y, width, height);
    //    saveBufferImageToFile(dest,"x_"+x+"y_"+y+".png");
        emoji[indexX][indexY]=dest;
        System.out.println("info:" + indexX +" "+ indexY);
    }

    public void setEmojiInfo(int x,int y,String info)
    {
        emojiInfo[x][y]=info;
    }

    public String getInfo(int x,int y)
    {
      return  emojiInfo[x][y];
    }
    private void cropImage(BufferedImage src, int width, int height,int x,int y) {
        BufferedImage dest = src.getSubimage(x, y, width, height);
        allSmile.add(dest);
    }
    public BufferedImage getSmile() {
        return smile;
    }

    public void setSmile(BufferedImage smile) {
        this.smile = smile;
    }

    public ArrayList<BufferedImage> getAllSmile() {
        return allSmile;
    }

    public void setAllSmile(ArrayList<BufferedImage> allSmile) {
        this.allSmile = allSmile;
    }

    public BufferedImage getSmileFromIndex(int index)
    {
        return allSmile.get(index);
    }

    public BufferedImage getEmojiFromIndex(int x,int y)
    {

        return emoji[x][y];
    }

    public void addEmojiToList(String path)
    {
        try {
            BufferedImage temp;
            temp = ImageIO.read(new File(path));
            allSmile.add(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveBufferImageToFile(BufferedImage bufferedImage, String name)
    {
        File outputfile = new File(name);
        try {
            ImageIO.write(bufferedImage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
