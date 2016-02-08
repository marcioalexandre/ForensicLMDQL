package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.ColumnRefException;
import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.ArrayList;


public class GroupByClause {

    ArrayList<ColumnRef> columnRef = new ArrayList<ColumnRef>();

    String tokenBefore = "group by"; 
    String[] tokenAfter = {"having","order",null};

    public GroupByClause(String query) throws NoTokenFoundException, 
                                              QueryExpressionException {
        String clause = Util.clauseIdentifier(query,tokenBefore,tokenAfter).trim();
        if(clause.equals(""))
            throw new QueryExpressionException("Reference of GROUP BY clause exists, but is empty");
        ArrayList<String> clauseItem = Util.stringToArrayList(clause.trim(),',');
        this.columnRef = new ArrayList<ColumnRef>();
        for(int i=0;i<clauseItem.size();i++) {// preenche a lista de ColumnRef
            try {
                this.columnRef.add(new ColumnRef(clauseItem.get(i)));
            } catch(ColumnRefException e) { // não é para acontecer
                throw new QueryExpressionException(e.getMessage());
            }
        }
    }

    public ArrayList<ColumnRef> getColumnRef() {
        return columnRef;
    }
}
