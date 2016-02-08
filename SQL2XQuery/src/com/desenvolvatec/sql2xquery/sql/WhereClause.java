package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;


public class WhereClause extends ConditItemList{

    String tokenBefore = "where";
    String[] tokenAfter = {"group","order",null};
    
    public WhereClause(String query) throws NoTokenFoundException, 
                                            QueryExpressionException {
        String clause = Util.clauseIdentifier(query,tokenBefore,tokenAfter).trim();
        if(clause.equals(""))
            throw new QueryExpressionException("Reference of WHERE clause exists, but is empty");
        this.buildList(clause);
    }

    public WhereClause(){    
    }
}
