package com.EM_System.pojo;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Result {
    String tagName;
    LinkedHashMap<String, String> attributes;
    String msg;
    ArrayList<Result> results;

    public Result(String tagName, LinkedHashMap<String, String> attributes, String msg, ArrayList<Result> results) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.msg = msg;
        this.results = results;
    }

    public Result() {
        this.tagName = "error";
        this.attributes = new LinkedHashMap<>();
        this.msg = "Invalid input";
        this.results = new ArrayList<>();
    }

    public Element getDomElement(Document document) {
        Element element = document.createElement(tagName);

        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            Attr newAttr = document.createAttribute(attr.getKey());
            newAttr.setValue(attr.getValue());
            element.setAttributeNode(newAttr);
        }

        for (Result result : results) {
            element.appendChild(result.getDomElement(document));
        }

        if (msg != null) {
            element.appendChild(document.createTextNode(msg));
        }

        return element;
    }
}
