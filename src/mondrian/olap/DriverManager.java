/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/DriverManager.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2002-2002 Kana Software, Inc.
// Copyright (C) 2002-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 15 January, 2002
*/

package mondrian.olap;
import javax.sql.DataSource;

import mondrian.lmdql.Parameters;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.spi.CatalogLocator;

/**
 * The basic service for managing a set of OLAP drivers.
 *
 * @author jhyde
 * @since 15 January, 2002
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/olap/DriverManager.java#2 $
 */
public class DriverManager {

    public DriverManager() {
    }

    /**
     * Creates a connection to a Mondrian OLAP Engine
     * using a connect string
     * and a catalog locator.
     *
     * @param connectString Connect string of the form
     *   'property=value;property=value;...'.
     *   See {@link mondrian.olap.Util#parseConnectString} for more details of the format.
     *   See {@link mondrian.rolap.RolapConnectionProperties} for a list of
     *   allowed properties.
     * @param locator Use to locate real catalog url by a customized
     *   configuration value. If <code>null</code>, leave the catalog url
     *   unchanged.
     * @return A {@link Connection}
     * @post return != null
     */
    public static Connection getConnection(
        String connectString,
        CatalogLocator locator)
    {
        Util.PropertyList properties = Util.parseConnectString(connectString);
        return getConnection(properties, locator);
    }

    /**
     * Creates a connection to a Mondrian OLAP Engine.
     *
     * @param properties Collection of properties which define the location
     *   of the connection.
     *   See {@link mondrian.rolap.RolapConnection} for a list of allowed properties.
     * @param locator Use to locate real catalog url by a customized
     *   configuration value. If <code>null</code>, leave the catalog url
     *   unchanged.
     * @return A {@link Connection}
     * @post return != null
     */
    public static Connection getConnection(
        Util.PropertyList properties,
        CatalogLocator locator)
    {
        return getConnection(properties, locator, null);
    }

    /**
     * Creates a connection to a Mondrian OLAP Engine
     * using a list of connection properties,
     * a catalog locator,
     * and a JDBC data source.
     *
     * @param properties Collection of properties which define the location
     *   of the connection.
     *   See {@link mondrian.rolap.RolapConnection} for a list of allowed properties.
     * @param locator Use to locate real catalog url by a customized
     *   configuration value. If <code>null</code>, leave the catalog url
     *   unchanged.
     * @param dataSource - if not null an external DataSource to be used
     *        by Mondrian
     * @return A {@link Connection}
     * @post return != null
     */
    public static Connection getConnection(
        Util.PropertyList properties,
        CatalogLocator locator,
        DataSource dataSource)
    {
        String provider = properties.get("PROVIDER", "mondrian");
        if (!provider.equalsIgnoreCase("mondrian")) {
            throw Util.newError("Provider not recognized: " + provider);
        }
        if (locator != null) {
            String catalog = properties.get(
                RolapConnectionProperties.Catalog.name());
            properties.put(
                RolapConnectionProperties.Catalog.name(),
                locator.locate(catalog));
        }

        Parameters.mdxConnProperties = properties; // para facilitar acesso as parametros, mesmo sem o objeto perto #Paulo Caetano

        return new RolapConnection(properties, dataSource);
    }

    // M�todo acrescentado para compatibilizar com  
    // o m�todo com.tonbeller.jpivot.mondrian.MondrianModel.initialize que chama
    // um m�todo mondrian.olap.DriverManager.getConnection com essa assinatura,
    // passando false no boolean.
    // As classes utilizadas do JPivot e Tonbeller est�o no JPivot 1.8.0
    // Paulo Caetano, 10/11/09
    public static Connection getConnection( 
            Util.PropertyList properties,
            CatalogLocator locator,
            DataSource dataSource,
            boolean quemSabe) {
        return getConnection(properties, locator, dataSource);
    	
    }

}

// End DriverManager.java
