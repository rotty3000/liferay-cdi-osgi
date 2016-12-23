package com.liferay.cdi.test.components;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.cdi.test.interfaces.BundleScoped;

@Component(
	property = {"key=value"},
	scope = ServiceScope.BUNDLE
)
public class DSServiceBundleScope implements BundleScoped {

	@Override
	public Object get() {
		return this;
	}

}
