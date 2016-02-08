/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/aggmatcher/JdbcSchema.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/

package mondrian.rolap.aggmatcher;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.sql.DataSource;

import mondrian.olap.MondrianDef;
import mondrian.olap.MondrianProperties;
import mondrian.olap.Util;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RolapAggregator;
import mondrian.rolap.RolapStar;
import mondrian.rolap.sql.SqlQuery;

import org.apache.log4j.Logger;

/**
 * This class is used to scrape a database and store information about its
 * tables and columnIter.
 *
 * <p>The structure of this information is as follows:
 * A database has tables. A table has columnIter. A column has one or more usages.
 * A usage might be a column being used as a foreign key or as part of a
 * measure.
 *
 * <p> Tables are created when calling code requests the set of available
 * tables. This call <code>getTables()</code> causes all tables to be loaded.
 * But a table's columnIter are not loaded until, on a table-by-table basis,
 * a request is made to get the set of columnIter associated with the table.
 * Since, the AggTableManager first attempts table name matches (recognition)
 * most tables do not match, so why load their columnIter.
 * Of course, as a result, there are a host of methods that can throw an
 * {@link SQLException}, rats.
 *
 * @author Richard M. Emberson
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/aggmatcher/JdbcSchema.java#2 $
 */
public class JdbcSchema {
    private static final Logger LOGGER =
        Logger.getLogger(JdbcSchema.class);

    private static final MondrianResource mres = MondrianResource.instance();

    /**
     * Get the Logger.
     */
    public Logger getLogger() {
        return LOGGER;
    }

    public interface Factory {
        JdbcSchema makeDB(DataSource dataSource);
        void clearDB(JdbcSchema db);
        void removeDB(JdbcSchema db);
    }

    private static final Map<DataSource, SoftReference<JdbcSchema>> dbMap =
        new HashMap<DataSource, SoftReference<JdbcSchema>>();

    /**
     * How often between sweeping through the dbMap looking for nulls.
     */
    private static final int SWEEP_COUNT = 10;
    private static int sweepDBCount = 0;

    public static class StdFactory implements Factory {
        StdFactory() {
        }
        public JdbcSchema makeDB(DataSource dataSource) {
            JdbcSchema db = new JdbcSchema(dataSource);
            return db;
        }
        public void clearDB(JdbcSchema db) {
            // NoOp
        }
        public void removeDB(JdbcSchema db) {
            // NoOp
        }
    }

    private static Factory factory;

    private static void makeFactory() {
        if (factory == null) {
            String classname =
                    MondrianProperties.instance().JdbcFactoryClass.get();
            if (classname == null) {
                factory = new StdFactory();
            } else {
                try {
                    Class<?> clz = Class.forName(classname);
                    factory = (Factory) clz.newInstance();
                } catch (ClassNotFoundException ex) {
                    throw mres.BadJdbcFactoryClassName.ex(classname);
                } catch (InstantiationException ex) {
                    throw mres.BadJdbcFactoryInstantiation.ex(classname);
                } catch (IllegalAccessException ex) {
                    throw mres.BadJdbcFactoryAccess.ex(classname);
                }
            }
        }
    }

    /**
     * Creates or retrieves an instance of the JdbcSchema for the given
     * DataSource.
     *
     * @param dataSource DataSource
     * @return instance of the JdbcSchema for the given DataSource
     */
    public static synchronized JdbcSchema makeDB(DataSource dataSource) {
        makeFactory();

        JdbcSchema db = null;
        SoftReference<JdbcSchema> ref = dbMap.get(dataSource);
        if (ref != null) {
            db = ref.get();
        }
        if (db == null) {
            db = factory.makeDB(dataSource);
            dbMap.put(dataSource, new SoftReference<JdbcSchema>(db));
        }

        sweepDB();

        return db;
    }

