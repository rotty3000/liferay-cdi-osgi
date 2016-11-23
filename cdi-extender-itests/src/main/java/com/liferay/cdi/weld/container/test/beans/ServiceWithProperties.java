package com.liferay.cdi.weld.container.test.beans;

import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.ServiceProperty;

@Service(
	properties = {
		@ServiceProperty(
			key = "test.key.b1", value = "test.value.b1"
		),
		@ServiceProperty(
			key = "test.key.b2", value = "test.value.b2"
		)
	}
)
public class ServiceWithProperties {
}
