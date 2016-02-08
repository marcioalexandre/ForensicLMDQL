package com.desenvolvatec.sql2xquery.xquery;

import java.sql.ResultSetMetaData;
import java.sql.Types;

import java.util.ArrayList;


public class XQResultSetMetaData implements ResultSetMetaData {

    private String res;
    private ArrayList<String> fieldNames;

    public XQResultSetMetaData(String res) {
        this.res = res; this.fieldNames = new ArrayList<String>();
        String line = this.res.trim().substring(6); // depois de <line>
        while(!line.equals("</line>")){
            int pos = line.indexOf(">");
            if(pos!=-1){
                String name = line.substring(1,pos);
                this.fieldNames.add(name);
                line = line.substring(line.indexOf(">",pos+1)+1);
            } else
                line = "</line>";
        }
    }

    public int getColumnCount() {
        return this.fieldNames.size();
    }

    public boolean isAutoIncrement(int column) {
        return false;
    }

    public boolean isCaseSensitive(int column) {
        return false;
    }

    public boolean isSearchable(int column) {
        return false;
    }

    public boolean isCurrency(int column) {
        return false;
    }

    public int isNullable(int column) {
        return 0; // columnNoNulls
    }

    public boolean isSigned(int column) {
        return false;
    }

    public int getColumnDisplaySize(int column) {
        return 0;
    }

    public String getColumnLabel(int column) {
        return null;
    }

    public String getColumnName(int column) {
        if(column>0 && column<=this.fieldNames.size())
            return this.fieldNames.get(column-1);
        return null;
    }

    public String getSchemaName(int column) {
        return null;
    }

    public int getPrecision(int column) {
        return 0;
    }

    public int getScale(int column) {
        return 0;
    }

    public String getTableName(int column) {
        return null;
    }

    public String getCatalogName(int column) {
        return null;
    }

    public int getColumnType(int column) {
        if(column>0 && column<=this.fieldNames.size())
            return Types.VARCHAR; // tudo é tratado como VARCHAR
        return -1;
    }

    public String getColumnTypeName(int column) {
        if(column>0 && column<=this.fieldNames.size())
            return "VARCHAR";
        return null;
    }

    public boolean isReadOnly(int column) {
        return false;
    }

    public boolean isWritable(int column) {
        return false;
    }

    public boolean isDefinitelyWritable(int column) {
        return false;
    }

    public String getColumnClassName(int column) {
        return "String"; // a princípio, tudo é String
    }
}
