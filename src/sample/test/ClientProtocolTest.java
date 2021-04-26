package sample.test;

import sample.ClientXmlPorocol.VacoomProtocol;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientProtocolTest {

    public static void main(String[] args) {
        VacoomProtocol protocol = new VacoomProtocol();

        String result =protocol.startCall("result","dudosik","testrOk","127.0.0.1","53444");
        parseRequest(result);
    }

    public static void parseRequest(String request) {
      int  howNeedString=0;

        Pattern p = Pattern.compile("\"([^\"]*)\""); //Результаты тегов, например: <from to="client".. будет client
        Pattern p1 = Pattern.compile("\\w+(?=\\=)");//Сами теги, например: <from to="client".. будет to
        int howNeeds=0;
        Matcher m = p.matcher(request);
        Matcher m1 = p1.matcher(request);

        while (m.find()) {
            System.out.println(m.group(1));
            howNeedString++;
        }
        while (m1.find()) {
            System.out.println(m1.group(0));
            howNeeds++;
        }
        String[] strings1 = new String[howNeedString];
        String[] strings2 = new String[howNeeds];

        m.reset();
        m1.reset();

       int numOfNow = 0;
        int numOfNow1 = 0;

        while (m.find()) {
            strings1[numOfNow++] = m.group(1);
        }
        while (m1.find()) {
            strings2[numOfNow1++] = m1.group(0);
        }
        parseAnswerAccessUDP(strings1,strings2);
    }
    private static void parseAnswerAccessUDP(String[] commands,String[] suffix) {
        HashMap<String, String> protocol = new HashMap<>();

        for (int i=0; i<commands.length;i++)
        {
            protocol.put(suffix[i],commands[i]);
        }
        switch (protocol.get("actionClient"))//содержит код запроса
        {
            case "startCall":
            {
                String gg = "wow";
                break;
            }
            case "default":
                System.out.println(commands);
                break;
        }
    }
}
