/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/calc/DateTimeCalc.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.calc;

import java.util.Date;

import mondrian.olap.Evaluator;

/**
 * Compiled expression whose result is a {@link Date}, representing an MDX
 * DateTime value.
 *
 * <p>When implementing this interface, it is convenient to extend
 * {@link mondrian.calc.impl.AbstractDateTimeCalc}, but it is not required.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/calc/DateTimeCalc.java#2 $
 * @since Sep 26, 2005
 */
public interface DateTimeCalc extends Calc {
    /**
     * Evaluates this expression to yield a {@link Date} value.
     *
     * @param evaluator Evaluation context
     * @return evaluation result
     */
    Date evaluateDateTime(Evaluator evaluator);
}

// End DateTimeCalc.java
