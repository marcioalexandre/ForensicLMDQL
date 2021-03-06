/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2004-2005 TONBELLER AG
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.rolap;

import java.util.List;

import mondrian.olap.Evaluator;
import mondrian.rolap.aggmatcher.AggStar;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.rolap.sql.SqlQuery;
import mondrian.rolap.sql.TupleConstraint;

/**
 * TupleConstaint which restricts the result of a tuple sqlQuery to a
 * set of parents.  All parents must belong to the same level.
 *
 * @author av
 * @since Nov 10, 2005
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/DescendantsConstraint.java#2 $
 */
class DescendantsConstraint implements TupleConstraint {
    List<RolapMember> parentMembers;
    MemberChildrenConstraint mcc;

    /**
     * Creates a DescendantsConstraint.
     *
     * @param parentMembers list of parents all from the same level
     * @param mcc the constraint that would return the children for each single parent
     */
    public DescendantsConstraint(
        List<RolapMember> parentMembers,
        MemberChildrenConstraint mcc)
    {
        this.parentMembers = parentMembers;
        this.mcc = mcc;
    }

    public void addConstraint(SqlQuery sqlQuery, RolapCube baseCube) {
        mcc.addMemberConstraint(sqlQuery, baseCube, null, parentMembers);
    }

    public void addLevelConstraint(
        SqlQuery sqlQuery,
        RolapCube baseCube,
        AggStar aggStar, 
        RolapLevel level)
    {
        mcc.addLevelConstraint(sqlQuery, baseCube, aggStar, level);
    }

    public MemberChildrenConstraint getMemberChildrenConstraint(RolapMember parent) {
        return mcc;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns null, because descendants is not cached.
     */
    public Object getCacheKey() {
        return null;
    }

    public Evaluator getEvaluator() {
        return null;
    }
}

// End DescendantsConstraint.java
