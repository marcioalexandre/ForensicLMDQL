package com.desenvolvatec.xpathplus.framework;

import com.desenvolvatec.xpathplus.xmldb.StartPoint;
import com.desenvolvatec.xpathplus.xmldb.XMLUtil;

import java.io.File;
import java.io.IOException;

import java.sql.SQLException;

import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XPathPlusExecutor {

    private Vector documentSet = null;
    private Context context = null;
    private boolean filePrint = true;
    private boolean arcsFilter = true;
    private int base = StartPoint.INSTANCE;
    private Vector<Arc> arcs = null;
    private String configFileName = null;;
    private StartPoint startpoint = null;
    
    public XPathPlusExecutor(String configFileName) throws ParserConfigurationException, SAXException, IOException, SQLException, ClassNotFoundException {
        this.configFileName = configFileName;
        if(this.configFileName!=null){
            this.startpoint = XMLUtil.getStartPoint(new File(this.configFileName));
            if(this.startpoint!=null){
                this.arcsFilter = this.startpoint.isFilter();
                this.filePrint = this.startpoint.isPrint();
                this.base = this.startpoint.getBase();
            } else {
                this.filePrint = true;
                this.arcsFilter = true;
            }
        }
        this.context = this.filePrint?new Context():null;
        if(this.startpoint!=null) execute(this.startpoint.getExpression());
    }
    
    public XPathPlusExecutor(String command,boolean arcsFilter,boolean filePrint) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        this.filePrint = filePrint;
        this.arcsFilter = arcsFilter;
        this.context = this.filePrint?new Context():null;
        execute(command);
    }
    
    public XPathPlusExecutor(String command,boolean arcsFilter,boolean filePrint,int base) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        this.filePrint = filePrint;
        this.arcsFilter = arcsFilter;
        this.base = base;
        if(this.base==StartPoint.OPERATOR_DEFINITION){
            this.configFileName = command; // neste caso, o command será antes o path do configFile
            command = XMLUtil.getCommandOperatorDefinition(new File(this.configFileName)); 
        }
        this.context = this.filePrint?new Context():null;
        execute(command);
    }
    
//	Recebe a entrada (input) e envia ao parser para que este determine qual o tipo do comando (CommandUse ou CommandPath)
    public void execute(String command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        Parser parser = new Parser();
        parser.setCommand(command.trim());
        this.execute(parser.getCommand());
    }

//	Decide qual Execute usar, a depender do tipo de comando
    public void execute(Command command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        if (command.getClass() == CommandAllArcs.class){
            this.execute((CommandAllArcs)command);
        } else {
            if (command.getClass() == CommandXPathPlus.class) {
                this.execute((CommandXPathPlus)command);
            } else {
                if (command.getClass() == CommandXPath.class){
                    this.execute((CommandXPath)command);
                }
            }
        }
    }

//	Todos os arcos: Gera o resultado a partir de um objeto da classe Documento, 
//              	explicitando as relacoes entre os arcos em forma de Strings
    public void execute(CommandAllArcs command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        if(this.filePrint){
            this.context.addResult("Using file " + command.getFile());
            this.context.addResult("");
        }
        Expansor expansor = this.getDTS(command);
        Vector<Arc> documentArcs = new Vector<Arc>();
        Vector<Arc> filteredArcs = new Vector<Arc>();
        for (int i=0; documentSet.size() > i ;i++){
            documentArcs.clear();
            filteredArcs.clear();
            if(this.filePrint) this.context.addResult("Arcs from "+((XmlDocument)documentSet.get(i)).getDocumentName()+": ");
            documentArcs = ((XmlDocument)documentSet.get(i)).getArcs(expansor.getInicialDocument(this.base));
            filteredArcs = expansor.getFilteredArcs(documentArcs,this.arcsFilter);
            for (int j=0; filteredArcs.size() > j; j++){
                Arc arc = filteredArcs.get(j); 
                if(this.filePrint)
                    this.context.addResult("Exist arc between " + arc.getLabelFrom() + " (" + arc.getNodesFrom().size() + ") and " +
                                                                  arc.getLabelTo()   + " (" + arc.getNodesTo().size() + ")");
            }
            if(this.filePrint) this.context.addResult("");
            addArcs(filteredArcs);
        }
    }
            
//	Arcos de um elemento especifico
    public void execute(CommandXPathPlus command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        if(this.filePrint){
            this.context.addResult("Using file: " + command.getFile());
            this.context.addResult("Using element: " + command.getElement());
            this.context.addResult("Using type: " + command.getType());
        }
        if ("specificArc".equalsIgnoreCase(command.getType())){
            this.specificArc(command);
        } else if ("sourceArc".equalsIgnoreCase(command.getType())) {
            this.sourceArc(command);
        } else if ("destinationArc".equalsIgnoreCase(command.getType())){
            this.destinationArc(command);
        }
    }
    
