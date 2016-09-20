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

import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Terry Jia
 */
public class LiferayServerNameValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        final CodeUpgradeOp op = context( CodeUpgradeOp.class );

        final String serverName = op.getLiferayServerName().content( true );

        return ( ServerUtil.getServer( serverName ) != null )
            ? Status.createOkStatus() : Status.createErrorStatus( "Liferay runtime must be configured." );
    }
}
