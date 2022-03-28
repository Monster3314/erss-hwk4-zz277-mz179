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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
        } else if (name.equals("transactions")) {
            return parseTransactionsRequest(root);
        } else {
            // TODO: error msg
            return null;
        }
    }

    private CreateRequest parseCreateRequest(Element root) {
        NodeList accountList = root.getElementsByTagName("account");
        NodeList symbolList = root.getElementsByTagName("symbol");
        ArrayList<Account> accounts = new ArrayList<>();
        ArrayList<Position> positions = new ArrayList<>();

        for (int i = 0; i < accountList.getLength(); i++) {
            Node node = accountList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element account = (Element) node;
                // TODO: NumberFormatException
                int id = Integer.parseInt(account.getAttribute("id"));
                double balance = Double.parseDouble(account.getAttribute("balance"));
                accounts.add(new Account(id, balance));
            }
        }

        for (int i = 0; i < symbolList.getLength(); i++) {
            Node node = symbolList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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
                        positions.add(new Position(id, num, symName));
                    }
                }
            }
        }

        return new CreateRequest(accounts, positions);
    }

    private TransactionsRequest parseTransactionsRequest(Element root) {
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<Order> queries = new ArrayList<>();
        ArrayList<Order> cancels = new ArrayList<>();

        NodeList orderList = root.getElementsByTagName("order");
        NodeList queryList = root.getElementsByTagName("query");
        NodeList cancelList = root.getElementsByTagName("cancel");

        int accountId = Integer.parseInt(root.getAttribute("id"));

        for (int i = 0; i < orderList.getLength(); i++) {
            Node node = orderList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element order = (Element) node;
                String sym = order.getAttribute("sym");
                int amount = Integer.parseInt(order.getAttribute("amount"));
                double limit = Double.parseDouble(order.getAttribute("limit"));
                orders.add(new Order(amount, limit, sym, accountId));
            }
        }

        ParseQueryAndCancel(queries, queryList);

        ParseQueryAndCancel(cancels, cancelList);

        return new TransactionsRequest(orders, queries, cancels);
    }

    private void ParseQueryAndCancel(ArrayList<Order> transactions, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element query = (Element) node;
                int id = Integer.parseInt(query.getAttribute("id"));
                transactions.add(new Order(id));
            }
        }
    }
}
