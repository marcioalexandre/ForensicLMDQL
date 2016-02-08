package marcio.forense;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.*;
import org.exist.xmldb.*;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.DatabaseImpl;
import org.exist.xmldb.XmldbURI;

public class Xlink2 {
	private String empresa;
	private String empresaComp;
	private int ano;
	private int mes;
	private int dia;
	private String quadrante;
	private String nomearquivo;	
	String usgaapPrefix = "http://fasb.org/us-gaap/";
	String fbPrefix = "http://www.facebook.com/";
	String deiPrefix = "http://xbrl.sec.gov/dei/";
	String msftPrefix = "http://www.microsoft.com/";
	String bobsPrefix = "http://bffc.com/";
	String investPrefix = "http://xbrl.sec.gov/invest/";
	
	String exch = "http://xbrl.sec.gov/exch/2013-01-31"; 
	String invest="http://xbrl.sec.gov/invest/2013-01-31";
	String country="http://xbrl.sec.gov/country/2013-01-31"; 
	String currency="http://xbrl.sec.gov/currency/2012-01-31";
	String ustypes="http://fasb.org/us-types/2013-01-31";
	
	public Xlink2(){}	
	public Xlink2 (String empresa, int ano, int mes, int dia, String quadrante){
		System.out.println("entrou no Building 1");
		this.ano = ano;
		this.dia = dia;
		this.mes = mes;
		this.empresaComp = empresa;
		if (empresa.equals("facebook")){
			this.empresa = "fb";
		}else{
			if (empresa.equals("microsoft")){
				this.empresa = "msft";
			}else{
				this.empresa = "bobs";
			}
		}
		this.quadrante = quadrante;
		//montando prefixo us-gaap e dei:
		if (quadrante.equals("Q1") || quadrante.equals("Q2") && ano==2013){
			this.usgaapPrefix += (ano-1)+"-01-31";
			this.deiPrefix += (ano-1)+"-01-31";
			this.investPrefix += (ano-1)+"-01-31";
		}else{
			this.usgaapPrefix += ano+"-01-31";
			this.deiPrefix += ano+"-01-31";
			this.investPrefix += ano+"-01-31";
		}
		//montando prefixo fb:
		this.fbPrefix += ano+""+mes+""+dia;
		this.bobsPrefix += ano+""+mes+""+dia;
		//montando prefix msft
		this.msftPrefix += ano+""+mes+""+dia;
		this.nomearquivo = this.empresa+"-"+this.ano+this.mes+this.dia+".xml";
		System.out.println("Ano, mes, dia, empresa, empresaComp, quadrante, us, dei, msft,fb, arquivo: "+this.ano+";"+this.mes+";"+this.dia+";"+this.empresa+";"+this.empresaComp+";"+this.quadrante+";"+this.usgaapPrefix+";"+this.deiPrefix+";"+this.msftPrefix+";"+this.fbPrefix+";"+this.nomearquivo);
	}
	
		 private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
		    /**
		     * args[0] Should be the name of the collection to access
		     * args[1] Should be the XPath expression to execute
		     */
		    public ArrayList<String> queryExecute(ArrayList<ElementoDAO> arrEle) throws Exception {
		        
		        final String driver = "org.exist.xmldb.DatabaseImpl";
		        System.out.println("Driver: "+driver);
		        ArrayList<String> arrValor = new ArrayList<String>(); 
		        
		        
		     // initialize database driver
		        Class cl = Class.forName(driver);
		        Database database = (Database) cl.newInstance();
		        database.setProperty("create-database", "true");
		        DatabaseManager.registerDatabase(database);
		        Collection col = null;
		        //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
		        String colPath = "/db";
		        System.out.println("Caminho do banco:"+URI+colPath);
		        
		        for (ElementoDAO ele:arrEle){
		        	String query = null;
		        		if (this.empresa.equals("fb")){
		        			query = "declare namespace us-gaap='"+this.usgaapPrefix+"'; "
		        					+  "declare namespace dei='"+this.deiPrefix+"'; "
		        					+  "declare namespace fb='"+this.fbPrefix+"'; "
		        					+  "declare namespace msft='"+this.msftPrefix+"'; "
		        					+  "declare namespace bobs='"+this.bobsPrefix+"'; "
		        					+  "declare namespace invest='"+this.investPrefix+"'; "
		        					+  "declare namespace exch ='"+this.exch+"'; " 
			           				+  "declare namespace country='"+this.country+"'; " 
			           				+  "declare namespace currency='"+this.currency+"'; "
			           				+  "declare namespace ustypes='"+this.ustypes+"'; "		        					
			           				+  "distinct-values(doc('"+this.nomearquivo+"')//"
			           				+  ele.getNome()+"[contains(@contextRef, '"+ano+"')])";
		        					//+  ele.getNome()+"[contains(@contextRef, '"+ano+quadrante+"')])";
		        		}else{
		        			if (this.empresa.equals("msft")){
		        				query = "declare namespace us-gaap='"+this.usgaapPrefix+"'; "
			        					+  "declare namespace dei='"+this.deiPrefix+"'; "
			        					+  "declare namespace fb='"+this.fbPrefix+"'; "
			        					+  "declare namespace msft='"+this.msftPrefix+"'; "
			        					+  "declare namespace bobs='"+this.bobsPrefix+"'; "
			        					+  "declare namespace invest='"+this.investPrefix+"'; "
			        					+  "declare namespace exch ='"+this.exch+"'; " 
				           				+  "declare namespace country='"+this.country+"'; " 
				           				+  "declare namespace currency='"+this.currency+"'; "
				           				+  "declare namespace ustypes='"+this.ustypes+"'; "		        					
				           				+  "distinct-values(doc('"+this.nomearquivo+"')//"
			        					+  ele.getNome()+"[contains(@contextRef, '"+ano+""+mes+""+dia+"')])";
		        			}
		        		}
			           try { 
		        	//String dbPath = 
		        	
		        	//String query = "declare namespace us-gaap='http://fasb.org/us-gaap/2013-01-31'; "
            		//		+ "distinct-values(doc('fb-20131231.xml')//us-gaap:AccountsPayableCurrent[contains(@contextRef, '2013')])";
		        	
			           //System.out.println("Query criada:"+query);
			            
			        	col = DatabaseManager.getCollection(URI+colPath);
			            XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			            xpqs.setProperty("indent", "yes");
			            ResourceSet result = xpqs.query(query);
			            ResourceIterator i = result.getIterator();
			            Resource res = null;
			            while(i.hasMoreResources()) {
			            	res = i.nextResource();
				            arrValor.add(String.valueOf(res.getContent()));
			            }
			            xpqs = null;
			            result =null;
			            i = null;
			            res = null;
	            
			           }finally {
			            //dont forget to cleanup
			            if(col != null) {
			                try { 
			                	col.close(); 
			                } catch(Exception xe) {
			                	xe.printStackTrace();
			                }
			            }
			           }
		        }//for elementoDAO
		        
				return arrValor;
		    
		}

}
