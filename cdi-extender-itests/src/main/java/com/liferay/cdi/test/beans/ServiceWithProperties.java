package com.liferay.cdi.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.ServiceProperty;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.Pojo;

@Service(
	properties = {
		@ServiceProperty(
			key = "test.key.b1", value = "test.value.b1"
		),
		@ServiceProperty(
			key = "test.key.b2", value = "test.value.b2"
		)
	},
	type = {ServiceWithProperties.class, BeanThingy.class}
)
public class ServiceWithProperties implements BeanThingy<Pojo> {

	@Override
	public String doSomething() {
		return _pojo.foo("FIELD");
	}

	@Override
	public com.liferay.cdi.test.interfaces.Pojo getThingy() {
		return _pojo;
	}

	@Inject
	private PojoImpl _pojo;

}
