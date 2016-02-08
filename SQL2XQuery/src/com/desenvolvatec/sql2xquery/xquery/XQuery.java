package com.desenvolvatec.sql2xquery.xquery;

import com.desenvolvatec.sql2xquery.exception.FlworExpressionException;
import com.desenvolvatec.sql2xquery.exception.ImproperConfigFileException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;


public class XQuery {

    private String[] xquery = null;
    private Document configDoc = null;
    private String xmldb = null;

    public XQuery(File configFile) throws FileNotFoundException,ParserConfigurationException, 
                                                SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        this.configDoc = db.parse(configFile); // gerando document
        loadXmlDb();
    }
    
    public XQuery(String configFileContent) throws ParserConfigurationException,SAXException, IOException {
        InputStream configFileStream = new ByteArrayInputStream(configFileContent.getBytes());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        this.configDoc = db.parse(configFileStream); // gerando document
        loadXmlDb();
    }

    public XQResultSet executeSQLQuery(Connection conn,String sqlQuery) throws QueryExpressionException,ImproperConfigFileException, 
                                                                               FlworExpressionException,SQLException {
        this.xquery = XQueryGenerator.fromSQL(sqlQuery,this.configDoc); // elemento 0 (ZERO) é parametrização
        String[] res = null;
        if(this.xmldb.equals("DB2")) // java.sql.Connection só com DB2 (ate´agora)
            res = XQueryRunner.runDB2(this.xquery,conn);
        else // outro banco ainda não reconhecido
            throw new ImproperConfigFileException("XML database "+this.xmldb.toUpperCase()+" not recognized");
        // transforma (e retorna) o resultado em ResultSet
        return new XQResultSet(res);
    }
    
    public XQResultSet executeSQLQuery(XmlManager bkManager,String sqlQuery) throws QueryExpressionException,ImproperConfigFileException, 
                                                                                    FlworExpressionException,XmlException {
        this.xquery = XQueryGenerator.fromSQL(sqlQuery,this.configDoc); // elemento 0 (ZERO) é parametrização
        String[] res = null;
        if(this.xmldb.equals("BERKELEY")) // com.sleepycat.dbxml.XmlManager só com Oracle Berkeley
            res = XQueryRunner.runBerkeley(this.xquery,bkManager);
        else // outro banco ainda não reconhecido
            throw new ImproperConfigFileException("XML database "+this.xmldb.toUpperCase()+" not recognized");
        // transforma (e retorna) o resultado em ResultSet
        return new XQResultSet(res);
    }

    public XQResultSet executeSQLQuery(Collection coll,String sqlQuery) throws QueryExpressionException,ImproperConfigFileException, 
                                                                            FlworExpressionException,SQLException,XMLDBException {
        this.xquery = XQueryGenerator.fromSQL(sqlQuery,this.configDoc); // elemento 0 (ZERO) é parametrização
        String[] res = null;
        if(this.xmldb.equals("EXIST")) // org.xmldb.api.base.Collection só com DB2 (ate´agora)
            res = XQueryRunner.runExist(this.xquery,coll);
        else // outro banco ainda não reconhecido
            throw new ImproperConfigFileException("XML database "+this.xmldb.toUpperCase()+" not recognized");
        // transforma (e retorna) o resultado em ResultSet
        return new XQResultSet(res);
    }

    private void loadXmlDb(){
        // carregando o nome do banco de dados XML a ser utilizado
        NodeList nlDb = this.configDoc.getElementsByTagName("xmldb"); 
        Element tagDb = (Element)nlDb.item(0); // só existe uma tag xmldb
        this.xmldb = tagDb.getAttribute("name");
    }

}
