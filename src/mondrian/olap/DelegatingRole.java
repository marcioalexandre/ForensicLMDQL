/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/DelegatingRole.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2002-2002 Kana Software, Inc.
// Copyright (C) 2002-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, Oct 5, 2002
*/

package mondrian.olap;

/**
 * <code>DelegatingRole</code> implements {@link Role} by
 * delegating all methods to an underlying {@link Role}.
 *
 * It is a convenient base class if you want to override just a few of
 * {@link Role}'s methods.
 *
 * @author Richard M. Emberson
 * @since Mar 29 2007
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/DelegatingRole.java#2 $
 */
public class DelegatingRole implements Role {
    protected final Role role;

    public DelegatingRole(Role role) {
        assert role != null;
        this.role = role;
    }

    public Access getAccess(Schema schema) {
        return role.getAccess(schema);
    }

    public Access getAccess(Cube cube) {
        return role.getAccess(cube);
    }

    public Access getAccess(Dimension dimension) {
        return role.getAccess(dimension);
    }

    public Access getAccess(Hierarchy hierarchy) {
        return role.getAccess(hierarchy);
    }

    public static class DelegatingHierarchyAccess
        implements HierarchyAccess 
    {
        protected final HierarchyAccess hierarchyAccess;

        public DelegatingHierarchyAccess(HierarchyAccess hierarchyAccess) {
            assert hierarchyAccess != null;
            this.hierarchyAccess = hierarchyAccess;
        }

        public Access getAccess(Member member) {
            return hierarchyAccess.getAccess(member);
        }

        public int getTopLevelDepth() {
            return hierarchyAccess.getTopLevelDepth();
        }

        public int getBottomLevelDepth() {
            return hierarchyAccess.getBottomLevelDepth();
        }

        public RollupPolicy getRollupPolicy() {
            return hierarchyAccess.getRollupPolicy();
        }

        public boolean hasInaccessibleDescendants(Member member) {
            return hierarchyAccess.hasInaccessibleDescendants(member);
        }
    }

    public HierarchyAccess getAccessDetails(Hierarchy hierarchy) {
        return new DelegatingHierarchyAccess(role.getAccessDetails(hierarchy));
    }

    public Access getAccess(Level level) {
        return role.getAccess(level);
    }

    public Access getAccess(Member member) {
        return role.getAccess(member);
    }

    public Access getAccess(NamedSet set) {
        return role.getAccess(set);
    }

    public boolean canAccess(OlapElement olapElement) {
        return role.canAccess(olapElement);
    }
}

// End DelegatingRole.java
