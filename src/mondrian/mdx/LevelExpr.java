/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/mdx/LevelExpr.java#2 $
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
import mondrian.olap.Level;
import mondrian.olap.Util;
import mondrian.olap.Validator;
import mondrian.olap.type.LevelType;
import mondrian.olap.type.Type;

/**
 * Usage of a {@link mondrian.olap.Level} as an MDX expression.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/mdx/LevelExpr.java#2 $
 * @since Sep 26, 2005
 */
public class LevelExpr extends ExpBase implements Exp {
    private final Level level;

    /**
     * Creates a level expression.
     *
     * @param level Level
     * @pre level != null
     */
    public LevelExpr(Level level) {
        Util.assertPrecondition(level != null, "level != null");
        this.level = level;
    }

    /**
     * Returns the level.
     *
     * @post return != null
     */
    public Level getLevel() {
        return level;
    }

    public String toString() {
        return level.getUniqueName();
    }

    public Type getType() {
        return LevelType.forLevel(level);
    }

    public LevelExpr clone() {
        return new LevelExpr(level);
    }

    public int getCategory() {
        return Category.Level;
    }

    public Exp accept(Validator validator) {
        return this;
    }

    public Calc accept(ExpCompiler compiler) {
        return ConstantCalc.constantLevel(level);
    }

    public Object accept(MdxVisitor visitor) {
        return visitor.visit(this);
    }

}

// End LevelExpr.java
