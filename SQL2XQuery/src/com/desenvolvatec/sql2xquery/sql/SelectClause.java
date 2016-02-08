package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.InvalidColumnItemException;
import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.ArrayList;


public class SelectClause {
    public static int ALL = 0;
    public static int DISTINCT = 1;

    int selectType = 0; // ALL (padrão)
    ArrayList<ColumnItem> columnItem = new ArrayList<ColumnItem>();
    String tokenBefore = "select";
    String tokenAfter[] = {"from"};
    
    public SelectClause(String query) throws NoTokenFoundException, 
                                             QueryExpressionException {
        String clause = Util.clauseIdentifier(query,tokenBefore,tokenAfter).trim();
        this.selectType = (clause.indexOf("distinct ")!=-1 && clause.substring(0,9).equalsIgnoreCase("distinct "))?DISTINCT:ALL; // identificando se é DISTINCT
        if(selectType==DISTINCT)
            clause = clause.substring(9);
        else if(clause.indexOf("all ")!=-1 && clause.substring(0,4).equalsIgnoreCase("all ")) // ALL com o modificador especificado
             clause = clause.substring(4);
        clause = clause.trim();
        if(clause.length()==0) // não tem nada, é erro
            throw new QueryExpressionException("SELECT clause is empty");
        ArrayList<String> clauseItem = Util.stringToArrayList(clause.trim(),',');
        this.columnItem = new ArrayList<ColumnItem>();
        for(int i=0;i<clauseItem.size();i++){ // preenche a lista de ColumnItem
            try {
                this.columnItem.add(new ColumnItem(clauseItem.get(i)));
            } catch (InvalidColumnItemException e) { // é constValue, não admitido aqui
                throw new QueryExpressionException("Invalid ColumnItem in SELECT clause: "+clauseItem.get(i));
            }
        }
    }


    public int getSelectType() {
        return selectType;
    }

    public ArrayList<ColumnItem> getColumnItem() {
        return columnItem;
    }
}


