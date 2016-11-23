package com.liferay.cdi.weld.container.test;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {
		"key=value"
	},
	scope = ServiceScope.PROTOTYPE,
	service = DSServicePrototypeScope.class
)
public class DSServicePrototypeScope {

}
