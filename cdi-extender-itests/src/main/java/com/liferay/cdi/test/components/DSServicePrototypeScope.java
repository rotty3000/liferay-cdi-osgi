package com.liferay.cdi.test.components;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.cdi.test.interfaces.PrototypeScoped;

@Component(
	property = {"key=value"},
	scope = ServiceScope.PROTOTYPE
)
public class DSServicePrototypeScope implements PrototypeScoped {

	@Override
	public Object get() {
		return this;
	}

}
