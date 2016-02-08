package marcio.forense;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.*;
import org.exist.xmldb.*;

public class Xlink3 {
	private String empresa;
	private String empresaComp;
	int ano;
	private String mes;
	private int dia;
	String quadrante;
	String nomearquivo;	
	String usgaapPrefix = "http://fasb.org/us-gaap/";
	String fbPrefix = "http://www.facebook.com/";
	String deiPrefix = "http://xbrl.sec.gov/dei/";
	String msftPrefix = "http://www.microsoft.com/";
	String bobsPrefix = "http://bffc.com/";
	String investPrefix = "http://xbrl.sec.gov/invest/";
	/*
	String exch = "http://xbrl.sec.gov/exch/2013-01-31"; 
	String invest="http://xbrl.sec.gov/invest/2013-01-31";
	String country="http://xbrl.sec.gov/country/2013-01-31"; 
	String currency="http://xbrl.sec.gov/currency/2012-01-31";
	String ustypes="http://fasb.org/us-types/2013-01-31";
	*/
	public Xlink3(){}	
	public Xlink3 (String empresa, int ano, String mes, int dia, String quadrante){
		//System.out.println("entrou no Building 1");
		this.ano = ano;
		this.dia = dia;
		this.mes = mes;
		this.empresaComp = empresa;
		if (empresa.equalsIgnoreCase("facebook")){
			this.empresa = "fb";
		}
		if (empresa.equalsIgnoreCase("microsoft")){
				this.empresa = "msft";
		}
		if (empresa.equalsIgnoreCase("bobs")){
			this.empresa = "bobs";
		}
		
		System.out.println("Empresa:"+this.empresa+", em xlink3");
		this.quadrante = quadrante;
		//montando prefixo us-gaap e dei:
		if (quadrante.equals("Q1") || quadrante.equals("Q2") && ano==2013){
			this.usgaapPrefix += (ano-1)+"-01-31";
			this.deiPrefix += (ano-1)+"-01-31";
			this.investPrefix += (ano-1)+"-01-31";
		}else{
			if (quadrante.equals("Q3") || quadrante.equals("Q4") && ano==2013){
				this.usgaapPrefix += (ano)+"-01-31";
				this.deiPrefix += (ano)+"-01-31";
				this.investPrefix += (ano)+"-01-31";
			}else{
				if (ano==2012){
					this.usgaapPrefix += ano+"-01-31";
					this.deiPrefix += ano+"-01-31";
					this.investPrefix += ano+"-01-31";			
			}
		}
		}
		//montando prefixo fb:
		this.fbPrefix += ano+""+mes+""+dia;
		this.bobsPrefix += ano+""+mes+""+dia;
		//montando prefix msft
		this.msftPrefix += ano+""+mes+""+dia;
		this.nomearquivo = this.empresa+"-"+this.ano+this.mes+this.dia;
		//System.out.println("Ano, mes, dia, empresa, empresaComp, quadrante, us, dei, msft,fb, arquivo: "+this.ano+";"+this.mes+";"+this.dia+";"+this.empresa+";"+this.empresaComp+";"+this.quadrante+";"+this.usgaapPrefix+";"+this.deiPrefix+";"+this.msftPrefix+";"+this.fbPrefix+";"+this.nomearquivo);
	}
	
	private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";

