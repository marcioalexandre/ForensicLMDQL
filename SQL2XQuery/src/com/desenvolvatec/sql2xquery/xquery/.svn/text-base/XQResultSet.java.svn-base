package com.desenvolvatec.sql2xquery.xquery;

import java.io.InputStream;
import java.io.Reader;

import java.math.BigDecimal;

import java.net.URL;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.Calendar;
import java.util.Map;


public class XQResultSet implements ResultSet {

    private String[] res;
    private int index;

    public XQResultSet(String[] res) {
        this.res = res;
        this.index = -1;
    }

    public boolean next() {
        if(this.res!=null) {
            if(this.index == res.length-1)
                return false;
            this.index++;
            return true;
        } else
            return false;
    }

    public void close() {
        this.res = null;
        this.index = -1;
    }

    public boolean wasNull() {
        return false;
    }

    public String getString(int columnIndex) {
        String line = this.res[index].trim().substring(6); // depois de <line>
        for(int i=1;i<=columnIndex && !line.equals("</line>");i++){
            int pos = line.indexOf(">");
            if(pos!=-1){
                String value = line.substring(pos+1,line.indexOf("</",1));
                if(i==columnIndex)
                    return value;
                line = line.substring(line.indexOf(">",pos+1)+1);
            } else
                line = "</line>";
        }
        return null; // se não achou
    }

    public boolean getBoolean(int columnIndex) {
        String s = getString(columnIndex);
        if(s==null)
            return false;
        try {
            boolean n = Boolean.parseBoolean(s);
            return n;
        } catch(Exception e){
            return false;
        }
    }

    public byte getByte(int columnIndex) {
        String s = getString(columnIndex);
        if(s==null)
            return 0;
        try {
            byte n = Byte.parseByte(s);
            return n;
        } catch(Exception e){
            return 0;
        }
    }

    public short getShort(int columnIndex) {
        String s = getString(columnIndex);
        if(s==null)
            return 0;
        try {
            short n = Short.parseShort(s);
            return n;
        } catch(Exception e){
            return 0;
        }
    }

    public int getInt(int columnIndex) {
        String s = getString(columnIndex);
        if(s==null)
            return 0;
        try {
            int n = Integer.parseInt(s);
            return n;
        } catch(Exception e){
            return 0;
        }
    }

    public long getLong(int columnIndex) {
        String s = getString(columnIndex);
        if(s==null)
            return 0l;
        try {
            long n = Long.parseLong(s);
            return n;
        } catch(Exception e){
            return 0l;
        }
    }

    public float getFloat(int columnIndex) {
        String s = getString(columnIndex);
        if(s==null)
            return 0.0f;
        try {
            float n = Float.parseFloat(s);
            return n;
        } catch(Exception e){
            return 0.0f;
        }
    }

    public double getDouble(int columnIndex) {
        String s = getString(columnIndex);
        if(s==null)
            return 0.0;
        try {
            double n = Double.parseDouble(s);
            return n;
        } catch(Exception e){
            return 0.0;
        }
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) {
        return null;
    }

    public byte[] getBytes(int columnIndex) {
        return new byte[0];
    }

    public Date getDate(int columnIndex) {
        return null;
    }

    public Time getTime(int columnIndex) {
        return null;
    }

    public Timestamp getTimestamp(int columnIndex) {
        return null;
    }

    public InputStream getAsciiStream(int columnIndex) {
        return null;
    }

    public InputStream getUnicodeStream(int columnIndex) {
        return null;
    }

    public InputStream getBinaryStream(int columnIndex) {
        return null;
    }

    public String getString(String columnName) {
        String line = this.res[index].trim().substring(6); // depois de <line>
        for(int i=0;!line.equals("</line>");i++){
            int pos = line.indexOf(">");
            if(pos!=-1){
                String name = line.substring(1,pos);
                String value = line.substring(pos+1,line.indexOf("</",1));
                if(name.equals(columnName))
                    return value;
                line = line.substring(line.indexOf(">",pos+1)+1);
            } else
                line = "</line>";
        }
        return null; // se não achou
    }

