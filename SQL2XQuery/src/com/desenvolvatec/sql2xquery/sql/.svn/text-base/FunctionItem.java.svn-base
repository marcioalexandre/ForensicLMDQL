package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.exception.ColumnRefException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;
import com.desenvolvatec.sql2xquery.exception.SQLFunctionException;


public class FunctionItem {

    public static int SUM = 0;
    public static int AVG = 1;
    public static int COUNT = 2;
    public static int MAX = 3;
    public static int MIN = 4;
    public static int UPPER = 5;
    public static int LOWER = 6;
    public static int ISNULL = 7;
    
    int functionType = -1; // sem default
    
    int selectType = SelectClause.ALL; // default
    ColumnRef columnRef = null;
    
    Object constValue = null;
    
    boolean asterisk = false;
    
    public FunctionItem(String expression) throws SQLFunctionException, 
                                                  QueryExpressionException {
        expression = expression.trim();
        // Tenta instanciar um item de função. Lança uma exceção se não for função.
        int pos = expression.indexOf("(");
        if(pos!=-1) {// pode ser função
            try{
                if(expression.substring(0,4).equalsIgnoreCase("sum(") || expression.substring(0,5).equalsIgnoreCase("sum ("))
                    this.functionType = SUM;
                else if(expression.substring(0,4).equalsIgnoreCase("avg(") || expression.substring(0,5).equalsIgnoreCase("avg ("))
                    this.functionType = AVG;
                else if(expression.substring(0,4).equalsIgnoreCase("max(") || expression.substring(0,5).equalsIgnoreCase("max ("))
                    this.functionType = MAX;
                else if(expression.substring(0,4).equalsIgnoreCase("min(") || expression.substring(0,5).equalsIgnoreCase("min ("))
                    this.functionType = MIN;
                else if(expression.substring(0,6).equalsIgnoreCase("count(") || expression.substring(0,7).equalsIgnoreCase("count ("))
                    this.functionType = COUNT;
                else if(expression.substring(0,6).equalsIgnoreCase("upper(") || expression.substring(0,7).equalsIgnoreCase("upper ("))
                    this.functionType = UPPER;
                else if(expression.substring(0,6).equalsIgnoreCase("lower(") || expression.substring(0,7).equalsIgnoreCase("lower ("))
                    this.functionType = LOWER;
                else if(expression.substring(0,7).equalsIgnoreCase("isnull(") || expression.substring(0,8).equalsIgnoreCase("isnull ("))
                    this.functionType = ISNULL;
            } catch(Exception e) {
                System.out.println(e.getMessage()+": "+expression);
            }    
            if(this.functionType==-1) // não foi nenhuma função predefinida
                throw new SQLFunctionException("Expression is not a valid function type");
            // é função existente/predefinida
            expression = expression.substring(pos+1);
            pos = expression.indexOf(")");
            if(pos==-1)
                throw new QueryExpressionException("Unclosed parenthesis in function");
            expression = expression.substring(0,pos);
            // identificando se é ALL ou DISTINCT
            this.selectType = (expression.indexOf("distinct ")!=-1 && expression.substring(0,9).equalsIgnoreCase("distinct "))?SelectClause.DISTINCT:SelectClause.ALL; // identificando se é DISTINCT
            if(selectType==SelectClause.DISTINCT)
                expression = expression.substring(9);
            else if(expression.indexOf("all ")!=-1 && expression.substring(0,4).equalsIgnoreCase("all ")) // ALL com o modificador especificado
                 expression = expression.substring(4);
            expression = expression.trim();
            if(expression.length()==0) // não tem nada, é erro
                 throw new QueryExpressionException("Function body is empty");
            // verificando se é asterisco
            this.asterisk = expression.equals("*")?true:false;
            if(this.asterisk) {
                this.constValue = null;
                this.columnRef = null;
                return;
            }
            // verificando se é coluna ou constante
            try {
                this.columnRef = new ColumnRef(expression);
                this.constValue = null;
            } catch (ColumnRefException e) {
                this.columnRef = null;
                this.constValue = expression;
            }
        } else
            throw new SQLFunctionException("Expression is not a valid function type");
    }


    public int getFunctionType() {
        return functionType;
    }

    public int getSelectType() {
        return selectType;
    }

    public ColumnRef getColumnRef() {
        return columnRef;
    }

    public Object getConstValue() {
        return constValue;
    }

    public boolean isAsterisk() {
        return asterisk;
    }
}