	public Boolean checkDocumento(String name) throws Exception{
		   // initialize database driver
		   Boolean bool = false;
		   final String driver = "org.exist.xmldb.DatabaseImpl";
		   //System.out.println("Driver: "+driver);
		   Class cl = Class.forName(driver);
		   Database database = (Database) cl.newInstance();
		   database.setProperty("create-database", "true");
		   DatabaseManager.registerDatabase(database);
		   Collection col = null;
	       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
	       String colPath = "/db";
	       //System.out.println("Caminho do banco:"+URI+colPath);
	       String xquery = null;
	       xquery = "distinct-values(doc('InstancesCollection.xml')//fb/doc[contains(@id,'"+name+"')]/@id)";
	       System.out.println("Xquery:"+xquery);
	       /*distinct-values(doc('"+this.nomearquivo+"')//+  ele.getNome()+"[contains(@contextRef, '"+ano+""+mes+""+dia+"')])";
	       query = "for $e in doc('db/xbrl/InstancesCollection.xml')"
	    		   + "//fb/doc[contains(@name,'"+name+"')]" 
	    		   + " return $e";
	       */
	       //System.out.println(xquery);
   try {     
		 col = DatabaseManager.getCollection(URI+colPath);
		 XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		 xpqs.setProperty("indent", "yes");
		 ResourceSet result = xpqs.query(xquery);
		 ResourceIterator i = result.getIterator();
		 System.out.println("Qtd"+i.toString());
		 Resource res = null;
		 while(i.hasMoreResources()) {
         	bool = true;
         	res = i.nextResource();
		 }
		 xpqs = null;
		 result =null;
		 i = null;
		 res = null;	            
	}finally {
		 if(col != null) {
		   try { 
		       	col.close(); 
		   } catch(Exception xe) {
		       	xe.printStackTrace();
		   }
		 }
	}
   return bool;
}
	
	public ArrayList<String> getDocs() throws Exception{
		   // initialize database driver
		   ArrayList<String> arrFile = new ArrayList<String>(); 
		   final String driver = "org.exist.xmldb.DatabaseImpl";
		   //System.out.println("Driver: "+driver);
		   Class cl = Class.forName(driver);
		   Database database = (Database) cl.newInstance();
		   database.setProperty("create-database", "true");
		   DatabaseManager.registerDatabase(database);
		   Collection col = null;
	       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
	       String colPath = "/db";
	       System.out.println("Caminho do banco:"+URI+colPath);
	       String xquery = null;
	       xquery = "distinct-values(doc('InstancesCollection.xml')//fb/doc/@id)";
	       /*distinct-values(doc('"+this.nomearquivo+"')//+  ele.getNome()+"[contains(@contextRef, '"+ano+""+mes+""+dia+"')])";
	       query = "for $e in doc('db/xbrl/InstancesCollection.xml')"
	    		   + "//fb/doc[contains(@name,'"+name+"')]" 
	    		   + " return $e";
	       */
	       //System.out.println(xquery);
try {     
		 col = DatabaseManager.getCollection(URI+colPath);
		 XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		 xpqs.setProperty("indent", "yes");
		 ResourceSet result = xpqs.query(xquery);
		 ResourceIterator i = result.getIterator();
		 System.out.println("Qtd"+i.toString());
		 Resource res = null;
		 while(i.hasMoreResources()) {
			 res = i.nextResource();
			 arrFile.add(String.valueOf(res.getContent()));
		 }
		 xpqs = null;
		 result =null;
		 i = null;
		 res = null;	            
	}finally {
		 if(col != null) {
		   try { 
		       	col.close(); 
		   } catch(Exception xe) {
		       	xe.printStackTrace();
		   }
		 }
	}
	return arrFile;
}
		    
	public int getYearOfDoc(String filename) throws Exception{
		   // initialize database driver
		   int year = 0; 
		   final String driver = "org.exist.xmldb.DatabaseImpl";
		   //System.out.println("Driver: "+driver);
		   Class cl = Class.forName(driver);
		   Database database = (Database) cl.newInstance();
		   database.setProperty("create-database", "true");
		   DatabaseManager.registerDatabase(database);
		   Collection col = null;
	       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
	       String colPath = "/db";
	       //System.out.println("Caminho do banco:"+URI+colPath);
	       String xquery = null;
	       xquery = "distinct-values(doc('InstancesCollection.xml')//fb/doc[contains(@id,'"+filename+"')]/@year)";
	       /*distinct-values(doc('"+this.nomearquivo+"')//+  ele.getNome()+"[contains(@contextRef, '"+ano+""+mes+""+dia+"')])";
	       query = "for $e in doc('db/xbrl/InstancesCollection.xml')"
	    		   + "//fb/doc[contains(@name,'"+name+"')]" 
	    		   + " return $e";
	       */
	       //System.out.println(xquery);
try {     
		 col = DatabaseManager.getCollection(URI+colPath);
		 XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		 xpqs.setProperty("indent", "yes");
		 ResourceSet result = xpqs.query(xquery);
		 ResourceIterator i = result.getIterator();
		 //System.out.println("Qtd"+i.toString());
		 Resource res = null;
		 while(i.hasMoreResources()) {
			 res = i.nextResource();
			 year = Integer.parseInt((String) res.getContent());
		 }
		 xpqs = null;
		 result =null;
		 i = null;
		 res = null;	            
	}finally {
		 if(col != null) {
		   try { 
		       	col.close(); 
		   } catch(Exception xe) {
		       	xe.printStackTrace();
		   }
		 }
	}
	return year;
}

