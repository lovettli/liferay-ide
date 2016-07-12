package com.liferay.ide.project.core.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;

import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.PortalRuntime;

/**
 * @author Lovett Li
 */
public class ServiceWrapperCommand
{

    private final IServer _server;

    public ServiceWrapperCommand( IServer server )
    {
        _server = server;
    }

    public String[] getServiceWrapper() throws Exception
    {

        if( _server == null )
        {
            return getStaticServiceWrapper();
        }
        else
        {
            Map<String, String[]> wrappers = getDynamicServiceWrapper();
            updateServiceWrapperStaticFile( wrappers );

            return wrappers.keySet().toArray( new String[0] );
        }
    }

    private File checkStaticWrapperFile() throws IOException
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/wrappers-static.json" ) );
        final File servicesFile = new File( url.getFile() );

        if( servicesFile.exists() )
        {
            return servicesFile;
        }

        throw new FileNotFoundException( "can't find static services file wrappers-static.json" );
    }

    private Map<String,String[]> getDynamicServiceWrapper()
    {
        final IPath bundleLibPath =
            ( (PortalRuntime) _server.getRuntime().loadAdapter( PortalRuntime.class, null ) ).getAppServerLibGlobalDir();
        final IPath bundleServerPath =
            ( (PortalRuntime) _server.getRuntime().loadAdapter( PortalRuntime.class, null ) ).getAppServerDir();
        final Map<String, String[]> map = new LinkedHashMap<>();
        List<File> libFiles;
        File portalkernelJar = null;

        try
        {
            libFiles = FileListing.getFileListing( new File( bundleLibPath.toOSString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( "portal-kernel.jar" ) )
                {
                    portalkernelJar = lib;
                    break;
                }
            }

            libFiles = FileListing.getFileListing( new File( bundleServerPath.append( "../osgi" ).toOSString() ) );
            libFiles.add( portalkernelJar );

            if( !libFiles.isEmpty() )
            {
                for( File lib : libFiles )
                {
                    if( lib.getName().endsWith( ".lpkg" ) )
                    {
                        try(JarFile jar = new JarFile( lib ))
                        {
                            Enumeration<JarEntry> enu = jar.entries();

                            while( enu.hasMoreElements() )
                            {
                                JarInputStream jarInputStream = null;

                                try
                                {
                                    JarEntry entry = enu.nextElement();

                                    String name = entry.getName();

                                    if( name.contains( ".api-" ) )
                                    {
                                        JarEntry jarentry = jar.getJarEntry( name );
                                        InputStream inputStream = jar.getInputStream( jarentry );

                                        jarInputStream = new JarInputStream( inputStream );
                                        JarEntry nextJarEntry;

                                        while( ( nextJarEntry = jarInputStream.getNextJarEntry() ) != null )
                                        {
                                            String entryName = nextJarEntry.getName();

                                            getServiceWrapperList( map, entryName, jarInputStream);
                                        }

                                    }
                                }
                                catch( Exception e )
                                {
                                }
                                finally
                                {
                                    if( jarInputStream != null )
                                    {
                                        jarInputStream.close();
                                    }
                                }
                            }
                        }
                        catch( IOException e )
                        {
                        }
                    }
                    else if( lib.getName().endsWith( "api.jar" ) || lib.getName().equals( "portal-kernel.jar" ) )
                    {
                        JarInputStream jarinput = null;

                        try(JarFile jar = new JarFile( lib ))
                        {
                            jarinput = new JarInputStream( new FileInputStream( lib ) );
                            Enumeration<JarEntry> enu = jar.entries();

                            while( enu.hasMoreElements() )
                            {
                                JarEntry entry = enu.nextElement();
                                String name = entry.getName();

                                getServiceWrapperList( map, name, jarinput );
                            }
                        }
                        catch( IOException e )
                        {
                        }
                        finally {

                            if( jarinput != null )
                            {
                                try
                                {
                                    jarinput.close();
                                }
                                catch( IOException e )
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        catch( FileNotFoundException e )
        {
            e.printStackTrace();
        }

        return map;
    }

    private void getServiceWrapperList( final Map<String,String[]> wrapperMap, String name, JarInputStream jarInputStream )
    {
        if( name.endsWith( "ServiceWrapper.class" ) && !( name.contains( "$" ) ) )
        {
            name = name.replaceAll( "\\\\", "." ).replaceAll( "/", "." );
            name = name.substring( 0, name.lastIndexOf( "." ) );
            Attributes mainAttributes = jarInputStream.getManifest().getMainAttributes();
            String bundleName = mainAttributes.getValue( "Bundle-SymbolicName" );
            String version = mainAttributes.getValue( "Bundle-Version" );

            wrapperMap.put( name, new String[]{bundleName , version} );
        }

    }

    @SuppressWarnings( "unchecked" )
    private String[] getStaticServiceWrapper() throws Exception
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/wrappers-static.json" ) );
        final File servicesFile = new File( url.getFile() );

        if( servicesFile.exists() )
        {
            final ObjectMapper mapper = new ObjectMapper();

            List<String> map = mapper.readValue( servicesFile, List.class );
            String[] wrappers = map.toArray( new String[0] );

            return wrappers;
        }

        throw new FileNotFoundException( "can't find static services file wrapper-static.json" );
    }

    private void updateServiceWrapperStaticFile( final Map<String, String[]> wrappers ) throws Exception
    {
        final File wrappersFile = checkStaticWrapperFile();
        final ObjectMapper mapper = new ObjectMapper();

        final Job job = new WorkspaceJob( "Update ServiceWrapper static file...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    mapper.writeValue( wrappersFile, wrappers );
                }
                catch( IOException e )
                {
                    return Status.CANCEL_STATUS;
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();

    }
}
