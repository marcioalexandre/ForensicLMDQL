/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap.type;

/**
 * The type of an expression representing a date, time or timestamp.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/type/DateTimeType.java#2 $
 * @since Jan 2, 2008
 */
public class DateTimeType extends ScalarType {
    /**
     * Creates a DateTime type.
     */
    public DateTimeType() {
        super("DATETIME");
    }

    public boolean equals(Object obj) {
        return obj instanceof DateTimeType;
    }
}

// End DateTimeType.java
