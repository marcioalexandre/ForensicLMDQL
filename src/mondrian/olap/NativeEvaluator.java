/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/NativeEvaluator.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap;

import mondrian.calc.ResultStyle;

/**
 * Allows expressions to be evaluated native, e.g. in SQL.
 *
 * @author av
 * @since Nov 11, 2005
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/NativeEvaluator.java#2 $
 */

public interface NativeEvaluator {
    Object execute(ResultStyle resultStyle);
}

// End NativeEvaluator.java