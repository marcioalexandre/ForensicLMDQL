package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.Util;
import com.desenvolvatec.sql2xquery.exception.InvalidColumnItemException;
import com.desenvolvatec.sql2xquery.exception.NoValidConditItemException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.ArrayList;


public class InComp extends ConditItem {

    ColumnItem columnItem = null;
    boolean notOperator = false;
    ArrayList constValue = new ArrayList();


    public InComp(String expression) throws NoValidConditItemException, 
                                            QueryExpressionException {
        expression = expression.trim(); int incremPos = 0;
        // identificando a operação in
        int pos = -1;
        if((pos=expression.indexOf(" not in "))!=-1 || (pos=expression.indexOf(" not in("))!=-1){
            this.notOperator = true;
            incremPos = 8;
        } else if((pos=expression.indexOf(" in "))!=-1 || (pos=expression.indexOf(" in("))!=-1){
            this.notOperator = false;
            incremPos = 4;
        } else
            throw new NoValidConditItemException("Expression is not a valid IN comparison type");
        // lado esquerdo da expressão
        try {
            this.columnItem = new ColumnItem(expression.substring(0,pos).trim());
        } catch (InvalidColumnItemException e) { // não é para acontecer aqui
            throw new QueryExpressionException("Column is not valid for the IN comparison type: "+expression.substring(0,pos).trim());
        }
        // lado direito da expressão
        String rightSide = expression.substring(pos+incremPos).trim();
        // tirando os parênteses - início e fim
        if(rightSide.substring(0,1).equals("("))
            rightSide = rightSide.substring(1);
        else
            throw new QueryExpressionException("Values not well defined for the IN comparison type: "+rightSide);
        if(rightSide.substring(rightSide.length()-1).equals(")"))
            rightSide = rightSide.substring(0,rightSide.length()-1);
        else
            throw new QueryExpressionException("Values not well defined for the IN comparison type: "+rightSide);
        ArrayList<String> values = Util.stringToArrayList(rightSide,',');
        if (values==null || values.size()==0)
            throw new QueryExpressionException("Values not well defined for the IN comparison type: "+rightSide);
        this.constValue = new ArrayList();
        for(int i=0;i<values.size();i++)
            this.constValue.add(values.get(i));
    }


    public ColumnItem getColumnItem() {
        return columnItem;
    }

    public boolean isNotOperator() {
        return notOperator;
    }

    public ArrayList getConstValue() {
        return constValue;
    }
}
