/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/RolapMeasure.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2001-2002 Kana Software, Inc.
// Copyright (C) 2001-2006 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 10 August, 2001
*/

package mondrian.rolap;
import mondrian.olap.CellFormatter;
import mondrian.olap.Member;

/**
 * Interface implemented by all measures (both stored and calculated).
 *
 * @author jhyde
 * @since 10 August, 2001
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/RolapMeasure.java#2 $
 */
public interface RolapMeasure extends Member {
    /**
     * Returns the object that formats cells of this measure, or null to use
     * default formatting.
     *
     * @return formatter
     */
    CellFormatter getFormatter();
}

// End RolapMeasure.java
