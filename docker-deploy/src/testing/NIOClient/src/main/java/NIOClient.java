import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class NIOClient
{
    SocketChannel channel;
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    DocumentBuilder builder;

    public NIOClient() throws TransformerConfigurationException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    public void initClient(String host, int port) throws IOException
    {
        InetSocketAddress servAddr = new InetSocketAddress(host, port);

        this.channel = SocketChannel.open(servAddr);
    }

    public void sendAndRecv(String words) throws IOException
    {
        words = words.length() + '\n' + words;
        byte[] msg = words.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        System.out.println("Client sending: " + words);
        channel.write(buffer);
        buffer.clear();
        channel.read(buffer);
        System.out.println("Client received: " + new String(buffer.array()).trim());

        channel.close();
    }

    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
        NIOClient client = new NIOClient();
        client.initClient("152.3.77.189", 12345);

        client.sendAndRecv(client.getStringFromDocument(client.createAccount(1, 100000000)));
        client.sendAndRecv(client.getStringFromDocument(client.createAccount(2, 0)));
        client.sendAndRecv(client.getStringFromDocument(client.addPosition(2, "sym", 100000000)));
        client.sendAndRecv(client.getStringFromDocument(client.createOrder(10, 123, 10)));
    }

    public DOMSource createAccount(int id, double balance) {
        Document document = builder.newDocument();
        Element root = document.createElement("create");
        document.appendChild(root);

        Element element = document.createElement("account");
        root.appendChild(element);

        Attr idAttr = document.createAttribute("id");
        idAttr.setValue(String.valueOf(id));
        element.setAttributeNode(idAttr);

        Attr balanceAttr = document.createAttribute("balance");
        balanceAttr.setValue(String.valueOf(balance));
        element.setAttributeNode(balanceAttr);

        return new DOMSource(document);
    }

    public DOMSource addPosition(int id, String symbol, int amount) {
        Document document = builder.newDocument();
        Element root = document.createElement("create");
        document.appendChild(root);

        Element element = document.createElement("symbol");
        root.appendChild(element);

        Attr symAttr = document.createAttribute("sym");
        symAttr.setValue(symbol);
        element.setAttributeNode(symAttr);

        Element child = document.createElement("account");
        element.appendChild(child);

        Attr idAttr = document.createAttribute("id");
        idAttr.setValue(String.valueOf(id));
        child.setAttributeNode(idAttr);

        child.appendChild(document.createTextNode(String.valueOf(amount)));

        return new DOMSource(document);
    }

    public DOMSource createOrder(int num, int accountId, int amount) {
        Document document = builder.newDocument();
        Element root = document.createElement("transactions");
        document.appendChild(root);

        Attr attr = document.createAttribute("id");
        attr.setValue(String.valueOf(accountId));
        root.setAttributeNode(attr);

        for (int i = 0; i < num; i++) {
            root.appendChild(createOrderElement(document, "sym", amount, 10));
        }

        return new DOMSource(document);
    }

    public Element createOrderElement(Document document, String symbol, int amount, double price) {
        Element element = document.createElement("order");

        Attr symAttr = document.createAttribute("sym");
        symAttr.setValue(symbol);
        element.setAttributeNode(symAttr);

        Attr amountAttr = document.createAttribute("amount");
        amountAttr.setValue(String.valueOf(amount));
        element.setAttributeNode(amountAttr);

        Attr limitAttr = document.createAttribute("limit");
        limitAttr.setValue(String.valueOf(price));
        element.setAttributeNode(limitAttr);

        return element;
    }

    public String getStringFromDocument(DOMSource source) throws TransformerException {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }
}