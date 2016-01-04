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
package com.liferay.ide.ui;

import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class WorkspaceHelper implements WorkspaceHelperMBean
{

    @Override
    public String openDir( String path )
    {
        String retval = null;

        final File dir = new File( path );

        if( ( !dir.exists() )  || ( !dir.isDirectory() ) )
        {
            retval = "Directory doesn't exist or isn't a directory.";
        }

        final File dotProject = new File( dir, ".project" );

        if ( dotProject.exists() )
        {
            retval = importExistingProject( dir );
        }
        else
        {
            retval = "Directory must have a .project file to open.";
        }

        return retval;
    }

    private String importExistingProject( File dir )
    {
        String retval = null;

        try
        {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final IProjectDescription description =
                workspace.loadProjectDescription( new Path( dir.getAbsolutePath() ).append( ".project" ) );

            final String name = description.getName();

            final IProject project = workspace.getRoot().getProject( name );

            if( project.exists() )
            {
                retval = "Project with name " + name + " already exists";
            }
            else
            {
                final IRunnableWithProgress runnable = new IRunnableWithProgress()
                {
                    @Override
                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                    {
                        try
                        {
                            project.create( description, monitor );
                            project.open( IResource.BACKGROUND_REFRESH, monitor );

                            try
                            {
                                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                            }
                            catch( CoreException e)
                            {
                               // ignore error this is just best effort
                            }

                            final IWorkbench workbench = PlatformUI.getWorkbench();
                            final Shell shell = workbench.getActiveWorkbenchWindow().getShell();
                            shell.forceActive();
                            shell.forceFocus();

                            PackageExplorerPart view = PackageExplorerPart.openInActivePerspective();
                            view.selectAndReveal( project );
                        }
                        catch( CoreException e )
                        {
                            LiferayUIPlugin.logError( "Unable to import project " + name, e );
                        }
                    }
                };

                UIUtil.async( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            new ProgressMonitorDialog( UIUtil.getActiveShell() ).run( true, true, runnable );
                        }
                        catch( InvocationTargetException | InterruptedException e )
                        {
                        }
                    }
                });
            }
        }
        catch( CoreException e )
        {
            retval = e.getMessage();
        }

        return retval;
    }

}
