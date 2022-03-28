package com.EM_System;

import com.EM_System.pojo.CreateRequest;
import com.EM_System.pojo.Request;
import com.EM_System.pojo.TransactionsRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
        Element element = document.getDocumentElement();
        String name = element.getNodeName();
        if (name.equals("create")) {
            return parseCreateRequest(element);
        }
        else if (name.equals("transactions")) {
            return parseTransactionsRequest(element);
        }
        else {
            // TODO: error msg
            return null;
        }
    }

    private CreateRequest parseCreateRequest(Element element) {
        return new CreateRequest();
    }

    private TransactionsRequest parseTransactionsRequest(Element element) {
        return new TransactionsRequest();
    }
}
