package com.liferay.cdi.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.Pojo;

@Service(type = {ConstructorInjectedService.class, BeanThingy.class})
public class ConstructorInjectedService implements BeanThingy<Pojo> {

	@Inject
	public ConstructorInjectedService(PojoImpl pojo) {
		_pojo = pojo;
	}

	@Override
	public String doSomething() {
		return _pojo.foo("CONSTRUCTOR");
	}

	@Override
	public Pojo getThingy() {
		return _pojo;
	}

	private PojoImpl _pojo;

}
