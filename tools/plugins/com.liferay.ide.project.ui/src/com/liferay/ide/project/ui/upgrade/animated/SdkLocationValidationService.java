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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.osgi.framework.Version;

/**
 * @author Andy Wu
 * @author Simon Jiang
 */
public class SdkLocationValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        int countPossibleWorkspaceSDKProjects = SDKUtil.countPossibleWorkspaceSDKProjects();

        if( countPossibleWorkspaceSDKProjects > 1 )
        {
            return StatusBridge.create( ProjectCore.createErrorStatus( "This workspace has more than one SDK. " ) );
        }

        final Path sdkLocation = op().getSdkLocation().content( true );

        if( sdkLocation == null || sdkLocation.isEmpty() )
        {
            return StatusBridge.create( ProjectCore.createErrorStatus( "This sdk location is empty " ) );
        }

        SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkLocation ) );

        if( sdk != null )
        {
            IStatus status = sdk.validate( true );

            if( !status.isOK() )
            {
                return StatusBridge.create( status );
            }

            String version = sdk.getVersion();

            if( version != null )
            {
                Version sdkVersion = new Version( version );
                int result = sdkVersion.compareTo( new Version( "6.2.0" ) );

                if( result < 0 )
                {
                    return StatusBridge.create(
                        ProjectCore.createErrorStatus( "This tool doesn't support 6.1.x." ) );
                }
            }
        }
        else
        {
            return StatusBridge.create( ProjectCore.createErrorStatus( "This sdk location is not correct" ) );
        }

        return retval;
    }

    private LiferayUpgradeDataModel op()
    {
        return context( LiferayUpgradeDataModel.class );
    }
}
