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

package com.liferay.ide.project.core.upgrade.service;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 */

public class LiferayServerNameDefaultValueService extends DefaultValueService implements IServerLifecycleListener
{

    static final String NONE = "<None>";

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        ServerCore.addServerLifecycleListener( this );
    }

    @Override
    public void dispose()
    {
        ServerCore.removeServerLifecycleListener( this );

        super.dispose();
    }

    @Override
    protected String compute()
    {
        IServer[] servers = ServerCore.getServers();

        String value = NONE;

        if( !CoreUtil.isNullOrEmpty( servers ) )
        {
            for( IServer server : servers )
            {
                if( LiferayServerCore.newPortalBundle( server.getRuntime().getLocation() ) != null )
                {
                    value = server.getName();

                    break;
                }
            }
        }

        return value;
    }

    @Override
    public void serverAdded( IServer server )
    {
        refresh();
    }

    @Override
    public void serverChanged( IServer server )
    {
        refresh();
    }

    @Override
    public void serverRemoved( IServer server )
    {
        refresh();
    }

}
