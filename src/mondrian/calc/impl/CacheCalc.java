/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/calc/impl/CacheCalc.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.calc.impl;

import mondrian.calc.Calc;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.ExpCacheDescriptor;

/**
 * Calculation which retrieves the value of an underlying calculation
 * from cache.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/calc/impl/CacheCalc.java#2 $
 * @since Oct 10, 2005
 */
public class CacheCalc extends GenericCalc {
    private final ExpCacheDescriptor key;

    public CacheCalc(Exp exp, ExpCacheDescriptor key) {
        super(exp);
        this.key = key;
    }

    public Object evaluate(Evaluator evaluator) {
        return evaluator.getCachedResult(key);
    }

    public Calc[] getCalcs() {
        return new Calc[] {key.getCalc()};
    }
}

// End CacheCalc.java
