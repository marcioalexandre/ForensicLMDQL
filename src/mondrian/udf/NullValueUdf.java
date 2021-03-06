/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/udf/NullValueUdf.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.udf;

import mondrian.olap.Evaluator;
import mondrian.olap.Syntax;
import mondrian.olap.Util;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.Type;
import mondrian.spi.UserDefinedFunction;

/**
 * Definition of the user-defined function "NullValue" which always
 * returns Java "null".
 *
 * @author remberson,jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/udf/NullValueUdf.java#2 $
 */
public class NullValueUdf implements UserDefinedFunction {

    public String getName() {
        return "NullValue";
    }

    public String getDescription() {
        return "Returns the null value";
    }

    public Syntax getSyntax() {
        return Syntax.Function;
    }

    public Type getReturnType(Type[] parameterTypes) {
        return new NumericType();
    }

    public Type[] getParameterTypes() {
        return new Type[0];
    }

    public Object execute(Evaluator evaluator, Argument[] arguments) {
        return Util.nullValue;
    }

    public String[] getReservedWords() {
        // This function does not require any reserved words.
        return null;
    }
}

// End NullUdf.java
