package com.liferay.cdi.weld.container.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoadingException;
import org.osgi.framework.wiring.BundleWiring;

public class BundleResourcesLoader implements ResourceLoader {
	
	public BundleResourcesLoader(BundleWiring bundleWiring) {
		_bundleWiring = bundleWiring;
	}

	@Override
	public void cleanup() {
		_bundleWiring = null;
	}

	@Override
	public Class<?> classForName(String name) {
        try {
            return classLoader().loadClass(name);
        } 
        catch (ClassNotFoundException e) {
            throw new ResourceLoadingException(ERROR_LOADING_CLASS + name, e);
        } 
        catch (LinkageError e) {
            throw new ResourceLoadingException(ERROR_LOADING_CLASS + name, e);
        } 
        catch (TypeNotPresentException e) {
            throw new ResourceLoadingException(ERROR_LOADING_CLASS + name, e);
        }
	}

	@Override
	public URL getResource(String name) {
		return _bundleWiring.getBundle().getResource(name);
	}

	@Override
	public Collection<URL> getResources(String name) {
		try {
			Enumeration<URL> resources = _bundleWiring.getBundle().getResources(name);
			
			if (resources == null) {
				return Collections.emptyList();
			}

			return Collections.list(resources);
		} catch (IOException e) {
            throw new ResourceLoadingException("Error loading resource " + name, e);
        }
	}

	protected ClassLoader classLoader() {
        return _bundleWiring.getClassLoader();
    }

    private static final String ERROR_LOADING_CLASS = "Error loading class ";

	private BundleWiring _bundleWiring;
	
}
