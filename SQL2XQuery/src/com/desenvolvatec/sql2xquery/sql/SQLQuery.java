package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.ArrayList;


public class SQLQuery {

    ArrayList<SelectStmt> selects = new  ArrayList<SelectStmt>();

    public SQLQuery(String newQuery,boolean conditionalExpressionDivision) throws QueryExpressionException {
        SelectStmt select = new SelectStmt(newQuery);
        int nDiv = divisionsNumber(select);
        if(conditionalExpressionDivision && nDiv>1) { // quer dividir e tem condi��es de dividir as express�es condicionais
            for(int i=0;select.havingClause!=null && i<select.havingClause.conditItemList.size();i++) // removendo todas as strings (s� tem parenteses e AND)
                if(select.havingClause.conditItemList.get(i) instanceof String)
                    select.havingClause.conditItemList.remove(i--); // removendo e voltando, pois o removido vai embora e o pr�ximo passa a ser o atual
            for(int i=0;select.whereClause!=null && i<select.whereClause.conditItemList.size();i++) // removendo todas as strings (s� tem parenteses e AND)
                if(select.whereClause.conditItemList.get(i) instanceof String)
                    select.whereClause.conditItemList.remove(i--); // removendo e voltando, pois o removido vai embora e o pr�ximo passa a ser o atual
            // se ficar "limpo" - nao � para acontecer - coloca null
            if(select.havingClause!=null && select.havingClause.conditItemList.size()==0 ) select.havingClause = null;
            if(select.whereClause!=null && select.whereClause.conditItemList.size()==0 ) select.whereClause = null;
            SelectStmt sel = new SelectStmt(select);
            sel.havingClause = new HavingClause(); sel.whereClause = new WhereClause();
            boolean conditionAdded = true;
            while (conditionAdded){
                conditionAdded = false;
                if(select.havingClause!=null){
                    for(int i=0;i<select.havingClause.conditItemList.size();i++){
                        Object cond = select.havingClause.conditItemList.get(i);
                        boolean columnsRelationship = false;
                        if(cond instanceof RelatComp){
                            RelatComp rc = (RelatComp)cond;
                            columnsRelationship = rc.constValue==null && rc.leftColumnItem.columItemType==ColumnItem.COLUMN &&
                                                  rc.rightColumnItem.columItemType==ColumnItem.COLUMN;
                            if(columnsRelationship) {
                                sel.havingClause.conditItemList.add(cond); // adicionando uma condi��o having
                                sel.havingClause.conditItemList.add("and"); // adicionando um and em seguida
                            } 
                        }
                        if(!conditionAdded && !columnsRelationship) {
                            sel.havingClause.conditItemList.add(cond); // adicionando uma condi��o having de relacionamento
                            sel.havingClause.conditItemList.add("and"); // adicionando um and em seguida
                            conditionAdded = true;
                            select.havingClause.conditItemList.remove(i--); // removendo ela do select base
                        }
                    }
                } 
                if(!conditionAdded && select.whereClause!=null) {
                    for(int i=0;i<select.whereClause.conditItemList.size();i++){
                        Object cond = select.whereClause.conditItemList.get(i);
                        boolean columnsRelationship = false;
                        if(cond instanceof RelatComp){
                            RelatComp rc = (RelatComp)cond;
                            columnsRelationship = rc.constValue==null && rc.leftColumnItem.columItemType==ColumnItem.COLUMN &&
                                                  rc.rightColumnItem.columItemType==ColumnItem.COLUMN;
                            if(columnsRelationship) {
                                sel.whereClause.conditItemList.add(cond); // adicionando uma condi��o where de relacionamento
                                sel.whereClause.conditItemList.add("and"); // adicionando um and em seguida
                            } 
                        }
                        if(!conditionAdded && !columnsRelationship) {
                            sel.whereClause.conditItemList.add(cond); // adicionando uma condi��o where
                            sel.whereClause.conditItemList.add("and"); // adicionando um and em seguida
                            conditionAdded = true;
                            select.whereClause.conditItemList.remove(i--); // removendo ela do select base
                        }
                    }
                }
                if(conditionAdded) { // alguma condi��o nova adicionada
                    // se n�o tiver nada, coloca null + removendo "and"
                    if(sel.havingClause.conditItemList.size()==0) 
                        sel.havingClause = null;
                    else if(sel.havingClause.conditItemList.get(sel.havingClause.conditItemList.size()-1).equals("and"))
                        sel.havingClause.conditItemList.remove(sel.havingClause.conditItemList.size()-1);
                    if(sel.whereClause.conditItemList.size()==0) 
                        sel.whereClause = null;
                    else if(sel.whereClause.conditItemList.get(sel.whereClause.conditItemList.size()-1).equals("and"))
                        sel.whereClause.conditItemList.remove(sel.whereClause.conditItemList.size()-1);
                    // adicionando ao array e iniciando o pr�ximo
                    selects.add(sel);
                    sel = new SelectStmt(select);
                    sel.havingClause = new HavingClause(); sel.whereClause = new WhereClause();
                }
            }
        } else // n�o vai dividir as express�es condicionais
            selects.add(select); // adiciona s� ele mesmo
    }
    
    static int divisionsNumber(SelectStmt select){
        // retorna n�mero de divis�es poss�veis
        // retorna 0 se encontrar OR ou NOT nas jun��es das express�es condicionais (s� pode ter AND)
        // as colunas de relacionamento n�o s�o contadas para fins de divis�o
        ArrayList wCond=null,hCond=null; int numExp=0;
        if(select.whereClause!=null) // where
            wCond = select.whereClause.conditItemList;
        for(int i=0;wCond!=null && i<wCond.size();i++){
            Object cond = wCond.get(i);
            if(cond instanceof String){
                String s = (String)cond;
                if(s.indexOf("not")!=-1 || s.indexOf("or")!=-1) // achou NOT ou OR
                    return 0;
            } else if(cond instanceof RelatComp) {
                RelatComp rc = (RelatComp)cond;
                if(rc.constValue!=null || rc.leftColumnItem.columItemType!=ColumnItem.COLUMN ||
                                          rc.rightColumnItem.columItemType!=ColumnItem.COLUMN) // n�o � relacionamento entre colunas
                    numExp++;
            } else numExp++;
        }
        if(select.havingClause!=null) // having
            hCond = select.havingClause.conditItemList;
        for(int i=0;hCond!=null && i<hCond.size();i++){
            Object cond = hCond.get(i);
            if(cond instanceof String){
                String s = (String)cond;
                if(s.indexOf("not")!=-1 || s.indexOf("or")!=-1) // achou NOT ou OR
                    return 0;
            } else if(cond instanceof RelatComp) {
                RelatComp rc = (RelatComp)cond;
                if(rc.constValue!=null || rc.leftColumnItem.columItemType!=ColumnItem.COLUMN &&
                                          rc.rightColumnItem.columItemType!=ColumnItem.COLUMN) // n�o � relacionamento entre colunas
                    numExp++;
            } else numExp++;
        }
        return numExp;
    }

    public ArrayList<SelectStmt> getSelects() {
        return selects;
    }
}