    public boolean getBoolean(String columnName) {
        String s = getString(columnName);
        if(s==null)
            return false;
        try {
            boolean n = Boolean.parseBoolean(s);
            return n;
        } catch(Exception e){
            return false;
        }
    }

    public byte getByte(String columnName) {
        String s = getString(columnName);
        if(s==null)
            return 0;
        try {
            byte n = Byte.parseByte(s);
            return n;
        } catch(Exception e){
            return 0;
        }
    }

    public short getShort(String columnName) {
        String s = getString(columnName);
        if(s==null)
            return 0;
        try {
            short n = Short.parseShort(s);
            return n;
        } catch(Exception e){
            return 0;
        }
    }

    public int getInt(String columnName) {
        String s = getString(columnName);
        if(s==null)
            return 0;
        try {
            int n = Integer.parseInt(s);
            return n;
        } catch(Exception e){
            return 0;
        }
    }

    public long getLong(String columnName) {
        String s = getString(columnName);
        if(s==null)
            return 0l;
        try {
            long n = Long.parseLong(s);
            return n;
        } catch(Exception e){
            return 0l;
        }
    }

    public float getFloat(String columnName) {
        String s = getString(columnName);
        if(s==null)
            return 0.0f;
        try {
            float n = Float.parseFloat(s);
            return n;
        } catch(Exception e){
            return 0.0f;
        }
    }

    public double getDouble(String columnName) {
        String s = getString(columnName);
        if(s==null)
            return 0.0;
        try {
            double n = Double.parseDouble(s);
            return n;
        } catch(Exception e){
            return 0.0;
        }
    }

    public BigDecimal getBigDecimal(String columnName, int scale) {
        return null;
    }

    public byte[] getBytes(String columnName) {
        return new byte[0];
    }

    public Date getDate(String columnName) {
        return null;
    }

    public Time getTime(String columnName) {
        return null;
    }

    public Timestamp getTimestamp(String columnName) {
        return null;
    }

    public InputStream getAsciiStream(String columnName) {
        return null;
    }

    public InputStream getUnicodeStream(String columnName) {
        return null;
    }

    public InputStream getBinaryStream(String columnName) {
        return null;
    }

    public SQLWarning getWarnings() {
        return null;
    }

    public void clearWarnings() {
    }

    public String getCursorName() {
        return null;
    }

    public ResultSetMetaData getMetaData() {
        return new XQResultSetMetaData(this.res[this.index]);
    }

    public Object getObject(int columnIndex) {
        return getString(columnIndex);
    }

    public Object getObject(String columnName) {
        return getString(columnName);
    }

    public int findColumn(String columnName) {
        return 0;
    }

    public Reader getCharacterStream(int columnIndex) {
        return null;
    }

    public Reader getCharacterStream(String columnName) {
        return null;
    }

    public BigDecimal getBigDecimal(int columnIndex) {
        return null;
    }

    public BigDecimal getBigDecimal(String columnName) {
        return null;
    }

    public boolean isBeforeFirst() {
        return false;
    }

    public boolean isAfterLast() {
        return false;
    }

    public boolean isFirst() {
        return false;
    }

    public boolean isLast() {
        return false;
    }

    public void beforeFirst() {
    }

    public void afterLast() {
    }

    public boolean first() {
        if(this.res!=null) {
            this.index = 0;
            return true;
        } else
            return false;
    }

    public boolean last() {
        if(this.res!=null) {
            this.index = this.res.length - 1;
            return true;
        } else
            return false;
    }

    public int getRow() {
        return 0;
    }

    public boolean absolute(int row) {
        return false;
    }

    public boolean relative(int rows) {
        return false;
    }

    public boolean previous() {
        if(this.index <= 0)
            return false;
        this.index--;
        return true;
    }

    public void setFetchDirection(int direction) {
    }

    public int getFetchDirection() {
        return 0;
    }

    public void setFetchSize(int rows) {
    }

    public int getFetchSize() {
        return 0;
    }

    public int getType() {
        return 0;
    }

    public int getConcurrency() {
        return 0;
    }

    public boolean rowUpdated() {
        return false;
    }

    public boolean rowInserted() {
        return false;
    }

    public boolean rowDeleted() {
        return false;
    }

