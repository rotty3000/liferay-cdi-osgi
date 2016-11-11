package com.liferay.cdi.weld.container.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

@SuppressWarnings("rawtypes")
public class CdiContainerServiceFactory implements ServiceFactory {

	@Override
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return new WeldCdiContainer(bundle);
	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		WeldCdiContainer container = (WeldCdiContainer)service;

		container.shutdown();
	}

}