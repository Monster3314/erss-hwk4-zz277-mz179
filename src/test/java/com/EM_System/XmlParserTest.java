//package com.EM_System;
//
//import com.EM_System.pojo.Request;
//import com.EM_System.pojo.Result;
//import org.junit.Test;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//
//public class XmlParserTest {
//
//    @Test
//    public void testParseCreate() throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException {
//        XmlParser xmlParser = new XmlParser();
//        InputStream inputStream = new FileInputStream("src/test/java/com/EM_System/create.xml");
//        Request request = xmlParser.parse(inputStream);
//        System.out.println(request);
//    }
//
//    @Test
//    public void testParseTransaction() throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException {
//        XmlParser xmlParser = new XmlParser();
//        InputStream inputStream = new FileInputStream("src/test/java/com/EM_System/transaction.xml");
//        Request request = xmlParser.parse(inputStream);
//        System.out.println(request);
//    }
//
//    @Test
//    public void testCreateXmlResponse() throws TransformerException, ParserConfigurationException {
//        ArrayList<Result> results = new ArrayList<>();
//        results.add(new Result("test", new LinkedHashMap<>(), "msg", new ArrayList<>()));
//
//        XmlParser xmlParser = new XmlParser();
//        xmlParser.CreateXmlResponse(System.out, results);
//    }
//
//}