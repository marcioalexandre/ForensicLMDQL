/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/calc/impl/AbstractExpCompiler.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.calc.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mondrian.calc.BooleanCalc;
import mondrian.calc.Calc;
import mondrian.calc.DateTimeCalc;
import mondrian.calc.DimensionCalc;
import mondrian.calc.DoubleCalc;
import mondrian.calc.DummyExp;
import mondrian.calc.ExpCompiler;
import mondrian.calc.HierarchyCalc;
import mondrian.calc.IntegerCalc;
import mondrian.calc.IterCalc;
import mondrian.calc.LevelCalc;
import mondrian.calc.ListCalc;
import mondrian.calc.MemberCalc;
import mondrian.calc.ParameterSlot;
import mondrian.calc.ResultStyle;
import mondrian.calc.StringCalc;
import mondrian.calc.TupleCalc;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.Parameter;
import mondrian.olap.Util;
import mondrian.olap.Validator;
import mondrian.olap.fun.CastFunDef;
import mondrian.olap.fun.DimensionCurrentMemberFunDef;
import mondrian.olap.fun.HierarchyCurrentMemberFunDef;
import mondrian.olap.fun.HierarchyDimensionFunDef;
import mondrian.olap.fun.LevelHierarchyFunDef;
import mondrian.olap.fun.MemberHierarchyFunDef;
import mondrian.olap.fun.MemberLevelFunDef;
import mondrian.olap.type.BooleanType;
import mondrian.olap.type.DecimalType;
import mondrian.olap.type.DimensionType;
import mondrian.olap.type.HierarchyType;
import mondrian.olap.type.LevelType;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NullType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.ScalarType;
import mondrian.olap.type.StringType;
import mondrian.olap.type.TupleType;
import mondrian.olap.type.Type;
import mondrian.olap.type.TypeUtil;
import mondrian.resource.MondrianResource;

/**
 * Abstract implementation of the {@link mondrian.calc.ExpCompiler} interface.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/calc/impl/AbstractExpCompiler.java#2 $
 * @since Sep 29, 2005
 */
public class AbstractExpCompiler implements ExpCompiler {
    private final Evaluator evaluator;
    private final Validator validator;
    private final Map<Parameter, ParameterSlotImpl> parameterSlots =
        new HashMap<Parameter, ParameterSlotImpl>();
    private List<ResultStyle> resultStyles;

    /**
     * Creates an AbstractExpCompiler
     *
     * @param evaluator Evaluator
     * @param validator Validator
     */
    public AbstractExpCompiler(Evaluator evaluator, Validator validator) {
        this(evaluator, validator, ResultStyle.ANY_LIST);
    }

