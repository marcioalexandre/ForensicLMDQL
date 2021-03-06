/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/TupleFunDef.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2002-2002 Kana Software, Inc.
// Copyright (C) 2002-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 3 March, 2002
*/
package mondrian.olap.fun;
import java.io.PrintWriter;

import mondrian.calc.Calc;
import mondrian.calc.ExpCompiler;
import mondrian.calc.MemberCalc;
import mondrian.calc.impl.AbstractTupleCalc;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Category;
import mondrian.olap.Dimension;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.ExpBase;
import mondrian.olap.FunDef;
import mondrian.olap.Member;
import mondrian.olap.Syntax;
import mondrian.olap.Validator;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.TupleType;
import mondrian.olap.type.Type;
import mondrian.olap.type.TypeUtil;
import mondrian.resource.MondrianResource;

/**
 * <code>TupleFunDef</code> implements the '( ... )' operator which builds
 * tuples, as in <code>([Time].CurrentMember,
 * [Stores].[USA].[California])</code>.
 *
 * @author jhyde
 * @since 3 March, 2002
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/fun/TupleFunDef.java#2 $
 */
public class TupleFunDef extends FunDefBase {
    private final int[] argTypes;
    static final ResolverImpl Resolver = new ResolverImpl();

    private TupleFunDef(int[] argTypes) {
        super(
            "()",
            "(<Member> [, <Member>]...)",
            "Parenthesis operator constructs a tuple.  If there is only one member, the expression is equivalent to the member expression.",
            Syntax.Parentheses,
            Category.Tuple,
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
        // _Tuple(<Member1>[,<MemberI>]...), which is written
        // (<Member1>[,<MemberI>]...), has type [Hie1] x ... x [HieN].
        //
        // If there is only one member, it merely represents a parenthesized
        // expression, whose Hierarchy is that of the member.
        if (args.length == 1) {
            return TypeUtil.toMemberType(args[0].getType());
        } else {
            MemberType[] types = new MemberType[args.length];
            for (int i = 0; i < args.length; i++) {
                Exp arg = args[i];
                types[i] = TypeUtil.toMemberType(arg.getType());
            }
            checkDimensions(types);
            return new TupleType(types);
        }
    }

    public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
        final Exp[] args = call.getArgs();
        final MemberCalc[] memberCalcs = new MemberCalc[args.length];
        for (int i = 0; i < args.length; i++) {
            memberCalcs[i] = compiler.compileMember(args[i]);
        }
        return new CalcImpl(call, memberCalcs);
    }

    private void checkDimensions(MemberType[] memberTypes) {
        for (int i = 0; i < memberTypes.length; i++) {
            MemberType memberType = memberTypes[i];
            for (int j = 0; j < i; j++) {
                MemberType member1 = memberTypes[j];
                final Dimension dimension = memberType.getDimension();
                final Dimension dimension1 = member1.getDimension();
                if (dimension != null && dimension == dimension1) {
                    throw MondrianResource.instance().DupDimensionsInTuple.ex(
                            dimension.getUniqueName());
                }
            }
        }
    }

    public static class CalcImpl extends AbstractTupleCalc {
        private final MemberCalc[] memberCalcs;

        public CalcImpl(ResolvedFunCall call, MemberCalc[] memberCalcs) {
            super(call, memberCalcs);
            this.memberCalcs = memberCalcs;
        }

        public Member[] evaluateTuple(Evaluator evaluator) {
            final Member[] members = new Member[memberCalcs.length];
            for (int i = 0; i < members.length; i++) {
                final Member member = members[i]
                        = memberCalcs[i].evaluateMember(evaluator);
                if (member == null || member.isNull()) {
                    return null;
                }
            }
            return members;
        }

        public MemberCalc[] getMemberCalcs() {
            return memberCalcs;
        }
    }

    private static class ResolverImpl extends ResolverBase {
        public ResolverImpl() {
            super("()", null, null, Syntax.Parentheses);
        }

        public FunDef resolve(
                Exp[] args, Validator validator, int[] conversionCount) {
            // Compare with TupleFunDef.getReturnCategory().  For example,
            //   ([Gender].members) is a set,
            //   ([Gender].[M]) is a member,
            //   (1 + 2) is a numeric,
            // but
            //   ([Gender].[M], [Marital Status].[S]) is a tuple.
            if (args.length == 1) {
                return new ParenthesesFunDef(args[0].getCategory());
            } else {
                final int[] argTypes = new int[args.length];
                for (int i = 0; i < args.length; i++) {
                    Exp arg = args[i];
                    // Arg must be a member:
                    //  OK: ([Gender].[S], [Time].[1997])   (member, member)
                    //  OK: ([Gender], [Time])           (dimension, dimension)
                    // Not OK: ([Gender].[S], [Store].[Store City]) (member, level)
                    if (!validator.canConvert(
                            arg, Category.Member, conversionCount)) {
                        return null;
                    }
                    argTypes[i] = Category.Member;
                }
                return new TupleFunDef(argTypes);
            }
        }
    }
}

// End TupleFunDef.java
