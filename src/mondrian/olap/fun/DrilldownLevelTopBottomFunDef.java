/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap.fun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mondrian.calc.Calc;
import mondrian.calc.DummyExp;
import mondrian.calc.ExpCompiler;
import mondrian.calc.IntegerCalc;
import mondrian.calc.LevelCalc;
import mondrian.calc.ListCalc;
import mondrian.calc.ResultStyle;
import mondrian.calc.impl.AbstractListCalc;
import mondrian.calc.impl.ValueCalc;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Category;
import mondrian.olap.Dimension;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.FunDef;
import mondrian.olap.Level;
import mondrian.olap.Member;
import mondrian.olap.NativeEvaluator;
import mondrian.olap.SchemaReader;
import mondrian.olap.type.ScalarType;

/**
 * Definition of the <code>DrilldownLevelTop</code> and
 * <code>DrilldownLevelBottom</code> MDX builtin functions.
 *
 * <p>Syntax:
 *
 * <blockquote><pre>
 * DrilldownLevelTop(Set_Expression, Count [, [Level_Expression][, Numeric_Expression]])
 * DrilldownLevelBottom(Set_Expression, Count [, [Level_Expression][, Numeric_Expression]])
 * </pre></blockquote>
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/DrilldownLevelTopBottomFunDef.java#3 $
 * @since Oct 18, 2007
 */
class DrilldownLevelTopBottomFunDef extends FunDefBase {
    final boolean top;

    static final MultiResolver DrilldownLevelTopResolver = new MultiResolver(
            "DrilldownLevelTop",
            "DrilldownLevelTop(Set_Expression, Count [, [Level_Expression][, Numeric_Expression]])",
            "Drills down the topmost members of a set, at a specified level, to one level below.",
            new String[] {"fxxn", "fxxnl", "fxxnln", "fxxnen"}) {
        protected FunDef createFunDef(Exp[] args, FunDef dummyFunDef) {
            return new DrilldownLevelTopBottomFunDef(dummyFunDef, true);
        }
    };

    static final MultiResolver DrilldownLevelBottomResolver = new MultiResolver(
            "DrilldownLevelBottom",
            "DrilldownLevelBottom(Set_Expression, Count [, [Level_Expression][, Numeric_Expression]])",
            "Drills down the bottommost members of a set, at a specified level, to one level below.",
            new String[] {"fxxn", "fxxnl", "fxxnln", "fxxnen"}) {
        protected FunDef createFunDef(Exp[] args, FunDef dummyFunDef) {
            return new DrilldownLevelTopBottomFunDef(dummyFunDef, false);
        }
    };

    public DrilldownLevelTopBottomFunDef(FunDef dummyFunDef, final boolean top) {
        super(dummyFunDef);
        this.top = top;
    }

    public Calc compileCall(final ResolvedFunCall call, ExpCompiler compiler) {
        // Compile the member list expression. Ask for a mutable list, because
        // we're going to insert members into it later.
        final ListCalc listCalc =
            compiler.compileList(call.getArg(0), true);
        final IntegerCalc integerCalc =
            compiler.compileInteger(call.getArg(1));
        final LevelCalc levelCalc =
            call.getArgCount() > 2
                && call.getArg(2).getCategory() != Category.Empty
                ? compiler.compileLevel(call.getArg(2))
                : null;
        final Calc orderCalc =
                call.getArgCount() > 3 ?
                compiler.compileScalar(call.getArg(3), true) :
                new ValueCalc(
                    new DummyExp(
                        new ScalarType()));
        return new AbstractListCalc(call, new Calc[] {listCalc, integerCalc, orderCalc}) {
            public List evaluateList(Evaluator evaluator) {
                // Use a native evaluator, if more efficient.
                // TODO: Figure this out at compile time.
                SchemaReader schemaReader = evaluator.getSchemaReader();
                NativeEvaluator nativeEvaluator =
                    schemaReader.getNativeSetEvaluator(
                        call.getFunDef(), call.getArgs(), evaluator, this);
                if (nativeEvaluator != null) {
                    return (List) nativeEvaluator.execute(ResultStyle.LIST);
                }

                List<Member> list = listCalc.evaluateList(evaluator);
                int n = integerCalc.evaluateInteger(evaluator);
                if (n == FunUtil.IntegerNull || n <= 0) {
                    return list;
                }
                Level level;
                if (levelCalc == null) {
                    level = null;
                } else {
                    level = levelCalc.evaluateLevel(evaluator);
                }
                List<Member> result = new ArrayList<Member>();
                for (Member member : list) {
                    result.add(member);
                    if (level != null && member.getLevel() != level) {
                        if (level.getDimension() != member.getDimension()) {
                            throw newEvalException(
                                DrilldownLevelTopBottomFunDef.this,
                                "Level '"
                                    + level.getUniqueName()
                                    + "' not compatible with member '"
                                    + member.getUniqueName()
                                    + "'");
                        }
                        continue;
                    }
                    Member[] children = schemaReader.getMemberChildren(member);
                    sortMembers(
                        evaluator.push(),
                        Arrays.asList(children),
                        orderCalc,
                        top,
                        true);
                    int x = Math.min(n, children.length);
                    for (int i = 0; i < x; i++) {
                        result.add(children[i]);
                    }
                }
                return result;
            }

            public boolean dependsOn(Dimension dimension) {
                return anyDependsButFirst(getCalcs(), dimension);
            }
        };
    }
}

// End DrilldownLevelTopBottomFunDef.java
