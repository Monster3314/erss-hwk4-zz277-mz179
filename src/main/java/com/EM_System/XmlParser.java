package com.EM_System;

import com.EM_System.pojo.CreateRequest;
import com.EM_System.pojo.Request;
import com.EM_System.pojo.TransactionsRequest;
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

public class XmlParser {

    private final DocumentBuilder builder;

    public XmlParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.builder = factory.newDocumentBuilder();
    }

    public Request parse(InputStream inputStream) throws IOException, SAXException {
        Document document = builder.parse(inputStream);
        Element root = document.getDocumentElement();
        String name = root.getNodeName();
        if (name.equals("create")) {
            return parseCreateRequest(root);
        }
        else if (name.equals("transactions")) {
            return parseTransactionsRequest(root);
        }
        else {
            // TODO: error msg
            return null;
        }
    }

    private CreateRequest parseCreateRequest(Element root) {
        NodeList accountList = root.getElementsByTagName("account");
        NodeList symbolList = root.getElementsByTagName("symbol");

        for (int i = 0; i < accountList.getLength(); i++) {
            Node node = accountList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element account = (Element) node;
                // TODO: NumberFormatException
                int id = Integer.parseInt(account.getAttribute("id"));
                double balance = Double.parseDouble(account.getAttribute("balance"));
            }
        }

        for (int i = 0; i < symbolList.getLength(); i++) {
            Node node = symbolList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element symbol = (Element) node;
                // TODO: NumberFormatException
                int id = Integer.parseInt(symbol.getAttribute("id"));
                int num = Integer.parseInt(symbol.getTextContent());
            }
        }

        return new CreateRequest();
    }

    private TransactionsRequest parseTransactionsRequest(Element root) {
        return new TransactionsRequest();
    }
}
