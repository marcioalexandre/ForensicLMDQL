/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap.fun;

import mondrian.calc.BooleanCalc;
import mondrian.calc.Calc;
import mondrian.calc.ExpCompiler;
import mondrian.calc.ResultStyle;
import mondrian.calc.StringCalc;
import mondrian.calc.impl.AbstractBooleanCalc;
import mondrian.calc.impl.AbstractStringCalc;
import mondrian.calc.impl.GenericCalc;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.Validator;
import mondrian.olap.type.Type;
import mondrian.olap.type.TypeUtil;

/**
 * Definition of the <code>Iif</code> MDX function.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/IifFunDef.java#2 $
 * @since Jan 17, 2008
 */
public class IifFunDef extends FunDefBase {
    protected IifFunDef(
        String name,
        String description,
        String flags)
    {
        super(name, description, flags);
    }

    public Type getResultType(Validator validator, Exp[] args) {
        return TypeUtil.computeCommonType(
            true, args[1].getType(), args[2].getType());
    }

    public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
        final BooleanCalc booleanCalc =
            compiler.compileBoolean(call.getArg(0));
        final Calc calc1 =
            compiler.compileAs(
                call.getArg(1), call.getType(), ResultStyle.ANY_LIST);
        final Calc calc2 =
            compiler.compileAs(
                call.getArg(2), call.getType(), ResultStyle.ANY_LIST);
        return new GenericCalc(call) {
            public Object evaluate(Evaluator evaluator) {
                final boolean b =
                    booleanCalc.evaluateBoolean(evaluator);
                Calc calc = b ? calc1 : calc2;
                return calc.evaluate(evaluator);
            }

            public Calc[] getCalcs() {
                return new Calc[] {booleanCalc, calc1, calc2};
            }
        };
    }

    // IIf(<Logical Expression>, <String Expression>, <String Expression>)
    static final FunDefBase STRING_INSTANCE = new FunDefBase(
        "IIf",
        "Returns one of two string values determined by a logical test.",
        "fSbSS")
    {
        public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
            final BooleanCalc booleanCalc =
                compiler.compileBoolean(call.getArg(0));
            final StringCalc calc1 = compiler.compileString(call.getArg(1));
            final StringCalc calc2 = compiler.compileString(call.getArg(2));
            return new AbstractStringCalc(
                call, new Calc[] {booleanCalc, calc1, calc2}) {
                public String evaluateString(Evaluator evaluator) {
                    final boolean b =
                        booleanCalc.evaluateBoolean(evaluator);
                    StringCalc calc = b ? calc1 : calc2;
                    return calc.evaluateString(evaluator);
                }
            };
        }
    };

    // IIf(<Logical Expression>, <Numeric Expression>, <Numeric Expression>)
    static final FunDefBase NUMERIC_INSTANCE =
        new IifFunDef(
            "IIf",
            "Returns one of two numeric values determined by a logical test.",
            "fnbnn")
        {
            public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
                final BooleanCalc booleanCalc =
                    compiler.compileBoolean(call.getArg(0));
                final Calc calc1 = compiler.compileScalar(call.getArg(1), true);
                final Calc calc2 = compiler.compileScalar(call.getArg(2), true);
                return new GenericCalc(call) {
                    public Object evaluate(Evaluator evaluator) {
                        final boolean b =
                            booleanCalc.evaluateBoolean(evaluator);
                        Calc calc = b ? calc1 : calc2;
                        return calc.evaluate(evaluator);
                    }

                    public Calc[] getCalcs() {
                        return new Calc[] {booleanCalc, calc1, calc2};
                    }
                };
            }
        };

    // IIf(<Logical Expression>, <Tuple Expression>, <Tuple Expression>)
    static final FunDefBase TUPLE_INSTANCE =
        new IifFunDef(
            "IIf",
            "Returns one of two tuples determined by a logical test.",
            "ftbtt")
        {
            public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
                final BooleanCalc booleanCalc =
                    compiler.compileBoolean(call.getArg(0));
                final Calc calc1 = compiler.compileTuple(call.getArg(1));
                final Calc calc2 = compiler.compileTuple(call.getArg(2));
                return new GenericCalc(call) {
                    public Object evaluate(Evaluator evaluator) {
                        final boolean b =
                            booleanCalc.evaluateBoolean(evaluator);
                        Calc calc = b ? calc1 : calc2;
                        return calc.evaluate(evaluator);
                    }

                    public Calc[] getCalcs() {
                        return new Calc[] {booleanCalc, calc1, calc2};
                    }
                };
            }
        };

    // IIf(<Logical Expression>, <Boolean Expression>, <Boolean Expression>)
    static final FunDefBase BOOLEAN_INSTANCE = new FunDefBase(
        "IIf",
        "Returns boolean determined by a logical test.",
        "fbbbb")
    {
        public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
            final BooleanCalc booleanCalc =
                compiler.compileBoolean(call.getArg(0));
            final BooleanCalc booleanCalc1 =
                compiler.compileBoolean(call.getArg(1));
            final BooleanCalc booleanCalc2 =
                compiler.compileBoolean(call.getArg(2));
            Calc[] calcs = {booleanCalc, booleanCalc1, booleanCalc2};
            return new AbstractBooleanCalc(call, calcs) {
                public boolean evaluateBoolean(Evaluator evaluator) {
                    final boolean condition =
                        booleanCalc.evaluateBoolean(evaluator);
                    if (condition) {
                        return booleanCalc1.evaluateBoolean(evaluator);
                    } else {
                        return booleanCalc2.evaluateBoolean(evaluator);
                    }
                }
            };
        }
    };

    // IIf(<Logical Expression>, <Member Expression>, <Member Expression>)
    static final IifFunDef MEMBER_INSTANCE =
        new IifFunDef(
            "IIf",
            "Returns one of two member values determined by a logical test.",
            "fmbmm");

    // IIf(<Logical Expression>, <Level Expression>, <Level Expression>)
    static final IifFunDef LEVEL_INSTANCE =
        new IifFunDef(
            "IIf",
            "Returns one of two level values determined by a logical test.",
            "flbll");

    // IIf(<Logical Expression>, <Hierarchy Expression>, <Hierarchy Expression>)
    static final IifFunDef HIERARCHY_INSTANCE =
        new IifFunDef(
            "IIf",
            "Returns one of two hierarchy values determined by a logical test.",
            "fhbhh");

    // IIf(<Logical Expression>, <Dimension Expression>, <Dimension Expression>)
    static final IifFunDef DIMENSION_INSTANCE =
        new IifFunDef(
            "IIf",
            "Returns one of two dimension values determined by a logical test.",
            "fdbdd");

    // IIf(<Logical Expression>, <Set Expression>, <Set Expression>)
    static final IifFunDef SET_INSTANCE =
        new IifFunDef(
            "IIf",
            "Returns one of two set values determined by a logical test.",
            "fxbxx");
}

// End IifFunDef.java
