/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/RolapHierarchy.java#6 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2001-2002 Kana Software, Inc.
// Copyright (C) 2001-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 10 August, 2001
*/

package mondrian.rolap;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import mondrian.calc.Calc;
import mondrian.calc.DummyExp;
import mondrian.calc.ExpCompiler;
import mondrian.calc.ListCalc;
import mondrian.calc.impl.AbstractListCalc;
import mondrian.calc.impl.ConstantCalc;
import mondrian.calc.impl.ValueCalc;
import mondrian.mdx.HierarchyExpr;
import mondrian.mdx.ResolvedFunCall;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.Access;
import mondrian.olap.CellFormatter;
import mondrian.olap.Dimension;
import mondrian.olap.DimensionType;
import mondrian.olap.Evaluator;
import mondrian.olap.Exp;
import mondrian.olap.Formula;
import mondrian.olap.Hierarchy;
import mondrian.olap.HierarchyBase;
import mondrian.olap.Id;
import mondrian.olap.Level;
import mondrian.olap.LevelType;
import mondrian.olap.Member;
import mondrian.olap.MondrianDef;
import mondrian.olap.Property;
import mondrian.olap.Role;
import mondrian.olap.Syntax;
import mondrian.olap.Util;
import mondrian.olap.Validator;
import mondrian.olap.fun.AggregateFunDef;
import mondrian.olap.fun.BuiltinFunTable;
import mondrian.olap.fun.FunDefBase;
import mondrian.olap.fun.FunUtil;
import mondrian.olap.type.MemberType;
import mondrian.olap.type.NumericType;
import mondrian.olap.type.SetType;
import mondrian.olap.type.Type;
import mondrian.resource.MondrianResource;
import mondrian.rolap.sql.SqlQuery;

import org.apache.log4j.Logger;

/**
 * <code>RolapHierarchy</code> implements {@link Hierarchy} for a ROLAP database.
 *
 * @author jhyde
 * @since 10 August, 2001
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/RolapHierarchy.java#6 $
 */
public class RolapHierarchy extends HierarchyBase {

    private static final Logger LOGGER = Logger.getLogger(RolapHierarchy.class);

    /**
     * The raw member reader. For a member reader which incorporates access
     * control and deals with hidden members (if the hierarchy is ragged), use
     * {@link #createMemberReader(Role)}.
     */
    private MemberReader memberReader;
    protected MondrianDef.Hierarchy xmlHierarchy;
    private String memberReaderClass;
    protected MondrianDef.RelationOrJoin relation;
    private Member defaultMember;
    private String defaultMemberName;
    private RolapNullMember nullMember;

    private String sharedHierarchyName;

    private Exp aggregateChildrenExpression;

    /**
     * Type for members of this hierarchy. Set once to avoid excessive newing.
     */
    final Type memberType = MemberType.forHierarchy(this);

    /**
     * The level that the null member belongs too.
     */
    protected final RolapLevel nullLevel;

    /**
     * The 'all' member of this hierarchy. This exists even if the hierarchy
     * does not officially have an 'all' member.
     */
    private RolapMember allMember;
    private static final String ALL_LEVEL_CARDINALITY = "1";

    RolapHierarchy(RolapDimension dimension, String subName, boolean hasAll) {
        super(dimension, subName, hasAll);
        this.allLevelName = "(All)";
        this.allMemberName = "All " + name + "s";
        if (hasAll) {
            this.levels = new RolapLevel[1];
            this.levels[0] = new RolapLevel(
                    this, 0, this.allLevelName, null, null, null, null,
                    null, null, null, RolapProperty.emptyArray, RolapLevel.FLAG_ALL | RolapLevel.FLAG_UNIQUE, null,
                    RolapLevel.HideMemberCondition.Never, LevelType.Regular, "");
        } else {
            this.levels = new RolapLevel[0];
        }

        // The null member belongs to a level with very similar properties to
        // the 'all' level.
        this.nullLevel = new RolapLevel(
                this, 0, this.allLevelName, null, null, null, null, null, null,
                null, RolapProperty.emptyArray,
                RolapLevel.FLAG_ALL | RolapLevel.FLAG_UNIQUE,
                null,
                RolapLevel.HideMemberCondition.Never,
                LevelType.Null, "");
    }

