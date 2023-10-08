import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Task1 {
    private static Logger logger = LoggerFactory.getLogger(Task1.class);
    private static final String PROPERTY_FILE_NAME = "app.properties";

    public static void main(String[] args) {
        printApplicationParameters();
        String format = setMessageFormat();
        Properties properties = setupProperties();
        String message = generateMessage(properties);
        printMessageToConsole(format, message);
        logger.info("Task complete");
    }

    private static void printApplicationParameters() {
        System.out.println("\u001B[33m Application parameters:");
        System.out.println("\u001B[34m messageFormat: \"json\" (default) or \"xml\"");
        System.out.println(" propertyFile: Extended property file name " +
                "(default): property file included in application");
        System.out.println("\u001B[0m");
    }

    private static String setMessageFormat() {
        String userFormat = System.getProperty("messageFormat");
        if (userFormat == null)
            return "json";
        userFormat = userFormat.toLowerCase();
        if (userFormat.equals("xml"))
            return "xml";
        else
            return "json";
    }

    private static Properties setupProperties() {
        String extendedPropertyFileName = System.getProperty("propertyFile");
        Properties current = new Properties();
        try {
            if (extendedPropertyFileName != null) {
                logger.info("Declared Extended property file " + extendedPropertyFileName);
                current.load(new FileInputStream(extendedPropertyFileName));
            } else {
                logger.info("Extended property file not assigned");
                logger.info("Used default property file " + PROPERTY_FILE_NAME);
                current.load(Task1.class.getResourceAsStream("/app.properties"));
            }
        } catch (IOException e) {
            logger.error("Extended property file " + extendedPropertyFileName + " not found");
            logger.error("Used default property file! ");
            try {
                current.load(Task1.class.getResourceAsStream("/app.properties"));
            } catch (IOException ex) {
                logger.error("default property file loading error");
            }
        }
        return current;
    }

    private static String generateMessage(Properties properties) {
        String userName = properties.getProperty("username");
        String message = "Привіт ";
        if (userName == null) {
            logger.error("There is no property username in file" + PROPERTY_FILE_NAME);
            logger.error("Message set to default \"Привіт unknownUser\"");
            return "Привіт unknownUser";
        }
        logger.info("message generation complete");
        return message + userName;
    }

    private static void printMessageToConsole(String format, String message) {
        System.out.println("\u001B[32m");
        if (format.equals("json")) {
            ObjectNode json = createJsonMessage(message);
            System.out.println("result is: " + json);
        } else {
            Document xmlObject = createXMLMessage(message);
            printXML(xmlObject.getDocumentElement());
        }
        System.out.println("\u001B[0m");
    }

    private static ObjectNode createJsonMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("message", message);
        return json;
    }

    private static Document createXMLMessage(String message) {
        Document document = null;
        try {
            // Создаем фабрику для создания парсера XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Создаем новый документ XML
            document = builder.newDocument();

            // Создаем корневой элемент "message"
            Element rootElement = document.createElement("message");

            // Создаем текстовый узел с содержимым сообщения
            Text textNode = document.createTextNode(message);

            // Добавляем текстовый узел в корневой элемент
            rootElement.appendChild(textNode);

            // Добавляем корневой элемент в документ
            document.appendChild(rootElement);
        } catch (Exception e) {
            logger.error("Error while xml creation.");
        }
        return document;
    }

    public static void printXML(Element element) {
        System.out.print("<" + element.getTagName() + ">");
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                printXML((Element) child);
            } else if (child instanceof org.w3c.dom.Text) {
                System.out.print(child.getTextContent());
            }
        }
        System.out.print("</" + element.getTagName() + ">");
    }
}