	public String getQuadranteOfDoc(String filename) throws Exception{
		   // initialize database driver
			String quadrante = null; 
		   final String driver = "org.exist.xmldb.DatabaseImpl";
		   //System.out.println("Driver: "+driver);
		   Class cl = Class.forName(driver);
		   Database database = (Database) cl.newInstance();
		   database.setProperty("create-database", "true");
		   DatabaseManager.registerDatabase(database);
		   Collection col = null;
	       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
	       String colPath = "/db";
	       //System.out.println("Caminho do banco:"+URI+colPath);
	       String xquery = null;
	       xquery = "distinct-values(doc('InstancesCollection.xml')//fb/doc[contains(@id,'"+filename+"')]/@q)";
	       /*distinct-values(doc('"+this.nomearquivo+"')//+  ele.getNome()+"[contains(@contextRef, '"+ano+""+mes+""+dia+"')])";
	       query = "for $e in doc('db/xbrl/InstancesCollection.xml')"
	    		   + "//fb/doc[contains(@name,'"+name+"')]" 
	    		   + " return $e";
	       */
	       //System.out.println(xquery);
try {     
		 col = DatabaseManager.getCollection(URI+colPath);
		 XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		 xpqs.setProperty("indent", "yes");
		 ResourceSet result = xpqs.query(xquery);
		 ResourceIterator i = result.getIterator();
		 //System.out.println("Qtd"+i.toString());
		 Resource res = null;
		 while(i.hasMoreResources()) {
			 res = i.nextResource();
			 quadrante = (String) res.getContent();
		 }
		 xpqs = null;
		 result =null;
		 i = null;
		 res = null;	            
	}finally {
		 if(col != null) {
		   try { 
		       	col.close(); 
		   } catch(Exception xe) {
		       	xe.printStackTrace();
		   }
		 }
	}
	return quadrante;
}

	public ArrayList<String> getElements(String file) throws Exception {
	   ArrayList<String> arrElemento = new ArrayList<String>();    
	   // initialize database driver
	   final String driver = "org.exist.xmldb.DatabaseImpl";
	   //System.out.println("Driver: "+driver);
	   Class cl = Class.forName(driver);
	   Database database = (Database) cl.newInstance();
	   database.setProperty("create-database", "true");
	   DatabaseManager.registerDatabase(database);
	   Collection col = null;
       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
       String colPath = "/db";
       //System.out.println("Caminho do banco:"+URI+colPath);

       String query = null;
	        	//pegar elementos do esquema
       query = "declare namespace xsd='http://www.w3.org/2001/XMLSchema'; "
    		   + "distinct-values(doc('"+file+"')//xsd:element//@id)";
		System.out.println(query);
	    try {     
	    	
	    	//pegar esquema próprio
			 col = DatabaseManager.getCollection(URI+colPath);
			 XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			 xpqs.setProperty("indent", "yes");
			 ResourceSet result = xpqs.query(query);
			 ResourceIterator i = result.getIterator();
			 Resource res = null;
			 while(i.hasMoreResources()) {
				 res = i.nextResource();
				 String elemento = String.valueOf(res.getContent());
				 elemento = elemento.replace("fb_", "fb:");
				 arrElemento.add(elemento);
			 }
			 xpqs = null;
			 result =null;
			 i = null;
			 res = null;
			 
			 //pegar esquema us-gaap
			 query = 	"declare namespace xs='http://www.w3.org/2001/XMLSchema'; "
					 +	"distinct-values(doc('us-gaapSchema.xsd')//xs:element//@id)";
			 col = DatabaseManager.getCollection(URI+colPath);
			 xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			 xpqs.setProperty("indent", "yes");
			 result = xpqs.query(query);
			 i = result.getIterator();
			 res = null;
			 int j = 0;
			 while(i.hasMoreResources() && j<=18000) {
				 res = i.nextResource();
				 String elemento = String.valueOf(res.getContent());
				 elemento = elemento.replace("us-gaap_", "us-gaap:");
				 arrElemento.add(elemento);
				 j++;
			 }
			 xpqs = null;
			 result =null;
			 i = null;
			 res = null;
		}finally {
			 if(col != null) {
			   try { 
			       	col.close(); 
			   } catch(Exception xe) {
			       	xe.printStackTrace();
			   }
			 }
		}	        
			return arrElemento;
		    
		}

