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

package com.liferay.ide.project.ui.upgrade.animated;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.dialog.CustomProjectSelectionDialog;
import com.liferay.ide.project.ui.upgrade.action.CompileAction;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Adny
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class BuildServicePage extends Page
{

    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };

    public BuildServicePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        GridLayout layout = new GridLayout( 1, true );
        this.setLayout( layout );

        Label title = new Label( this, SWT.LEFT );
        title.setText( "Build Service" );
        title.setFont( new Font( null, "Times New Roman", 16, SWT.NORMAL ) );

        Text content = new Text( this, SWT.MULTI );
        final String descriptor = "In this step, we will delete some legacy servicebuilder related files" +
            " and re-run build-service on servicebuilder projects.\n" +
            "Note: Please make sure the default installed jre is JDK 8  (Preferences-Java-Installed JREs)\n";
        content.setText( descriptor );
        content.setEditable( false );
        content.setBackground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ) );

        Button buildServiceButton = new Button( this, SWT.PUSH );
        buildServiceButton.setText( "Build Service" );
        buildServiceButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {

                List<IProject> projects = getServiceBuilderProjects();

                CustomProjectSelectionDialog dialog = new CustomProjectSelectionDialog( UIUtil.getActiveShell() );

                dialog.setProjects( projects );

                List<IProject> liferayServiceProjects = new ArrayList<>();

                if( dialog.open() == Window.OK )
                {
                    final Object[] selectedProjects = dialog.getResult();

                    if( selectedProjects != null )
                    {
                        for( Object project : selectedProjects )
                        {
                            if( project instanceof IJavaProject )
                            {
                                IJavaProject p = (IJavaProject) project;
                                liferayServiceProjects.add( p.getProject() );
                            }
                        }
                    }
                }

                try
                {
                    PlatformUI.getWorkbench().getProgressService().busyCursorWhile( new IRunnableWithProgress()
                    {

                        public void run( IProgressMonitor monitor )
                            throws InvocationTargetException, InterruptedException
                        {
                            try
                            {
                                for( IProject project : liferayServiceProjects )
                                {
                                    deleteLegacyFiles( project, monitor );

                                    SDK sdk = SDKUtil.getSDK( project );

                                    sdk.runCommand(
                                        project, project.getFile( "build.xml" ), ISDKConstants.TARGET_BUILD_SERVICE,
                                        null, monitor );

                                    project.refreshLocal( IResource.DEPTH_INFINITE, monitor );

                                    IConsole console = CompileAction.getConsole( "build-service" );

                                    if( console != null )
                                    {
                                        ProcessConsole pc = (ProcessConsole) console;

                                        if( pc.getDocument().get().contains( "BUILD FAILED" ) )
                                        {
                                            return;
                                        }
                                    }
                                }
                            }
                            catch( CoreException e )
                            {
                            }
                        }
                    } );
                }
                catch( Exception e1 )
                {
                }
            }

            private void deleteLegacyFiles( IProject project, IProgressMonitor monitor ) throws CoreException
            {
                String relativePath = "/docroot/WEB-INF/src/META-INF";
                IFile portletSpringXML = project.getFile( relativePath + "/portlet-spring.xml" );
                IFile shardDataSourceSpringXML = project.getFile( relativePath + "/shard-data-source-spring.xml" );

                if( portletSpringXML.exists() )
                {
                    portletSpringXML.delete( true, monitor );
                }

                if( shardDataSourceSpringXML.exists() )
                {
                    shardDataSourceSpringXML.delete( true, monitor );
                }
            }

            private List<IProject> getServiceBuilderProjects()
            {
                List<IProject> results = new ArrayList<IProject>();

                IProject[] projects = CoreUtil.getAllProjects();

                for( IProject project : projects )
                {
                    IFile serviceFile = project.getFile( "/docroot/WEB-INF/service.xml" );

                    if( serviceFile.exists() )
                    {
                        results.add( project );
                    }
                }

                return results;
            }
        } );

        setActions( actions );

        this.setPageId( BUILDSERVICE_PAGE_ID );
    }
}