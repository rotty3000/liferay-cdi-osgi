package com.liferay.cdi.test.components;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.liferay.cdi.test.interfaces.SingletonScoped;

@Component(
	property = {Constants.SERVICE_RANKING + ":Integer=-1"}
)
public class ServiceSingletonThree implements SingletonScoped<ServiceSingletonThree> {

	@Override
	public ServiceSingletonThree get() {
		return this;
	}

}