    /**
     * Clears information in a JdbcSchema associated with a DataSource.
     *
     * @param dataSource DataSource
     */
    public static synchronized void clearDB(DataSource dataSource) {
        makeFactory();

        SoftReference<JdbcSchema> ref = dbMap.get(dataSource);
        if (ref != null) {
            JdbcSchema db = ref.get();
            if (db != null) {
                factory.clearDB(db);
                db.clear();
            } else {
                dbMap.remove(dataSource);
            }
        }
        sweepDB();
    }

    /**
     * Removes a JdbcSchema associated with a DataSource.
     *
     * @param dataSource DataSource
     */
    public static synchronized void removeDB(DataSource dataSource) {
        makeFactory();

        SoftReference<JdbcSchema> ref = dbMap.remove(dataSource);
        if (ref != null) {
            JdbcSchema db = ref.get();
            if (db != null) {
                factory.removeDB(db);
                db.remove();
            }
        }
        sweepDB();
    }

    /**
     * Every SWEEP_COUNT calls to this method, go through all elements of
     * the dbMap removing all that either have null values (null SoftReference)
     * or those with SoftReference with null content.
     */
    private static void sweepDB() {
        if (sweepDBCount++ > SWEEP_COUNT) {
            Iterator<SoftReference<JdbcSchema>> it = dbMap.values().iterator();
            while (it.hasNext()) {
                SoftReference<JdbcSchema> ref = it.next();
                if ((ref == null) || (ref.get() == null)) {
                    try {
                        it.remove();
                    } catch (Exception ex) {
                        // Should not happen, but might still like to
                        // know that something's funky.
                        LOGGER.warn(ex);
                    }
                }

            }
            // reset
            sweepDBCount = 0;
        }
    }


    //
    // Types of column usages.
    //
    public static final int UNKNOWN_COLUMN_USAGE         = 0x0001;
    public static final int FOREIGN_KEY_COLUMN_USAGE     = 0x0002;
    public static final int MEASURE_COLUMN_USAGE         = 0x0004;
    public static final int LEVEL_COLUMN_USAGE           = 0x0008;
    public static final int FACT_COUNT_COLUMN_USAGE      = 0x0010;
    public static final int IGNORE_COLUMN_USAGE          = 0x0020;

    public static final String UNKNOWN_COLUMN_NAME         = "UNKNOWN";
    public static final String FOREIGN_KEY_COLUMN_NAME     = "FOREIGN_KEY";
    public static final String MEASURE_COLUMN_NAME         = "MEASURE";
    public static final String LEVEL_COLUMN_NAME           = "LEVEL";
    public static final String FACT_COUNT_COLUMN_NAME      = "FACT_COUNT";
    public static final String IGNORE_COLUMN_NAME          = "IGNORE";

    /**
     * Enumeration of ways that an aggregate table can use a column.
     */
    enum UsageType {
        UNKNOWN,
        FOREIGN_KEY,
        MEASURE,
        LEVEL,
        FACT_COUNT,
        IGNORE
    }

    /**
     * Determine if the parameter represents a single column type, i.e., the
     * column only has one usage.
     *
     * @param columnType
     * @return true if column has only one usage.
     */
    public static boolean isUniqueColumnType(Set<UsageType> columnType) {
        return columnType.size() == 1;
    }

    /**
     * Maps from column type enum to column type name or list of names if the
     * parameter represents more than on usage.
     */
    public static String convertColumnTypeToName(Set<UsageType> columnType) {
        if (columnType.size() == 1) {
            return columnType.iterator().next().name();
        }
        // it's a multi-purpose column
        StringBuilder buf = new StringBuilder();
        int k = 0;
        for (UsageType usage : columnType) {
            if (k++ > 0) {
                buf.append('|');
            }
            buf.append(usage.name());
        }
        return buf.toString();
    }