    public void updateNull(int columnIndex) {
    }

    public void updateBoolean(int columnIndex, boolean x) {
    }

    public void updateByte(int columnIndex, byte x) {
    }

    public void updateShort(int columnIndex, short x) {
    }

    public void updateInt(int columnIndex, int x) {
    }

    public void updateLong(int columnIndex, long x) {
    }

    public void updateFloat(int columnIndex, float x) {
    }

    public void updateDouble(int columnIndex, double x) {
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) {
    }

    public void updateString(int columnIndex, String x) {
    }

    public void updateBytes(int columnIndex, byte[] x) {
    }

    public void updateDate(int columnIndex, Date x) {
    }

    public void updateTime(int columnIndex, Time x) {
    }

    public void updateTimestamp(int columnIndex, Timestamp x) {
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) {
    }

    public void updateBinaryStream(int columnIndex, InputStream x, 
                                   int length) {
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) {
    }

    public void updateObject(int columnIndex, Object x, int scale) {
    }

    public void updateObject(int columnIndex, Object x) {
    }

    public void updateNull(String columnName) {
    }

    public void updateBoolean(String columnName, boolean x) {
    }

    public void updateByte(String columnName, byte x) {
    }

    public void updateShort(String columnName, short x) {
    }

    public void updateInt(String columnName, int x) {
    }

    public void updateLong(String columnName, long x) {
    }

    public void updateFloat(String columnName, float x) {
    }

    public void updateDouble(String columnName, double x) {
    }

    public void updateBigDecimal(String columnName, BigDecimal x) {
    }

    public void updateString(String columnName, String x) {
    }

    public void updateBytes(String columnName, byte[] x) {
    }

    public void updateDate(String columnName, Date x) {
    }

    public void updateTime(String columnName, Time x) {
    }

    public void updateTimestamp(String columnName, Timestamp x) {
    }

    public void updateAsciiStream(String columnName, InputStream x, 
                                  int length) {
    }

    public void updateBinaryStream(String columnName, InputStream x, 
                                   int length) {
    }

    public void updateCharacterStream(String columnName, Reader reader, 
                                      int length) {
    }

    public void updateObject(String columnName, Object x, int scale) {
    }

    public void updateObject(String columnName, Object x) {
    }

    public void insertRow() {
    }

    public void updateRow() {
    }

    public void deleteRow() {
    }

    public void refreshRow() {
    }

    public void cancelRowUpdates() {
    }

    public void moveToInsertRow() {
    }

    public void moveToCurrentRow() {
    }

    public Statement getStatement() {
        return null;
    }

    public Object getObject(int i, Map<String, Class<?>> map) {
        return null;
    }

    public Ref getRef(int i) {
        return null;
    }

    public Blob getBlob(int i) {
        return null;
    }

    public Clob getClob(int i) {
        return null;
    }

    public Array getArray(int i) {
        return null;
    }

    public Object getObject(String colName, Map<String, Class<?>> map) {
        return null;
    }

    public Ref getRef(String colName) {
        return null;
    }

    public Blob getBlob(String colName) {
        return null;
    }

    public Clob getClob(String colName) {
        return null;
    }

    public Array getArray(String colName) {
        return null;
    }

    public Date getDate(int columnIndex, Calendar cal) {
        return null;
    }

    public Date getDate(String columnName, Calendar cal) {
        return null;
    }

    public Time getTime(int columnIndex, Calendar cal) {
        return null;
    }

    public Time getTime(String columnName, Calendar cal) {
        return null;
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) {
        return null;
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) {
        return null;
    }

    public URL getURL(int columnIndex) {
        return null;
    }

    public URL getURL(String columnName) {
        return null;
    }

    public void updateRef(int columnIndex, Ref x) {
    }

    public void updateRef(String columnName, Ref x) {
    }

    public void updateBlob(int columnIndex, Blob x) {
    }

    public void updateBlob(String columnName, Blob x) {
    }

    public void updateClob(int columnIndex, Clob x) {
    }

    public void updateClob(String columnName, Clob x) {
    }

    public void updateArray(int columnIndex, Array x) {
    }

    public void updateArray(String columnName, Array x) {
    }
}
