/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/ValueFunDef.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2002-2002 Kana Software, Inc.
// Copyright (C) 2002-2006 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, Jun 14, 2002
*/

package mondrian.olap.fun;

import java.io.PrintWriter;

import mondrian.olap.Category;
import mondrian.olap.Exp;
import mondrian.olap.ExpBase;
import mondrian.olap.Syntax;
import mondrian.olap.Validator;
import mondrian.olap.type.Type;

/**
 * A <code>ValueFunDef</code> is a pseudo-function to evaluate a member or
 * a tuple. Similar to {@link TupleFunDef}.
 *
 * @author jhyde
 * @since Jun 14, 2002
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/ValueFunDef.java#2 $
 */
class ValueFunDef extends FunDefBase {
    private final int[] argTypes;

    ValueFunDef(int[] argTypes) {
        super(
            "_Value()",
            "_Value([<Member>, ...])",
            "Pseudo-function which evaluates a tuple.",
            Syntax.Parentheses,
            Category.Numeric,
            argTypes);
        this.argTypes = argTypes;
    }

    public int getReturnCategory() {
        return Category.Tuple;
    }

    public int[] getParameterCategories() {
        return argTypes;
    }

    public void unparse(Exp[] args, PrintWriter pw) {
        ExpBase.unparseList(pw, args, "(", ", ", ")");
    }

    public Type getResultType(Validator validator, Exp[] args) {
        return null;
    }

}

// End ValueFunDef.java