	public String getValueDoc(String name, Xlink3 xl2, String element) throws Exception{
			System.out.println("Função: getValueDoc (Xlink3)");
		    String value = null;
		   // initialize database driver
		   final String driver = "org.exist.xmldb.DatabaseImpl";
		   //System.out.println("Driver: "+driver);
		   Class cl = Class.forName(driver);
		   Database database = (Database) cl.newInstance();
		   database.setProperty("create-database", "true");
		   DatabaseManager.registerDatabase(database);
		   Collection col = null;
	       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
	       String colPath = "/db";
	       //System.out.println("Caminho do banco:"+URI+colPath);
	       String query = null;
	       if (ano != 2012){
    		   if (xl2.empresa.equalsIgnoreCase("msft")){
	    		   query = "	declare namespace us-gaap='"+xl2.usgaapPrefix+"'; "
						+  "declare namespace dei='"+xl2.deiPrefix+"'; "
						+  "declare namespace fb='"+xl2.fbPrefix+"'; "
						+  "declare namespace msft='"+xl2.msftPrefix+"'; "
						+  "declare namespace bobs='"+xl2.bobsPrefix+"'; "
						+  "declare namespace invest='"+xl2.investPrefix+"'; "
						/*
						+  "declare namespace exch ='"+this.exch+"'; " 
	      				+  "declare namespace country='"+this.country+"'; " 
	      				+  "declare namespace currency='"+this.currency+"'; "
	      				+  "declare namespace ustypes='"+this.ustypes+"'; "
	      				*/
		    		    + "distinct-values(doc('"+name+"')//"+element+"[contains(@contextRef,'"+xl2.ano+"') and contains(@unitRef,'iso4217_USD')])";
    		   }else{
    			   if (xl2.empresa.equalsIgnoreCase("fb")){
    				   query = "	declare namespace us-gaap='"+xl2.usgaapPrefix+"'; "
						+  "declare namespace dei='"+xl2.deiPrefix+"'; "
						+  "declare namespace fb='"+xl2.fbPrefix+"'; "
						+  "declare namespace msft='"+xl2.msftPrefix+"'; "
						+  "declare namespace bobs='"+xl2.bobsPrefix+"'; "
						+  "declare namespace invest='"+xl2.investPrefix+"'; "
						/*
						+  "declare namespace exch ='"+this.exch+"'; " 
	      				+  "declare namespace country='"+this.country+"'; " 
	      				+  "declare namespace currency='"+this.currency+"'; "
	      				+  "declare namespace ustypes='"+this.ustypes+"'; "
	      				*/
		    		    + "distinct-values(doc('"+name+"')//"+element+"[contains(@contextRef,'"+xl2.ano+xl2.quadrante+"') and contains(@unitRef,'usd')])";
    			   }
    		   }
	       }else{
	   		   if (xl2.empresa.equalsIgnoreCase("msft")){
	    		   query = "	declare namespace us-gaap='"+xl2.usgaapPrefix+"'; "
						+  "declare namespace dei='"+xl2.deiPrefix+"'; "
						+  "declare namespace fb='"+xl2.fbPrefix+"'; "
						+  "declare namespace msft='"+xl2.msftPrefix+"'; "
						+  "declare namespace bobs='"+xl2.bobsPrefix+"'; "
						+  "declare namespace invest='"+xl2.investPrefix+"'; "
						/*
						+  "declare namespace exch ='"+this.exch+"'; " 
	      				+  "declare namespace country='"+this.country+"'; " 
	      				+  "declare namespace currency='"+this.currency+"'; "
	      				+  "declare namespace ustypes='"+this.ustypes+"'; "
	      				*/
		    		    + "distinct-values(doc('"+name+"')//"+element+"[contains(@contextRef,'"+xl2.ano+"') and contains(@unitRef,'iso4217_USD')])";
    		   }else{
    			   if (xl2.empresa.equalsIgnoreCase("fb")){
    				   query = "	declare namespace us-gaap='"+xl2.usgaapPrefix+"'; "
						+  "declare namespace dei='"+xl2.deiPrefix+"'; "
						+  "declare namespace fb='"+xl2.fbPrefix+"'; "
						+  "declare namespace msft='"+xl2.msftPrefix+"'; "
						+  "declare namespace bobs='"+xl2.bobsPrefix+"'; "
						+  "declare namespace invest='"+xl2.investPrefix+"'; "
						/*
						+  "declare namespace exch ='"+this.exch+"'; " 
	      				+  "declare namespace country='"+this.country+"'; " 
	      				+  "declare namespace currency='"+this.currency+"'; "
	      				+  "declare namespace ustypes='"+this.ustypes+"'; "
	      				*/
		    		    + "distinct-values(doc('"+name+"')//"+element+"[contains(@contextRef,'"+xl2.ano+xl2.quadrante+"') and contains(@unitRef,'usd')])";
    			   }
    		   }
	       }
	       System.out.println("Xquery:"+query);
		    try {     
				 col = DatabaseManager.getCollection(URI+colPath);
				 XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
				 xpqs.setProperty("indent", "yes");
				 ResourceSet result = xpqs.query(query);
				 ResourceIterator i = result.getIterator();
				 Resource res = null;
				 if(i.hasMoreResources()) {
					 res = i.nextResource();
					 value = String.valueOf(res.getContent());
					 //System.out.println("Valor:"+value);
				 }
				 xpqs = null;
				 result =null;
				 i = null;
				 res = null;	            
			}catch(Exception e){
				System.out.println("Erro Xlink3 getValueDoc: "+e.getMessage());
			}finally {
				 if(col != null) {
				   try { 
				       	col.close(); 
				   } catch(Exception xe) {
				       	xe.printStackTrace();
				   }
				 }
			}	        

				return value;
			
			    
	}
	
