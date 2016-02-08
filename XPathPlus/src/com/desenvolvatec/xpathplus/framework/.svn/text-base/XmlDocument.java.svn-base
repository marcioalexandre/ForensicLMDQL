package com.desenvolvatec.xpathplus.framework;

import com.desenvolvatec.xpathplus.xmldb.DB2;
import com.desenvolvatec.xpathplus.xmldb.StartPoint;
import com.desenvolvatec.xpathplus.xmldb.XMLUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.sql.SQLException;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;


public class XmlDocument{

	private Document docDom;
	private NodeList nodeList;
	private Vector<Arc> arcs =  new Vector<Arc>();
	private Vector referencedDocumentsList =  new Vector();
	private String documentName;
	private String documentPath;
        private String configFileName;
		
	public XmlDocument(String path, String file, int fileType, String configFileName) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
            // fileType - identifica se é de instância (0), linkbase (1) ou de operator definition (2) #Werther
            // isso é irrelevante para arquivo avulso
            this.documentName = file;
            this.documentPath = path;
            this.configFileName = configFileName;
            this.docDom = getDOM(path,file,fileType,configFileName);
            this.nodeList = this.docDom.getDocumentElement().getElementsByTagName("*");
	}
        
        public static Document getDOM(String path, String file, int fileType, String configFileName) throws ParserConfigurationException,SAXException, IOException,SQLException,ClassNotFoundException {
            // devolve o documento DOM baseado no endereço do arquivo (ou expressão xquery) #Werther
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            if(file.indexOf("xquery:")!=-1 && file.substring(0,7).equalsIgnoreCase("xquery:")) {// proveniente de banco XML com acesso por xquery #Werther
                if(configFileName==null) // não era nem para entrar aqui, pois xquery só com arquivo de configuração
                    throw new IOException("Unknown config file: null");
                File configFile = new File(configFileName); // vendo arquivo de configuração - qual o banco XML
                String content = null, xquery = file.substring(7);
                // executando a xquery para pegar o conteúdo do arquivo
                if(XMLUtil.xmlDb(configFile,fileType).equals("DB2"))
                    content = new DB2(xquery,configFile,fileType).getResultText(); // só DB2 por enquanto
                //
                InputStream contentStream = new ByteArrayInputStream(content.getBytes("UTF-8")); // compatibilizando com o cabeçalho do XML
                return builder.parse(contentStream);
            } else // proveniente de arquivo avulso
                return builder.parse(path+file);
        }
        
	public String getDocumentName(){
            return this.documentName;
	}

	public NodeList getNodeList (){
            return this.nodeList;
	}
	
	public Document getDocDom (){
            return this.docDom;
	}
	
	public Vector getReferencedDocumentsList() throws ParserConfigurationException,SAXException,IOException,ClassNotFoundException {
            // acréscimo de tratamento de identificação de xquery #Werther
            boolean isFirstDocument = true;
            for (int i=0; i<this.nodeList.getLength(); i++){
                if(nodeList.item(i).getAttributes().getNamedItem("xlink:href") != null){
                    if (nodeList.item(i).getAttributes().getNamedItem("xlink:href").getNodeValue().contains(".xml") // pode ser xml ou
                            || nodeList.item(i).getAttributes().getNamedItem("xlink:href").getNodeValue().contains(".xsd") // pode ser xsd ou
                            || nodeList.item(i).getAttributes().getNamedItem("xlink:href").getNodeValue().contains("xquery:")){ // pode ser uma expressão xquery #Werther
                        String hrefNodeValue = nodeList.item(i).getAttributes().getNamedItem("xlink:href").getNodeValue().trim();
                        boolean isXquery = hrefNodeValue.contains("xquery:");
                        if(isXquery && hrefNodeValue.contains("#")) // incorporar a teste de utilização do # quando xquery para pegar o nome real do documento referenciado #Werther
                            hrefNodeValue = hrefNodeValue.substring(0,hrefNodeValue.indexOf("#"));

                        String referencedDocumentName = isXquery?hrefNodeValue:hrefNodeValue.substring(0, hrefNodeValue.lastIndexOf(".")+4);
                        boolean added = false;
                        if(isFirstDocument){
                            // considera arquivo de instância apenas o primeiro
                            try { // o documeto pode estar com uma referência equivocada
                                XmlDocument referencedDocument =  isXquery?  new XmlDocument(this.documentPath,hrefNodeValue,StartPoint.TAXONOMY,this.configFileName):
                                                                            new XmlDocument(this.documentPath,hrefNodeValue.substring(0, hrefNodeValue.lastIndexOf(".")+4),0,this.configFileName);
                                this.referencedDocumentsList.add(referencedDocument);
                                isFirstDocument = false;
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int j=0;referencedDocumentsList.size()>j;j++){
                            if(((XmlDocument)referencedDocumentsList.get(j)).getDocumentName().equalsIgnoreCase(referencedDocumentName)){
                                added = true;
                            }
                        }
                        if (!added){
                            try { // o documeto pode estar com uma referência equivocada
                                XmlDocument referencedDocument = isXquery?  new XmlDocument(this.documentPath,hrefNodeValue,StartPoint.TAXONOMY,this.configFileName):
                                                                            new XmlDocument(this.documentPath,hrefNodeValue.substring(0, hrefNodeValue.lastIndexOf(".")+4),0,this.configFileName);
                                this.referencedDocumentsList.add(referencedDocument);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return this.referencedDocumentsList;
	}
	
	//Busca todos os arcos (origem e destino)
	private Vector<Arc> getNonRefinedArcs(){
            Vector<Arc> arcsList = new Vector<Arc>();
            for (int i=0; i < nodeList.getLength(); i++){
                if (nodeList.item(i).getAttributes().getNamedItem("xlink:type") != null){
                    if ("arc".equalsIgnoreCase(nodeList.item(i).getAttributes().getNamedItem("xlink:type").getNodeValue())){
                        Arc arc = new Arc();
                        arc.setNode(nodeList.item(i));
                        arc.setLabelFrom(nodeList.item(i).getAttributes().getNamedItem("xlink:from").getNodeValue());
                        arc.setLabelTo(nodeList.item(i).getAttributes().getNamedItem("xlink:to").getNodeValue());
                        if (nodeList.item(i).getAttributes().getNamedItem("xlink:arcrole") != null){
                            arc.setArcRole(nodeList.item(i).getAttributes().getNamedItem("xlink:arcrole").getNodeValue());
                        }	
                        arc.setDocument(this.documentName);
                        // vai procurar os nodos que compôem o arco - estão no mesmo documento #Werther
                        for(int j=0;j<nodeList.getLength();j++){ 
                            // procura o FROM
                            if(nodeList.item(j).getAttributes().getNamedItem("xlink:label")!=null &&
                               nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue().trim().equalsIgnoreCase(arc.getLabelFrom()) &&
                                (nodeList.item(j).getAttributes().getNamedItem("xlink:type")!=null && (
                                 nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue().equalsIgnoreCase("locator") ||
                                 nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue().equalsIgnoreCase("resource"))))
                                arc.addNodeFrom(nodeList.item(j));
                            // procura o TO
                            if(nodeList.item(j).getAttributes().getNamedItem("xlink:label")!=null &&
                               nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue().trim().equalsIgnoreCase(arc.getLabelTo()) &&
                                (nodeList.item(j).getAttributes().getNamedItem("xlink:type")!=null && (
                                 nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue().equalsIgnoreCase("locator") ||
                                 nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue().equalsIgnoreCase("resource"))))
                                arc.addNodeTo(nodeList.item(j));
                        }
                        //
                        arcsList.add(arc);
                    }
                }
            }
            return arcsList;
	}
	
	//Busca todos os arcos (origem e destino) e devolve o ID ou Name
	public Vector<Arc> getArcs(Document instanceDocument) throws ParserConfigurationException {
            Vector<Arc> arcsList = new Vector<Arc>();
            String elementSource = null;
            String elementID = null;
            arcsList = this.getNonRefinedArcs();
            for (int i=0; i<arcsList.size(); i++){
                for (int j=0; j<nodeList.getLength(); j++){
                    if (nodeList.item(j).getAttributes().getNamedItem("xlink:label") != null){	
                    }
                    if ((nodeList.item(j).getAttributes().getNamedItem("xlink:href")!=null) 
                            && (nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue().contains("#"))){
                        elementID = nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue().substring(nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue().lastIndexOf("#")+1,
                                            nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue().length());	
                        //Obtem local de definicao do elemento
                        if (nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue()
                                .substring(0,nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue().lastIndexOf("#")+1).length()>=4){
                            elementSource = nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue()
                            .substring(0, nodeList.item(j).getAttributes().getNamedItem("xlink:href").getNodeValue().lastIndexOf("#"));
                        } else {
                            elementSource = arcsList.get(i).getDocument();
                        }
                        if ((nodeList.item(j).getAttributes().getNamedItem("xlink:type")!=null)
                                && (nodeList.item(j).getAttributes().getNamedItem("xlink:label")!=null)
                                && "locator".equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue()) 
                                && arcsList.get(i).getLabelFrom().equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue())){
                            arcsList.get(i).setLabelFrom(this.getElementRealName(elementSource, elementID, instanceDocument));
                        }
                        if ((nodeList.item(j).getAttributes().getNamedItem("xlink:type")!=null) && (nodeList.item(j).getAttributes().getNamedItem("xlink:label")!=null)
                                && "locator".equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue()) 
                                && arcsList.get(i).getLabelTo().equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue())){	
                            arcsList.get(i).setLabelTo(this.getElementRealName(elementSource, elementID, instanceDocument));
                        }
                    }
                    if ((nodeList.item(j).getAttributes().getNamedItem("xlink:type")!=null)&& (nodeList.item(j).getAttributes().getNamedItem("xlink:label")!=null)
                            && "resource".equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue()) 
                            && arcsList.get(i).getLabelFrom().equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue())){
                        if (nodeList.item(j).getAttributes().getNamedItem("id") != null){
                            arcsList.get(i).setLabelFrom(nodeList.item(j).getAttributes().getNamedItem("id").getNodeValue());
                        } else {
                            arcsList.get(i).setLabelFrom(nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue());
                        }
                    }
                    if ((nodeList.item(j).getAttributes().getNamedItem("xlink:type")!=null)&& (nodeList.item(j).getAttributes().getNamedItem("xlink:label")!=null)
                            && "resource".equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:type").getNodeValue()) 
                            && arcsList.get(i).getLabelTo().equalsIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue())){
                        if (nodeList.item(j).getAttributes().getNamedItem("id") != null){
                            arcsList.get(i).setLabelTo(nodeList.item(j).getAttributes().getNamedItem("id").getNodeValue());
                        } else {
                            arcsList.get(i).setLabelTo(nodeList.item(j).getAttributes().getNamedItem("xlink:label").getNodeValue());
                        }
                    }
                }
                arcs.add((Arc)arcsList.get(i));
            }
            return arcs;
	}
	
	private String getElementRealName(String elementSource, String elementID, Document instanceDocument) throws ParserConfigurationException {
            String elementRealName = "";
            String targetNamespace = "";
            String prefix = "";
            try{
                Document sourceFile = getDOM(this.documentPath,elementSource,StartPoint.TAXONOMY,this.configFileName); // SERÁ QUE PODE SER INSTANCE?? #Werther
                NodeList sourceNodeList = sourceFile.getDocumentElement().getElementsByTagName("*");
                if (sourceFile.getDocumentElement().getAttributes().getNamedItem("targetNamespace") != null){
                        targetNamespace = this.getTargetNamespace(sourceFile.getDocumentElement());
                        prefix = this.getElementPrefix(instanceDocument, targetNamespace);
                }
                for (int i=0; i<sourceNodeList.getLength(); i++){
                    if (sourceNodeList.item(i).getAttributes().getNamedItem("id") != null){
                        if (sourceNodeList.item(i).getAttributes().getNamedItem("id").getNodeValue().equalsIgnoreCase(elementID)){
                            if(sourceNodeList.item(i).getAttributes().getNamedItem("name") != null){
                                    elementRealName = prefix + (prefix.equals("")?"":":") + sourceNodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();
                            } else {
                                    elementRealName = sourceNodeList.item(i).getNodeName();
                            }
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                throw new ParserConfigurationException("XML Document Error B: " + e.getMessage());
            }
            return elementRealName;
	}
	
	private String getTargetNamespace (Element rootElement){
		String targetNamespace = "";
		targetNamespace = rootElement.getAttributes().getNamedItem("targetNamespace").getNodeValue();
		return targetNamespace; 
	}
	
	private String getElementPrefix (Document instanceDocument, String targetNamespace){
		String prefix = "";
		for (int i=0; i<instanceDocument.getDocumentElement().getAttributes().getLength(); i++){
			Node attributeNode = instanceDocument.getDocumentElement().getAttributes().item(i);
			if (attributeNode.getNodeValue().contains(targetNamespace)){
				if (attributeNode.getNodeName().contains("xmlns")){
					prefix = attributeNode.getNodeName().
					substring((attributeNode.getNodeName().lastIndexOf(":"))+1, attributeNode.getNodeName().length());
				}
			}
		}
		return prefix;
	}

}
