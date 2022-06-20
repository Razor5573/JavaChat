package XMLVersion;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class XMLWriter {

    private XMLOutputStream toServer;

    XMLWriter(OutputStream os) {
        toServer = new XMLOutputStream(os);
    }

    private void sendMessage(Document outMessage) throws IOException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(outMessage);
            StreamResult streamResult = new StreamResult(toServer);
            transformer.transform(domSource, streamResult);
            toServer.send();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void sendCommandMessage(String command, String name, String type, String USID, String message) throws ParserConfigurationException, TransformerException, IOException {
        // creating builders and etc
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document outMessage = documentBuilder.newDocument();
        // creating command root and setting its name
        Element root = outMessage.createElement("command");
        Attr commandName = outMessage.createAttribute("name");
        commandName.setValue(command);
        root.setAttributeNode(commandName);
        outMessage.appendChild(root);
        // creating xml
        if (!message.equals("")) {
            Element msg = outMessage.createElement("message");
            msg.appendChild(outMessage.createTextNode(message));
            root.appendChild(msg);
        }
        // if someone is reading this - xml is retarded
        if (!USID.equals("")) {
            Element outUSID = outMessage.createElement("session");
            outUSID.appendChild(outMessage.createTextNode(USID));
            root.appendChild(outUSID);
        }
        // ffs why am i doing this send help
        if (!name.equals("")) {
            Element outName = outMessage.createElement("name");
            outName.appendChild(outMessage.createTextNode(name));
            root.appendChild(outName);
        }

        if (!type.equals("")) {
            Element outType = outMessage.createElement("type");
            outType.appendChild(outMessage.createTextNode(type));
            root.appendChild(outType);
        }
        // some sending magic (i don't even know if this shit works)
        sendMessage(outMessage);
    }

    public void sendSuccessMessage(String successType, String USID, HashMap<String, String> otherUsers) throws ParserConfigurationException, TransformerException, IOException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document outMessage = documentBuilder.newDocument();
        Element root = outMessage.createElement("success");
        outMessage.appendChild(root);

        if (successType.equals("login")) {

            Element outUSID = outMessage.createElement("session");
            outUSID.appendChild(outMessage.createTextNode(USID));
            root.appendChild(outUSID);

        } else if (successType.equals("list")) {

            // hashmap <name, type>

            Element outList = outMessage.createElement("listusers");

            for (Map.Entry<String, String> user : otherUsers.entrySet()) {

                Element outUser = outMessage.createElement("user");

                Element outName = outMessage.createElement("name");
                outName.appendChild(outMessage.createTextNode(user.getKey()));
                outUser.appendChild(outName);

                Element outType = outMessage.createElement("type");
                outType.appendChild(outMessage.createTextNode(user.getValue()));
                outUser.appendChild(outType);

                root.appendChild(outUser);
            }

            root.appendChild(outList);
        }

        sendMessage(outMessage);

    }

    public void sendErrorMessage(String message) throws ParserConfigurationException, TransformerException, IOException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document outMessage = documentBuilder.newDocument();
        Element root = outMessage.createElement("error");
        outMessage.appendChild(root);

        Element msg = outMessage.createElement("message");
        msg.appendChild(outMessage.createTextNode(message));
        root.appendChild(msg);

        sendMessage(outMessage);
    }

    @SuppressWarnings("Duplicates")
    public void sendEventMessage(String type, String message, String name) throws IOException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document outMessage = documentBuilder.newDocument();
        Element root = outMessage.createElement("event");
        Attr commandName = outMessage.createAttribute("name");
        commandName.setValue(type);
        root.setAttributeNode(commandName);
        outMessage.appendChild(root);

        if (!message.equals("")) {
            Element msg = outMessage.createElement("message");
            msg.appendChild(outMessage.createTextNode(message));
            root.appendChild(msg);
        }

        Element outName = outMessage.createElement("name");
        outName.appendChild(outMessage.createTextNode(name));
        root.appendChild(outName);

        sendMessage(outMessage);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

}
