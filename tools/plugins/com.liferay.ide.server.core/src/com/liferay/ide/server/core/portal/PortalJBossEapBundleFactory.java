/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.server.util.JavaUtil;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class PortalJBossEapBundleFactory extends PortalJBossBundleFactory
{

    private static final String EAP61_DIR_META_INF = "modules/system/layers/base/org/jboss/as/product/eap/dir/META-INF";

    @Override
    public PortalBundle create( Map<String, String> appServerProperties )
    {
        return new PortalJBossEapBundle( appServerProperties );
    }

    @Override
    public PortalBundle create( IPath location )
    {
        return new PortalJBossEapBundle( location );
    }

    @Override
    protected boolean detectBundleDir( IPath path )
    {
        if( !path.toFile().exists() )
        {
            return false;
        }

        if( path.append( "bundles" ).toFile().exists() && path.append( "modules" ).toFile().exists() &&
            path.append( "standalone" ).toFile().exists() && path.append( "bin" ).toFile().exists() )
        {
            return getEAP6xVersion( path.toFile(), EAP61_DIR_META_INF, "6.", "eap", "EAP" ) != null;
        }

        return false;
    }

    private String getEAP6xVersion(
        File location, String metaInfPath, String versionPrefix, String slot, String releaseName )
    {
        IPath rootPath = new Path( location.getAbsolutePath() );
        IPath productConf = rootPath.append( "bin/product.conf" );

        if( productConf.toFile().exists() )
        {
            Properties p = PropertiesUtil.loadProperties( productConf.toFile() );

            if( p != null )
            {
                String product = (String) p.get( "slot" );

                if( slot.equals( product ) )
                {
                    return getEAP6xVersionNoSlotCheck( location, metaInfPath, versionPrefix, releaseName );
                }
            }
        }

        return null;
    }

    public static String getEAP6xVersionNoSlotCheck(
        File location, String metaInfPath, String versionPrefix, String releaseName )
    {
        IPath rootPath = new Path( location.getAbsolutePath() );
        IPath eapDir = rootPath.append( metaInfPath );

        if( eapDir.toFile().exists() )
        {
            IPath manifest = eapDir.append( "MANIFEST.MF" );
            String type = JavaUtil.getManifestProperty( manifest.toFile(), "JBoss-Product-Release-Name" );
            String version = JavaUtil.getManifestProperty( manifest.toFile(), "JBoss-Product-Release-Version" );
            boolean matchesName = releaseName == null || releaseName.equals( type );
            boolean matchesVersion = versionPrefix == null || version.startsWith( versionPrefix );

            if( matchesName && matchesVersion )
                return version;
        }

        return null;
    }
}
