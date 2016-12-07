package com.liferay.cdi.test;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.cdi.test.DSServiceBundleScope;

@Component(
	property = {
		"scope=bundle"
	},
	scope = ServiceScope.BUNDLE,
	service = DSServiceBundleScope.class
)
public class DSServiceBundleScope {

}
