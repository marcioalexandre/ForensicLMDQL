/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/type/CubeType.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap.type;

import mondrian.olap.Cube;
import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;

/**
 * The type of an expression which represents a Cube or Virtual Cube.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/type/CubeType.java#2 $
 */
public class CubeType implements Type {
    private final Cube cube;

    /**
     * Creates a type representing a cube.
     */
    public CubeType(Cube cube) {
        this.cube = cube;
    }

    public Cube getCube() {
        return cube;
    }

    public boolean usesDimension(Dimension dimension, boolean definitely) {
        return false;
    }

    public Dimension getDimension() {
        return null;
    }

    public Hierarchy getHierarchy() {
        return null;
    }

    public Level getLevel() {
        return null;
    }

    public int hashCode() {
        return cube.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof CubeType) {
            CubeType that = (CubeType) obj;
            return this.cube.equals(that.cube);
        } else {
            return false;
        }
    }

    public Type computeCommonType(Type type, int[] conversionCount) {
        return this.equals(type)
            ? this
            : null;
    }
}

// End CubeType.java
