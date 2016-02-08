/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap4j/MondrianOlap4jCellSet.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import mondrian.olap.Axis;
import mondrian.olap.AxisOrdinal;
import mondrian.olap.MondrianException;
import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.Result;

import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.CellSetMetaData;
import org.olap4j.Position;

/**
 * Implementation of {@link CellSet}
 * for the Mondrian OLAP engine.
 *
 * <p>This class has sub-classes which implement JDBC 3.0 and JDBC 4.0 APIs;
 * it is instantiated using {@link Factory#newCellSet}.</p>
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap4j/MondrianOlap4jCellSet.java#2 $
 * @since May 24, 2007
 */
abstract class MondrianOlap4jCellSet implements CellSet {
    final MondrianOlap4jStatement olap4jStatement;
    final Query query;
    private Result result;
    protected boolean closed;
    private final MondrianOlap4jCellSetMetaData metaData;
    private final List<CellSetAxis> axisList =
        new ArrayList<CellSetAxis>();
    private CellSetAxis filterAxis;

    public MondrianOlap4jCellSet(
        MondrianOlap4jStatement olap4jStatement,
        Query query)
    {
        assert olap4jStatement != null;
        assert query != null;
        this.olap4jStatement = olap4jStatement;
        this.query = query;
        this.closed = false;
        if (olap4jStatement instanceof MondrianOlap4jPreparedStatement) {
            this.metaData =
                ((MondrianOlap4jPreparedStatement) olap4jStatement)
                    .cellSetMetaData;
        } else {
            this.metaData =
                new MondrianOlap4jCellSetMetaData(
                    olap4jStatement, query);
        }
    }

    /**
     * Executes a query. Not part of the olap4j API; internal to the mondrian
     * driver.
     *
     * <p>This method may take some time. While it is executing, a client may
     * execute {@link MondrianOlap4jStatement#cancel()}.
     */
    void execute() {
        query.setQueryTimeoutMillis(olap4jStatement.timeoutSeconds * 1000);
        result = olap4jStatement.olap4jConnection.connection.execute(query);

        // initialize axes
        mondrian.olap.Axis[] axes = result.getAxes();
        QueryAxis[] queryAxes = result.getQuery().getAxes();
        assert axes.length == queryAxes.length;
        for (int i = 0; i < axes.length; i++) {
            Axis axis = axes[i];
            QueryAxis queryAxis = queryAxes[i];
            axisList.add(new MondrianOlap4jCellSetAxis(this, queryAxis, axis));
        }

        // initialize filter axis
        QueryAxis queryAxis = result.getQuery().getSlicerAxis();
        final Axis axis = result.getSlicerAxis();
        if (queryAxis == null) {
            // Dummy slicer axis.
            queryAxis =
                new QueryAxis(
                    false, null, AxisOrdinal.SLICER,
                    QueryAxis.SubtotalVisibility.Undefined);
        }
        filterAxis = new MondrianOlap4jCellSetAxis(this, queryAxis, axis);
    }

    public CellSetMetaData getMetaData() {
        return metaData;
    }

    public List<CellSetAxis> getAxes() {
        return axisList;
    }

    public CellSetAxis getFilterAxis() {
        return filterAxis;
    }

    public Cell getCell(List<Integer> coordinates) {
        int[] coords = new int[coordinates.size()];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = coordinates.get(i);
        }
        return getCellInternal(coords);
    }

    public Cell getCell(int ordinal) {
        final int[] pos = ordinalToCoordinateArray(ordinal);
        return getCellInternal(pos);
    }

    private int[] ordinalToCoordinateArray(int ordinal) {
        Axis[] axes = result.getAxes();
        final int[] pos = new int[axes.length];
        int modulo = 1;
        for (int i = 0; i < axes.length; i++) {
            int prevModulo = modulo;
            modulo *= axes[i].getPositions().size();
            pos[i] = (ordinal % modulo) / prevModulo;
        }
        if (ordinal < 0 || ordinal >= modulo) {
            throw new IndexOutOfBoundsException(
                "Cell ordinal " + ordinal
                    + ") lies outside CellSet bounds ("
                    + getBoundsAsString() + ")");
        }
        return pos;
    }

    public Cell getCell(Position... positions) {
        int[] coords = new int[positions.length];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = positions[i].getOrdinal();
        }
        return getCellInternal(coords);
    }

    private Cell getCellInternal(int[] pos) {
        mondrian.olap.Cell cell;
        try {
            cell = result.getCell(pos);
        } catch (MondrianException e) {
            if (e.getMessage().indexOf("coordinates out of range") >= 0) {
                throw new IndexOutOfBoundsException(
                    "Cell coordinates (" + getCoordsAsString(pos)
                        + ") fall outside CellSet bounds ("
                        + getCoordsAsString(pos) + ")");
            } else if (e.getMessage().indexOf("coordinates should have dimension") >= 0) {
                throw new IllegalArgumentException(
                    "Cell coordinates should have dimension "
                        + axisList.size() + ")");
            } else {
                throw e;
            }
        }
        return new MondrianOlap4jCell(pos, this, cell);
    }

    private String getBoundsAsString() {
        StringBuilder buf = new StringBuilder();
        Axis[] axes = result.getAxes();
        for (int i = 0; i < axes.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(axes[i].getPositions().size());
        }
        return buf.toString();
    }

    private static String getCoordsAsString(int[] pos) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < pos.length; i++) {
            int po = pos[i];
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(po);
        }
        return buf.toString();
    }

    public List<Integer> ordinalToCoordinates(int ordinal) {
        final int[] ints = ordinalToCoordinateArray(ordinal);
        final List<Integer> list = new ArrayList<Integer>(ints.length);
        for (int i : ints) {
            list.add(i);
        }
        return list;
    }

    public int coordinatesToOrdinal(List<Integer> coordinates) {
        List<CellSetAxis> axes = getAxes();
        if (coordinates.size() != axes.size()) {
            throw new IllegalArgumentException(
                "Coordinates have different dimension " + coordinates.size()
                    + " than axes " + axes.size());
        }
        int modulo = 1;
        int ordinal = 0;
        int k = 0;
        for (CellSetAxis axis : axes) {
            final Integer coordinate = coordinates.get(k++);
            if (coordinate < 0 || coordinate >= axis.getPositionCount()) {
                throw new IndexOutOfBoundsException(
                    "Coordinate " + coordinate
                        + " of axis " + k
                        + " is out of range ("
                        + getBoundsAsString() + ")");
            }
            ordinal += coordinate * modulo;
            modulo *= axis.getPositionCount();
        }
        return ordinal;
    }

    public boolean next() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException {
        this.closed = true;
    }

    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public byte getByte(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public short getShort(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getInt(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public long getLong(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public float getFloat(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public double getDouble(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(
        int columnIndex, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public byte getByte(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public short getShort(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getInt(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public long getLong(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public float getFloat(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public double getDouble(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(
        String columnLabel, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int findColumn(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getType() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(
        int columnIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(
        int columnIndex, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(
        int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(
        int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(
        int columnIndex, Reader x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(
        int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(
        String columnLabel, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(
        String columnLabel, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(String columnLabel, byte x[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(
        String columnLabel, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(
        String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(
        String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(
        String columnLabel, Reader reader, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(
        String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(
        int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(
        String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(
        int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(
        String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // implement Wrapper

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}

// End MondrianOlap4jCellSet.java