    /**
     * Creates an AbstractExpCompiler which is constrained to produce one of
     * a set of result styles.
     *
     * @param evaluator Evaluator
     * @param validator Validator
     * @param resultStyles List of result styles, preferred first, must not be
     */
    public AbstractExpCompiler(
        Evaluator evaluator,
        Validator validator,
        List<ResultStyle> resultStyles)
    {
        this.evaluator = evaluator;
        this.validator = validator;
        this.resultStyles = (resultStyles == null)
            ? ResultStyle.ANY_LIST : resultStyles;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public Validator getValidator() {
        return validator;
    }

    /**
     * {@inheritDoc}
     *
     * Uses the current ResultStyle to compile the expression.
     */
    public Calc compile(Exp exp) {
        return exp.accept(this);
    }

    /**
     * {@inheritDoc}
     *
     * Uses a new ResultStyle to compile the expression.
     */
    public Calc compileAs(
        Exp exp,
        Type resultType,
        List<ResultStyle> preferredResultTypes)
    {
        assert preferredResultTypes != null;
        if (Util.Retrowoven) {
            // Copy and replace ITERABLE
            // A number of functions declare that they can accept
            // ITERABLEs so here is where that those are converted to innocent
            // LISTs for jdk1.4 and other retrowoven code.
            List<ResultStyle> tmp =
                new ArrayList<ResultStyle>(preferredResultTypes.size());
            for (ResultStyle preferredResultType : preferredResultTypes) {
                tmp.add(
                    (preferredResultType == ResultStyle.ITERABLE)
                        ? ResultStyle.LIST
                        : preferredResultType);
            }
            preferredResultTypes = tmp;
        }
        List<ResultStyle> save = this.resultStyles;
        try {
            this.resultStyles = preferredResultTypes;
            if (resultType != null && resultType != exp.getType()) {
                if (resultType instanceof MemberType) {
                    return compileMember(exp);
                } else if (resultType instanceof LevelType) {
                    return compileLevel(exp);
                } else if (resultType instanceof HierarchyType) {
                    return compileHierarchy(exp);
                } else if (resultType instanceof DimensionType) {
                    return compileDimension(exp);
                }
            }
            return compile(exp);
        } finally {
            this.resultStyles = save;
        }
    }

    public MemberCalc compileMember(Exp exp) {
        final Type type = exp.getType();
        if (type instanceof DimensionType) {
            final DimensionCalc dimensionCalc = compileDimension(exp);
            return new DimensionCurrentMemberFunDef.CalcImpl(
                    new DummyExp(TypeUtil.toMemberType(type)), dimensionCalc);
        } else if (type instanceof HierarchyType) {
            final HierarchyCalc hierarchyCalc = compileHierarchy(exp);
            return new HierarchyCurrentMemberFunDef.CalcImpl(
                    new DummyExp(TypeUtil.toMemberType(type)), hierarchyCalc);
        } else if (type instanceof NullType) {
            throw MondrianResource.instance().NullNotSupported.ex();
        }
        assert type instanceof MemberType;
        return (MemberCalc) compile(exp);
    }

    public LevelCalc compileLevel(Exp exp) {
        final Type type = exp.getType();
        if (type instanceof MemberType) {
            // <Member> --> <Member>.Level
            final MemberCalc memberCalc = compileMember(exp);
            return new MemberLevelFunDef.CalcImpl(
                    new DummyExp(LevelType.forType(type)),
                    memberCalc);
        }
        assert type instanceof LevelType;
        return (LevelCalc) compile(exp);
    }

    public DimensionCalc compileDimension(Exp exp) {
        final Type type = exp.getType();
        if (type instanceof HierarchyType) {
            final HierarchyCalc hierarchyCalc = compileHierarchy(exp);
            return new HierarchyDimensionFunDef.CalcImpl(
                new DummyExp(new DimensionType(type.getDimension())),
                    hierarchyCalc);
        }
        assert type instanceof DimensionType : type;
        return (DimensionCalc) compile(exp);
    }

    public HierarchyCalc compileHierarchy(Exp exp) {
        final Type type = exp.getType();
        if (type instanceof DimensionType ||
                type instanceof MemberType) {
            // <Dimension> --> <Dimension>.CurrentMember.Hierarchy
            final MemberCalc memberCalc = compileMember(exp);
            return new MemberHierarchyFunDef.CalcImpl(
                    new DummyExp(HierarchyType.forType(type)),
                    memberCalc);
        }
        if (type instanceof LevelType) {
            // <Level> --> <Level>.Hierarchy
            final LevelCalc levelCalc = compileLevel(exp);
            return new LevelHierarchyFunDef.CalcImpl(
                    new DummyExp(HierarchyType.forType(type)),
                    levelCalc);
        }
        assert type instanceof HierarchyType;
        return (HierarchyCalc) compile(exp);
    }

    public IntegerCalc compileInteger(Exp exp) {
        final Calc calc = compileScalar(exp, false);
        final Type type = calc.getType();
        if (type instanceof DecimalType
            && ((DecimalType) type).getScale() == 0) {
            return (IntegerCalc) calc;
        } else if (type instanceof NumericType) {
            if (calc instanceof ConstantCalc) {
                ConstantCalc constantCalc = (ConstantCalc) calc;
                return new ConstantCalc(
                    new DecimalType(Integer.MAX_VALUE, 0),
                    constantCalc.evaluateInteger(null));
            } else if (calc instanceof DoubleCalc) {
                final DoubleCalc doubleCalc = (DoubleCalc) calc;
                return new AbstractIntegerCalc(exp, new Calc[] {doubleCalc}) {
                    public int evaluateInteger(Evaluator evaluator) {
                        return (int) doubleCalc.evaluateDouble(evaluator);
                    }
                };
            }
        }
        return (IntegerCalc) calc;
    }

    public StringCalc compileString(Exp exp) {
        return (StringCalc) compile(exp);
    }

    public DateTimeCalc compileDateTime(Exp exp) {
        return (DateTimeCalc) compile(exp);
    }

    public ListCalc compileList(Exp exp) {
        return compileList(exp, false);
    }

    public ListCalc compileList(Exp exp, boolean mutable) {
        if (mutable) {
            return (ListCalc) compileAs(exp, null, ResultStyle.MUTABLELIST_ONLY);
        } else {
            return (ListCalc) compileAs(exp, null, ResultStyle.LIST_ONLY);
        }
    }

    public IterCalc compileIter(Exp exp) {
        return (IterCalc) compileAs(exp, null, ResultStyle.ITERABLE_ONLY);
    }

    public BooleanCalc compileBoolean(Exp exp) {
        final Calc calc = compileScalar(exp, false);
        if (calc instanceof BooleanCalc) {
            if (calc instanceof ConstantCalc) {
                final Object o = calc.evaluate(null);
                if (!(o instanceof Boolean)) {
                    return ConstantCalc.constantBoolean(
                        CastFunDef.toBoolean(o, new BooleanType()));
                }
            }
            return (BooleanCalc) calc;
        } else if (calc instanceof DoubleCalc) {
            final DoubleCalc doubleCalc = (DoubleCalc) calc;
            return new AbstractBooleanCalc(exp, new Calc[] {doubleCalc}) {
                public boolean evaluateBoolean(Evaluator evaluator) {
                    return doubleCalc.evaluateDouble(evaluator) != 0;
                }
            };
        } else if (calc instanceof IntegerCalc) {
            final IntegerCalc integerCalc = (IntegerCalc) calc;
            return new AbstractBooleanCalc(exp, new Calc[] {integerCalc}) {
                public boolean evaluateBoolean(Evaluator evaluator) {
                    return integerCalc.evaluateInteger(evaluator) != 0;
                }
            };
        } else {
            return (BooleanCalc) calc;
        }
    }

    public DoubleCalc compileDouble(Exp exp) {
        final DoubleCalc calc = (DoubleCalc) compileScalar(exp, false);
        if (calc instanceof ConstantCalc
            && !(calc.evaluate(null) instanceof Double)) {
            return ConstantCalc.constantDouble(
                calc.evaluateDouble(null));
        }
        return calc;
    }

    public TupleCalc compileTuple(Exp exp) {
        return (TupleCalc) compile(exp);
    }

    public Calc compileScalar(Exp exp, boolean specific) {
        final Type type = exp.getType();
        if (type instanceof MemberType) {
            MemberType memberType = (MemberType) type;
            MemberCalc calc = compileMember(exp);
            return new MemberValueCalc(
                    new DummyExp(memberType.getValueType()),
                    new MemberCalc[] {calc});
        } else if (type instanceof DimensionType) {
            final DimensionCalc dimensionCalc = compileDimension(exp);
            MemberType memberType = MemberType.forType(type);
            final MemberCalc dimensionCurrentMemberCalc =
                    new DimensionCurrentMemberFunDef.CalcImpl(
                            new DummyExp(memberType),
                            dimensionCalc);
            return new MemberValueCalc(
                    new DummyExp(memberType.getValueType()),
                    new MemberCalc[] {dimensionCurrentMemberCalc});
        } else if (type instanceof HierarchyType) {
            HierarchyType hierarchyType = (HierarchyType) type;
            MemberType memberType =
                    MemberType.forHierarchy(hierarchyType.getHierarchy());
            final HierarchyCalc hierarchyCalc = compileHierarchy(exp);
            final MemberCalc hierarchyCurrentMemberCalc =
                    new HierarchyCurrentMemberFunDef.CalcImpl(
                            new DummyExp(memberType), hierarchyCalc);
            return new MemberValueCalc(
                    new DummyExp(memberType.getValueType()),
                    new MemberCalc[] {hierarchyCurrentMemberCalc});
        } else if (type instanceof TupleType) {
            TupleType tupleType = (TupleType) type;
            TupleCalc tupleCalc = compileTuple(exp);
            final TupleValueCalc scalarCalc = new TupleValueCalc(
                    new DummyExp(tupleType.getValueType()), tupleCalc);
            return scalarCalc.optimize();
        } else if (type instanceof ScalarType) {
            if (specific) {
                if (type instanceof BooleanType) {
                    return compileBoolean(exp);
                } else if (type instanceof NumericType) {
                    return compileDouble(exp);
                } else if (type instanceof StringType) {
                    return compileString(exp);
                } else {
                    return compile(exp);
                }
            } else {
                return compile(exp);
            }
        } else {
            return compile(exp);
        }
    }

    public ParameterSlot registerParameter(Parameter parameter) {
        ParameterSlot slot = parameterSlots.get(parameter);
        if (slot != null) {
            return slot;
        }
        int index = parameterSlots.size();
        ParameterSlotImpl slot2 = new ParameterSlotImpl(parameter, index);
        parameterSlots.put(parameter, slot2);
        slot2.value = parameter.getValue();

        // Compile the expression only AFTER the parameter has been
        // registered with a slot. Otherwise a cycle is possible.
        Calc calc = parameter.getDefaultExp().accept(this);
        slot2.setDefaultValueCalc(calc);
        return slot2;
    }

    public List<ResultStyle> getAcceptableResultStyles() {
        return resultStyles;
    }

    /**
     * Implementation of {@link ParameterSlot}.
     */
    private static class ParameterSlotImpl implements ParameterSlot {
        private final Parameter parameter;
        private final int index;
        private Calc defaultValueCalc;
        private Object value;
        private Object cachedDefaultValue;

        /**
         * Creates a ParameterSlotImpl.
         *
         * @param parameter Parameter
         * @param index Unique index of the slot
         */
        public ParameterSlotImpl(
            Parameter parameter, int index)
        {
            this.parameter = parameter;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public Calc getDefaultValueCalc() {
            return defaultValueCalc;
        }

        public Parameter getParameter() {
            return parameter;
        }

        /**
         * Sets a compiled expression to compute the default value of the
         * parameter.
         *
         * @param calc Compiled expression to compute default value of
         * parameter
         * 
         * @see #getDefaultValueCalc()
         */
        private void setDefaultValueCalc(Calc calc) {
            this.defaultValueCalc = calc;
        }

        public void setParameterValue(Object value) {
            this.value = value;
        }

        public Object getParameterValue() {
            return value;
        }

        public void setCachedDefaultValue(Object value) {
            this.cachedDefaultValue = value;
        }

        public Object getCachedDefaultValue() {
            return cachedDefaultValue;
        }
    }
}

// End AbtractExpCompiler.java
