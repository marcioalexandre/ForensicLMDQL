/*
package mondrian.lmdql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mondrian.olap.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.desenvolvatec.xpathplus.framework.Arc;
import com.desenvolvatec.xpathplus.framework.XPathPlusExecutor;

public class XLink {
	
	public static String queryLabelProcessed(String query){
	// Verifica se existem labels que não estão configuradas no MDX, 
	// mas referenciadas no label linkbase
		loadXpathplusConfigFile();
		if(query.trim().indexOf("WITH")==0) // é query pré-programada, não precisa tratamento
			return query;
		String newQuery = query;
		String[] nomes = getBracketNames(query);
		for(int i=0;i<nomes.length;i++){
			if(Parameters.xmlMdxFileContent.indexOf("["+nomes[i]+"]")==-1) {// não achou, vai tentar localizar equivalente na taxonomia de label
				String[] similar = getMatchLabel(nomes[i]); 
				newQuery = newQuery.replace("["+nomes[i]+"]", "["+similar[0]+"]"); // troca todos com o primeiro que achou
				newQuery = newQuery.replace("[All "+nomes[i]+"]", "[All "+similar[0]+"]"); // troca todos com o primeiro que achou
			}
		}
		return newQuery;
	}
	
	public static String[] getMatchLabel(String nome){
	// Encontra, nos arcos, nome(s) equivalente(s) ao fornecido, de acordo com o label linkbase
		ArrayList<String> equivNodeValue = new ArrayList<String>();
		try {
			if(Parameters.labelArcs==null){ // carrega os arcos de labels apenas se ainda não o fez
				XPathPlusExecutor xpathplus = new XPathPlusExecutor(Parameters.xmlXPathPlusFileName);
				Parameters.labelArcs = new ArrayList<Arc>(xpathplus.getArcs()); // obtém todos os arcos possíveis dos linkbases de label (ponto de partida)
			}
			// localizando arco (e conjunto associado) com o nome referenciado
			for(Arc arc: Parameters.labelArcs){
				for(Node nodeTo: arc.getNodesTo()) { // o nome está no nodesTo
					String nodeValue = nodeTo==null?null:(nodeTo.getTextContent()==null?null:nodeTo.getTextContent().trim());
					if(nodeValue!=null && nodeValue.equals(nome)){ // achou o nó com o valor do nome
						for(Node nodeFrom: arc.getNodesFrom()){ // pegando o href que está em nomesFrom
							String href = nodeFrom.getAttributes().getNamedItem("xlink:href").getNodeValue();
							if(href!=null){ // achou href 
								for(Arc otherArc: Parameters.labelArcs){ // verificando outros nodeFrom com o mesmo href
									for(Node otherNodeFrom: otherArc.getNodesFrom()) { // o novo nome está no nodesTo
										if(href.equals(otherNodeFrom.getAttributes().getNamedItem("xlink:href").getNodeValue())){ // achou outro href
											for(Node otherNodeTo: otherArc.getNodesTo()){ // procurando o nome nos nodeTo correspondentes
												String otherNodeValue = otherNodeTo==null?null:(otherNodeTo.getTextContent()==null?null:otherNodeTo.getTextContent().trim());
												if(otherNodeValue!=null && 
													(Parameters.xmlMdxFileContent.indexOf("\""+otherNodeValue+"\"")!=-1 || // está no mdx file
													 Parameters.xmlMdxFileContent.indexOf("\"All "+otherNodeValue+"\"")!=-1 ||
													 Parameters.xmlMdxFileContent.indexOf("["+otherNodeValue+"]")!=-1 ||
													 Parameters.xmlMdxFileContent.indexOf("[All "+otherNodeValue+"]")!=-1))
													equivNodeValue.add(otherNodeValue);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(equivNodeValue.size()==0) // se não achou nada, adiciona somente o que veio
				equivNodeValue.add(nome);
			return  equivNodeValue.toArray(new String[equivNodeValue.size()]); 
		} catch (Exception e) {
			return new String[]{nome}; // em caso de erro, retorna o que veio
		}
	}

	public static String[] getBracketNames(String query) {
	// Obtem todos os nomes passíveis de troca de label - identificados por estar entre [] ou entre "[All " e "]"
		ArrayList<String> nomes = new ArrayList<String>();
		// Para [Nome]
		for(int pos=query.indexOf("[");pos>=0;pos = query.indexOf("[",pos+1)){
			int posFinal = query.indexOf("]",pos);
			if(posFinal>=0){
				String name = query.substring(pos+1,posFinal);
				boolean existe = false;
				for(String nome : nomes)
					if(nome.equals(name)) {
						existe = true;
						break;
					}
				if(!existe) nomes.add(name);
			}
		} 
		// Para [All Nomes]
		for(int pos=query.indexOf("[All ");pos>=0;pos = query.indexOf("[All ",pos+1)){
			int posFinal = query.indexOf("]",pos);
			if(posFinal>=0){
				String name = query.substring(pos+5,posFinal);
				boolean existe = false;
				for(String nome : nomes)
					if(nome.equals(name)) {
						existe = true;
						break;
					}
				if(!existe) nomes.add(name);
			}
		}
		if(nomes.size()==0)	
			return null;
		return nomes.toArray(new String[nomes.size()]);
	}
	
	public static void loadXpathplusConfigFile(){
		// definindo o file name do xpathplus.xml
		if(Parameters.xmlXPathPlusFileName==null)
        	Parameters.xmlXPathPlusFileName = Parameters.pathServer+File.separator+"/WEB-INF/lib/xpathplus.xml";
		if(Parameters.xmlXPathPlusFileDoc==null){
			try{
		        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		        DocumentBuilder db = dbf.newDocumentBuilder();
		        Parameters.xmlXPathPlusFileDoc = db.parse(new File(Parameters.xmlXPathPlusFileName)); // gerando document
			} catch(Exception e){
				System.out.println("Error while loading xpathplus config file: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
*/