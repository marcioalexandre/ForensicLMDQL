package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.InvalidColumnItemException;
import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.ArrayList;


public class OrderByClause {

    public static int ASC_ORDER = 0;
    public static int DESC_ORDER = 1;

    ArrayList<ColumnItem> columnItem = new ArrayList<ColumnItem>();
    
    int orderType = ASC_ORDER;

    String tokenBefore = "order by";
    String[] tokenAfter = {null};

    public OrderByClause(String query) throws NoTokenFoundException,QueryExpressionException {
        String clause = Util.clauseIdentifier(query,tokenBefore,tokenAfter).trim();
        if(clause.equals(""))
            throw new QueryExpressionException("Reference of ORDER BY clause exists, but is empty");
        int pos = -1;
        if((pos=clause.indexOf(" desc"))!=-1)
            this.orderType = DESC_ORDER;
        else {
            pos=clause.indexOf(" asc");
            this.orderType = ASC_ORDER;
        }
        clause = pos==-1?clause:clause.substring(0,pos).trim(); // extraindo o ASC ou DESC, se existir
        ArrayList<String> clauseItem = Util.stringToArrayList(clause.trim(),',');
        this.columnItem = new ArrayList<ColumnItem>();
        for(int i=0;i<clauseItem.size();i++){ // preenche a lista de ColumnItem
            try {
                this.columnItem.add(new ColumnItem(clauseItem.get(i)));
            } catch (InvalidColumnItemException e) { // é constValue, não admitido aqui
                throw new QueryExpressionException("Invalid ColumnItem in ORDER BY clause");
            }
        }
    }


    public ArrayList<ColumnItem> getColumnItem() {
        return columnItem;
    }

    public int getOrderType() {
        return orderType;
    }
}
