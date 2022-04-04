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
import java.util.concurrent.*;
import java.util.Random;

public class NIOClient
{
    SocketChannel channel;
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    DocumentBuilder builder;
    public static final ExecutorService executor = Executors.newCachedThreadPool();
    //public static final ThreadPoolExecutor executor
    //        = new ThreadPoolExecutor (8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

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
        words = words.length() + System.lineSeparator() + words;
        byte[] msg = words.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        System.out.println("Client sending: " + words);
        channel.write(buffer);
        buffer.clear();
        byte[] ans = new byte[1000];
        ByteBuffer buffer = ByteBuffer.wrap(ans);
        channel.read(buffer);
        System.out.println("Client received: " + new String(buffer.array()));

        channel.close();
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        NIOClient client = new NIOClient();
        client.initClient("152.3.77.189", 12345);
        Random rand = new Random();
        for(int i=0;i<100;i++){
          executor.execute(new Runnable() {
                            @Override
                            public void run() {
                              try{
                                  int tempAccount = rand.nextInt(1000);
                                  client.sendAndRecv(client.getStringFromDocument(client.createAccount(tempAccount, 10000)));
                                  client.sendAndRecv(client.getStringFromDocument(client.addPosition(tempAccount, "sym", rand.nextInt(100))));
                                  client.sendAndRecv(client.getStringFromDocument(client.createOrder(5, tempAccount, rand.nextInt(100), 10.0 * rand.nextDouble())));
                                  client.sendAndRecv(client.getStringFromDocument(client.createQuery(rand.nextInt(100), tempAccount)));
                              } catch(Exception e){
                                System.out.println(e.getClass());
                              }
                            }
                        }
          );
        }
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

    public DOMSource createOrder(int num, int accountId, int amount, double price) {
        Document document = builder.newDocument();
        Element root = document.createElement("transactions");
        document.appendChild(root);

        Attr attr = document.createAttribute("id");
        attr.setValue(String.valueOf(accountId));
        root.setAttributeNode(attr);

        for (int i = 0; i < num; i++) {
            root.appendChild(createOrderElement(document, "sym", amount, price));
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

    public DOMSource createQuery(int num, int accountId) {
        Document document = builder.newDocument();
        Element root = document.createElement("transactions");
        document.appendChild(root);

        Attr attr = document.createAttribute("id");
        attr.setValue(String.valueOf(accountId));import org.w3c.dom.Attr;
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
import java.util.concurrent.*;
import java.util.Random;

public class NIOClient
{
    SocketChannel channel;
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    DocumentBuilder builder;
    public static final ExecutorService executor = Executors.newCachedThreadPool();
    //public static final ThreadPoolExecutor executor
    //        = new ThreadPoolExecutor (8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

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
        words = words.length() + System.lineSeparator() + words;
        byte[] msg = words.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        System.out.println("Client sending: " + words);
        channel.write(buffer);
        buffer.clear();
        byte[] ans = new char[1000];
        buffer = ByteBuffer.wrap(ans);
        channel.read(buffer);
        System.out.println("Client received: " + new String(buffer.array()));

        channel.close();
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        NIOClient client = new NIOClient();
        client.initClient("152.3.77.189", 12345);
        Random rand = new Random();
        for(int i=0;i<100;i++){
          executor.execute(new Runnable() {
                            @Override
                            public void run() {
                              try{
                                  int tempAccount = rand.nextInt(1000);
                                  client.sendAndRecv(client.getStringFromDocument(client.createAccount(tempAccount, 10000)));
                                  client.sendAndRecv(client.getStringFromDocument(client.addPosition(tempAccount, "sym", rand.nextInt(100))));
                                  client.sendAndRecv(client.getStringFromDocument(client.createOrder(5, tempAccount, rand.nextInt(100), 10.0 * rand.nextDouble())));
                                  client.sendAndRecv(client.getStringFromDocument(client.createQuery(rand.nextInt(100), tempAccount)));
                              } catch(Exception e){
                                System.out.println(e.getClass());
                              }
                            }
                        }
          );
        }
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

    public DOMSource createOrder(int num, int accountId, int amount, double price) {
        Document document = builder.newDocument();
        Element root = document.createElement("transactions");
        document.appendChild(root);

        Attr attr = document.createAttribute("id");
        attr.setValue(String.valueOf(accountId));
        root.setAttributeNode(attr);

        for (int i = 0; i < num; i++) {
            root.appendChild(createOrderElement(document, "sym", amount, price));
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

    public DOMSource createQuery(int num, int accountId) {
        Document document = builder.newDocument();
        Element root = document.createElement("transactions");
        document.appendChild(root);

        Attr attr = document.createAttribute("id");
        attr.setValue(String.valueOf(accountId));
        root.setAttributeNode(attr);

        for (int i = 0; i < num; i++) {
            root.appendChild(createQueryElement(document, i));
        }

        return new DOMSource(document);
    }

    public Element createQueryElement(Document document, int id) {
        Element element = document.createElement("query");

        Attr symAttr = document.createAttribute("id");
        symAttr.setValue(String.valueOf(id));
        element.setAttributeNode(symAttr);

        return element;
    }

    public String getStringFromDocument(DOMSource source) throws TransformerException {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }
}

        root.setAttributeNode(attr);

        for (int i = 0; i < num; i++) {
            root.appendChild(createQueryElement(document, i));
        }

        return new DOMSource(document);
    }

    public Element createQueryElement(Document document, int id) {
        Element element = document.createElement("query");

        Attr symAttr = document.createAttribute("id");
        symAttr.setValue(String.valueOf(id));
        element.setAttributeNode(symAttr);

        return element;
    }

    public String getStringFromDocument(DOMSource source) throws TransformerException {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }
}
