/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/CaseMatchFunDef.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap.fun;

import java.util.ArrayList;
import java.util.List;

import mondrian.calc.Calc;
import mondrian.calc.ExpCompiler;
import mondrian.calc.impl.ConstantCalc;
import mondrian.calc.impl.GenericCalc;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.FunDef;
import mondrian.olap.Syntax;
import mondrian.olap.Util;
import mondrian.olap.Validator;

/**
 * Definition of the matched <code>CASE</code> MDX operator.
 *
 * Syntax is:
 * <blockquote><pre><code>Case &lt;Expression&gt;
 * When &lt;Expression&gt; Then &lt;Expression&gt;
 * [...]
 * [Else &lt;Expression&gt;]
 * End</code></blockquote>.
 *
 * @see CaseTestFunDef
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/CaseMatchFunDef.java#2 $
 * @since Mar 23, 2006
 */
class CaseMatchFunDef extends FunDefBase {
    static final ResolverImpl Resolver = new ResolverImpl();

    private CaseMatchFunDef(FunDef dummyFunDef) {
        super(dummyFunDef);
    }

    public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
        final Exp[] args = call.getArgs();
        final List<Calc> calcList = new ArrayList<Calc>();
        final Calc valueCalc =
                compiler.compileScalar(args[0], true);
        calcList.add(valueCalc);
        final int matchCount = (args.length - 1) / 2;
        final Calc[] matchCalcs = new Calc[matchCount];
        final Calc[] exprCalcs = new Calc[matchCount];
        for (int i = 0, j = 1; i < exprCalcs.length; i++) {
            matchCalcs[i] = compiler.compileScalar(args[j++], true);
            calcList.add(matchCalcs[i]);
            exprCalcs[i] = compiler.compile(args[j++]);
            calcList.add(exprCalcs[i]);
        }
        final Calc defaultCalc =
                args.length % 2 == 0 ?
                compiler.compile(args[args.length - 1]) :
                ConstantCalc.constantNull(call.getType());
        calcList.add(defaultCalc);
        final Calc[] calcs = calcList.toArray(new Calc[calcList.size()]);

        return new GenericCalc(call) {
            public Object evaluate(Evaluator evaluator) {

                Object value = valueCalc.evaluate(evaluator);
                for (int i = 0; i < matchCalcs.length; i++) {
                    Object match = matchCalcs[i].evaluate(evaluator);
                    if (match.equals(value)) {
                        return exprCalcs[i].evaluate(evaluator);
                    }
                }
                return defaultCalc.evaluate(evaluator);
            }

            public Calc[] getCalcs() {
                return calcs;
            }
        };
    }

    private static class ResolverImpl extends ResolverBase {
        private ResolverImpl() {
            super(
                    "_CaseMatch",
                    "Case <Expression> When <Expression> Then <Expression> [...] [Else <Expression>] End",
                    "Evaluates various expressions, and returns the corresponding expression for the first which matches a particular value.",
                    Syntax.Case);
        }

        public FunDef resolve(
                Exp[] args, Validator validator, int[] conversionCount) {
            if (args.length < 3) {
                return null;
            }
            int valueType = args[0].getCategory();
            int returnType = args[2].getCategory();
            int j = 0;
            int clauseCount = (args.length - 1) / 2;
            int mismatchingArgs = 0;
            if (!validator.canConvert(args[j++], valueType, conversionCount)) {
                mismatchingArgs++;
            }
            for (int i = 0; i < clauseCount; i++) {
                if (!validator.canConvert(args[j++], valueType, conversionCount)) {
                    mismatchingArgs++;
                }
                if (!validator.canConvert(args[j++], returnType, conversionCount)) {
                    mismatchingArgs++;
                }
            }
            if (j < args.length) {
                if (!validator.canConvert(args[j++], returnType, conversionCount)) {
                    mismatchingArgs++;
                }
            }
            Util.assertTrue(j == args.length);
            if (mismatchingArgs != 0) {
                return null;
            }

            FunDef dummy = createDummyFunDef(this, returnType, args);
            return new CaseMatchFunDef(dummy);
        }

        public boolean requiresExpression(int k) {
            return true;
        }
    }
}

// End CaseMatchFunDef.java
