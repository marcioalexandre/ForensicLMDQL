/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/SqlStatement.java#3 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.rolap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import mondrian.lmdql.Database;
import mondrian.lmdql.Parameters;
import mondrian.olap.Util;

import com.desenvolvatec.sql2xquery.xquery.XQuery;

/**
 * SqlStatement contains a SQL statement and associated resources throughout
 * its lifetime.
 *
 * <p>The goal of SqlStatement is to make tracing, error-handling and
 * resource-management easier. None of the methods throws a SQLException;
 * if an error occurs in one of the methods, the method wraps the exception
 * in a {@link RuntimeException} describing the high-level operation, logs
 * that the operation failed, and throws that RuntimeException.
 *
 * <p>If methods succeed, the method generates lifecycle logging such as
 * the elapsed time and number of rows fetched.
 *
 * <p>There are a few obligations on the caller. The caller must:<ul>
 * <li>call the {@link #handle(Exception)} method if one of the contained
 *     objects (say the {@link java.sql.ResultSet}) gives an error;
 * <li>call the {@link #close()} method if all operations complete
 *     successfully.
 * <li>increment the {@link #rowCount} field each time a row is fetched.
 * </ul>
 *
 * <p>The {@link #close()} method is idempotent. You are welcome to call it
 * more than once.
 *
 * <p>SqlStatement is not thread-safe.
 *
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/SqlStatement.java#3 $
 * @author jhyde
 * @since 2.3
 */
public class SqlStatement {
    
    private final DataSource dataSource;
    private Connection jdbcConnection;
    private ResultSet resultSet;
    private final String sql;
    private final int maxRows;
    private final String component;
    private final int resultSetType;
    private final int resultSetConcurrency;
    private final RolapUtil.Semaphore querySemaphore = RolapUtil
        .getQuerySemaphore();
    private final String message;
    private boolean haveSemaphore;
    public int rowCount;
    private long startTime;
    
    // used for SQL logging, allows for a SQL Statement UID
    private static long executeCount = -1;
    
    SqlStatement(
        DataSource dataSource,
        String sql,
        int maxRows,
        String component,
        String message,
        int resultSetType,
        int resultSetConcurrency)
    {
        this.dataSource = dataSource;
        this.sql = sql;
        this.maxRows = maxRows;
        this.component = component;
        this.message = message;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
    }

