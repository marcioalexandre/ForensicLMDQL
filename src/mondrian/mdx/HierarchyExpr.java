/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/mdx/HierarchyExpr.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.mdx;

import mondrian.calc.Calc;
import mondrian.calc.ExpCompiler;
import mondrian.calc.impl.ConstantCalc;
import mondrian.olap.Category;
import mondrian.olap.Exp;
import mondrian.olap.ExpBase;
import mondrian.olap.Hierarchy;
import mondrian.olap.Util;
import mondrian.olap.Validator;
import mondrian.olap.type.HierarchyType;
import mondrian.olap.type.Type;

/**
 * Usage of a {@link mondrian.olap.Hierarchy} as an MDX expression.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/mdx/HierarchyExpr.java#2 $
 * @since Sep 26, 2005
 */
public class HierarchyExpr extends ExpBase implements Exp {
    private final Hierarchy hierarchy;

    /**
     * Creates a hierarchy expression.
     *
     * @param hierarchy Hierarchy
     * @pre hierarchy != null
     */
    public HierarchyExpr(Hierarchy hierarchy) {
        Util.assertPrecondition(hierarchy != null, "hierarchy != null");
        this.hierarchy = hierarchy;
    }

    /**
     * Returns the hierarchy.
     *
     * @post return != null
     */
    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public String toString() {
        return hierarchy.getUniqueName();
    }

    public Type getType() {
        return HierarchyType.forHierarchy(hierarchy);
    }

    public HierarchyExpr clone() {
        return new HierarchyExpr(hierarchy);
    }

    public int getCategory() {
        return Category.Hierarchy;
    }

    public Exp accept(Validator validator) {
        return this;
    }

    public Calc accept(ExpCompiler compiler) {
        return ConstantCalc.constantHierarchy(hierarchy);
    }

    public Object accept(MdxVisitor visitor) {
        return visitor.visit(this);
    }
}

// End HierarchyExpr.java