	public ArrayList<String> getInstanceElements(String filename, String ano, String quadrante, ArrayList<String> arrEle) throws Exception{
			System.out.println("Nomo do arquivo xml:"+filename);
		    ArrayList<String> eles = null;
		   // initialize database driver
			   final String driver = "org.exist.xmldb.DatabaseImpl";
			   //System.out.println("Driver: "+driver);
			   Class cl = Class.forName(driver);
			   Database database = (Database) cl.newInstance();
			   database.setProperty("create-database", "true");
			   DatabaseManager.registerDatabase(database);
		       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
		       String colPath = "/db";
			   Collection col = null;
		    try {     
				 int j=0;
				 for (String ele:arrEle){
					 col = null;
					 //if (j==0){

				       col = DatabaseManager.getCollection(URI+colPath);
				       XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
				       xpqs.setProperty("indent", "yes");
				       ResourceSet result = null;
				       ResourceIterator i = null;
				       Resource res = null;
						 //System.out.println("Caminho do banco:"+URI+colPath);
						 String query = null;
						 query = "	declare namespace us-gaap='"+this.usgaapPrefix+"'; "
								+  "declare namespace dei='"+this.deiPrefix+"'; "
								+  "declare namespace fb='"+this.fbPrefix+"'; "
								+  "declare namespace msft='"+this.msftPrefix+"'; "
								+  "declare namespace bobs='"+this.bobsPrefix+"'; "
								+  "declare namespace invest='"+this.investPrefix+"'; "
								/*
								+  "declare namespace exch ='"+this.exch+"'; " 
			      				+  "declare namespace country='"+this.country+"'; " 
			      				+  "declare namespace currency='"+this.currency+"'; "
			      				+  "declare namespace ustypes='"+this.ustypes+"'; "
			      				*/
				    		    +  "distinct-values(doc('"+filename+"')//"+ele+"[contains(@contextRef,'"+ano+quadrante+"') and contains(@unitRef,'usd')])";
								
					 //}else{
						// query = "distinct-values(doc('"+filename+"')//"+ele+"[contains(@contextRef,'"+this.ano+"') and contains(@unitRef,'usd')])";
					 //}
				     if (j < 10){
				    	 System.out.println(query);
				     }
				     j++;
					 result = xpqs.query(query);
					 i = result.getIterator();
					 while(i.hasMoreResources()) {
						 res = i.nextResource();
						 eles.add(String.valueOf(res.getContent()));
					 }
					 xpqs = null;
					 result =null;
					 i = null;
					 res = null;
					 col.close(); 
				 }		

			}catch(Exception e){
				System.out.println("Erro Xlink3 getValueDoc: "+e.getMessage());
			}finally {
				 if(col != null) {
				   try { 
				       	col.close(); 
				   } catch(Exception xe) {
				       	xe.printStackTrace();
				   }
				 }
			}	        
				return eles;
			    
	}
	public String getValueElement(String filename, String ele){
		System.out.println("Nomo do arquivo xml (getValueElement):"+filename);
	    String element = null;
		Collection col = null;
	    try {   
	 	   // initialize database driver
			   final String driver = "org.exist.xmldb.DatabaseImpl";
			   //System.out.println("Driver: "+driver);
			   Class cl = Class.forName(driver);
			   Database database = (Database) cl.newInstance();
			   database.setProperty("create-database", "true");
			   DatabaseManager.registerDatabase(database);
		       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
		       String colPath = "/db";

		       col = DatabaseManager.getCollection(URI+colPath);
		       XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		       xpqs.setProperty("indent", "yes");
		       ResourceSet result = null;
		       ResourceIterator i = null;
		       Resource res = null;
				 //System.out.println("Caminho do banco:"+URI+colPath);
				 String query = null;
				 query = "	declare namespace us-gaap='"+this.usgaapPrefix+"'; "
						+  "declare namespace dei='"+this.deiPrefix+"'; "
						+  "declare namespace fb='"+this.fbPrefix+"'; "
						+  "declare namespace msft='"+this.msftPrefix+"'; "
						+  "declare namespace bobs='"+this.bobsPrefix+"'; "
						+  "declare namespace invest='"+this.investPrefix+"'; "
						/*
						+  "declare namespace exch ='"+this.exch+"'; " 
	      				+  "declare namespace country='"+this.country+"'; " 
	      				+  "declare namespace currency='"+this.currency+"'; "
	      				+  "declare namespace ustypes='"+this.ustypes+"'; "
	      				*/
		    		    //+  "distinct-values(doc('"+filename+".xml')//"+ele+"[contains(@contextRef,'"+ano+quadrante+"') and contains(@unitRef,'usd')])";
						+  "distinct-values(doc('"+filename+".xml')//"+ele+"[contains(@contextRef,'"+ano+quadrante+"') ])";
			 //}else{
				// query = "distinct-values(doc('"+filename+"')//"+ele+"[contains(@contextRef,'"+this.ano+"') and contains(@unitRef,'usd')])";
			 //}
			 //System.out.println(query);
			 result = xpqs.query(query);
			 i = result.getIterator();
			 if(i.hasMoreResources()) {
				 res = i.nextResource();
				 element=String.valueOf(res.getContent());
			 }
			 if (element == null || element.equals(null) || element == "" || element.equals("")){
				 i = null;
				 res = null;
				 query = "	declare namespace us-gaap='"+this.usgaapPrefix+"'; "
							+  "declare namespace dei='"+this.deiPrefix+"'; "
							+  "declare namespace fb='"+this.fbPrefix+"'; "
							+  "declare namespace msft='"+this.msftPrefix+"'; "
							+  "declare namespace bobs='"+this.bobsPrefix+"'; "
							+  "declare namespace invest='"+this.investPrefix+"'; "
							/*
							+  "declare namespace exch ='"+this.exch+"'; " 
		      				+  "declare namespace country='"+this.country+"'; " 
		      				+  "declare namespace currency='"+this.currency+"'; "
		      				+  "declare namespace ustypes='"+this.ustypes+"'; "
		      				*/
			    		    //+  "distinct-values(doc('"+filename+".xml')//"+ele+"[contains(@contextRef,'"+ano+quadrante+"') and contains(@unitRef,'usd')])";
							+  "distinct-values(doc('"+filename+".xml')//"+ele+"[contains(@contextRef,'"+ano+"') ])";
				 result = xpqs.query(query);
				 i = result.getIterator();
				 if(i.hasMoreResources()) {
					 res = i.nextResource();
					 element=String.valueOf(res.getContent());
				 }
			 }
			 
			 System.out.println("Query (xlink3.getValueElement):"+query);
			 xpqs = null;
			 result =null;
			 i = null;
			 res = null;
			 col.close(); 
	    }catch(Exception e){
	    	System.out.println("Erro Xlink3 getValueDoc: "+e.getMessage());
	    }finally {
		 if(col != null) {
		   try { 
		       	col.close(); 
		   } catch(Exception xe) {
		       	xe.printStackTrace();
		   }
		 }
	}	        
		return element;
	}
	public String getElementByLabel(String filename, String label){
		System.out.println("Nomo do arquivo xml:"+filename);
	    String element = null;
		Collection col = null;
	    try {   
	 	   // initialize database driver
			   final String driver = "org.exist.xmldb.DatabaseImpl";
			   //System.out.println("Driver: "+driver);
			   Class cl = Class.forName(driver);
			   Database database = (Database) cl.newInstance();
			   database.setProperty("create-database", "true");
			   DatabaseManager.registerDatabase(database);
		       //String colPath = "/db/"+this.empresaComp+"/"+this.ano+"/"+this.quadrante;
		       String colPath = "/db";

		       col = DatabaseManager.getCollection(URI+colPath);
		       XPathQueryService xpqs = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		       xpqs.setProperty("indent", "yes");
		       ResourceSet result = null;
		       ResourceIterator i = null;
		       Resource res = null;
				 //System.out.println("Caminho do banco:"+URI+colPath);
				 String query = null;
				 query = 	"declare namespace link='http://www.xbrl.org/2003/linkbase'; "
				 			+ "declare namespace xlink='http://www.w3.org/1999/xlink'; "
				 			+ "distinct-values(doc('"+filename+"_lab.xml')//link:linkbase/"
				 			+ "link:labelLink/link:label[contains(text(),'"+label+"')]/@xlink:label)";
				 System.out.println(query);
			 result = xpqs.query(query);
			 i = result.getIterator();
			 if(i.hasMoreResources()) {
				 res = i.nextResource();
				 element=String.valueOf(res.getContent());
			 }
			 xpqs = null;
			 result =null;
			 i = null;
			 res = null;
			 col.close(); 
	    }catch(Exception e){
	    	System.out.println("Erro Xlink3 getValueDoc: "+e.getMessage());
	    }finally {
		 if(col != null) {
		   try { 
		       	col.close(); 
		   } catch(Exception xe) {
		       	xe.printStackTrace();
		   }
		 }
	}	    
	    /*
	    System.out.println("Elemento do Xlink3 (antes):"+element);
	    
	    if (!element.equals(null)){
	    */  
	    		System.out.println("Elemento (Xlink3 linha 639):"+element);
		    	String[] tkEle = element.split("us-gaap");
			    if (!tkEle[0].equals(null)){
			    	element = "us-gaap:";
			    	tkEle = tkEle[1].split("_");
			    	element += tkEle[1];
			    }

		  /*
		    }else{
		    	tkEle = element.split("fb");
		    	if (!tkEle[0].equals(null)){
			    	element = "us-gaap:";
			    	tkEle = tkEle[1].split("_");
			    	element += tkEle[1];
			    }else{
			    	tkEle = element.split("msft");
			    	if (!tkEle[0].equals(null)){
				    	element = "us-gaap:";
				    	tkEle = tkEle[1].split("_");
				    	element += tkEle[1];
			    	}
			    }
		    }
	    }else{
	    	element = "Não existe!";
	    }
	    System.out.println("Elemento do Xlink3(depois):"+element);
	    */
		return element;
	}
	
	}

