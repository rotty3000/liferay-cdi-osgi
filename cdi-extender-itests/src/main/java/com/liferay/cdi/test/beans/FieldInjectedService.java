package com.liferay.cdi.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.Pojo;

@Service(type = {FieldInjectedService.class, BeanThingy.class})
public class FieldInjectedService implements BeanThingy<Pojo> {

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