    /**
     * Converts a {@link java.sql.Types} value to a
     * {@link mondrian.rolap.sql.SqlQuery.Datatype}.
     *
     * @param javaType JDBC type code, as per {@link java.sql.Types}
     * @return Datatype
     */
    public static SqlQuery.Datatype getDatatype(int javaType) {
        switch (javaType) {
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
        case Types.BIGINT:
            return SqlQuery.Datatype.Integer;
        case Types.FLOAT:
        case Types.REAL:
        case Types.DOUBLE:
        case Types.NUMERIC:
        case Types.DECIMAL:
            return SqlQuery.Datatype.Numeric;
        case Types.BOOLEAN:
            return SqlQuery.Datatype.Boolean;
        case Types.DATE:
            return SqlQuery.Datatype.Date;
        case Types.TIME:
            return SqlQuery.Datatype.Time;
        case Types.TIMESTAMP:
            return SqlQuery.Datatype.Timestamp;
        case Types.CHAR:
        case Types.VARCHAR:
        default:
            return SqlQuery.Datatype.String;
        }
    }

    /**
     * Returns true if the parameter is a java.sql.Type text type.
     */
    public static boolean isText(int javaType) {
        switch (javaType) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
            return true;
        default:
            return false;
        }
    }

    enum TableUsageType {
        UNKNOWN,
        FACT,
        AGG
    }

    /**
     * A table in a database.
     */
    public class Table {

        /**
         * A column in a table.
         */
        public class Column {

            /**
             * A usage of a column.
             */
            public class Usage {
                private final UsageType usageType;
                private String symbolicName;
                private RolapAggregator aggregator;

                ////////////////////////////////////////////////////
                //
                // These instance variables are used to hold
                // stuff which is determines at one place and
                // then used somewhere else. Generally, a usage
                // is created before all of its "stuff" can be
                // determined, hence, usage is not a set of classes,
                // rather its one class with a bunch of instance
                // variables which may or may not be used.
                //

                // measure stuff
                public RolapStar.Measure rMeasure;

                // hierarchy stuff
                public MondrianDef.Relation relation;
                public MondrianDef.Expression joinExp;
                public String levelColumnName;

                // level
                public RolapStar.Column rColumn;

                // for subtables
                public RolapStar.Table rTable;
                public String rightJoinConditionColumnName;

                // It is used to hold the (possible null) prefix to
                // use during aggregate table generation (See AggGen).
                public String usagePrefix;
                //
                ////////////////////////////////////////////////////

                Usage(UsageType usageType) {
                    this.usageType = usageType;
                }

                /**
                 * This is the column with which this usage is associated.
                 *
                 * @return the usage's column.
                 */
                public Column getColumn() {
                    return JdbcSchema.Table.Column.this;
                }

                /**
                 * The column usage type.
                 */
                public UsageType getUsageType() {
                    return usageType;
                }

                /**
                 * Set the symbolic (logical) name associated with this usage.
                 * For example, this might be the measure's name.
                 *
                 * @param symbolicName
                 */
                public void setSymbolicName(final String symbolicName) {
                    this.symbolicName = symbolicName;
                }

                /**
                 * Get usage's symbolic name.
                 */
                public String getSymbolicName() {
                    return symbolicName;
                }

                /**
                 * Set the aggregator associated with this usage (if its a
                 * measure usage).
                 *
                 * @param aggregator
                 */
                public void setAggregator(final RolapAggregator aggregator) {
                    this.aggregator = aggregator;
                }

                /**
                 * Get the aggregator associated with this usage (if its a
                 * measure usage, otherwise null).
                 */
                public RolapAggregator getAggregator() {
                    return aggregator;
                }

                public String toString() {
                    StringWriter sw = new StringWriter(64);
                    PrintWriter pw = new PrintWriter(sw);
                    print(pw, "");
                    pw.flush();
                    return sw.toString();
                }

                public void print(final PrintWriter pw, final String prefix) {
                    if (getSymbolicName() != null) {
                        pw.print("symbolicName=");
                        pw.print(getSymbolicName());
                    }
                    if (getAggregator() != null) {
                        pw.print(", aggregator=");
                        pw.print(getAggregator().getName());
                    }
                    pw.print(", columnType=");
                    pw.print(getUsageType().name());
                }
            }

            /** This is the name of the column. */
            private final String name;

            /** This is the java.sql.Type enum of the column in the database. */
            private int type;
            /**
             * This is the java.sql.Type name of the column in the database.
             */
            private String typeName;

            /** This is the size of the column in the database. */
            private int columnSize;

            /** The number of fractional digits. */
            private int decimalDigits;

            /** Radix (typically either 10 or 2). */
            private int numPrecRadix;

            /** For char types the maximum number of bytes in the column. */
            private int charOctetLength;

            /**
             * False means the column definitely does not allow NULL values.
             */
            private boolean isNullable;

            public final MondrianDef.Column column;

            private final List<JdbcSchema.Table.Column.Usage> usages;

            /**
             * This contains the enums of all of the column's usages.
             */
            private final Set<UsageType> usageTypes =
                Util.enumSetNoneOf(UsageType.class);

            private Column(final String name) {
                this.name = name;
                this.column =
                    new MondrianDef.Column(
                        JdbcSchema.Table.this.getName(),
                        name);
                this.usages = new ArrayList<JdbcSchema.Table.Column.Usage>();
            }

            /**
             * For testing ONLY
            JdbcSchema.Table.Column copy() {
                Column column = new Column(name);
                column.type = type;
                column.typeName = typeName;
                column.columnSize = columnSize;
                column.decimalDigits = decimalDigits;
                column.numPrecRadix = numPrecRadix;
                column.charOctetLength = charOctetLength;
                column.isNullable = isNullable;

                return column;
            }
             */
            /**
             * For testing ONLY
            void clearUsages() {
                // empty
            }
             */


            /**
             * This is the column's name in the database, not a symbolic name.
             */
            public String getName() {
                return name;
            }

            /**
             * Set the columnIter java.sql.Type enun of the column.
             *
             * @param type
             */
            private void setType(final int type) {
                this.type = type;
            }

            /**
             * Get the columnIter java.sql.Type enun of the column.
             */
            public int getType() {
                return type;
            }

            /**
             * Set the columnIter java.sql.Type name.
             *
             * @param typeName
             */
            private void setTypeName(final String typeName) {
                this.typeName = typeName;
            }

            /**
             * Get the columnIter java.sql.Type name.
             */
            public String getTypeName() {
                return typeName;
            }

            /**
             * Get this column's table.
             */
            public Table getTable() {
                return JdbcSchema.Table.this;
            }

            /**
             * Return true if this column is numeric.
             */
            public SqlQuery.Datatype getDatatype() {
                return JdbcSchema.getDatatype(getType());
            }

            /**
             * Set the size in bytes of the column in the database.
             *
             * @param columnSize
             */
            private void setColumnSize(final int columnSize) {
                this.columnSize = columnSize;
            }

            /**
             * Get the size in bytes of the column in the database.
             *
             */
            public int getColumnSize() {
                return columnSize;
            }

            /**
             * Set number of fractional digits.
             *
             * @param decimalDigits
             */
            private void setDecimalDigits(final int decimalDigits) {
                this.decimalDigits = decimalDigits;
            }

            /**
             * Get number of fractional digits.
             */
            public int getDecimalDigits() {
                return decimalDigits;
            }

            /**
             * Set Radix (typically either 10 or 2).
             *
             * @param numPrecRadix
             */
            private void setNumPrecRadix(final int numPrecRadix) {
                this.numPrecRadix = numPrecRadix;
            }

            /**
             * Get Radix (typically either 10 or 2).
             */
            public int getNumPrecRadix() {
                return numPrecRadix;
            }

            /**
             * For char types the maximum number of bytes in the column.
             *
             * @param charOctetLength
             */
            private void setCharOctetLength(final int charOctetLength) {
                this.charOctetLength = charOctetLength;
            }

            /**
             * For char types the maximum number of bytes in the column.
             */
            public int getCharOctetLength() {
                return charOctetLength;
            }

            /**
             * False means the column definitely does not allow NULL values.
             *
             * @param isNullable
             */
            private void setIsNullable(final boolean isNullable) {
                this.isNullable = isNullable;
            }

            /**
             * False means the column definitely does not allow NULL values.
             */
            public boolean isNullable() {
                return isNullable;
            }

            /**
             * How many usages does this column have. A column has
             * between 0 and N usages. It has no usages if usages is some
             * administrative column. It has one usage if, for example, its
             * the fact_count column or a level column (for a collapsed
             * dimension aggregate). It might have 2 usages if its a foreign key
             * that is also used as a measure. If its a column used in N
             * measures, then usages will have N usages.
             */
            public int numberOfUsages() {
                return usages.size();
            }

            /**
             * Return true if the column has at least one usage.
             */
            public boolean hasUsage() {
                return (usages.size() != 0);
            }

            /**
             * Return true if the column has at least one usage of the given
             * column type.
             */
            public boolean hasUsage(UsageType columnType) {
                return usageTypes.contains(columnType);
            }

            /**
             * Get an iterator over all usages.
             */
            public List<Usage> getUsages() {
                return usages;
            }

            /**
             * Get an iterator over all usages of the given column type.
             */
            public Iterator<Usage> getUsages(UsageType usageType) {

                // Yes, this is legal.
                class ColumnTypeIterator implements Iterator<Usage> {
                    private final Iterator<Usage> usageIter;
                    private final UsageType usageType;
                    private Usage nextUsage;

                    ColumnTypeIterator(
                        final List<Usage> usages,
                        final UsageType columnType)
                    {
                        this.usageIter = usages.iterator();
                        this.usageType = columnType;
                    }

                    public boolean hasNext() {
                        while (usageIter.hasNext()) {
                            Usage usage = usageIter.next();
                            if (usage.getUsageType() == this.usageType) {
                                nextUsage = usage;
                                return true;
                            }

                        }
                        nextUsage = null;
                        return false;
                    }

                    public Usage next() {
                        return nextUsage;
                    }

                    public void remove() {
                        usageIter.remove();
                    }
                }

                return new ColumnTypeIterator(getUsages(), usageType);
            }

            /**
             * Create a new usage of a given column type.
             */
            public Usage newUsage(UsageType usageType) {
                this.usageTypes.add(usageType);

                Usage usage = new Usage(usageType);
                usages.add(usage);
                return usage;
            }

            public String toString() {
                StringWriter sw = new StringWriter(256);
                PrintWriter pw = new PrintWriter(sw);
                print(pw, "");
                pw.flush();
                return sw.toString();
            }

            public void print(final PrintWriter pw, final String prefix) {
                pw.print(prefix);
                pw.print("name=");
                pw.print(getName());
                pw.print(", typename=");
                pw.print(getTypeName());
                pw.print(", size=");
                pw.print(getColumnSize());

                switch (getType()) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE:
                    break;
                case Types.NUMERIC:
                case Types.DECIMAL:
                    pw.print(", decimalDigits=");
                    pw.print(getDecimalDigits());
                    pw.print(", numPrecRadix=");
                    pw.print(getNumPrecRadix());
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                    pw.print(", charOctetLength=");
                    pw.print(getCharOctetLength());
                    break;
                case Types.LONGVARCHAR:
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                default:
                    break;
                }
                pw.print(", isNullable=");
                pw.print(isNullable());

                if (hasUsage()) {
                    pw.print(" Usages [");
                    for (Usage usage : getUsages()) {
                        pw.print('(');
                        usage.print(pw, prefix);
                        pw.print(')');
                    }
                    pw.println("]");
                }
            }
        }

        /** Name of table. */
        private final String name;
        /** Map from column name to column. */
        private Map<String, Column> columnMap;
        /** Sum of all of the table's column's column sizes. */
        private int totalColumnSize;
        /**
         * Is the table a fact, aggregate or other table type.
         * Note: this assumes that a table has only ONE usage.
         */
        private TableUsageType tableUsageType;

        /**
         * Typical table types are: "TABLE", "VIEW", "SYSTEM TABLE",
         * "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
         * (Depends what comes out of JDBC.)
         */
        private final String tableType;

        // mondriandef stuff
        public MondrianDef.Table table;

        private boolean allColumnsLoaded;

        private Table(final String name, String tableType) {
            this.name = name;
            this.tableUsageType = TableUsageType.UNKNOWN;
            this.tableType = tableType;
        }

        public void load() throws SQLException {
            loadColumns();
        }

        /**
         * For testing ONLY
        JdbcSchema.Table copy() {
            Table table = new Table(name);
            table.totalColumnSize = totalColumnSize;
            table.tableUsage = tableUsage;
            table.tableType = tableType;

            Map m = table.getColumnMap();
            for (Iterator usageIter = getColumns(); usageIter.hasNext(); ) {
                Column column = (Column) usageIter.next();
                m.put(column.getName(), column.copy());
            }

            return table;
        }
         */
        /**
         * For testing ONLY
        void clearUsages() {
            this.tableUsage = UNKNOWN_TABLE_USAGE;
            for (Iterator usageIter = getColumns(); usageIter.hasNext(); ) {
                Column column = (Column) usageIter.next();
                column.clearUsages();
            }
        }
         */

        /**
         * Get the name of the table.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the total size of a row (sum of the column sizes).
         */
        public int getTotalColumnSize() {
            return totalColumnSize;
        }

        /**
         * Get the number of rows in the table.
         */
        public int getNumberOfRows() {
            return -1;
        }

        /**
         * Returns the collection of columns in this Table.
         */
        public Collection<Column> getColumns() {
            return getColumnMap().values();
        }

        /**
         * Returns an iterator over all column usages of a given type.
         */
        public Iterator<JdbcSchema.Table.Column.Usage> getColumnUsages(
            final UsageType usageType)
        {

            class CTIterator implements Iterator<JdbcSchema.Table.Column.Usage> {
                private final Iterator<Column> columnIter;
                private final UsageType columnType;
                private Iterator<JdbcSchema.Table.Column.Usage> usageIter;
                private JdbcSchema.Table.Column.Usage nextObject;

                CTIterator(Collection<Column> columns, UsageType columnType) {
                    this.columnIter = columns.iterator();
                    this.columnType = columnType;
                }

                public boolean hasNext() {
                    while (true) {
                        while ((usageIter == null) || ! usageIter.hasNext()) {
                            if (! columnIter.hasNext()) {
                                nextObject = null;
                                return false;
                            }
                            Column c = columnIter.next();
                            usageIter = c.getUsages().iterator();
                        }
                        JdbcSchema.Table.Column.Usage usage = usageIter.next();
                        if (usage.getUsageType() == columnType) {
                            nextObject = usage;
                            return true;
                        }
                    }
                }
                public JdbcSchema.Table.Column.Usage next() {
                    return nextObject;
                }
                public void remove() {
                    usageIter.remove();
                }
            }
            return new CTIterator(getColumns(), usageType);
        }

        /**
         * Get a column by its name.
         */
        public Column getColumn(final String columnName) {
            return getColumnMap().get(columnName);
        }

        /**
         * Return true if this table contains a column with the given name.
         */
        public boolean constainsColumn(final String columnName) {
            return getColumnMap().containsKey(columnName);
        }

        /**
         * Set the table usage (fact, aggregate or other).
         *
         * @param tableUsageType
         */
        public void setTableUsageType(final TableUsageType tableUsageType) {
            // if usageIter has already been set, then usageIter can NOT be reset
            if ((this.tableUsageType != TableUsageType.UNKNOWN) &&
                    (this.tableUsageType != tableUsageType)) {

                throw mres.AttemptToChangeTableUsage.ex(
                    getName(),
                    this.tableUsageType.name(),
                    tableUsageType.name());
            }
            this.tableUsageType = tableUsageType;
        }

        /**
         * Get the table's usage type.
         */
        public TableUsageType getTableUsageType() {
            return tableUsageType;
        }

        /**
         * Get the table's type.
         */
        public String getTableType() {
            return tableType;
        }

        public String toString() {
            StringWriter sw = new StringWriter(256);
            PrintWriter pw = new PrintWriter(sw);
            print(pw, "");
            pw.flush();
            return sw.toString();
        }
        public void print(final PrintWriter pw, final String prefix) {
            pw.print(prefix);
            pw.println("Table:");
            String subprefix = prefix + "  ";
            String subsubprefix = subprefix + "  ";

            pw.print(subprefix);
            pw.print("name=");
            pw.print(getName());
            pw.print(", type=");
            pw.print(getTableType());
            pw.print(", usage=");
            pw.println(getTableUsageType().name());

            pw.print(subprefix);
            pw.print("totalColumnSize=");
            pw.println(getTotalColumnSize());

            pw.print(subprefix);
            pw.println("Columns: [");
            for (Column column : getColumnMap().values()) {
                column.print(pw, subsubprefix);
                pw.println();
            }
            pw.print(subprefix);
            pw.println("]");
        }

        /**
         * Get all of the columnIter associated with a table and create Column
         * objects with the column's name, type, type name and column size.
         *
         * @throws SQLException
         */
        private void loadColumns() throws SQLException {
            if (! allColumnsLoaded) {
                Connection conn = getDataSource().getConnection();
                try {
                    DatabaseMetaData dmd = conn.getMetaData();

                    String schema = JdbcSchema.this.getSchemaName();
                    String catalog = JdbcSchema.this.getCatalogName();
                    String tableName = getName();
                    String columnNamePattern = "%";

                    ResultSet rs = null;
                    try {
                        Map<String, Column> map = getColumnMap();
                        rs = dmd.getColumns(catalog,
                                            schema,
                                            tableName,
                                            columnNamePattern);
                        while (rs.next()) {
                            String name = rs.getString(4);
                            int type = rs.getInt(5);
                            String typeName = rs.getString(6);
                            int columnSize = rs.getInt(7);
                            int decimalDigits = rs.getInt(9);
                            int numPrecRadix = rs.getInt(10);
                            int charOctetLength = rs.getInt(16);
                            String isNullable = rs.getString(18);

                            Column column = new Column(name);
                            column.setType(type);
                            column.setTypeName(typeName);
                            column.setColumnSize(columnSize);
                            column.setDecimalDigits(decimalDigits);
                            column.setNumPrecRadix(numPrecRadix);
                            column.setCharOctetLength(charOctetLength);
                            column.setIsNullable(!"NO".equals(isNullable));

                            map.put(name, column);
                            totalColumnSize += column.getColumnSize();
                        }
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        //ignore
                    }
                }

                allColumnsLoaded = true;
            }
        }

        private Map<String, Column> getColumnMap() {
            if (columnMap == null) {
                columnMap = new HashMap<String, Column>();
            }
            return columnMap;
        }
    }

    private DataSource dataSource;
    private String schema;
    private String catalog;
    private boolean allTablesLoaded;

    /**
     * Tables by name. We use a sorted map so {@link #getTables()}'s output
     * is in deterministic order.
     */
    private final SortedMap<String, Table> tables = new TreeMap<String, Table>();

    JdbcSchema(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This forces the tables to be loaded.
     *
     * @throws SQLException
     */
    public void load() throws SQLException {
        loadTables();
    }

    /**
     * For testing ONLY
    JdbcSchema copy() {
        JdbcSchema jdbcSchema = new JdbcSchema(dataSource);
        jdbcSchema.setSchemaName(getSchemaName());
        jdbcSchema.setCatalogName(getCatalogName());

        Map m = jdbcSchema.getTablesMap();
        for (Iterator usageIter = getTables(); usageIter.hasNext(); ) {
            Table table = (Table) usageIter.next();
            m.put(table.getName(), table.copy());
        }

        return jdbcSchema;
    }
     */

    /**
     * For testing ONLY
    void clearUsages() {
        for (Iterator usageIter = getTables(); usageIter.hasNext(); ) {
            Table table = (Table) usageIter.next();
            table.clearUsages();
        }
    }
     */

    protected void clear() {
        // keep the DataSource, clear/reset everything else
        allTablesLoaded = false;
        schema = null;
        catalog = null;
        tables.clear();
    }
    protected void remove() {
        // set ALL instance variables to null
        clear();
        dataSource = null;
    }

    /**
     * This is used for testing allowing one to load tables and their columnIter
     * from more than one datasource
     */
    void resetAllTablesLoaded() {
        allTablesLoaded = false;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Set the database's schema name.
     *
     * @param schema
     */
    public void setSchemaName(final String schema) {
        this.schema = schema;
    }

    /**
     * Get the database's schema name.
     */
    public String getSchemaName() {
        return schema;
    }

    /**
     * Set the database's catalog name.
     */
    public void setCatalogName(final String catalog) {
        this.catalog = catalog;
    }

    /**
     * Get the database's catalog name.
     */
    public String getCatalogName() {
        return catalog;
    }

    /**
     * Returns the database's tables. The collection is sorted by table name.
     */
    public synchronized Collection<Table> getTables() {
        return getTablesMap().values();
    }

    /**
     * Gets a table by name.
     */
    public synchronized Table getTable(final String tableName) {
        return getTablesMap().get(tableName);
    }

    public String toString() {
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw);
        print(pw, "");
        pw.flush();
        return sw.toString();
    }
    public void print(final PrintWriter pw, final String prefix) {
        pw.print(prefix);
        pw.println("JdbcSchema:");
        String subprefix = prefix + "  ";
        String subsubprefix = subprefix + "  ";

        pw.print(subprefix);
        pw.println("Tables: [");
        Iterator it = getTablesMap().values().iterator();
        while (it.hasNext()) {
            Table table = (Table) it.next();
            table.print(pw, subsubprefix);
        }
        pw.print(subprefix);
        pw.println("]");
    }

    /**
     * This method gets all of the tables (and views) in the database.
     * If called a second time, this method is a no-op.
     *
     * @throws SQLException
     */
    private void loadTables() throws SQLException {
        if (! allTablesLoaded) {
            Connection conn = getDataSource().getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            String schema = getSchemaName();
            String catalog = getCatalogName();
            String[] tableTypes = { "TABLE", "VIEW" };
            String tableName = "%";

            ResultSet rs = null;
            try {
                rs = dmd.getTables(catalog,
                                   schema,
                                   tableName,
                                   tableTypes);
                if (rs != null) {
                    while (rs.next()) {
                        addTable(rs);
                    }
                } else {
                    getLogger().debug("ERROR: rs == null");
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
            try {
                conn.close();
            } catch (SQLException e) {
                //ignore
            }

            allTablesLoaded = true;
        }
    }

    /**
     * Make a Table from an ResultSet - the table's name is the ResultSet third
     * entry.
     *
     * @param rs
     * @throws SQLException
     */
    protected void addTable(final ResultSet rs) throws SQLException {
        String name = rs.getString(3);
        String tableType = rs.getString(4);
        Table table = new Table(name, tableType);

        tables.put(table.getName(), table);
    }

    private SortedMap<String, Table> getTablesMap() {
        return tables;
    }

    public static synchronized void clearAllDBs() {
        factory = null;
        makeFactory();
    }
}

// End JdbcSchema.java
