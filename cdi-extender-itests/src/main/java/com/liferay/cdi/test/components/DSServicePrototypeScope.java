package com.liferay.cdi.test.components;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.cdi.test.components.DSServicePrototypeScope;

@Component(
	property = {
		"key=value"
	},
	scope = ServiceScope.PROTOTYPE,
	service = DSServicePrototypeScope.class
)
public class DSServicePrototypeScope {

}
