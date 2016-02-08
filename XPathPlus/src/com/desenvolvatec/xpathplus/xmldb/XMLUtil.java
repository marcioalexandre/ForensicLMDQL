package com.desenvolvatec.xpathplus.xmldb;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;


public class XMLUtil {


    public static String xmlDb(File configFile,int base) throws ParserConfigurationException,SAXException,IOException {
    // carregando o nome do banco de dados XML a ser utilizado
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document configDoc = db.parse(configFile); // gerando document
        NodeList nlBase = base==StartPoint.INSTANCE?
                                configDoc.getElementsByTagName("instance"):
                                configDoc.getElementsByTagName("taxonomy"); 
        Element tagBase = (Element)nlBase.item(0); // só existe uma tag instance ou taxonomy
        NodeList nlDb = tagBase.getElementsByTagName("xmldb"); 
        Element tagDb = (Element)nlDb.item(0); // só existe uma tag xmldb para cada (instance ou taxonomy)
        return tagDb.getAttribute("name");
    }
    
    public static StartPoint getStartPoint(File configFile) throws ParserConfigurationException,SAXException,IOException {
        // verificando as condições do startpoint
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document configDoc = db.parse(configFile); // gerando document
        NodeList nlSP = configDoc.getElementsByTagName("startpoint");
        if(nlSP==null) return null;
        Element tagSP = (Element)nlSP.item(0); // só existe uma tag startpoint
        if(tagSP==null) return null;
        StartPoint sp = new StartPoint();
        sp.setExpression(tagSP.getAttribute("expression"));
        sp.setFilter(tagSP.getAttribute("filter").equalsIgnoreCase("yes"));
        sp.setPrint(tagSP.getAttribute("print").equalsIgnoreCase("yes"));
        sp.setBase(tagSP.getAttribute("base").equals("instance")?0:(tagSP.getAttribute("base").equals("taxonomy")?1:2));
        return sp;
    }

    public static String getCommandOperatorDefinition(File configFile) throws ParserConfigurationException,SAXException,IOException {
        // verificando as condições de execução para o operator definition, baseado em um startpoint de retorno
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document configDoc = db.parse(configFile); // gerando document
        NodeList nlSP = configDoc.getElementsByTagName("operatordefinition");
        if(nlSP==null) return null;
        Element tagSP = (Element)nlSP.item(0); // só existe uma tag operatordefinition
        if(tagSP==null) return null;
        return tagSP.getAttribute("expression");
    }

}
