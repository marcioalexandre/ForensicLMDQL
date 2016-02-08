package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.ArrayList;


public class FromClause {

    ArrayList<TableRef> tableRef = new ArrayList<TableRef>();

    String tokenBefore = "from";
    String[] tokenAfter = {"where","group","order",null};
    
    public FromClause(String query) throws NoTokenFoundException, 
                                           QueryExpressionException {
        String clause = Util.clauseIdentifier(query,tokenBefore,tokenAfter).trim();
        if(clause.length()==0) // não tem nada, é erro
            throw new QueryExpressionException("FROM clause is empty");
        ArrayList<String> clauseItem = Util.stringToArrayList(clause.trim(),',');
        this.tableRef = new ArrayList<TableRef>();
        for(int i=0;i<clauseItem.size();i++) // preenche a lista de TableRef
            this.tableRef.add(new TableRef(clauseItem.get(i)));
    }

    public ArrayList<TableRef> getTableRef() {
        return tableRef;
    }
}

