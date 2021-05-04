package org.voicemessanger.client.clientxmlporocol;

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

    public String updateAvatars(String login,String avatarID)
    {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "updateAvatars")
                            .attr("login", login)
                            .attr("avatarID", avatarID)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
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

    public String getFriend(String userLogin) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "getFriend")
                            .attr("login", userLogin)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    public String confirmFriend(String login, String userFriend) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "confirmFriend")
                            .attr("login", login)
                            .attr("friend", userFriend)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    public String cancelFriend(String login, String userFriend) {
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "server")
                            .attr("type", "set")
                            .add("vacoom")
                            .attr("action", "cancelFriend")
                            .attr("login", login)
                            .attr("friend", userFriend)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }
    public String startCall(String type,String whoAmI, String userFriend,String myIp, String port) {
        /*
<from to="client" type="set" who="client">
<vacoom action="startCall" friend="testerOk" ipUser="176.121.196.136" login="Vladik" port="55576"/>
</from>
         */
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "client")
                            .attr("type", type)
                            .add("vacoom")
                            .attr("action", "relay")
                            .attr("actionClient", "startCall")
                            .attr("login", whoAmI)
                            .attr("ipUser", myIp)
                            .attr("port", port)
                            .attr("friend", userFriend)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    public String sendKeyFriend(String type,String whoAmI, String userFriend,String key) {
        /*
<from to="client" type="set" who="client">
<vacoom action="startCall" friend="testerOk" ipUser="176.121.196.136" login="Vladik" port="55576"/>
</from>
         */
        String xml = null;
        try {
            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "client")
                            .attr("type", type)
                            .add("vacoom")
                            .attr("action", "relay")
                            .attr("actionClient", "sendKey")
                            .attr("login", whoAmI)
                            .attr("key", key)
                            .attr("friend", userFriend)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    public String clientDHstart(String type,String whoAmI, String userFriend,String p,String g,String hash,String publKey) {

        String xml = null;
        try {
            if(type.equals("set")) {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "client")
                                .attr("to", "client")
                                .attr("type", type)
                                .add("vacoom")
                                .attr("action", "relay")
                                .attr("actionClient", "firstDH")
                                .attr("login", whoAmI)
                                .attr("p", p)
                                .attr("g", g)
                                .attr("hash", hash)
                                .attr("friend", userFriend)
                                .set("")
                ).xml();
                return prtocolToString(xml);
            }
            if(type.equals("result"))
            {
                xml = new Xembler(
                        new Directives()
                                .add("from")
                                .attr("who", "client")
                                .attr("to", "client")
                                .attr("type", type)
                                .add("vacoom")
                                .attr("action", "relay")
                                .attr("actionClient", "firstDH")
                                .attr("login", whoAmI)
                                .attr("public", publKey)
                                .attr("friend", userFriend)
                                .set("")
                ).xml();
                return prtocolToString(xml);
            }

        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    public String sendDHpublic (String type,String whoAmI, String userFriend,String publKey) {
        String xml = null;
        try {

            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "client")
                            .attr("type", type)
                            .add("vacoom")
                            .attr("action", "relay")
                            .attr("actionClient", "publicDH")
                            .attr("login", whoAmI)
                            .attr("public", publKey)
                            .attr("friend", userFriend)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    public String halfDHhash (String type,String whoAmI, String userFriend,String hash) {
        String xml = null;
        try {

            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "client")
                            .attr("type", type)
                            .add("vacoom")
                            .attr("action", "relay")
                            .attr("actionClient", "halfDH")
                            .attr("login", whoAmI)
                            .attr("hashSharedKey", hash)
                            .attr("friend", userFriend)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }

    public String sharedKeyDHstatus (String type,String whoAmI, String userFriend,String status) {
        String xml = null;
        try {

            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "client")
                            .attr("type", type)
                            .add("vacoom")
                            .attr("action", "relay")
                            .attr("actionClient", "DHstatus")
                            .attr("login", whoAmI)
                            .attr("status", status)
                            .attr("friend", userFriend)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }
    public String firstCall (String type,String whoAmI, String userFriend,String status) {
        String xml = null;
        try {

            xml = new Xembler(
                    new Directives()
                            .add("from")
                            .attr("who", "client")
                            .attr("to", "client")
                            .attr("type", type)
                            .add("vacoom")
                            .attr("action", "relay")
                            .attr("actionClient", "firstCall")
                            .attr("login", whoAmI)
                            .attr("friend", userFriend)
                            .attr("status", status)
                            .set("")
            ).xml();
        } catch (ImpossibleModificationException e) {
            e.printStackTrace();
        }
        return prtocolToString(xml);
    }
}
