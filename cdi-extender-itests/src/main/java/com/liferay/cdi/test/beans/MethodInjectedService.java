package com.liferay.cdi.test.beans;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanService;
import com.liferay.cdi.test.interfaces.Pojo;

@Service(type = {MethodInjectedService.class, BeanService.class})
@Singleton
public class MethodInjectedService implements BeanService<Pojo> {

	@Inject
	public void setPojo(Pojo pojo) {
		_pojo = pojo;
	}

	@Override
	public String doSomething() {
		return _pojo.foo("METHOD");
	}

	@Override
	public Pojo get() {
		return _pojo;
	}

	private Pojo _pojo;

}
