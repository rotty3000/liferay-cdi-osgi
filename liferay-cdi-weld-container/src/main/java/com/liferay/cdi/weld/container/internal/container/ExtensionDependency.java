package com.liferay.cdi.weld.container.internal.container;

import javax.enterprise.inject.spi.Extension;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.Constants;

public class ExtensionDependency {

	public ExtensionDependency(BundleContext bundleContext, Long bundleId, String name) {
		_string = "(&(" + org.osgi.framework.Constants.SERVICE_BUNDLEID + "=" + bundleId + ")(" +
			Constants.CDI_EXTENSION_CAPABILITY + "=" + name + "))";

		try {
			_filter = bundleContext.createFilter(_string);
		}
		catch (InvalidSyntaxException ise) {
			throw new RuntimeException(ise);
		}
	}

	public boolean matches(ServiceReference<Extension> reference) {
		return _filter.match(reference);
	}

	@Override
	public String toString() {
		return _string;
	}

	private final Filter _filter;
	private final String _string;

}