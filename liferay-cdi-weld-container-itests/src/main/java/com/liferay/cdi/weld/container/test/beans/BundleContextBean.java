package com.liferay.cdi.weld.container.test.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.framework.BundleContext;

@ApplicationScoped
public class BundleContextBean {

	public BundleContext getBundleContext() {
		return _bundleContext;
	}

	@Inject
	private BundleContext _bundleContext;

}