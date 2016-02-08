package com.desenvolvatec.sql2xquery.sql;

import com.desenvolvatec.sql2xquery.exception.NoValidConditItemException;
import com.desenvolvatec.sql2xquery.exception.QueryExpressionException;

import java.util.ArrayList;


public abstract class ConditItemList {
    
    ArrayList conditItemList = new ArrayList();
    
    
    void buildList(String expression) throws QueryExpressionException {
        // Divide uma clausula conditional em array onde seus elementos são iniciados por 
        // AND (exceto em BetweenComp), OR, ou NOT (exceto nas negativas de InComp, IsNullComp and BetweenComp), que são os separadores. 
        // Depois o método auxiliar addCond isola esses separadores.
        // Parenteses também são separadores, mas são totalmente isolados, e apenas o que não fazem parte das funções.
        this.conditItemList = new ArrayList();
        String s = ""; 
        boolean isString = false, isInFunction = false, isBetweenComp = false;
        for(int i=0;i<expression.length();i++){
            String caracter = expression.substring(i,i+1);
            try {
                if(caracter.equals("'")) // início ou fim de string na query
                    isString = !isString;
                else if(expression.substring(i,i+4).equals(" in ") || // vendo se está em um InComp ou função
                        expression.substring(i,i+4).equals(" in(") ||
                        expression.substring(i,i+7).equals(" upper(") ||
                        expression.substring(i,i+6).equals("upper(") ||
                        expression.substring(i,i+7).equals(" lower(") ||
                        expression.substring(i,i+6).equals("lower(") ||
                        expression.substring(i,i+8).equals(" isnull(") ||
                        expression.substring(i,i+7).equals("isnull("))
                    isInFunction = true;
                else if(expression.substring(i,i+9).equals(" between ")) // vendo se está em um BetweenComp
                    isBetweenComp = true;
                else if(isBetweenComp && 
                        ( expression.substring(i-5,i).equals(" and ") ||
                          expression.substring(i-5,i).equals(" and(") ||
                          expression.substring(i-5,i).equals(")and ") ||
                          expression.substring(i-5,i).equals(")and(") ) ) // acabou o and do BetweenComp
                    isBetweenComp = false;
            } catch (StringIndexOutOfBoundsException e){}   // o i+4,i+5,i+9 podem dar esse erro, se tiver no final de expression
            if(!isString) {
                if(!isInFunction) {
                    if(caracter.equals("(") || caracter.equals(")")){ // isolando parenteses não função
                        if(!s.trim().equals(""))
                            addCond(s.trim()); // pode estar vazio
                        this.conditItemList.add(caracter);
                        s = ""; caracter = "";
                    } else {
                        try {
                            if( expression.substring(i,i+4).equals(" or ") || expression.substring(i,i+4).equals(" or(") ||
                                ((expression.substring(i,i+5).equals(" and(") || expression.substring(i,i+5).equals(" and ")) && !isBetweenComp) || // não é o and do between
                                expression.substring(i,i+5).equals(" not(") ||
                                ( expression.substring(i,i+5).equals(" not ") &&
                                  !expression.substring(i,i+8).equals(" not in ") && // evitando InComp
                                  !expression.substring(i,i+8).equals(" not in(") && // evitando InComp 
                                  !expression.substring(i-3,i+9).equals(" is not null") && // evitando IsNullComp
                                  !expression.substring(i,i+13).equals(" not between ") ) ) { // evitando BetweenComp
                                        s = s + caracter;
                                        if(!s.trim().equals(""))
                                            addCond(s.trim());
                                        s = ""; caracter = "";
                            }
                        } catch (StringIndexOutOfBoundsException e){}   // o i+5, i+4, etc. pode dar esse erro se tiver no final
                    }
                } else // é isInFunction
                    if(caracter.equals(")")) // fechou a função detectado por isInFunction            
                        isInFunction = false;
            }
            s = s + caracter;
        }
        if(!s.trim().equals("")) // adição da última string
            addCond(s.trim());
    }
    
    private void addCond(String s) throws QueryExpressionException {
        // Adiciona a string condicional s (ou seu objeto relacionado) à conditItemList. 
        // Porém se o string s for iniciado com OR ou AND ou NOT é filtrado
        s = s.trim();
        try {
            if(s.substring(0,3).equals("or ")) {
                this.conditItemList.add("or");
                this.conditItemList.add(defineConditItem(s.substring(3).trim()));
            } else if(s.substring(0,4).equals("and ")) {
                this.conditItemList.add("and");
                this.conditItemList.add(defineConditItem(s.substring(4).trim()));
            } else if(s.substring(0,4).equals("not ")) {
                this.conditItemList.add("not");
                this.conditItemList.add(defineConditItem(s.substring(4).trim()));
            } else 
                this.conditItemList.add(defineConditItem(s.trim()));
        } catch (StringIndexOutOfBoundsException e){
            if(s.equals("and")||s.equals("or")||s.equals("not")) // pode estar sozinhos (isolados por parenteses após)
                this.conditItemList.add(s.trim()); // 
            else // expressão menor
                this.conditItemList.add(defineConditItem(s.trim())); 
        }   
    }
    
    
     private ConditItem defineConditItem(String expression) throws QueryExpressionException {
         ConditItem cond = null;
         try {
             cond = new RelatComp(expression);
         } catch (NoValidConditItemException e0) {
             try {
                 cond = new BetweenComp(expression);
             } catch (NoValidConditItemException e1) {
                 try {
                     cond = new InComp(expression);
                 } catch (NoValidConditItemException e2) {
                     try {
                         cond = new IsNullComp(expression);
                     } catch (NoValidConditItemException e3) {
                         throw new QueryExpressionException("Invalid conditional expression: "+expression);
                     }
                 }
             }
         }
         return  cond;
     }


    public ArrayList getConditItemList() {
        return conditItemList;
    }

    public void setConditItemList(ArrayList conditItemList) {
        this.conditItemList = conditItemList;
    }
}