//	Comando XPath: Passa para o comando para o Evaluator, recebendo uma lista de ns 
//                 com os elementos resultantes da expressao
//	Problema na hora de retornar o resultado... O que imprimir?
    public void execute(CommandXPath command){
        if(this.filePrint){
            this.context.addResult("Using file: " + command.getFile());
            this.context.addResult("Using expression: " + command.getExpression());
        }
        try{
            XPath xpath = XPathFactory.newInstance().newXPath();
            InputSource inputSource = new InputSource(command.getFile());
            NodeList xpathEvaluationResult = (NodeList) xpath.evaluate(command.getExpression(), inputSource, XPathConstants.NODESET);
            for (int i = 0; xpathEvaluationResult.getLength()>i; i++){
                if(this.filePrint) this.context.addResult("Content: " + xpathEvaluationResult.item(i).toString());
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("XPath expression error - execute(CommandXPath): " + e.getMessage());
        }
    }
    
    public Context getContext(){
        return this.context;
    }
    
    public void specificArc(CommandXPathPlus command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        if(this.filePrint){
            this.context.addResult("Using file " + command.getFile());
            this.context.addResult("");
        }
        Expansor expansor = this.getDTS(command);
        Vector<Arc> documentArcs = new Vector<Arc>();
        Vector<Arc> filteredArcs = new Vector<Arc>();
        for (int i=0; documentSet.size() > i ;i++){
            documentArcs.clear();
            filteredArcs.clear();
            if(this.filePrint) this.context.addResult("Arcs from "+((XmlDocument)documentSet.get(i)).getDocumentName()+": ");
            documentArcs = ((XmlDocument)documentSet.get(i)).getArcs(expansor.getInicialDocument(this.startpoint==null?StartPoint.INSTANCE:this.startpoint.getBase()));
            filteredArcs = expansor.getFilteredArcs(documentArcs,this.arcsFilter);
            for (int j=0; filteredArcs.size() > j; j++){
                Arc arc = filteredArcs.get(j); 
                if (arc.getLabelFrom().equalsIgnoreCase(command.getElement())||(arc.getLabelTo().equalsIgnoreCase(command.getElement()))){
                    if(this.filePrint)
                        this.context.addResult("Exist arc between " + arc.getLabelFrom() + " (" + arc.getNodesFrom().size() + ") and " +
                                                                      arc.getLabelTo()   + " (" + arc.getNodesTo().size() + ")");
                }
            }
            if(this.filePrint) this.context.addResult("");
            addArcs(filteredArcs);
        }
    }
    
    public void sourceArc(CommandXPathPlus command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        if(this.filePrint){
            this.context.addResult("Using file " + command.getFile());
            this.context.addResult("");
        }
        Expansor expansor = this.getDTS(command);
        Vector<Arc> documentArcs = new Vector<Arc>();
        Vector<Arc> filteredArcs = new Vector<Arc>();
        for (int i=0; documentSet.size() > i ;i++){
            documentArcs.clear();
            filteredArcs.clear();
            if(this.filePrint) this.context.addResult("Arcs from "+((XmlDocument)documentSet.get(i)).getDocumentName()+": ");
            documentArcs = ((XmlDocument)documentSet.get(i)).getArcs(expansor.getInicialDocument(this.startpoint==null?StartPoint.INSTANCE:this.startpoint.getBase()));
            filteredArcs = expansor.getFilteredArcs(documentArcs,this.arcsFilter);
            for (int j=0; filteredArcs.size() > j; j++){
                Arc arc = filteredArcs.get(j); 
                if (arc.getLabelFrom().equalsIgnoreCase(command.getElement())){
                    if(this.filePrint)
                        this.context.addResult("Exist arc between " + arc.getLabelFrom() + " (" + arc.getNodesFrom().size() + ") and " +
                                                                      arc.getLabelTo()   + " (" + arc.getNodesTo().size() + ")");
                }
            }
            if(this.filePrint) this.context.addResult("");
            addArcs(filteredArcs);
        }
    }
    
    public void destinationArc(CommandXPathPlus command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        if(this.filePrint){
            this.context.addResult("Using file " + command.getFile());
            this.context.addResult("");
        }
        Expansor expansor = this.getDTS(command);
        Vector<Arc> documentArcs = new Vector<Arc>();
        Vector<Arc> filteredArcs = new Vector<Arc>();
        for (int i=0; documentSet.size() > i ;i++){
            documentArcs.clear();
            filteredArcs.clear();
            if(this.filePrint) this.context.addResult("Arcs from "+((XmlDocument)documentSet.get(i)).getDocumentName()+": ");
            documentArcs = ((XmlDocument)documentSet.get(i)).getArcs(expansor.getInicialDocument(this.startpoint==null?StartPoint.INSTANCE:this.startpoint.getBase()));
            filteredArcs = expansor.getFilteredArcs(documentArcs,this.arcsFilter);
            for (int j=0; filteredArcs.size() > j; j++){
                Arc arc = filteredArcs.get(j); 
                if (arc.getLabelTo().equalsIgnoreCase(command.getElement())){
                    if(this.filePrint)
                        this.context.addResult("Exist arc between " + arc.getLabelFrom() + " (" + arc.getNodesFrom().size() + ") and " +
                                                                      arc.getLabelTo()   + " (" + arc.getNodesTo().size() + ")");
                }
            }
            if(this.filePrint) this.context.addResult("");
            addArcs(filteredArcs);
        }
    }
    
    private void addArcs(Vector<Arc> filteredArcs) {
        if(this.arcs==null) 
            this.arcs = new Vector<Arc>();
        this.arcs.addAll(filteredArcs);
    }

    private Expansor getDTS(Command command) throws ParserConfigurationException,SAXException,IOException,SQLException,ClassNotFoundException {
        Expansor expansor = new Expansor(command.getPath(),command.getFile(),this.configFileName,this.startpoint==null?this.base:this.startpoint.getBase());
        documentSet = expansor.getDocumentSet();
        return expansor;
    }

    public Vector<Arc> getArcs() {
        return arcs;
    }

    public void setFilePrint(boolean filePrint) {
        this.filePrint = filePrint;
    }

    public boolean isFilePrint() {
        return filePrint;
    }

    public void setArcsFilter(boolean arcsFilter) {
        this.arcsFilter = arcsFilter;
    }

    public boolean isArcsFilter() {
        return arcsFilter;
    }

}
