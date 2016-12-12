package com.liferay.cdi.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.Pojo;

@Service(type = {MethodInjectedService.class, BeanThingy.class})
public class MethodInjectedService implements BeanThingy<Pojo> {

	@Inject
	public void setPojo(PojoImpl pojo) {
		_pojo = pojo;
	}

	@Override
	public String doSomething() {
		return _pojo.foo("METHOD");
	}

	@Override
	public com.liferay.cdi.test.interfaces.Pojo getThingy() {
		return _pojo;
	}

	private PojoImpl _pojo;

}
