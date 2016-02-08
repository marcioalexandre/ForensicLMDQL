package com.desenvolvatec.xpathplus.xmldb;

import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;


public class DB2 {

    private String resultText;
    private File configurationFile;
    private int connectionType;
    private static Connection instanceConn = null;
    private static Connection linkbaseConn = null;
    private static Connection opDefConn = null;
    
    public DB2(String xquery, File configFile, int base) throws SQLException,ClassNotFoundException,ParserConfigurationException,SAXException,IOException {
        this.connectionType = base;
        this.configurationFile = configFile;
        this.resultText = this.executeXQuery(xquery);
    }
    
    private DB2(){} // só para uso interno
    
    public static Connection getConnection(int connType,File configFile) throws ClassNotFoundException,SQLException,ParserConfigurationException,SAXException,IOException {
        return new DB2().getConn(connType,configFile);
    }
    
    private Connection getConn(int connType,File configFile) throws ClassNotFoundException,SQLException,ParserConfigurationException,SAXException,IOException {
        // estabelecendo conexão com db2 #Werther
        // note que se tentar estabelecer a conexão toda hora, pode ocasionar problema de desempenho
        if(connType==StartPoint.INSTANCE){
            if(instanceConn==null) {
                Class.forName(getDB2Parameter("driver", configFile, connType));
                instanceConn = DriverManager.getConnection(getDB2Parameter("url",configFile, connType),getDB2Parameter("user",configFile, connType),getDB2Parameter("password",configFile, connType));
            }
            return instanceConn;
        } else if(connType==StartPoint.TAXONOMY){ // LINKBASE
            if(linkbaseConn==null) {
                Class.forName(getDB2Parameter("driver", configFile, connType));
                linkbaseConn = DriverManager.getConnection(getDB2Parameter("url",configFile, connType),getDB2Parameter("user",configFile, connType),getDB2Parameter("password",configFile, connType));
            }
            return linkbaseConn;
        } else { // OPERATOR DEFINITION
            if(opDefConn==null) {
                Class.forName(getDB2Parameter("driver", configFile, connType));
                opDefConn = DriverManager.getConnection(getDB2Parameter("url",configFile, connType),getDB2Parameter("user",configFile, connType),getDB2Parameter("password",configFile, connType));
            }
            return opDefConn;
        }
    }
    
    private String getDB2Parameter(String parameter,File configFile, int connType) throws ParserConfigurationException, SAXException, IOException{
        // capturando parâmetros do arquivo de configuração #Werther
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(configFile);
        NodeList nlType = null; 
        if(connType==StartPoint.INSTANCE)
            nlType = doc.getElementsByTagName("instance"); 
        else if(connType==StartPoint.TAXONOMY)// LINKBASE
            nlType = doc.getElementsByTagName("taxonomy"); 
        else// OPERATOR DEFINITION
            nlType = doc.getElementsByTagName("operatordefinition"); 
        Element tagType = (Element)nlType.item(0); // só existe uma tag instance ou linkbase ou operatordefinition
        NodeList nlDB = tagType.getElementsByTagName("xmldb"); 
        Element tagDB = (Element)nlDB.item(0); // só existe uma tag xmldb para cada
        String database = tagDB.getAttribute("name");
        if(database.equals("DB2")){ // esse método é exclusivo para DB2
            NodeList nlPar = tagDB.getElementsByTagName(parameter); // procurando o parametro
            Element tagPar = (Element)nlPar.item(0); // só existe uma tag para o parâmetro
            return tagPar.getTextContent();
        }
        return null; // se não for DB2
    }

    private String executeXQuery(String xquery) throws SQLException,ClassNotFoundException,ParserConfigurationException,SAXException,IOException {
        String resXquery = "";
        Statement stmt = getConn(this.connectionType,this.configurationFile).createStatement();
        ResultSet rs = stmt.executeQuery("xquery " + xquery); // executando xquery no db2
        while (rs.next())
            resXquery += rs.getString(1) + "\n";
        return resXquery;
    }
    

    public String getResultText() {
        return resultText;
    }
}
