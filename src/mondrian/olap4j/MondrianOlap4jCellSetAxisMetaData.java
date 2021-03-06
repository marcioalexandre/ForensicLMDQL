/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mondrian.olap.AxisOrdinal;
import mondrian.olap.Exp;
import mondrian.olap.Id;
import mondrian.olap.QueryAxis;
import mondrian.olap.type.SetType;
import mondrian.olap.type.TupleType;
import mondrian.olap.type.Type;

import org.olap4j.Axis;
import org.olap4j.CellSetAxisMetaData;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Property;

/**
 * Implementation of {@link org.olap4j.CellSetMetaData}
 * for the Mondrian OLAP engine.
 *
 * @author jhyde
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap4j/MondrianOlap4jCellSetAxisMetaData.java#2 $
* @since Nov 17, 2007
*/
class MondrianOlap4jCellSetAxisMetaData implements CellSetAxisMetaData {
    private final QueryAxis queryAxis;
    private final MondrianOlap4jCellSetMetaData cellSetMetaData;
    private final List<Property> propertyList = new ArrayList<Property>();

    MondrianOlap4jCellSetAxisMetaData(
        MondrianOlap4jCellSetMetaData cellSetMetaData,
        QueryAxis queryAxis)
    {
        if (queryAxis == null) {
            queryAxis = new QueryAxis(
                false, null, AxisOrdinal.SLICER,
                QueryAxis.SubtotalVisibility.Undefined);
        }
        this.queryAxis = queryAxis;
        this.cellSetMetaData = cellSetMetaData;

        // populate property list
        for (Id id : queryAxis.getDimensionProperties()) {
            propertyList.add(
                Property.StandardMemberProperty.valueOf(
                    id.toStringArray()[0]));
        }
    }

    public Axis getAxisOrdinal() {
        switch (queryAxis.getAxisOrdinal()) {
        case SLICER:
            return Axis.FILTER;
        default:
            return Axis.valueOf(queryAxis.getAxisOrdinal().name());
        }
    }

    public List<Hierarchy> getHierarchies() {
        if (queryAxis.getAxisOrdinal() == AxisOrdinal.SLICER) {
            // Slicer contains all dimensions not mentioned on other axes.
            // The list contains the default hierarchy of
            // each dimension not already in the slicer or in another axes.
            Set<Dimension> dimensionSet = new HashSet<Dimension>();
            for (CellSetAxisMetaData cellSetAxisMetaData
                : cellSetMetaData.getAxesMetaData()) {
                for (Hierarchy hierarchy
                    : cellSetAxisMetaData.getHierarchies()) {
                    dimensionSet.add(hierarchy.getDimension());
                }
            }
            List<Hierarchy> hierarchyList =
                new ArrayList<Hierarchy>();
            for (Dimension dimension
                : cellSetMetaData.getCube().getDimensions()) {
                if (dimensionSet.add(dimension)) {
                    hierarchyList.add(dimension.getDefaultHierarchy());
                }
            }
            // In case a dimension has multiple hierarchies, return the
            // declared type of the slicer expression. For example, if the
            // WHERE clause contains [Time].[Weekly].[1997].[Week 6], the
            // slicer should contain [Time].[Weekly] not the default hierarchy
            // [Time].
            for (Hierarchy hierarchy : getHierarchiesNonFilter()) {
                if (hierarchy.getDimension().getHierarchies().size() == 1) {
                    continue;
                }
                for (int i = 0; i < hierarchyList.size(); i++) {
                    Hierarchy hierarchy1 = hierarchyList.get(i);
                    if (hierarchy1.getDimension().equals(
                        hierarchy.getDimension())
                        && hierarchy1 != hierarchy)
                    {
                        hierarchyList.set(i, hierarchy);
                    }
                }
            }
            return hierarchyList;
        } else {
            return getHierarchiesNonFilter();
        }
    }

    private List<Hierarchy> getHierarchiesNonFilter() {
        final Exp exp = queryAxis.getSet();
        if (exp == null) {
            return Collections.emptyList();
        }
        Type type = exp.getType();
        if (type instanceof SetType) {
            type = ((SetType) type).getElementType();
        }
        final MondrianOlap4jConnection olap4jConnection =
            cellSetMetaData.olap4jStatement.olap4jConnection;
        if (type instanceof TupleType) {
            final TupleType tupleType = (TupleType) type;
            List<Hierarchy> hierarchyList =
                new ArrayList<Hierarchy>();
            for (Type elementType : tupleType.elementTypes) {
                hierarchyList.add(
                    olap4jConnection.toOlap4j(
                        elementType.getHierarchy()));
            }
            return hierarchyList;
        } else {
            return Collections.singletonList(
                (Hierarchy) olap4jConnection.toOlap4j(
                    type.getHierarchy()));
        }
    }

    public List<Property> getProperties() {
        return propertyList;
    }
}

// End MondrianOlap4jCellSetAxisMetaData.java
