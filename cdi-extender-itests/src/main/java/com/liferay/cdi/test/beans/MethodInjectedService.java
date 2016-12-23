package com.liferay.cdi.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.Pojo;

@Service(type = {MethodInjectedService.class, BeanThingy.class})
public class MethodInjectedService implements BeanThingy<Pojo> {

	@Inject
	public void setPojo(Pojo pojo) {
		_pojo = pojo;
	}

	@Override
	public String doSomething() {
		return _pojo.foo("METHOD");
	}

	@Override
	public Pojo getThingy() {
		return _pojo;
	}

	private Pojo _pojo;

}
