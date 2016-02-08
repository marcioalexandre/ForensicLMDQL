package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.exception.InvalidColumnItemException;
import com.desenvolvatec.sql2xquery.exception.NoValidConditItemException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;


public class RelatComp extends ConditItem {

    public static int EQUALS = 0;
    public static int NOT_EQUALS = 1;
    public static int LESS = 2;
    public static int LESS_OR_EQUALS = 3;
    public static int GREATER = 4;
    public static int GREATER_OR_EQUALS = 5;
    
    ColumnItem leftColumnItem = null;
    int relatOper = -1; // sem operador relacional

    ColumnItem rightColumnItem = null;
    Object constValue = null;
    
    public RelatComp(String expression) throws NoValidConditItemException, 
                                               QueryExpressionException {
        expression = expression.trim(); int incremPos = 0;
        // identificando a operação
        int pos = -1;
        if((pos=expression.indexOf("<>"))!=-1){
            this.relatOper = NOT_EQUALS;
            incremPos = 2;
        } else if((pos=expression.indexOf("<="))!=-1){
            this.relatOper = LESS_OR_EQUALS;
            incremPos = 2;
        } else if((pos=expression.indexOf(">="))!=-1){
            this.relatOper = GREATER_OR_EQUALS;
            incremPos = 2;
        } else if((pos=expression.indexOf("<"))!=-1){
            this.relatOper = LESS;
            incremPos = 1;
        } else if((pos=expression.indexOf(">"))!=-1){
            this.relatOper = GREATER;
            incremPos = 1;
        } else if((pos=expression.indexOf("="))!=-1){
            this.relatOper = EQUALS;
            incremPos = 1;
        } else
            throw new NoValidConditItemException("Expression is not a valid relational comparison type");
        // lado esquerdo da expressão
        try {
            this.leftColumnItem = new ColumnItem(expression.substring(0,pos).trim());
        } catch (InvalidColumnItemException e) { // não é para acontecer aqui
            throw new QueryExpressionException("Left side column is not valid for a relational comparison type: "+expression.substring(0,pos).trim());
        }
        // lado direito da expressão
        try {
            this.rightColumnItem = new ColumnItem(expression.substring(pos+incremPos).trim());
            this.constValue = null;
        } catch (InvalidColumnItemException e) { // é um constValue
            this.rightColumnItem = null;
            this.constValue = expression.substring(pos+incremPos).trim();
        }
    }

    public ColumnItem getLeftColumnItem() {
        return leftColumnItem;
    }

    public int getRelatOper() {
        return relatOper;
    }

    public ColumnItem getRightColumnItem() {
        return rightColumnItem;
    }

    public Object getConstValue() {
        return constValue;
    }
}
