package com.desenvolvatec.xpathplus.framework;

import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;


public class Expansor{
	private Vector documentSet = new Vector();
	private String instanceFile;
	private String instancePath;
	private XmlDocument instance;
        private String configFileName;
	
	public Expansor(String instancePath, String instanceFileName, String configFileName, int base) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
            this.instanceFile = instanceFileName;
            this.instancePath = instancePath;
            this.configFileName = configFileName;
            instance = new XmlDocument(this.instancePath,this.instanceFile,base,configFileName); // definindo instância, construindo a partir daí
	}
	
	public Vector getDocumentSet () throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
            documentSet.add(instance);
            for (int i=0; i<documentSet.size(); i++){
                Vector temp = new Vector();
                temp = ((XmlDocument)documentSet.get(i)).getReferencedDocumentsList();
                boolean contains;
                for(int j=0;temp.size()>j;j++){
                    contains = false;
                    for (int k=0;documentSet.size()>k;k++){
                        if (((XmlDocument)temp.get(j)).getDocumentName().equalsIgnoreCase(((XmlDocument)documentSet.get(k)).getDocumentName())){
                            contains = true;
                        }
                    }
                    if (!contains){
                        documentSet.add(temp.get(j));
                    }
                }
            }
            return documentSet;
	}
	
	public Document getInicialDocument (int base) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
            // base = 0 (instancia) ou 1 (taxonomia)
            // localizando e obtendo o documento de´início das buscas xpath+
            return XmlDocument.getDOM(this.instancePath,this.instanceFile,base,this.configFileName);
	}
	
	public Vector<Arc> getFilteredArcs (Vector<Arc> documentArcs,boolean applyFilter){
            // filtra pelos documentos do vetor
            //Melhorar metodo de comparacao (quicksort?)
            if(!applyFilter) return documentArcs;
            Vector<Arc> filteredArcs = new Vector<Arc>();
            boolean matchesFrom;
            boolean matchesTo;
            NodeList instanceNodeList = this.instance.getNodeList();
            for (int i=0; i<documentArcs.size(); i++){
                matchesFrom = false;
                matchesTo = false;
                for (int j=0; j<instanceNodeList.getLength(); j++){
                    if (documentArcs.get(i).getLabelFrom().equalsIgnoreCase(instanceNodeList.item(j).getNodeName())){
                        matchesFrom = true;
                    }
                    if (documentArcs.get(i).getLabelTo().equalsIgnoreCase(instanceNodeList.item(j).getNodeName())){
                        matchesTo = true;
                    }
                    if (instanceNodeList.item(j).getAttributes().getNamedItem("id") != null){
                        if (documentArcs.get(i).getLabelFrom().equalsIgnoreCase(instanceNodeList.item(j).getAttributes().getNamedItem("id").getNodeValue())){
                            matchesFrom = true;
                        }
                        if (documentArcs.get(i).getLabelTo().equalsIgnoreCase(instanceNodeList.item(j).getAttributes().getNamedItem("id").getNodeValue())){
                            matchesTo = true;
                        }
                    }
                }
                if (matchesFrom || matchesTo){
                    filteredArcs.add(((Arc)documentArcs.get(i)));
                }
            }
            return filteredArcs;
	}
}
