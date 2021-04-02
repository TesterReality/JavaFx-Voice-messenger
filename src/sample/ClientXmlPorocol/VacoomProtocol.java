package sample.ClientXmlPorocol;

import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

public class VacoomProtocol {

    public VacoomProtocol() {
    }

    public static String prtocolToString(String xml) {
        byte[] gg = xml.getBytes();
        int index = xml.indexOf(">");
        xml = xml.substring(index + 3, xml.length());
        System.out.print(xml);
        return xml;
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

    /*Отправляет на проверку код из qr, когда мы ввостанавливаем пароль*/
    public String checkCodeRefresh(String param, String code) {
        String xml = "";

            try {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "client")
                                .attr("to", "server")
                                .attr("type", "set")
                                .add("vacoom")
                                .attr("action", "checkCodeRefresh")
                                .attr("email", param)
                                .attr("code", code)
                                .set("")
                ).xml();
            } catch (ImpossibleModificationException e) {
                e.printStackTrace();
            }

        return prtocolToString(xml);
    }

    /*запрос изменения пароля*/
    public String changePswd( String userLogin, String pswd) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "changePswd")
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
    public String checkCode(String param, String code, boolean isReg) {
        String xml = "";
        if (isReg)//false - значит запрос бы в контексте входа в ЛК true - регистрация
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

    /*Запросить у сервера код на mail*/
    public String getCodeMsg(String mail,boolean isRegistration)
    {
        String xml = null;
        if(isRegistration) {//регистрируемся или ввост. пароль
            try {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "client")
                                .attr("to", "server")
                                .attr("type", "set")
                                .add("vacoom")
                                .attr("action", "getCode")
                                .attr("mode", "registration")
                                .attr("email", mail)
                                .set("")
                ).xml();
            } catch (ImpossibleModificationException e) {
                e.printStackTrace();
            }
        }else
        {
            try {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "client")
                                .attr("to", "server")
                                .attr("type", "set")
                                .add("vacoom")
                                .attr("action", "getCode")
                                .attr("mode", "refresh")
                                .attr("email", mail)
                                .set("")
                ).xml();
            } catch (ImpossibleModificationException e) {
                e.printStackTrace();
            }
        }
        return prtocolToString(xml);
    }

    /*Регистрация пользователя*/
    public String registrationUser( String userLogin, String pswd, String code) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "registration")
                            .attr("login", userLogin)
                            .attr("pswd", pswd)
                            .attr("code", code)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }
    /*Получить логин пользователя по мейлу*/
    public String getUserLoginFromMail( String userMail) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "getMailLogin")
                            .attr("login", userMail)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }


}
