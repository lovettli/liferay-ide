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

import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 */
public class ExtAndThemePage extends Page
{

    public ExtAndThemePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, EXTANDTHEME_PAGE_ID, false );
    }

    @Override
    public String getPageTitle()
    {
        return "Ext and Theme Project";
    }

    public void getSpecialDescriptor( Composite parent, int style )
    {
        final String descriptor = "Theme and Ext projects are not supported to upgrade in this tool currenttly.\n" +
            "For Theme Projects, you can upgrade them manually.\n" +
            "For Ext Projects, we didn't provide support for them at Liferay 7.0.\n" +
            "If you have ext projects, you can change them into modules.\n" +
            "For more details, please see <a>Liferay Blade Samples</a>.\n";
        String url = "https://github.com/liferay/liferay-blade-samples";

        Link  link = SWTUtil.createHyperLink( this, style, descriptor, 1, url );
        link.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
    }

    @Override
    public int getGridLayoutCount()
    {
        return 2;
    }

    @Override
    public boolean getGridLayoutEqualWidth()
    {
        return false;
    }

}
