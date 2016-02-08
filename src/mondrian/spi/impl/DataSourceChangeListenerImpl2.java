/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/spi/impl/DataSourceChangeListenerImpl2.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.spi.impl;

import mondrian.olap.MondrianDef;
import mondrian.rolap.RolapHierarchy;
import mondrian.rolap.agg.Aggregation;
import mondrian.spi.DataSourceChangeListener;


/**
 * Default implementation of a data source change listener
 * that always returns that the datasource is changed.
 *
 * A change listener can be specified in the connection string.  It is used
 * to ask what is changed in the datasource (e.g. database).
 *
 * Everytime mondrian has to decide whether it will use data from cache, it
 * will call the change listener.  When the change listener tells mondrian
 * the datasource has changed for a dimension, cube, ... then mondrian will
 * flush the cache and read from database again.
 *
 * It is specified in the connection string, like this:
 *
 * <blockquote><code>
 * Jdbc=jdbc:odbc:MondrianFoodMart; JdbcUser=ziggy; JdbcPassword=stardust; DataSourceChangeListener=com.acme.MyChangeListener;
 * </code></blockquote>
 *
 * This class should be called in mondrian before any data is read, so
 * even before cache is build.  This way, the plugin is able to register
 * the first timestamp mondrian tries to read the datasource.
 *
 * @author Bart Pappyn
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/spi/impl/DataSourceChangeListenerImpl2.java#2 $
 * @since Dec 12, 2006
 */

public class DataSourceChangeListenerImpl2 implements DataSourceChangeListener {
	
    /** Creates a new instance of DataSourceChangeListenerImpl2 */
    public DataSourceChangeListenerImpl2() {
    }


    public synchronized boolean isHierarchyChanged(RolapHierarchy hierarchy) {
    	return true;
    }

    public synchronized boolean isAggregationChanged(Aggregation aggregation) {
        return false;
    }

    public String getTableName(RolapHierarchy hierarchy) {
        MondrianDef.RelationOrJoin relation = hierarchy.getRelation();
        if (relation instanceof MondrianDef.Table) {
            MondrianDef.Table tableRelation = (MondrianDef.Table) relation;
            return tableRelation.name;
        } else {
            return null;
        }
    }
}

// End DataSourceChangeListenerImpl2.java