package org.voicemessanger.server.serverxmlprotocol;

import org.voicemessanger.server.main.CloudinaryConfig;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

import java.util.ArrayList;

public class ServerVacoomProtocol {


    public ServerVacoomProtocol() {
    }

    public String sendAnswerFriends(ArrayList<String> friend_name, ArrayList<String> status, ArrayList<Boolean> statusOnline,ArrayList<String> avatars) {
        String xml = null;
        try {

            Directives directives = new Directives()
                    .add("from")
                    .attr("who", "server")
                    .attr("to", "client")
                    .attr("type", "result")
                    .add("vacoom")
                    .attr("action", "getFriend")
                    .add("friends");
            for (int i = 0; i < friend_name.size(); i++) {
                directives.attr("user" + i, friend_name.get(i) + ":" + status.get(i) + ":" + String.valueOf(statusOnline.get(i) + ":"+avatars.get(i)));
            }
            directives.set("");
            xml = new Xembler(directives).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        byte[] gg = xml.getBytes();
        int index = xml.indexOf(">");
        xml = xml.substring(index + 3, xml.length());
        System.out.print(xml);
        return xml;
    }
    public String sendOkAnswerAuthorization(String urlAvatar)
    {
        CloudinaryConfig cloudinaryConfig = new CloudinaryConfig();
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "server")
                            .attr("to", "client")
                            .attr("type", "result")
                            .add("vacoom")
                            .attr("action", "authorization")
                            .attr("status", "ok")
                            .attr("cloud_name", cloudinaryConfig.getCloud_name())
                            .attr("api_key", cloudinaryConfig.getApi_key())
                            .attr("api_secret", cloudinaryConfig.getApi_secret())
                            .attr("avatar",urlAvatar)

                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        byte[] gg = xml.getBytes();
        int index = xml.indexOf(">");
        xml = xml.substring(index + 3, xml.length());
        System.out.print(xml);
        return xml;
    }
    // ответ от сервера по коду и статусу
    public String sendAnswer(String action, String status) {

        if(status.contains(":"))//если нам ответить много чего
        {
            String [] codes;
            codes = status.split(":");
            String xml = null;
            try {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "server")
                                .attr("to", "client")
                                .attr("type", "result")
                                .add("vacoom")
                                .attr("action", action)
                                .attr("status", codes[0])
                                .attr("login",codes[1])
                                .set("")
                ).xml();
            } catch (ImpossibleModificationException e) {
                e.printStackTrace();
            }
            byte[] gg = xml.getBytes();
            int index = xml.indexOf(">");
            xml = xml.substring(index + 3, xml.length());
            System.out.print(xml);
            return xml;
        }

        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "server")
                            .attr("to", "client")
                            .attr("type", "result")
                            .add("vacoom")
                            .attr("action", action)
                            .attr("status", status)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        byte[] gg = xml.getBytes();
        int index = xml.indexOf(">");
        xml = xml.substring(index + 3, xml.length());
        System.out.print(xml);
        return xml;
    }

}
