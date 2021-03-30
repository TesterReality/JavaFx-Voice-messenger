package sample.serverSide.ServerXmlProtocol;

import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

public class ServerVacoomProtocol {


    public ServerVacoomProtocol() {
    }

    // ответ от сервера по коду и статусу
    public String sendAnswer(String action, String status) {
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
