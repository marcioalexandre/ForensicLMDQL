package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.exception.InvalidColumnItemException;
import com.desenvolvatec.sql2xquery.exception.NoValidConditItemException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;


public class BetweenComp extends ConditItem {

    ColumnItem columnItem = null;
    boolean notOperator = false;
    Object constValue1 = null;
    Object constValue2 = null;

    public BetweenComp(String expression) throws NoValidConditItemException, 
                                                 QueryExpressionException {
        expression = expression.trim(); int incremPos = 0;
        // identificando a operação between
        int pos = -1;
        if((pos=expression.indexOf(" not between "))!=-1){
            this.notOperator = true;
            incremPos = 13;
        } else if((pos=expression.indexOf(" between "))!=-1){
            this.notOperator = false;
            incremPos = 9;
        } else
            throw new NoValidConditItemException("Expression is not a valid BETWEEN comparison type");
        // lado esquerdo da expressão
        try {
            this.columnItem = new ColumnItem(expression.substring(0,pos).trim());
        } catch (InvalidColumnItemException e) { // não é para acontecer aqui
            throw new QueryExpressionException("Column is not valid for the BETWEEN comparison type: "+expression.substring(0,pos).trim());
        }
        // lado direito da expressão
        String rightSide = expression.substring(pos+incremPos).trim();
        // identificando o intervalo do between
        pos = -1;
        if((pos=rightSide.indexOf(" and "))!=-1)
            incremPos = 5;
        else
            throw new QueryExpressionException("Interval not well defined for the BETWEEN comparison type: "+rightSide);
        this.constValue1 = rightSide.substring(0,pos).trim();
        this.constValue2 = rightSide.substring(pos+incremPos).trim();
        if (((String)this.constValue1).length()==0 || ((String)this.constValue2).length()==0)
            throw new QueryExpressionException("Interval not well defined for the BETWEEN comparison type: "+rightSide);
    }

    public ColumnItem getColumnItem() {
        return columnItem;
    }

    public Object getConstValue1() {
        return constValue1;
    }

    public Object getConstValue2() {
        return constValue2;
    }

    public boolean isNotOperator() {
        return notOperator;
    }
}
