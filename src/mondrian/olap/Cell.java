/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/Cell.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2001-2002 Kana Software, Inc.
// Copyright (C) 2001-2006 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 6 August, 2001
*/

package mondrian.olap;

/**
 * A <code>Cell</code> is an item in the grid of a {@link Result}.  It is
 * returned by {@link Result#getCell}.
 *
 * @author jhyde
 * @since 6 August, 2001
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/Cell.java#2 $
 */
public interface Cell {
    /**
     * Returns the cell's raw value. This is useful for sending to further data
     * processing, such as plotting a chart.
     *
     * <p> The value is never null. It may have various types:<ul>
     *   <li>if the cell is null, the value is  {@link Util#nullValue};</li>
     *   <li>if the cell contains an error, the value is an instance of
     *       {@link Throwable};</li>
     *   <li>otherwise, the type of this value depends upon the type of
     *       measure: possible types include {@link java.math.BigDecimal},
     *       {@link Double}, {@link Integer} and {@link String}.</li>
     * </ul>
     *
     * @post return != null
     * @post (return instanceof Throwable) == isError()
     * @post (return instanceof Util.NullCellValue) == isNull()
     */
    Object getValue();


    /**
     * Return the cached formatted string, that survives an aggregate cache clear
     */
    String getCachedFormatString();

    /**
     * Returns the cell's value formatted according to the current format
     * string, and locale-specific settings such as currency symbol. The
     * current format string may itself be derived via an expression. For more
     * information about format strings, see {@link mondrian.util.Format}.
     */
    String getFormattedValue();

    /**
     * Returns whether the cell's value is null.
     */
    boolean isNull();

    /**
     * Returns whether the cell's calculation returned an error.
     */
    boolean isError();

    /**
     * Returns a SQL query that, when executed, returns drill through data
     * for this Cell.
     * If the parameter extendedContext is true, then the
     * query will include all the levels (i.e. columns) of non-constraining members
     * (i.e. members which are at the "All" level).
     * If the parameter extendedContext is false, the query will exclude
     * the levels (coulmns) of non-constraining members.
     * The result is null if the cell is based upon a calculated member.
     *
     */
    String getDrillThroughSQL(boolean extendedContext);

    /**
     * Returns true if drill through is possible for this Cell.
     * Returns false if the Cell is based on a calculated measure.
     * @return true if can drill through on this cell
     */
    boolean canDrillThrough();

    /**
     * Returns the number of fact table rows which contributed to this Cell.
     */
    int getDrillThroughCount();

    /**
     * Returns the value of a property.
     *
     * @param propertyName Case-sensitive property name
     * @return Value of property
     */
    Object getPropertyValue(String propertyName);

    /**
     * Returns the context member for a particular dimension.
     *
     * The member is defined as follows (note that there is always a
     * member):<ul>
     *
     * <li>If the dimension appears on one of the visible axes, the context
     * member is simply the member on the current row or column.
     *
     * <li>If the dimension appears in the slicer, the context member is the
     * member of that dimension in the slier.
     *
     * <li>Otherwise, the context member is the default member of that
     * dimension (usually the 'all' member).</ul>
     */
    Member getContextMember(Dimension dimension);
}

// End Cell.java
