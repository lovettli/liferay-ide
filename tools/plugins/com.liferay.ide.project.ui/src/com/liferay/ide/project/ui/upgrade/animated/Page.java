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

import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageValidationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public abstract class Page extends Composite
{

    public static String WELCOME_PAGE_ID = "welcome";
    public static String IMPORT_PAGE_ID = "import";
    public static String DESCRIPTORS_PAGE_ID = "descriptors";
    public static String FINDBREACKINGCHANGES_PAGE_ID = "findbreackingchanges";
    public static String BUILDSERVICE_PAGE_ID = "buildservice";
    public static String LAYOUTTEMPLATE_PAGE_ID = "layouttemplate";
    public static String CUSTOMJSP_PAGE_ID = "customjsp";
    public static String EXTANDTHEME_PAGE_ID = "extandtheme";
    public static String COMPILE_PAGE_ID = "compile";
    public static String DEPLOY_PAGE_ID = "deploy";

    public static Control createHorizontalSpacer( Composite comp, int hSpan )
    {
        Label l = new Label( comp, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = hSpan;
        l.setLayoutData( gd );
        return l;
    }

    public static Control createSeparator( Composite parent, int hspan )
    {
        Label label = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, false, hspan, 1 );
        label.setLayoutData( gd );
        return label;
    }
    protected boolean canBack = true;
    protected boolean canNext = true;

    protected LiferayUpgradeDataModel dataModel;

    protected final List<PageNavigatorListener> naviListeners =
        Collections.synchronizedList( new ArrayList<PageNavigatorListener>() );

    private String pageId;

    private int index;

    private String title = "title";

    protected PageAction[] actions;

    private PageAction selectedAction;

    protected final List<PageValidationListener> pageValidationListeners =
        Collections.synchronizedList( new ArrayList<PageValidationListener>() );

    public Page(
        Composite parent, int style, LiferayUpgradeDataModel dataModel, String pageId, boolean hasFinishAndSkipAction )
    {
        super( parent, style );

        this.dataModel = dataModel;

        setLayout( new GridLayout( getGridLayoutCount(), getGridLayoutEqualWidth() ) );

        Label title = new Label( this, SWT.LEFT );
        title.setText( getPageTitle() );
        title.setFont( new Font( null, "Times New Roman", 14, SWT.NORMAL ) );

        Text content = new Text( this, SWT.MULTI );
        content.setText( getDescriptor() );
        content.setEditable( false );
        content.setBackground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ) );

        getSpecialDescriptor( this, style );

        setPageId( pageId );

        if( hasFinishAndSkipAction )
        {
            setActions( new PageAction[] { new PageFinishAction(), new PageSkipAction() } );
        }
    }

    public void addPageNavigateListener( PageNavigatorListener listener )
    {
        this.naviListeners.add( listener );
    }

    public void addPageValidationListener( PageValidationListener listener )
    {
        this.pageValidationListeners.add( listener );
    }

    protected Label createLabel( Composite composite, String text )
    {
        Label label = new Label( composite, SWT.NONE );
        label.setText( text );

        GridDataFactory.generate( label, 2, 1 );

        return label;
    }

    protected Text createTextField( Composite composite, int style )
    {
        Text text = new Text( composite, SWT.BORDER | style );
        text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        return text;
    }

    @Override
    public boolean equals( Object obj )
    {
        Page comp = (Page) obj;

        return this.pageId == comp.pageId;
    }

    public PageAction[] getActions()
    {
        return this.actions;
    }

    public String getDescriptor()
    {
        return "";
    }

    public int getGridLayoutCount()
    {
        return 1;
    }

    public boolean getGridLayoutEqualWidth()
    {
        return true;
    }

    public final int getIndex()
    {
        return index;
    }

    public String getPageId()
    {
        return pageId;
    }

    public abstract String getPageTitle();

    public PageAction getSelectedAction()
    {
        return selectedAction;
    }

    public void getSpecialDescriptor( Composite parent, int style )
    {
    }

    public String getTitle()
    {
        return this.title;
    }

    public final void setActions( PageAction[] actions )
    {
        this.actions = actions;
    }

    protected void setBackPage( boolean canBack )
    {
        this.canBack = canBack;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    protected void setNextPage( boolean canBack )
    {
        this.canNext = canBack;
    }

    public void setPageId( String pageId )
    {
        this.pageId = pageId;
    }

    public void setSelectedAction( PageAction selectedAction )
    {
        this.selectedAction = selectedAction;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    protected boolean showBackPage()
    {
        return canBack;
    }

    protected boolean showNextPage()
    {
        return canNext;
    }

    protected void triggerValidationEvent( String validationMessage )
    {
        PageValidateEvent pe = new PageValidateEvent();
        pe.setPageId( getPageId() );
        pe.setMessage( validationMessage );

        for( PageValidationListener listener : pageValidationListeners )
        {
            listener.onValidation( pe );
        }
    }
}
