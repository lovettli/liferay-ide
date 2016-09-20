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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Andy Wu
 */
public class ImportLiferayModuleProjectOpMethods
{

    public static final Status execute( final ImportLiferayModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Module project...", 100 );

        String location = op.getLocation().content().toOSString();

        ILiferayProjectImporter importer = LiferayCore.getImporter( op.getBuildType().content() );

        Status retval = Status.createOkStatus();

        try
        {
            importer.importProject( location, monitor );
        }
        catch( CoreException e )
        {
            retval = Status.createErrorStatus( e );
        }

        return retval;
    }

    public static IStatus getBuildType( String location )
    {
        IStatus retval = null;

        final ILiferayProjectImporter[] importers = LiferayCore.getImporters();

        for( ILiferayProjectImporter importer : importers )
        {
            final IStatus status = importer.canImport( location );

            if( status == null )
            {
                retval = ProjectCore.createErrorStatus( "Location is not recognized as a valid project type." );
            }
            else if( status.isOK() )
            {
                retval = StatusBridge.create( Status.createStatus( Severity.OK, importer.getBuildType() ) );
                break;
            }
            else if( status.getSeverity() == IStatus.ERROR )
            {
                retval = StatusBridge.create( Status.createStatus( Severity.ERROR, status.getMessage() ) );
                break;
            }
            else if( status.getSeverity() == IStatus.WARNING )
            {
                retval = StatusBridge.create( Status.createStatus( Severity.WARNING, status.getMessage() ) );
                break;
            }
        }

        if( retval == null )
        {
            retval = StatusBridge.create( Status.createStatus( Severity.ERROR, "No project importers found." ) );
        }

        return retval;
    }

}
