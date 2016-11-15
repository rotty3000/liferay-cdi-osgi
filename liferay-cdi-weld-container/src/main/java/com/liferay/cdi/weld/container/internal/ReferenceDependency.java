package com.liferay.cdi.weld.container.internal;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ReferenceDependency {

	public ReferenceDependency(Bean<?> bean, InjectionPoint injectionPoint) {
		_bean = bean;
		_injectionPoint = injectionPoint;

		_string = null; // TODO calculate the target filter for the service.

		try {
			_filter = FrameworkUtil.createFilter(_string);
		}
		catch (InvalidSyntaxException ise) {
			throw new RuntimeException(ise);
		}
	}

	public void bind(ServiceReference<?> reference, Object service) {
		// TODO inject the service into the injectionPoint
	}

	public boolean matches(ServiceReference<?> reference) {
		return _filter.match(reference);
	}

	@Override
	public String toString() {
		return _string;
	}

	private final Bean<?> _bean;
	private final Filter _filter;
	private final InjectionPoint _injectionPoint;
	private final String _string;

}