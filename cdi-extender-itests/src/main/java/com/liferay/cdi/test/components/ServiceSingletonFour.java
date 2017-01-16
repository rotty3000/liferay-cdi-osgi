package com.liferay.cdi.test.components;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.liferay.cdi.test.interfaces.SingletonScoped;

@Component(
	property = {Constants.SERVICE_RANKING + ":Integer=4"}
)
public class ServiceSingletonFour implements SingletonScoped<ServiceSingletonFour> {

	@Override
	public ServiceSingletonFour get() {
		return this;
	}

}
