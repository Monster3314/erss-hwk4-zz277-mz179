package com.EM_System.app;

import com.EM_System.XmlParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.net.ServerSocket;

public class application {
    private XmlParser xmlParser;

    public application() throws ParserConfigurationException, TransformerConfigurationException {
        this.xmlParser = new XmlParser();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2333);

    }
}
