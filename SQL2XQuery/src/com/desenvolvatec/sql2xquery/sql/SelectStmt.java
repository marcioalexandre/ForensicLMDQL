package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.Collection;


public class SelectStmt {

    SelectClause columnItemList = null;
    FromClause fromClause = null;

    /**
     * @attribute
     */
    WhereClause whereClause = null;
    GroupByClause groupByClause = null;
    HavingClause havingClause = null;
    OrderByClause orderByClause = null;
    String query = null;
    WhereClause whereClause1;


    public SelectStmt(String newQuery) throws QueryExpressionException {
        this.query = newQuery;
        removeDoubleWhiteSpaces();
        try {
            this.columnItemList = new SelectClause(this.query);
            this.fromClause = new FromClause(this.query);
        } catch (NoTokenFoundException e) {
            throw new QueryExpressionException("SELECT and/or FROM clause not found");
        }
        try {
            this.whereClause = new WhereClause(this.query);
        } catch (NoTokenFoundException e) { }
        try {
            this.groupByClause = new GroupByClause(this.query);
        } catch (NoTokenFoundException e) { }
        try {
            this.havingClause = new HavingClause(this.query);
        } catch (NoTokenFoundException e) { }
        try {
            this.orderByClause = new OrderByClause(this.query);
        } catch (NoTokenFoundException e) { }
    }
    
    SelectStmt(SelectStmt select) {
        this.columnItemList = select.columnItemList;
        this.fromClause = select.fromClause;
        this.whereClause = select.whereClause;
        this.groupByClause = select.getGroupByClause();
        this.havingClause = select.havingClause;
        this.orderByClause = select.orderByClause;
    }

    void removeDoubleWhiteSpaces() throws QueryExpressionException {
        //  Remove espa�os brancos em excesso na query (a menos que estejam entre aspas simples (string)),
        // bem como quebra de linhas existentes (\n) ou eventuais tabs (\t).
        //  N�o considera que existem espa�os em branco dentro de nome de campos ou tabelas.
        this.query = this.query.replace("\n"," ");  this.query = this.query.replace("\t"," "); // tira todos os enters e tabs
        String newQuery = ""; boolean isString = false, isWhiteSpace = false;
        for(int i=0;i<this.query.length();i++) {
            String caracter = query.substring(i,i+1);
            if(caracter.equals("'")) // in�cio ou fim de string na query
                isString = !isString;
            if(!isString) { // n�o � string (n�o est� entre aspas simples - ' )
                if(caracter.equals(" ")&&isWhiteSpace) // espa�o em branco duplicado
                    caracter = "";
                else if(caracter.equals(" ")&&!isWhiteSpace) // primeiro espa�o em branco
                    isWhiteSpace = true;
                else // n�o � espa�o em branco
                    isWhiteSpace = false;
                caracter = caracter.toLowerCase(); // transformando os tokens e cl�usulas (exceto o conte�do das strings) em min�sculos
            }
            newQuery = newQuery + caracter;
        }
        if(isString) // n�o era para ter string ao final, pois todos os '' j� deveriam estar fechados
            throw new QueryExpressionException("Unclosed strings in query: "+this.query);
        this.query = newQuery.trim(); // query pronta para ser trabalhada
    }
    
    
    public SelectClause getColumnItemList() {
        return columnItemList;
    }

    public FromClause getFromClause() {
        return fromClause;
    }

    public WhereClause getWhereClause() {
        return whereClause;
    }

    public GroupByClause getGroupByClause() {
        return groupByClause;
    }

    public HavingClause getHavingClause() {
        return havingClause;
    }

    public OrderByClause getOrderByClause() {
        return orderByClause;
    }

    public String getQuery() {
        return query;
    }

}


