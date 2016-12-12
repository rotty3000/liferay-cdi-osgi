package com.liferay.cdi.test.components;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.cdi.test.components.DSServiceBundleScope;

@Component(
	property = {
		"scope=bundle"
	},
	scope = ServiceScope.BUNDLE,
	service = DSServiceBundleScope.class
)
public class DSServiceBundleScope {

}
