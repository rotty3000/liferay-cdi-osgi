package com.liferay.cdi.weld.container.test;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {
		"scope=bundle"
	},
	scope = ServiceScope.BUNDLE,
	service = DSServiceBundleScope.class
)
public class DSServiceBundleScope {

}
