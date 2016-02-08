package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;


public class TableRef {

    String tableName = null;
    String tableAlias = null;
    
    public TableRef(String expression) throws QueryExpressionException {
        // identificando o alias (pelo " as ")
        int pos = expression.indexOf(" as ");
        if(pos==-1)
            this.tableAlias = null;
        else {
            this.tableAlias = Util.removeQuotes(expression.substring(pos+5).trim());
            if(this.tableAlias.equals(""))
                throw new QueryExpressionException("Alias name is mandatory after the AS clause");
            expression = expression.substring(0,pos);
        }
        // obtendo o nome da tabela
        this.tableName = Util.removeQuotes(expression.trim());
        if(this.tableName.equals(""))
            throw new QueryExpressionException("Table name is empty");
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }
}
