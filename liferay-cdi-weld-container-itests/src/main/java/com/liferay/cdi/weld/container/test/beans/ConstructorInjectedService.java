package com.liferay.cdi.weld.container.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;

@Service
public class ConstructorInjectedService {

	@Inject
	public ConstructorInjectedService(Pojo pojo) {
		_pojo = pojo;
	}

	public String doSomething() {
		return _pojo.foo("CONSTRUCTOR");
	}

	private Pojo _pojo;

}
