package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.exception.InvalidColumnItemException;
import com.desenvolvatec.sql2xquery.exception.NoValidConditItemException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;


public class IsNullComp extends ConditItem {

    ColumnItem columnItem = null;
    boolean notOperator = false;
    
    public IsNullComp(String expression) throws NoValidConditItemException, 
                                                QueryExpressionException {
        expression = expression.trim(); int incremPos = 0;
        // identificando a operação "is null"
        int pos = -1;
        if((pos=expression.indexOf(" is not null"))!=-1){
            this.notOperator = true;  
            incremPos = 12;
        } else if((pos=expression.indexOf(" is null"))!=-1){
            this.notOperator = true;  
            incremPos = 8;
        } else
            throw new NoValidConditItemException("Expression is not a valid IsNull comparison type");
        // lado esquerdo da expressão
        try {
            this.columnItem = new ColumnItem(expression.substring(0,pos).trim());
        } catch (InvalidColumnItemException e) { // não é para acontecer aqui
            throw new QueryExpressionException("Column is not valid for a IsNull comparison type: "+expression.substring(0,pos).trim());
        }
        // lado direito da expressão - deve estar vazio
        String rightSide = expression.substring(pos+incremPos).trim();
        if(rightSide.length()!=0) // existe algo após o is null 
            throw new QueryExpressionException("Expression is not a valid IsNull comparison type: "+expression);
    }


    public ColumnItem getColumnItem() {
        return columnItem;
    }

    public boolean isNotOperator() {
        return notOperator;
    }
}
