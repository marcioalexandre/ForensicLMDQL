package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.ColumnRefException;
import com.desenvolvatec.sql2xquery.exception.InvalidColumnItemException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;
import com.desenvolvatec.sql2xquery.exception.SQLFunctionException;


public class ColumnItem {
    public static int COLUMN = 0;
    public static int FUNCTION = 1;

    int columItemType = 0; // COLUMN
    ColumnRef columnRef = null;
    FunctionItem functionItem = null;
    String columnAlias = null;
    
    public ColumnItem(String expression) throws InvalidColumnItemException, 
                                                QueryExpressionException {
        expression = expression.trim();
        // identificando o alias 
        int pos = -1, add=0;;
        if((pos=expression.indexOf(" as "))!=-1) add = 4; // pelo as
        else if((pos=expression.lastIndexOf(" "))!=-1) add = 1; // apenas por um espaço
        if(add==1&&pos<expression.indexOf(")")) // espaço antes de fechar o parentese
            pos = -1; // não tem alias
        //
        if(pos==-1)
            this.columnAlias = null;
        else {
            this.columnAlias = Util.removeQuotes(expression.substring(pos+add).trim());
            if(this.columnAlias.equals("")) // só poderia ser por AS
                throw new QueryExpressionException("Alias name is mandatory after the AS clause: "+this.columnAlias);
            expression = expression.substring(0,pos);
        }
        expression = expression.trim();
        // tentando ver se é função
        try {
            this.functionItem = new FunctionItem(expression);
        } catch (SQLFunctionException e) {
             this.functionItem = null;
        }
        if(this.functionItem!=null) {// é função
            this.columnRef = null;
            this.columItemType = FUNCTION;
            return;
        }
        // é coluna
        this.columItemType = COLUMN;
        try {
            this.columnRef = new ColumnRef(expression);
        } catch (ColumnRefException e) { // acontece na definição de ConditItem, quando vem um constValue
            this.columnRef = null;
            throw new InvalidColumnItemException("Invalid column reference: "+expression);
        }
    }

    public int getColumItemType() {
        return columItemType;
    }

    public ColumnRef getColumnRef() {
        return columnRef;
    }

    public FunctionItem getFunctionItem() {
        return functionItem;
    }

    public String getColumnAlias() {
        return columnAlias;
    }
}

