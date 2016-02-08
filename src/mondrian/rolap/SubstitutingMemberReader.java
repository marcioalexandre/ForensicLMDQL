/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.rolap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import mondrian.olap.Id;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.rolap.sql.TupleConstraint;

/**
 * Implementation of {@link MemberReader} which replaces given members
 * with a substitute.
 *
 * <p>Derived classes must implement the {@link #substitute(RolapMember)} and
 * {@link #desubstitute(RolapMember)} methods.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/SubstitutingMemberReader.java#4 $
 * @since Oct 5, 2007
 */
public abstract class SubstitutingMemberReader extends DelegatingMemberReader {
    private final TupleReader.MemberBuilder memberBuilder =
        new SubstitutingMemberBuilder();

    /**
     * Creates a SubstitutingMemberReader.
     *
     * @param memberReader Parent member reader
     */
    SubstitutingMemberReader(MemberReader memberReader) {
        super(memberReader);
    }

    // Helper methods

    private List<RolapMember> desubstitute(List<RolapMember> members) {
        List<RolapMember> list = new ArrayList<RolapMember>(members.size());
        for (RolapMember member : members) {
            list.add(desubstitute(member));
        }
        return list;
    }

    private List<RolapMember> substitute(List<RolapMember> members) {
        List<RolapMember> list = new ArrayList<RolapMember>(members.size());
        for (RolapMember member : members) {
            list.add(substitute(member));
        }
        return list;
    }

    // ~ -- Implementations of MemberReader methods ---------------------------

    public RolapMember getLeadMember(RolapMember member, int n) {
        return substitute(
            memberReader.getLeadMember(desubstitute(member), n));
    }

    public List<RolapMember> getMembersInLevel(
        RolapLevel level,
        int startOrdinal,
        int endOrdinal)
    {
        return substitute(
            memberReader.getMembersInLevel(level, startOrdinal, endOrdinal));
    }

    public void getMemberRange(
        RolapLevel level,
        RolapMember startMember,
        RolapMember endMember,
        List<RolapMember> list)
    {
        memberReader.getMemberRange(
            level,
            desubstitute(startMember),
            desubstitute(endMember),
            new SubstitutingMemberList(list));
    }

    public int compare(
        RolapMember m1,
        RolapMember m2,
        boolean siblingsAreEqual)
    {
        return memberReader.compare(
            desubstitute(m1),
            desubstitute(m2),
            siblingsAreEqual);
    }

    public RolapHierarchy getHierarchy() {
        return memberReader.getHierarchy();
    }

    public boolean setCache(MemberCache cache) {
        // cache semantics don't make sense if members are not comparable
        throw new UnsupportedOperationException();
    }

    public RolapMember[] getMembers() {
        // might make sense, but I doubt it
        throw new UnsupportedOperationException();
    }

    public List<RolapMember> getRootMembers() {
        return substitute(memberReader.getRootMembers());
    }

    public void getMemberChildren(
        RolapMember parentMember,
        List<RolapMember> children)
    {
        memberReader.getMemberChildren(
            desubstitute(parentMember),
            new SubstitutingMemberList(children));
    }

    public void getMemberChildren(
        List<RolapMember> parentMembers,
        List<RolapMember> children)
    {
        memberReader.getMemberChildren(
            desubstitute(parentMembers),
            new SubstitutingMemberList(children));
    }

    public int getMemberCount() {
        return memberReader.getMemberCount();
    }

    public RolapMember lookupMember(
        List<Id.Segment> uniqueNameParts,
        boolean failIfNotFound)
    {
        return substitute(
            memberReader.lookupMember(uniqueNameParts, failIfNotFound));
    }

    public void getMemberChildren(
        RolapMember member,
        List<RolapMember> children,
        MemberChildrenConstraint constraint)
    {
        memberReader.getMemberChildren(
            desubstitute(member), 
            new SubstitutingMemberList(children),
            constraint);
    }

    public void getMemberChildren(
        List<RolapMember> parentMembers,
        List<RolapMember> children,
        MemberChildrenConstraint constraint)
    {
        memberReader.getMemberChildren(
            substitute(parentMembers),
            new SubstitutingMemberList(children),
            constraint);
    }

    public List<RolapMember> getMembersInLevel(
        RolapLevel level,
        int startOrdinal,
        int endOrdinal,
        TupleConstraint constraint)
    {
        return substitute(
            memberReader.getMembersInLevel(
                level, startOrdinal, endOrdinal, constraint));
    }

    public RolapMember getDefaultMember() {
        return substitute(memberReader.getDefaultMember());
    }

    public RolapMember getMemberParent(RolapMember member) {
        return substitute(memberReader.getMemberParent(desubstitute(member)));
    }

    public TupleReader.MemberBuilder getMemberBuilder() {
        return memberBuilder;
    }

    /**
     * List which writes through to an underlying list, substituting members
     * as they are written and desubstituting as they are read.
     */
    private class SubstitutingMemberList extends AbstractList<RolapMember> {
        private final List<RolapMember> list;

        SubstitutingMemberList(List<RolapMember> list) {
            this.list = list;
        }

        public RolapMember get(int index) {
            return desubstitute(list.get(index));
        }

        public int size() {
            return list.size();
        }

        public RolapMember set(int index, RolapMember element) {
            return desubstitute(list.set(index, substitute(element)));
        }

        public void add(int index, RolapMember element) {
            list.add(index, substitute(element));
        }

        public RolapMember remove(int index) {
            return list.remove(index);
        }
    }

    private class SubstitutingMemberBuilder
        implements TupleReader.MemberBuilder
    {
        public MemberCache getMemberCache() {
            return memberReader.getMemberBuilder().getMemberCache();
        }

        public Object getMemberCacheLock() {
            return memberReader.getMemberBuilder().getMemberCacheLock();
        }

        public RolapMember makeMember(
            RolapMember parentMember,
            RolapLevel childLevel,
            Object value,
            Object captionValue,
            boolean parentChild,
            ResultSet resultSet,
            Object key,
            int column) throws SQLException
        {
            return substitute(
                memberReader.getMemberBuilder().makeMember(
                    desubstitute(parentMember),
                    childLevel,
                    value,
                    captionValue,
                    parentChild,
                    resultSet,
                    key,
                    column));
        }
    }
}

// End SubstitutingMemberReader.java
