package sample.ClientXmlPorocol;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.ResizeHelper;
import sample.ThreadClientInfoSingleton;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientParseProtocol extends VacoomProtocol {
    private int howNeedString;//сколько нужно выделить строк в массиве
    private int numOfNow; // текущее положение строки
    private int randomNum;
    String[] strings1;
    ArrayList<Integer> portCheck = new ArrayList<>();

    public ClientParseProtocol() {
    }

    public int parseRequest(String request) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(request);
        while (m.find()) {
            System.out.println(m.group(1));
            howNeedString++;
        }
        strings1 = new String[howNeedString];
        m.reset();
        numOfNow = 0;
        while (m.find()) {
            strings1[numOfNow++] = m.group(1);
        }
        return parseCommand(strings1);
    }

    private int parseCommand(String[] commands) {
        switch (commands[1])//мы должны отправить или получить
        {
            case "set"://значит НАМ подали запрос, мы должны ответить
            {
                return 1;
            }

            case "result":
            {
                return resultCommands(commands);
            }
        }
        return 1;
    }


    private void setCommands(String[] commands) {
        switch (commands[3])//содержит код запроса
        {
            case "default":
                break;
        }
    }
//0-ok 1-error
    private int resultCommands(String[] commands) {
        switch (commands[3])//содержит код запроса
        {
            case "authorization":
                switch (commands[4]) {
                    case "ok":
                        return 0;
                    case "error":
                        return 1;
                }
                break;
            case "checkCode":
                {
                    switch (commands[4]) {
                        case "ok":
                            return 0;
                        case "error_code":
                            return 1;
                    }
                    break;
                }
        }
        return 1;
    }

}
