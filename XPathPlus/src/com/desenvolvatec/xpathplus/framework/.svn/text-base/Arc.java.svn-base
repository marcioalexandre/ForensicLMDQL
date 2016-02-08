package com.desenvolvatec.xpathplus.framework;

import java.util.Vector;

import org.w3c.dom.Node;


public class Arc {
    private String labelFrom;
    private String labelTo;
    private String role;
    private String document; //especifica em qual documento estah o arco
    private Node node; // nó (DOM) do arco
    private Vector<Node> nodesFrom = new Vector<Node>(); // nós (DOM) FROM do arco - mesmo documento - pode ter mais de um
    private Vector<Node> nodesTo = new Vector<Node>(); // nós (DOM) TO do arco - mesmo documento - pode ter mais de eum
    
    public Arc (){
    }
    
    public String getLabelFrom (){
            return this.labelFrom;
    }
    
    public void setLabelFrom (String value){
            this.labelFrom = value.trim();
    }
    
    public String getLabelTo (){
            return this.labelTo;
    }
    
    public void setLabelTo (String value){
            this.labelTo = value.trim();
    }
    
    public String getArcRole (){
            return this.role;
    }
    
    public void setArcRole (String value){
            this.role = value.trim();
    }
    
    public String getDocument (){
            return this.document;
    }
    
    public void setDocument (String value){
            this.document = value.trim();
    }

    public void setNodesFrom(Vector<Node> nodesFrom) {
        this.nodesFrom = nodesFrom;
    }

    public Vector<Node> getNodesFrom() {
        return nodesFrom;
    }

    public void setNodesTo(Vector<Node> nodesTo) {
        this.nodesTo = nodesTo;
    }

    public Vector<Node> getNodesTo() {
        return nodesTo;
    }

    public void setNodeTo(Node node,int pos) {
        this.nodesTo.set(pos,node);
    }
    
    public void setNodeFrom(Node node,int pos) {
        this.nodesFrom.set(pos,node);
    }
    
    public void addNodeTo(Node node) {
        this.nodesTo.add(node);
    }
    
    public void addNodeFrom(Node node) {
        this.nodesFrom.add(node);
    }
    
    public Node getNodeTo(int pos) {
        return this.nodesTo.get(pos);
    }

    public Node getNodeFrom(int pos) {
        return this.nodesFrom.get(pos);
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
