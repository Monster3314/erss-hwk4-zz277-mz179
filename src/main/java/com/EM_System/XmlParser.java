package com.EM_System;

import com.EM_System.pojo.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import java.util.ArrayList;

public class XmlParser {

    private final DocumentBuilder builder;
    private final Transformer transformer;

    public XmlParser() throws ParserConfigurationException, TransformerConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.builder = factory.newDocumentBuilder();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        this.transformer = transformerFactory.newTransformer();
    }

    public Request parse(InputStream inputStream) throws IOException, SAXException {
        Document document = builder.parse(inputStream);
        Element root = document.getDocumentElement();
        String name = root.getNodeName();
        if (name.equals("create")) {
            return parseCreateRequest(root);
        } else if (name.equals("transactions")) {
            return parseTransactionsRequest(root);
        } else {
            // TODO: error msg
            return null;
        }
    }

    private CreateRequest parseCreateRequest(Element root) {
        ArrayList<CreateRequestItem> requestItems = new ArrayList<>();

        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                if (name.equals("account")) {
                    Element account = (Element) node;
                    // TODO: NumberFormatException
                    int id = Integer.parseInt(account.getAttribute("id"));
                    double balance = Double.parseDouble(account.getAttribute("balance"));
                    requestItems.add(new Account(id, balance));
                } else if (name.equals("symbol")) {
                    Element symbol = (Element) node;
                    String symName = symbol.getAttribute("sym");
                    NodeList symbols = symbol.getElementsByTagName("account");
                    for (int j = 0; j < symbols.getLength(); j++) {
                        Node symNode = symbols.item(j);
                        if (symNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element sym = (Element) symNode;
                            // TODO: NumberFormatException
                            int id = Integer.parseInt(sym.getAttribute("id"));
                            int num = Integer.parseInt(sym.getTextContent());
                            requestItems.add(new Position(id, num, symName));
                        }
                    }
                }
            }
        }

        return new CreateRequest(requestItems);
    }

    private TransactionsRequest parseTransactionsRequest(Element root) {

        ArrayList<TransactionsRequestItem> requestItems = new ArrayList<>();

        NodeList nodeList = root.getChildNodes();

        int accountId = Integer.parseInt(root.getAttribute("id"));

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                Element order = (Element) node;
                switch (name) {
                    case "order" -> {
                        String sym = order.getAttribute("sym");
                        int amount = Integer.parseInt(order.getAttribute("amount"));
                        double limit = Double.parseDouble(order.getAttribute("limit"));
                        Order toCreate = new Order(amount, limit, sym, accountId);
                        requestItems.add(new TransactionsRequestItem(TransactionsRequestItem.ORDER, toCreate));
                    }
                    case "query" -> {
                        int id = Integer.parseInt(order.getAttribute("id"));
                        requestItems.add(new TransactionsRequestItem(TransactionsRequestItem.QUERY, id));
                    }
                    case "cancel" -> {
                        int id = Integer.parseInt(order.getAttribute("id"));
                        requestItems.add(new TransactionsRequestItem(TransactionsRequestItem.CANCEL, id));
                    }
                }
            }
        }

        return new TransactionsRequest(requestItems);
    }

    public void CreateXmlResponse(OutputStream os, ArrayList<Result> results) throws TransformerException {
        Document document = builder.newDocument();
        Element root = document.createElement("results");
        document.appendChild(root);

        for (Result ret : results) {
            root.appendChild(ret.getDomElement(document));
        }

        DOMSource domSource = new DOMSource(document);
        StreamResult result = new StreamResult(os);
        transformer.transform(domSource, result);
    }
}
