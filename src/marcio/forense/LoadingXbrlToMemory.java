/*
package marcio.forense;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.*;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class LoadingXbrlToMemory {
	public static void main(String[] args) throws JDOMException, IOException {
		//Aqui você informa o nome do arquivo XML.  
		File f = new File("xbrl/Serpro/serpro.xsd");  
		  
		//Criamos uma classe SAXBuilder que vai processar o XML4  
		SAXBuilder sb = new SAXBuilder();  
		  
		//Este documento agora possui toda a estrutura do arquivo.  
		Document d = sb.build(f);  
		  
		//Recuperamos o elemento root  
		Element mural = d.getRootElement();  
		  
		//Recuperamos os elementos filhos (children)  
		List elements = mural.getChildren();  
		Iterator i = elements.iterator();  
		  
		//Iteramos com os elementos filhos, e filhos do dos filhos  
		while (i.hasNext()) {  
		    Element element = (Element) i.next();  
		    System.out.println("Códido:"+ element.getAttributeValue("id"));  
		    System.out.println("Prioridade:"+ element.getAttributeValue("prioridade"));  
		    System.out.println("Para:"+ element.getChildText("para"));  
		    System.out.println("De:"+ element.getChildText("de"));  
		    System.out.println("Corpo:"+ element.getChildText("corpo"));  
		} 
	}

/* usando Jaxb	
	public static void main(String[] args) throws JAXBException {
		try{
			
			JAXBContext context = JAXBContext.newInstance("marcio.forense.xbrl");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			@SuppressWarnings("unchecked")
			JAXBElement<ElementoDAO> element = (JAXBElement<ElementoDAO>) unmarshaller.unmarshal(new File("xbrl/Serpro/serpro.xml"));
			@SuppressWarnings("unused")
			ElementoDAO ele = element.getValue();
			
			
			
		}catch(Exception e){
			System.out.println("Erro: "+e.getMessage());
		}
		

	}
*/
//}