    public void execute() throws SQLException {
        if(Parameters.databaseType==Database.RELATIONAL)
            this.jdbcConnection = dataSource.getConnection();
        else // n�o tem conex�o jdbc e sim com XML #Paulo Caetano
            this.jdbcConnection = null;
        querySemaphore.enter();
        haveSemaphore = true;
        Statement statement = null;
        String status = "failed";
        long currId = 0;
        // Trace start of execution.
        if (RolapUtil.SQL_LOGGER.isDebugEnabled()) {
            currId = ++executeCount;
            StringBuffer sqllog = new StringBuffer();
            sqllog.append(currId + ": " + component + ": executing sql [");
            if (sql.indexOf('\n') >= 0) {
                // SQL appears to be formatted as multiple lines. Make it
                // start on its own line.
                sqllog.append("\n");
            }
            sqllog.append(sql);
            sqllog.append(']');
            RolapUtil.SQL_LOGGER.debug(sqllog.toString());
        }

        // Execute hook.
        RolapUtil.ExecuteQueryHook hook = RolapUtil.threadHooks.get();
        if (hook != null) {
            hook.onExecuteQuery(sql);
        }
        try {
            System.out.println("SQL: "+sql);
            startTime = System.currentTimeMillis();
            if(Parameters.databaseType==Database.RELATIONAL){ // executando relacional #Paulo Caetano
	            if (resultSetType < 0 || resultSetConcurrency < 0) {
	                statement = jdbcConnection.createStatement();
	            } else {
	                statement = jdbcConnection.createStatement(
	                    resultSetType,
	                    resultSetConcurrency);
	            }
	            if (maxRows > 0) {
	                statement.setMaxRows(maxRows);
	            }
            	this.resultSet = statement.executeQuery(sql);
            } else {// submetendo ao XML do DB2 e n�o ao relacional MySQL #Paulo Caetano
            	// 	Em alguns pontos n�o pude localizar onde era carregado o connectString do Mondrian, nem de onde pegava,
            	// por isso tive que criar essa vari�vel, para atribuir um valor padr�o, caso n�o achasse #Paulo Caetano
            	String sql2xqueryWebPath = Parameters.mdxConnProperties.get(RolapConnectionProperties.SQL2XQuery.name())==null?
            									"/WEB-INF/lib/sql2xquery.xml": // padr�o
            									Parameters.mdxConnProperties.get(RolapConnectionProperties.SQL2XQuery.name());
            	String sql2xqueryName = Parameters.pathServer + File.separator + sql2xqueryWebPath;
                File sql2xqueryFile = new File(sql2xqueryName);
                // carregando o conte�do do arquivo de configura��o sql2xquery
                if(Parameters.xmlSQL2XQueryContent==null){
                	InputStream is = new FileInputStream(sql2xqueryFile);
                    String texto = "";
                    try{ // lendo conte�do do xml (catalog) 
                        byte[] buffer = new byte[4096]; int read = -1;
                        while((read = is.read(buffer,0,buffer.length))!=-1)
                        	texto += new String(buffer,0,read);
                    } catch(Exception e){
                        throw Util.newError("Cannot get config file content: " +
                        		sql2xqueryName);
                    }
                    Parameters.xmlSQL2XQueryContent = texto; // colocando como par�metro
                	
                }
            	this.resultSet = new XQuery(Parameters.xmlSQL2XQueryContent).executeSQLQuery(Database.getDB2Conn(sql2xqueryFile),sql);
            }
            long time = System.currentTimeMillis();
            final long execMs = time - startTime;
            Util.addDatabaseTime(execMs);
            status = ", exec " + execMs + " ms";
        } catch (SQLException e) {
            status = ", failed (" + e + ")";
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e2) {
                // ignore
            }
            throw handle(e);
        } catch (Exception e){
        	System.out.println("Error in XQuery:");
        	e.printStackTrace();
        } finally {
            RolapUtil.SQL_LOGGER.debug(currId + ": " + status);

            if (RolapUtil.LOGGER.isDebugEnabled()) {
                RolapUtil.LOGGER.debug(component + ": executing sql [" +
                    sql + "]" + status);
            }
        }
    }

    /**
     * Closes all resources (statement, result set) held by this
     * SqlStatement.
     *
     * <p>If any of them fails, wraps them in a
     * {@link RuntimeException} describing the high-level operation which
     * this statement was performing. No further error-handling is required
     * to produce a descriptive stack trace, unless you want to absorb the
     * error.
     */
    public void close() {
        if (haveSemaphore) {
            haveSemaphore = false;
            querySemaphore.leave();
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw Util.newError(message + "; sql=[" + sql + "]");
            } finally {
                resultSet = null;
            }
        }
        if (jdbcConnection != null) {
            try {
                jdbcConnection.close();
            } catch (SQLException e) {
                throw Util.newError(message + "; sql=[" + sql + "]");
            } finally {
                jdbcConnection = null;
            }
        }
        long time = System.currentTimeMillis();
        long totalMs = time - startTime;
        String status = ", exec+fetch " + totalMs + " ms, " + rowCount + " rows";

        RolapUtil.SQL_LOGGER.debug(executeCount + ": " + status);
        
        if (RolapUtil.LOGGER.isDebugEnabled()) {
            RolapUtil.LOGGER.debug(component + ": done executing sql [" +
                sql + "]" + status);
        }        
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Handles an exception thrown from the ResultSet, implicitly calls
     * {@link #close}, and returns an exception which includes the full
     * stack, including a description of the high-level operation.
     *
     * @param e Exception
     * @return Runtime exception
     */
    public RuntimeException handle(Exception e) {
        RuntimeException runtimeException =
            Util.newError(e, message + "; sql=[" + sql + "]");
        try {
            close();
        } catch (RuntimeException re) {
            // ignore
        }
        return runtimeException;
    }
}

// End SqlStatement.java
