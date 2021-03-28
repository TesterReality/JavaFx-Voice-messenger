package sample.ClientXmlPorocol;

import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

public class VacoomProtocol {

    public VacoomProtocol() {
    }

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
        byte[] gg = xml.getBytes();
        int index = xml.indexOf(">");
        xml = xml.substring(index + 3, xml.length());
        System.out.print(xml);
        return xml;
    }
}
