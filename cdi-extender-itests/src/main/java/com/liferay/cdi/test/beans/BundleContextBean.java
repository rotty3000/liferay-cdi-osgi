package com.liferay.cdi.test.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.framework.BundleContext;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.TestQualifier;

@ApplicationScoped
@TestQualifier
public class BundleContextBean implements BeanThingy<BundleContext> {

	@Override
	public String doSomething() {
		return toString();
	}

	@Override
	public BundleContext getThingy() {
		return _bundleContext;
	}

	@Inject
	private BundleContext _bundleContext;

}