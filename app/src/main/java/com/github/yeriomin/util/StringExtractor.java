package com.github.yeriomin.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class StringExtractor {

    final static private String VALUES_DIR_PREFIX = "values-";

    final static private Map<String, String> stringNames = new HashMap<>();

    static {
    }

    private Map<String, String> englishStrings = new HashMap<>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Path to the other project's res directory is expected");
            System.exit(128);
        }
        File fromRoot = new File(args[0]);
        if (!fromRoot.exists() || !fromRoot.isDirectory()) {
            System.out.println("Path to the other project's res directory is expected");
            System.exit(128);
        }
        new StringExtractor().extract(fromRoot);
    }

    private void extract(File fromRoot) {
        englishStrings = getEnglishStrings();
        for (File valuesDir: fromRoot.listFiles()) {
            if (!valuesDir.isDirectory() || !valuesDir.getName().startsWith(VALUES_DIR_PREFIX)) {
                continue;
            }
            System.out.println("Processing " + valuesDir.getName());
            Map<String, String> wantedStrings = getStrings(new File(valuesDir, "strings.xml"));
            if (!wantedStrings.isEmpty()) {
                File localFile = getLocalFile(valuesDir.getName().substring(VALUES_DIR_PREFIX.length()));
                if (!localFile.exists()) {
                    create(localFile);
                }
                putStrings(wantedStrings, localFile);
            }
        }
    }

    private Map<String, String> getStrings(File from) {
        Map<String, String> wantedStrings = new HashMap<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(from);
            NodeList nodeList = doc.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element stringNode = (Element) nodeList.item(i);
                String stringName = stringNode.getAttribute("name");
                if (!stringNames.keySet().contains(stringName)) {
                    continue;
                }
                wantedStrings.put(stringNames.get(stringName), stringNode.getTextContent());
            }
        } catch (ParserConfigurationException | IOException |SAXException e) {
            System.out.println("Could not read xml document from " + from + ": " + e.getMessage());
        }
        return wantedStrings;
    }

    private Map<String, String> getEnglishStrings() {
        Map<String, String> wantedStrings = new HashMap<>();
        File from = new File("src\\main\\res\\values\\strings.xml");
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(from);
            NodeList nodeList = doc.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element stringNode = (Element) nodeList.item(i);
                wantedStrings.put(stringNode.getAttribute("name"), stringNode.getTextContent());
            }
        } catch (ParserConfigurationException | IOException |SAXException e) {
            System.out.println("Could not read xml document from " + from + ": " + e.getMessage());
        }
        return wantedStrings;
    }

    private void putStrings(Map<String, String> strings, File to) {
        Set<String> processedStrings = new HashSet<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(to);
            NodeList nodeList = doc.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element stringNode = (Element) nodeList.item(i);
                String stringName = stringNode.getAttribute("name");
                String stringContent = stringNode.getTextContent();
                if (englishStrings.containsKey(stringName) && englishStrings.get(stringName).equals(stringContent)) {
                    doc.removeChild(stringNode);
                } else if (strings.containsKey(stringName)) {
                    if (strings.get(stringName).equals(stringContent)) {
                        processedStrings.add(stringName);
                    } else {
                        doc.removeChild(stringNode);
                    }
                }
            }
            Node resources = doc.getElementsByTagName("resources").item(0);
            for (String name: strings.keySet()) {
                if (processedStrings.contains(name)) {
                    continue;
                }
                Element stringNode = doc.createElement("string");
                stringNode.setAttribute("name", name);
                stringNode.setTextContent(strings.get(name));
                resources.appendChild(stringNode);
            }
            writeXml(doc, to);
        } catch (ParserConfigurationException | IOException |SAXException e) {
            System.out.println("Could not read xml document from " + to + ": " + e.getMessage());
        }

    }

    private void create(File localFile) {
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            doc.appendChild(doc.createElement("resources"));
            writeXml(doc, localFile);
        } catch (ParserConfigurationException e) {
            System.out.println("Could not create xml document for " + localFile);
        }
    }

    private void writeXml(Document doc, File file) {
        try {
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
            doc.setXmlStandalone(true);
            Source src = new DOMSource(doc);
            Result dest = new StreamResult(file);
            aTransformer.transform(src, dest);
        } catch (TransformerException e) {
            System.out.println("Could not write xml to " + file);
        }
    }

    private File getLocalFile(String locale) {
        return new File("src\\main\\res\\values-" + locale + "\\strings.xml");
    }
}
