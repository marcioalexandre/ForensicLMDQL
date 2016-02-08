package com.desenvolvatec.sql2xquery;

import com.desenvolvatec.sql2xquery.exception.NoTokenFoundException;

import java.util.ArrayList;


public class Util {


    public static String clauseIdentifier(String expression, 
                                          String tokenBefore, 
                                          String[] tokenAfter) throws NoTokenFoundException {
        // Identifica a clausula de acordo com os tokens
        int pos = 
            expression.indexOf(tokenBefore + " "); // tokenBefore sempre tem que existir
        if (pos == -1) // não achou o token 
            throw new NoTokenFoundException("Token " + 
                                            tokenBefore.toUpperCase() + 
                                            " does not exist");
        String clause = expression.substring(pos + tokenBefore.length() + 1);
        if (tokenAfter != null) { // pode não ter o tokenAfter
            boolean achou = false;
            for (int i = 0; i < tokenAfter.length && !achou; i++) {
                String tk = tokenAfter[i];
                if (tk != null) { // pode ser nulo
                    pos = clause.indexOf(" " + tk);
                    if (pos > -1)
                        achou = true;
                } else { // não necessita ter um token no final
                    pos = clause.length();
                    achou = true;
                }
            }
            if (!achou)
                throw new NoTokenFoundException("Token(s) " + 
                                                stringArrayToString(tokenAfter,",").toUpperCase() + 
                                                " do not exist");
            clause = clause.substring(0, pos);
        }
        return clause.trim();
    }
    
    public static String stringArrayToString(String[] aStr,String separator){
        // transforma um array de strings em uma string 
        String seq = "";
        if(aStr!=null && aStr.length>0) {
            seq = aStr[0].trim();
            for(int i=1;i<aStr.length;i++)
                seq = seq +separator+aStr[i];
        }
        return seq;
    }
    
    public static String arraylistToString(ArrayList<String> aStr,String separator){
        // transforma um arraylist de strings em uma string 
        String seq = "";
        if(aStr!=null && aStr.size()>0) {
            seq = aStr.get(0).trim();
            for(int i=1;i<aStr.size();i++)
                seq = seq +separator+aStr.get(i);
        }
        return seq;
    }
    public static ArrayList<String> stringToArrayList(String expression,char separator) {
        // Converte uma string em um array de strings, baseado em separator (caracter)
        // ATENÇÃO: despreza ocorrência de separators dentro de strings (aspas simples)
        ArrayList<String> aStr = new ArrayList<String>();
        String s = "";
        boolean isString = false;
        for (int i = 0; i < expression.length(); i++) {
            String caracter = expression.substring(i, i + 1);
            if (caracter.equals("'")) // início ou fim de string na query
                isString = !isString;
            if (!isString && 
                caracter.equals(Character.toString(separator))) { // divisor das strings
                aStr.add(s.trim());
                s = "";
                caracter = "";
            }
            s = s + caracter;
        }
        if (!s.trim().equals("")) // adição da última string
            aStr.add(s.trim());
        return aStr;
    }
    
    public static String removeQuotes(String s) {
        // remove " ou ` existentes (SE HOUVEREM) no início e final
        int pos = -1;
        // tira do início
        if ((pos = s.indexOf("`")) == 0 || (pos = s.indexOf("\"")) == 0)
            s = s.substring(1);
        // tira do final
        if ((pos = s.lastIndexOf("`")) == s.length() - 1 || 
            (pos = s.lastIndexOf("\"")) == s.length() - 1)
            s = s.substring(0, s.length() - 1);
        return s.trim();
    }
    
}
