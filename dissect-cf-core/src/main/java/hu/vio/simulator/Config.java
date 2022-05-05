package hu.vio.simulator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Config {
    private static class ConfigData {
        public String key;
        public Object value;
        public Object type;

        public ConfigData(String key, Object value, Object type) {
            this.key = key;
            this.value = value;
            this.type = type;
        }
    }

    public final Map<String, ConfigData> config;

    public Config() {
        config = new HashMap<>();
    }

    public Config put(String key, Object value) {
        config.put(key, new ConfigData(key, value, value.getClass().getSimpleName()));
        return this;
    }

    public void print() {
        Logger.printTitle("Configuration", 25);
        Logger.endl();

        Logger.printTitle("Key", 25);
        Logger.printTitle("Value", 25);
        Logger.printTitle("Type", 25);
        Logger.endl();
        Logger.line(3, 25);

        for (String key: config.keySet()) {
            Logger.printCell(config.get(key).key, 25);
            Logger.printCell(config.get(key).value.toString(), 25);
            Logger.printCell(config.get(key).type.toString(), 25);
            Logger.endl();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(String file) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("configuration");
            doc.appendChild(rootElement);

            for (String key: config.keySet()) {
                Element data = doc.createElement("data");
                rootElement.appendChild(data);

                Element keyData = doc.createElement("key");
                keyData.appendChild(doc.createTextNode(config.get(key).key));
                data.appendChild(keyData);

                Element value = doc.createElement("value");
                value.appendChild(doc.createTextNode(config.get(key).value.toString()));
                data.appendChild(value);

                Element type = doc.createElement("type");
                type.appendChild(doc.createTextNode(config.get(key).type.toString()));
                data.appendChild(type);
            }

            try {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(file));
                transformer.transform(source, result);
                System.out.println("Configuration saved to " + file + "!");
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(config.get(key).value);
    }
}
