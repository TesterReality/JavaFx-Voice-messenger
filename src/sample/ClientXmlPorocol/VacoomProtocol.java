package sample.ClientXmlPorocol;

import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

public class VacoomProtocol {

    public VacoomProtocol() {
    }

    /*Авторизация пользователя на первой странице. Проверяет пароль и логин*/
    public String authorizationUser(String userLogin, String pswd) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "authorization")
                            .attr("login", userLogin)
                            .attr("pswd", pswd)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    /*Когда клиент получил код из qr, отправляет его на проверку*/
    public String checkCode(String param, String code, boolean isRfresh) {
        String xml = "";
        if (isRfresh)//false - значит запрос бы в контексте возобновления пароля
        {
            try {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "client")
                                .attr("to", "server")
                                .attr("type", "set")
                                .add("vacoom")
                                .attr("action", "checkCode")
                                .attr("email", param)
                                .attr("code", code)
                                .set("")
                ).xml();
            } catch (ImpossibleModificationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "client")
                                .attr("to", "server")
                                .attr("type", "set")
                                .add("vacoom")
                                .attr("action", "checkCode")
                                .attr("login", param)
                                .attr("code", code)
                                .set("")
                ).xml();
            } catch (ImpossibleModificationException e) {
                e.printStackTrace();
            }
        }
        return prtocolToString(xml);
    }

    public static String prtocolToString(String xml) {
        byte[] gg = xml.getBytes();
        int index = xml.indexOf(">");
        xml = xml.substring(index + 3, xml.length());
        System.out.print(xml);
        return xml;
    }
}