    /**
     * Creates a <code>RolapHierarchy</code>.
     *
     * @param dimension the dimension this hierarchy belongs to
     * @param xmlHierarchy the xml object defining this hierarchy
     * @param xmlCubeDimension the xml object defining the cube 
     *   dimension for this object 
     */
    RolapHierarchy(
        RolapDimension dimension,
        MondrianDef.Hierarchy xmlHierarchy,
        MondrianDef.CubeDimension xmlCubeDimension)
    {
        this(dimension, xmlHierarchy.name, xmlHierarchy.hasAll);

        assert(!(this instanceof RolapCubeHierarchy));
        
        this.xmlHierarchy = xmlHierarchy;
        this.relation = xmlHierarchy.relation;
        if (xmlHierarchy.relation instanceof MondrianDef.InlineTable) {
            this.relation =
                RolapUtil.convertInlineTableToRelation(
                    (MondrianDef.InlineTable) xmlHierarchy.relation,
                    getRolapSchema().getDialect());
        }
        this.memberReaderClass = xmlHierarchy.memberReaderClass;

        // Create an 'all' level even if the hierarchy does not officially
        // have one.
        if (xmlHierarchy.allMemberName != null) {
            this.allMemberName = xmlHierarchy.allMemberName;
        }
        if (xmlHierarchy.allLevelName != null) {
            this.allLevelName = xmlHierarchy.allLevelName;
        }
        RolapLevel allLevel = new RolapLevel(
            this, 0, this.allLevelName, null, null, null, null, null, null,
            null, RolapProperty.emptyArray,
            RolapLevel.FLAG_ALL | RolapLevel.FLAG_UNIQUE,
            null,
            RolapLevel.HideMemberCondition.Never,
            LevelType.Regular, ALL_LEVEL_CARDINALITY);
        allLevel.init(xmlCubeDimension);
        this.allMember = new RolapMember(
            null, allLevel, null, allMemberName, Member.MemberType.ALL);
        // assign "all member" caption
        if (xmlHierarchy.allMemberCaption != null &&
            xmlHierarchy.allMemberCaption.length() > 0) {
            this.allMember.setCaption(xmlHierarchy.allMemberCaption);
        }
        this.allMember.setOrdinal(0);

        // If the hierarchy has an 'all' member, the 'all' level is level 0.
        if (hasAll) {
            this.levels = new RolapLevel[xmlHierarchy.levels.length + 1];
            this.levels[0] = allLevel;
            for (int i = 0; i < xmlHierarchy.levels.length; i++) {
                final MondrianDef.Level xmlLevel = xmlHierarchy.levels[i];
                if (xmlLevel.getKeyExp() == null &&
                        xmlHierarchy.memberReaderClass == null) {
                    throw MondrianResource.instance().LevelMustHaveNameExpression.ex(xmlLevel.name);
                }
                levels[i + 1] = new RolapLevel(this, i + 1, xmlLevel);
            }
        } else {
            this.levels = new RolapLevel[xmlHierarchy.levels.length];
            for (int i = 0; i < xmlHierarchy.levels.length; i++) {
                levels[i] = new RolapLevel(this, i, xmlHierarchy.levels[i]);
            }
        }

        if (xmlCubeDimension instanceof MondrianDef.DimensionUsage) {
            String sharedDimensionName =
                ((MondrianDef.DimensionUsage) xmlCubeDimension).source;
            this.sharedHierarchyName = sharedDimensionName;
            if (subName != null) {
                this.sharedHierarchyName += "." + subName; // e.g. "Time.Weekly"
            }
        } else {
            this.sharedHierarchyName = null;
        }
        if (xmlHierarchy.relation != null &&
                xmlHierarchy.memberReaderClass != null) {
            throw MondrianResource.instance().
                HierarchyMustNotHaveMoreThanOneSource.ex(getUniqueName());
        }
        if (!Util.isEmpty(xmlHierarchy.caption)) {
            setCaption(xmlHierarchy.caption);
        }
        defaultMemberName = xmlHierarchy.defaultMember;
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RolapHierarchy)) {
            return false;
        }

        RolapHierarchy that = (RolapHierarchy)o;
        if (sharedHierarchyName == null || that.sharedHierarchyName == null) {
            return false;
        } else {
            return sharedHierarchyName.equals(that.sharedHierarchyName) &&
                getUniqueName().equals(that.getUniqueName());
        }
    }

    protected int computeHashCode() {
        return Util.hash(super.computeHashCode(), sharedHierarchyName);
    }

    /**
     * Initializes a hierarchy within the context of a cube.
     */
    void init(MondrianDef.CubeDimension xmlDimension) {
        // first create memberReader
        if (this.memberReader == null) {
            this.memberReader = getRolapSchema().createMemberReader(
                sharedHierarchyName, this, memberReaderClass);
        }
        for (Level level : levels) {
            ((RolapLevel) level).init(xmlDimension);
        }
        if (defaultMemberName != null) {
            List<Id.Segment> uniqueNameParts =
                Util.parseIdentifier(defaultMemberName);

            // We strip off the parent dimension name if the defaultMemberName
            // is the full unique name, [Time].[2004] rather than simply
            // [2004].
            //Dimension dim = getDimension();
            // What we should strip off is hierarchy name
            if (this.name.equals(uniqueNameParts.get(0).name)) {
                uniqueNameParts =
                    uniqueNameParts.subList(1, uniqueNameParts.size());
            }

            // Now lookup the name from the hierarchy's members.
            defaultMember = memberReader.lookupMember(uniqueNameParts, false);
            if (defaultMember == null) {
                throw Util.newInternal(
                    "Can not find Default Member with name \""
                        + defaultMemberName + "\" in Hierarchy \"" +
                        getName() + "\"");
            }
        }
    }

    void setMemberReader(MemberReader memberReader) {
        this.memberReader = memberReader;
    }

    MemberReader getMemberReader() {
        return this.memberReader;
    }

    RolapLevel newMeasuresLevel() {
        RolapLevel level =
            new RolapLevel(
                this, this.levels.length,
                "MeasuresLevel", null, null, null, null,
                null, null, null, RolapProperty.emptyArray, 0, null,
                RolapLevel.HideMemberCondition.Never, LevelType.Regular, "");
        this.levels = RolapUtil.addElement(this.levels, level);
        return level;
    }

    /**
     * If this hierarchy has precisely one table, returns that table;
     * if this hierarchy has no table, return the cube's fact-table;
     * otherwise, returns null.
     */
    MondrianDef.Relation getUniqueTable() {
        if (relation instanceof MondrianDef.Relation) {
            return (MondrianDef.Relation) relation;
        } else if (relation instanceof MondrianDef.Join) {
            return null;
        } else {
            throw Util.newInternal(
                    "hierarchy's relation is a " + relation.getClass());
        }
    }

    boolean tableExists(String tableName) {
        return (relation != null) && tableExists(tableName, relation);
    }

    private static boolean tableExists(
        String tableName,
        MondrianDef.RelationOrJoin relationOrJoin)
    {
        if (relationOrJoin instanceof MondrianDef.Relation) {
            MondrianDef.Relation relation =
                (MondrianDef.Relation) relationOrJoin;
            return relation.getAlias().equals(tableName);
        } else {
            MondrianDef.Join join = (MondrianDef.Join) relationOrJoin;
            return tableExists(tableName, join.left) ||
                tableExists(tableName, join.right);
        }
    }

    public RolapSchema getRolapSchema() {
        return (RolapSchema) dimension.getSchema();
    }

    public MondrianDef.RelationOrJoin getRelation() {
        return relation;
    }

    public MondrianDef.Hierarchy getXmlHierarchy() {
        return xmlHierarchy;
    }

    public Member getDefaultMember() {
        // use lazy initialization to get around bootstrap issues
        if (defaultMember == null) {
            List rootMembers = memberReader.getRootMembers();
            if (rootMembers.size() == 0) {
                throw MondrianResource.instance().InvalidHierarchyCondition.ex(this.getUniqueName());
            }
            defaultMember = (RolapMember) rootMembers.get(0);
        }
        return defaultMember;
    }

    public Member getNullMember() {
        // use lazy initialization to get around bootstrap issues
        if (nullMember == null) {
            nullMember = new RolapNullMember(nullLevel);
        }
        return nullMember;
    }

    /**
     * Returns the 'all' member.
     */
    public RolapMember getAllMember() {
        return allMember;
    }

    public Member createMember(
            Member parent,
            Level level,
            String name,
            Formula formula) {
        if (formula == null) {
            return new RolapMember(
                (RolapMember) parent, (RolapLevel) level, name);
        } else if (level.getDimension().isMeasures()) {
            return new RolapCalculatedMeasure(
                (RolapMember) parent, (RolapLevel) level, name, formula);
        } else {
            return new RolapCalculatedMember(
                (RolapMember) parent, (RolapLevel) level, name, formula);
        }
    }

    String getAlias() {
        return getName();
    }

    /**
     * Returns the name of the source hierarchy, if this hierarchy is shared,
     * otherwise null.
     *
     * <p>If this hierarchy is a public -- that is, it belongs to a dimension
     * which is a usage of a shared dimension -- then
     * <code>sharedHierarchyName</code> holds the unique name of the shared
     * hierarchy; otherwise it is null.
     *
     * <p> Suppose this hierarchy is "Weekly" in the dimension "Order Date" of
     * cube "Sales", and that "Order Date" is a usage of the "Time"
     * dimension. Then <code>sharedHierarchyName</code> will be
     * "[Time].[Weekly]".
     */
    public String getSharedHierarchyName() {
        return sharedHierarchyName;
    }

    /**
     * Adds to the FROM clause of the query the tables necessary to access the
     * members of this hierarchy. If <code>expression</code> is not null, adds
     * the tables necessary to compute that expression.
     *
     * <p> This method is idempotent: if you call it more than once, it only
     * adds the table(s) to the FROM clause once.
     *
     * @param query Query to add the hierarchy to
     * @param expression Level to qualify up to; if null, qualifies up to the
     *    topmost ('all') expression, which may require more columns and more joins
     */
    void addToFrom(SqlQuery query, MondrianDef.Expression expression) {
        if (relation == null) {
            throw Util.newError(
                    "cannot add hierarchy " + getUniqueName() +
                    " to query: it does not have a <Table>, <View> or <Join>");
        }
        final boolean failIfExists = false;
        MondrianDef.RelationOrJoin subRelation = relation;
        if (relation instanceof MondrianDef.Join) {
            if (expression != null) {
                // Suppose relation is
                //   (((A join B) join C) join D)
                // and the fact table is
                //   F
                // and our expression uses C. We want to make the expression
                //   F left join ((A join B) join C).
                // Search for the smallest subset of the relation which
                // uses C.
                subRelation = relationSubset(relation, expression.getTableAlias());

            }
        }
        query.addFrom(subRelation, null, failIfExists);
    }

    /**
     * Adds a table to the FROM clause of the query.
     * If <code>table</code> is not null, adds the table. Otherwise, add the
     * relation on which this hierarchy is based on.
     *
     * <p> This method is idempotent: if you call it more than once, it only
     * adds the table(s) to the FROM clause once.
     *
     * @param query Query to add the hierarchy to
     * @param table table to add to the query
     */
    void addToFrom(SqlQuery query, RolapStar.Table table) {
        if (getRelation() == null) {
            throw Util.newError(
                    "cannot add hierarchy " + getUniqueName() +
                    " to query: it does not have a <Table>, <View> or <Join>");
        }
        final boolean failIfExists = false;
        MondrianDef.RelationOrJoin subRelation = null;
        if (table != null) {
        	// Suppose relation is
        	//   (((A join B) join C) join D)
        	// and the fact table is
        	//   F
        	// and the table to add is C. We want to make the expression
        	//   F left join ((A join B) join C).
        	// Search for the smallest subset of the relation which
        	// joins with C.
        	subRelation = lookupRelationSubset(getRelation(), table);
        }

        if (subRelation == null) {
            // If no table is found or specified, add the entire base relation.
            subRelation = getRelation();
        }

        query.addFrom(subRelation, null, failIfExists);
    }

    /**
     * Returns the smallest subset of <code>relation</code> which contains
     * the relation <code>alias</code>, or null if these is no relation with
     * such an alias.
     * @param relation the relation in which to look for table by its alias
     * @param alias table alias to search for
     * @return the smallest containing relation or null if no matching table
     * is found in <code>relation</code>
     */
    private static MondrianDef.RelationOrJoin relationSubset(
        MondrianDef.RelationOrJoin relation,
        String alias)
    {
        if (relation instanceof MondrianDef.Relation) {
            MondrianDef.Relation table =
                (MondrianDef.Relation) relation;
            return table.getAlias().equals(alias)
                ? relation
                : null;

        } else if (relation instanceof MondrianDef.Join) {
            MondrianDef.Join join = (MondrianDef.Join) relation;
            MondrianDef.RelationOrJoin rightRelation = relationSubset(join.right, alias);
            return (rightRelation == null)
                ? relationSubset(join.left, alias)
                : join;

        } else {
            throw Util.newInternal("bad relation type " + relation);
        }
    }

    /**
     * Returns the smallest subset of <code>relation</code> which contains
     * the table <code>targetTable</code>, or null if the targetTable is not
     * one of the joining table in <code>relation</code>.
     *
     * @param relation the relation in which to look for targetTable
     * @param targetTable table to add to the query
     * @return the smallest containing relation or null if no matching table
     * is found in <code>relation</code>
     */
    private static MondrianDef.RelationOrJoin lookupRelationSubset(
        MondrianDef.RelationOrJoin relation,
        RolapStar.Table targetTable)
    {
        if (relation instanceof MondrianDef.Table) {
            MondrianDef.Table table = (MondrianDef.Table) relation;
            if (table.name.equals(targetTable.getTableName())) {
                return relation;
            } else {
                // Not the same table if table names are different
                return null;
            }
        } else if (relation instanceof MondrianDef.Join) {
            // Search inside relation, starting from the rightmost table,
            // and move left along the join chain.
            MondrianDef.Join join = (MondrianDef.Join) relation;
            MondrianDef.RelationOrJoin rightRelation =
                lookupRelationSubset(join.right, targetTable);
            if (rightRelation == null) {
                // Keep searching left.
                return lookupRelationSubset(
                    join.left, targetTable);
            } else {
                // Found a match.
                return join;
            }
        }
        return null;
    }

    /**
     * Creates a member reader which enforces the access-control profile of
     * <code>role</code>.
     *
     * <p>This method may not be efficient, so the caller should take care
     * not to call it too often. A cache is a good idea.
     *
     * @pre role != null
     * @post return != null
     */
    MemberReader createMemberReader(Role role) {
        final Access access = role.getAccess(this);
        switch (access) {
        case NONE:
            throw Util.newInternal("Illegal access to members of hierarchy "
                    + this);
        case ALL:
            return (isRagged())
                ? new RestrictedMemberReader(getMemberReader(), role)
                : getMemberReader();

        case CUSTOM:
            final Role.HierarchyAccess hierarchyAccess =
                role.getAccessDetails(this);
            final Role.RollupPolicy rollupPolicy =
                hierarchyAccess.getRollupPolicy();
            final NumericType returnType = new NumericType();
            switch (rollupPolicy) {
            case FULL:
                return new RestrictedMemberReader(getMemberReader(), role);
            case PARTIAL:
                Type memberType1 =
                    new mondrian.olap.type.MemberType(
                        getDimension(),
                        getHierarchy(),
                        null,
                        null);
                SetType setType = new SetType(memberType1);
                ListCalc listCalc =
                    new AbstractListCalc(
                        new DummyExp(setType), new Calc[0])
                    {
                        public List evaluateList(Evaluator evaluator) {
                            return Arrays.asList(
                                FunUtil.getNonEmptyMemberChildren(
                                    evaluator,
                                    ((RolapEvaluator) evaluator).getExpanding()));
                        }

                        public boolean dependsOn(Dimension dimension) {
                            return true;
                        }
                    };
                final Calc partialCalc =
                    new LimitedRollupAggregateCalc(returnType, listCalc);

                final Exp partialExp =
                    new ResolvedFunCall(
                        new FunDefBase("$x", "x", "In") {
                            public Calc compileCall(
                                ResolvedFunCall call,
                                ExpCompiler compiler)
                            {
                                return partialCalc;
                            }

                            public void unparse(Exp[] args, PrintWriter pw) {
                                pw.print("$RollupAccessibleChildren()");
                            }
                        },
                        new Exp[0],
                        returnType);
                return new LimitedRollupSubstitutingMemberReader(
                    role, hierarchyAccess, partialExp);

            case HIDDEN:
                Exp hiddenExp =
                    new ResolvedFunCall(
                        new FunDefBase("$x", "x", "In") {
                            public Calc compileCall(
                                ResolvedFunCall call, ExpCompiler compiler)
                            {
                                return new ConstantCalc(returnType, null);
                            }

                            public void unparse(Exp[] args, PrintWriter pw) {
                                pw.print("$RollupAccessibleChildren()");
                            }
                        },
                        new Exp[0],
                        returnType);
                return new LimitedRollupSubstitutingMemberReader(
                    role, hierarchyAccess, hiddenExp);
            default:
                throw Util.unexpected(rollupPolicy);
            }
        default:
            throw Util.badValue(access);
        }
    }

    /**
     * A hierarchy is ragged if it contains one or more levels with hidden
     * members.
     */
    public boolean isRagged() {
        for (Level level : levels) {
            if (((RolapLevel) level).getHideMemberCondition() !=
                RolapLevel.HideMemberCondition.Never) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an expression which will compute a member's value by aggregating
     * its children.
     *
     * <p>It is efficient to share one expression between all calculated members in
     * a parent-child hierarchy, so we only need need to validate the expression
     * once.
     */
    synchronized Exp getAggregateChildrenExpression() {
        if (aggregateChildrenExpression == null) {
            UnresolvedFunCall fc = new UnresolvedFunCall(
                "$AggregateChildren",
                Syntax.Internal,
                new Exp[] {new HierarchyExpr(this)});
            Validator validator =
                    Util.createSimpleValidator(BuiltinFunTable.instance());
            aggregateChildrenExpression = fc.accept(validator);
        }
        return aggregateChildrenExpression;
    }

    /**
     * Builds a dimension which maps onto a table holding the transitive
     * closure of the relationship for this parent-child level.
     *
     * <p>This method is triggered by the
     * {@link mondrian.olap.MondrianDef.Closure} element
     * in a schema, and is only meaningful for a parent-child hierarchy.
     *
     * <p>When a Schema contains a parent-child Hierarchy that has an
     * associated closure table, Mondrian creates a parallel internal
     * Hierarchy, called a "closed peer", that refers to the closure table.
     * This is indicated in the schema at the level of a Level, by including a
     * Closure element. The closure table represents
     * the transitive closure of the parent-child relationship.
     *
     * <p>The peer dimension, with its single hierarchy, and 3 levels (all,
     * closure, item) really 'belong to' the parent-child level. If a single
     * hierarchy had two parent-child levels (however unlikely this might be)
     * then each level would have its own auxiliary dimension.
     *
     * <p>For example, in the demo schema the [HR].[Employee] dimension
     * contains a parent-child hierarchy:
     *
     * <pre>
     * &lt;Dimension name="Employees" foreignKey="employee_id"&gt;
     *   &lt;Hierarchy hasAll="true" allMemberName="All Employees"
     *         primaryKey="employee_id"&gt;
     *     &lt;Table name="employee"/&gt;
     *     &lt;Level name="Employee Id" type="Numeric" uniqueMembers="true"
     *            column="employee_id" parentColumn="supervisor_id"
     *            nameColumn="full_name" nullParentValue="0"&gt;
     *       &lt;Closure parentColumn="supervisor_id" childColumn="employee_id"&gt;
     *          &lt;Table name="employee_closure"/&gt;
     *       &lt;/Closure&gt;
     *       ...
     * </pre>
     * The internal closed peer Hierarchy has this structure:
     * <pre>
     * &lt;Dimension name="Employees" foreignKey="employee_id"&gt;
     *     ...
     *     &lt;Hierarchy name="Employees$Closure"
     *         hasAll="true" allMemberName="All Employees"
     *         primaryKey="employee_id" primaryKeyTable="employee_closure"&gt;
     *       &lt;Join leftKey="supervisor_id" rightKey="employee_id"&gt;
     *         &lt;Table name="employee_closure"/&gt;
     *         &lt;Table name="employee"/&gt;
     *       &lt;/Join&gt;
     *       &lt;Level name="Closure"  type="Numeric" uniqueMembers="false"
     *           table="employee_closure" column="supervisor_id"/&gt;
     *       &lt;Level name="Employee" type="Numeric" uniqueMembers="true"
     *           table="employee_closure" column="employee_id"/&gt;
     *     &lt;/Hierarchy&gt;
     * </pre>
     *
     * <p>Note that the original Level with the Closure produces two Levels in
     * the closed peer Hierarchy: a simple peer (Employee) and a closed peer
     * (Closure).
     *
     * @param src a parent-child Level that has a Closure clause
     * @param clos a Closure clause
     * @return the closed peer Level in the closed peer Hierarchy
     */
    RolapDimension createClosedPeerDimension(
        RolapLevel src,
        MondrianDef.Closure clos,
        MondrianDef.CubeDimension xmlDimension) {

        // REVIEW (mb): What about attribute primaryKeyTable?

        // Create a peer dimension.
        RolapDimension peerDimension = new RolapDimension(
            dimension.getSchema(),
            dimension.getName() + "$Closure",
            DimensionType.StandardDimension);

        // Create a peer hierarchy.
        RolapHierarchy peerHier = peerDimension.newHierarchy(subName, true);
        peerHier.allMemberName = getAllMemberName();
        peerHier.allMember = getAllMember();
        peerHier.allLevelName = getAllLevelName();
        peerHier.sharedHierarchyName = getSharedHierarchyName();
        MondrianDef.Join join = new MondrianDef.Join();
        peerHier.relation = join;
        join.left = clos.table;         // the closure table
        join.leftKey = clos.parentColumn;
        join.right = relation;     // the unclosed base table
        join.rightKey = clos.childColumn;

        // Create the upper level.
        // This represents all groups of descendants. For example, in the
        // Employee closure hierarchy, this level has a row for every employee.
        int index = peerHier.levels.length;
        int flags = src.getFlags() &~ RolapLevel.FLAG_UNIQUE;
        MondrianDef.Expression keyExp =
            new MondrianDef.Column(clos.table.name, clos.parentColumn);

        RolapLevel level = new RolapLevel(peerHier, index++,
            "Closure",
            keyExp, null, null, null,
            null, null,  // no longer a parent-child hierarchy
            null,
            RolapProperty.emptyArray,
            flags,
            src.getDatatype(),
            src.getHideMemberCondition(),
            src.getLevelType(),
            "");
        peerHier.levels = RolapUtil.addElement(peerHier.levels, level);

        // Create lower level.
        // This represents individual items. For example, in the Employee
        // closure hierarchy, this level has a row for every direct and
        // indirect report of every employee (which is more than the number
        // of employees).
        flags = src.getFlags() | RolapLevel.FLAG_UNIQUE;
        keyExp = new MondrianDef.Column(clos.table.name, clos.childColumn);
        RolapLevel sublevel = new RolapLevel(
            peerHier,
            index++,
            "Item",
            keyExp,
            null,
            null,
            null,
            null,
            null,  // no longer a parent-child hierarchy
            null,
            RolapProperty.emptyArray,
            flags,
            src.getDatatype(),
            src.getHideMemberCondition(),
            src.getLevelType(),
            "");
        peerHier.levels = RolapUtil.addElement(peerHier.levels, sublevel);

        return peerDimension;
    }

    /**
     * Sets default member of this Hierarchy.
     *
     * @param defaultMember Default member
     */
    public void setDefaultMember(Member defaultMember) {
        if (defaultMember != null){
            this.defaultMember = defaultMember;
        }
    }


    /**
     * A <code>RolapNullMember</code> is the null member of its hierarchy.
     * Every hierarchy has precisely one. They are yielded by operations such as
     * <code>[Gender].[All].ParentMember</code>. Null members are usually
     * omitted from sets (in particular, in the set constructor operator "{ ...
     * }".
     */
    static class RolapNullMember extends RolapMember {
        RolapNullMember(final RolapLevel level) {
            super(null, level, null, RolapUtil.mdxNullLiteral, MemberType.NULL);
            assert level != null;
        }
    }

    /**
     * Calculated member which is also a measure (that is, a member of the
     * [Measures] dimension).
     */
    protected static class RolapCalculatedMeasure
        extends RolapCalculatedMember
        implements RolapMeasure
    {
        private CellFormatter cellFormatter;

        public RolapCalculatedMeasure(
            RolapMember parent, RolapLevel level, String name, Formula formula)
        {
            super(parent, level, name, formula);
        }

        public synchronized void setProperty(String name, Object value) {
            if (name.equals(Property.CELL_FORMATTER.getName())) {
                String cellFormatterClass = (String) value;
                try {
                    this.cellFormatter =
                        RolapCube.getCellFormatter(cellFormatterClass);
                } catch (Exception e) {
                    throw MondrianResource.instance().CellFormatterLoadFailed.ex(
                        cellFormatterClass, getUniqueName(), e);
                }
            }
            super.setProperty(name, value);
        }

        public CellFormatter getFormatter() {
            return cellFormatter;
        }
    }

    /**
     * Substitute for a member in a hierarchy whose rollup policy is 'partial'
     * or 'hidden'. The member is calculated using an expression which
     * aggregates only visible descendants.
     *
     * <p>Note that this class extends RolapCubeMember only because other code
     * expects that all members in a RolapCubeHierarchy are RolapCubeMembers.
     *
     * @see mondrian.olap.Role.RollupPolicy
     */
    public static class LimitedRollupMember extends RolapCubeMember {
        public final RolapMember member;
        private final Exp exp;

        LimitedRollupMember(
            RolapCubeMember member,
            Exp exp)
        {
            super(
                member.getParentMember(),
                member.getRolapMember(),
                member.getLevel(),
                member.getCube());
            assert !(member instanceof LimitedRollupMember);
            this.member = member;
            this.exp = exp;
        }

        public boolean equals(Object o) {
            return o instanceof LimitedRollupMember
                && ((LimitedRollupMember) o).member.equals(member);
        }

        public int hashCode() {
            return member.hashCode();
        }

        public Exp getExpression() {
            return exp;
        }

        protected boolean computeCalculated(final MemberType memberType) {
            return true;
        }
        
        public boolean isCalculated() {
            return true;
        }
    }

    /**
     * Member reader which wraps a hierarchy's member reader, and if the
     * role has limited access to the hierarchy, replaces members with
     * dummy members which evaluate to the sum of only the accessible children.
     */
    private class LimitedRollupSubstitutingMemberReader
        extends SubstitutingMemberReader
    {
        private final Role.HierarchyAccess hierarchyAccess;
        private final Exp exp;

        public LimitedRollupSubstitutingMemberReader(
            Role role,
            Role.HierarchyAccess hierarchyAccess,
            Exp exp)
        {
            super(
                new RestrictedMemberReader(
                    RolapHierarchy.this.getMemberReader(), role));
            this.hierarchyAccess = hierarchyAccess;
            this.exp = exp;
        }

        public RolapMember substitute(final RolapMember member) {
            if (member != null
                && (hierarchyAccess.getAccess(member) == Access.CUSTOM
                || hierarchyAccess.hasInaccessibleDescendants(member)))
            {
                // Member is visible, but at least one of its
                // descendants is not.
                return new LimitedRollupMember((RolapCubeMember)member, exp);
            } else {
                // No need to substitute. Member and all of its
                // descendants are accessible. Total for member
                // is same as for FULL policy.
                return member;
            }
        }

        public RolapMember desubstitute(RolapMember member) {
            if (member instanceof LimitedRollupMember) {
                return ((LimitedRollupMember) member).member;
            } else {
                return member;
            }
        }
    }

    /**
     * Compiled expression that computes rollup over a set of visible children.
     * The {@code listCalc} expression determines that list of children.
     */
    private static class LimitedRollupAggregateCalc
        extends AggregateFunDef.AggregateCalc
    {
        public LimitedRollupAggregateCalc(Type returnType, ListCalc listCalc) {
            super(
                new DummyExp(returnType),
                listCalc,
                new ValueCalc(new DummyExp(returnType)));
        }
    }
}

// End RolapHierarchy.java
