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

package com.liferay.ide.project.ui.upgrade.action;

import org.eclipse.sapphire.ui.Presentation;

import com.liferay.ide.project.ui.migration.MigrationView;
import com.liferay.ide.project.ui.migration.RunMigrationToolAction;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Terry Jia
 * @author Lovett Li
 */
public class FindBreakingChangesActionHandler extends BaseActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        MigrationView view = (MigrationView) UIUtil.showView( MigrationView.ID );
        new RunMigrationToolAction( "Run Migration Tool", view.getViewSite().getShell() ).run();
        return null;
    }

}
