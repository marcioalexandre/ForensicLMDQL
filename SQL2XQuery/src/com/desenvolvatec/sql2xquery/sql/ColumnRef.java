package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.ColumnRefException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;


public class ColumnRef {

    String tableName = null;
    String columnName = null;
    
    public ColumnRef(String expression) throws ColumnRefException, 
                                               QueryExpressionException {
        expression = expression.trim();
        // identifica tabela.coluna
        int pos = expression.indexOf(".");
        if(pos==-1){
            throw new ColumnRefException("Expression is not a valid column reference: "+expression);
        }
        if((this.tableName = Util.removeQuotes(expression.substring(0,pos)).trim()).equals(""))
            throw new QueryExpressionException("Table name is empty");
        if((this.columnName = Util.removeQuotes(expression.substring(pos+1)).trim()).equals(""))
            throw new QueryExpressionException("Column name is empty");
    }

    public boolean equals(ColumnRef columnRef){
        if(this.tableName.equals(columnRef.tableName) &&
           this.columnName.equals(columnRef.columnName))
            return true;
        else
            return false;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }
    
}
