package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;


public class HavingClause extends ConditItemList {

    String tokenBefore = "having";
    String[] tokenAfter = {"order",null};
    
    public HavingClause(String query) throws NoTokenFoundException, 
                                             QueryExpressionException {
        String clause = Util.clauseIdentifier(query,tokenBefore,tokenAfter).trim();
        if(clause.equals(""))
            throw new QueryExpressionException("Reference of HAVING clause exists, but is empty");
        this.buildList(clause);
    }
    
    public HavingClause(){    
    }

}
