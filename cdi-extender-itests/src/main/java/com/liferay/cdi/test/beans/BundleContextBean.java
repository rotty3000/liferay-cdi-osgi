package com.liferay.cdi.test.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.framework.BundleContext;

import com.liferay.cdi.test.interfaces.BeanService;
import com.liferay.cdi.test.interfaces.BundleContextBeanQualifier;

@ApplicationScoped
@BundleContextBeanQualifier
public class BundleContextBean implements BeanService<BundleContext> {

	@Override
	public String doSomething() {
		return toString();
	}

	@Override
	public BundleContext get() {
		return _bundleContext;
	}

	@Inject
	private BundleContext _bundleContext;

}