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
import java.io.*;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.Random;

public class NIOClient
{
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    DocumentBuilder builder;
    public static final ExecutorService executor = Executors.newFixedThreadPool(9);
    public NIOClient() throws TransformerConfigurationException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    public void sendAndRecv(String words, SocketChannel channel) throws IOException
    {
        words = words.length() + "\n" + words + "\n";
        byte[] msg = words.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        channel.write(buffer);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        while (true) {
            buffer.clear();
            len = channel.read(buffer);
            if (len == -1)
                break;
            buffer.flip();
            while (buffer.hasRemaining()) {
                bos.write(buffer.get());
            }
        }
        // System.out.println("Client received:"+new String(bos.toByteArray()));
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        Random rand = new Random();
        long startTime=System.currentTimeMillis(); 
        // SocketChannel socketChannel=SocketChannel.open();
        // socketChannel.connect(new InetSocketAddress("67.159.95.195", 12345));
        // int tempAccount = 68;
        //client.sendAndRecv(client.getStringFromDocument(client.createAccount(tempAccount, 10000000)), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.addPosition(tempAccount, "sym", rand.nextInt(10000))), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.createOrder(1, tempAccount, 300, 125)), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.createOrder(1, tempAccount, -100, 130)), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.createOrder(1, tempAccount, 200, 127)), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.createOrder(1, tempAccount, -500, 128)), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.createOrder(1, tempAccount, -200, 140)), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.createOrder(1, tempAccount, 400, 125)), socketChannel);
        //client.sendAndRecv(client.getStringFromDocument(client.createOrder(1, tempAccount, -400, 124)), socketChannel);
        for(int i=0;i<10000;i++){
          executor.execute(new Runnable() {
                            @Override
                            public void run() {
                              try{
                                NIOClient client = new NIOClient();
                                Socket socket = new Socket("67.159.95.195", 12345);
                                OutputStream os = socket.getOutputStream();
                                PrintWriter pw = new PrintWriter(os);
                                String req = "";
                                int choice = 0, tempAccount = rand.nextInt(100);
                                switch (choice){
                                    case 0:req = client.getStringFromDocument(client.createAccount(tempAccount, 10000));break;
                                    case 1:req = client.getStringFromDocument(client.addPosition(tempAccount, "sym", rand.nextInt(1000)));break;
                                    case 2:req = client.getStringFromDocument(client.createOrder(1, tempAccount, rand.nextInt(20)-10, 10000.0 * rand.nextDouble()));break;
                                    case 3:req = client.getStringFromDocument(client.createQuery(1, tempAccount));break;
                                }
                                pw.write(req.length() + "\n" + req + "\n");
                                pw.flush();
                                socket.shutdownOutput();
                                InputStream is = socket.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                int byte_num = 0;
                                try {
                                    byte_num = Integer.parseInt(br.readLine());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                char []recv = new char[byte_num];
                                try {
                                    br.read(recv, 0, byte_num);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String receive = new String(recv);
                                receive = receive.trim().replaceFirst("^([\\W]+)<","<");
                                System.out.println(receive);
                                br.close();
                                is.close();
                                pw.close();
                                os.close();
                                socket.close();
                            } catch(Exception e){
                                System.out.println(e.getClass());
                            }
                        }
                    }
            );
        }
        long endTime=System.currentTimeMillis();
        System.out.println((endTime - startTime)+"ms");
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
