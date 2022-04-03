import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

class NIOClientTest {

    @org.junit.jupiter.api.Test
    void getStringFromDocument() throws TransformerException, ParserConfigurationException {
        NIOClient client = new NIOClient();
//        client.createAccount(123, 123.45);
        System.out.println(client.getStringFromDocument(client.createAccount(123, 123.45)));
        System.out.println(client.getStringFromDocument(client.addPosition(123, "sym", 456)));
        System.out.println(client.getStringFromDocument(client.createOrder(3, 123, 10)));
    }
}