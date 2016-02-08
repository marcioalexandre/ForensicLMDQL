package mondrian.lmdql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.desenvolvatec.xpathplus.xmldb.DB2;

import com.desenvolvatec.xpathplus.xmldb.StartPoint;

public class Database {
	
	private static Connection conn;

	public final static int RELATIONAL = 0;
	public final static int XML = 1;
	
	
	public static Connection getDB2Conn(File configFile){
		Database db = new Database();
        if(conn==null){
            try { // estabelecendo conex�o com DB2 (XML) #Paulo Caetano
                Class.forName(db.getDB2Parameter("driver", configFile));
                conn = DriverManager.getConnection(db.getDB2Parameter("url",configFile),db.getDB2Parameter("user",configFile),db.getDB2Parameter("password",configFile));
            } catch (Exception e) {
            	System.out.println("Error trying to establish connection to DB2:");
                e.printStackTrace();
            }
        }
        return conn;
	}
	
	private String getDB2Parameter(String parameter,File configFile) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(configFile);
        NodeList nlDB = doc.getElementsByTagName("xmldb"); 
        Element tagDB = (Element)nlDB.item(0); // s� existe uma tag xmldb
        String database = tagDB.getAttribute("name");
        if(database.equals("DB2")){ // esse m�todo � exclusivo para DB2
	        NodeList nlPar = tagDB.getElementsByTagName(parameter); // procurando o parametro
	        Element tagPar = (Element)nlPar.item(0); // s� existe uma tag para o par�metro
	        return tagPar.getTextContent();
        }
        return null; // se n�o for DB2
	}
	
	public static Connection getDB2ConnOpDef() throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException{
		XLink.loadXpathplusConfigFile();
		return DB2.getConnection(StartPoint.OPERATOR_DEFINITION,new File(Parameters.xmlXPathPlusFileName));
	}
	
	public static String getOpDefXMLFile(Document doc) {
        NodeList nl = doc.getElementsByTagName("operatordefinition"); // s� tem um elemento
        if(nl==null || nl.item(0)==null)
        	return null; // n�o existe - N�O DEVE ACONTECER
        return nl.item(0).getAttributes().getNamedItem("expression").getNodeValue();
	}
}
