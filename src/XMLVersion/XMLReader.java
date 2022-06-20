package XMLVersion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class XMLReader {

    private XMLInputStream fromServer;
    private Document currentMessage;

    XMLReader(InputStream is) {
        fromServer = new XMLInputStream(is);
    }

    public void readXMLMessage () throws IOException {
        try {
            fromServer.receive();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            currentMessage = dBuilder.parse(fromServer);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    public String getHeader(){
        return currentMessage.getDocumentElement().getNodeName();
    }


    public String getCommandOrEventName(){
        try {
            return currentMessage.getDocumentElement().getAttribute("name");
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getUSID(){
        try {
            return currentMessage.getElementsByTagName("session").item(0).getTextContent();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getMessage(){
        try {
            return currentMessage.getElementsByTagName("message").item(0).getTextContent();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getName(){
        try {
            return currentMessage.getElementsByTagName("name").item(0).getTextContent();
        }catch (NullPointerException e) {
            return "";
        }
    }

    public String getType(){
        try {
            return currentMessage.getElementsByTagName("type").item(0).getTextContent();
        }catch (NullPointerException e) {
            return "";
        }
    }

    public Boolean isUserList(){
        return (currentMessage.getElementsByTagName("userlist").getLength() == 1);
    }

    public HashMap<String, String> getUsers(){
        HashMap<String, String> output = new HashMap<>();
        NodeList users = currentMessage.getElementsByTagName("user");
        for (int i = 0; i < users.getLength(); i++) {
            Node user = users.item(i);
            if (user.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) user;
                output.put(userElement.getElementsByTagName("name").item(0).getTextContent(),
                        userElement.getElementsByTagName("type").item(0).getTextContent());
            }
        }
        return output;
    }

}
