package com.liferay.cdi.weld.container.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;

@Service
public class FieldInjectedService {

	public String doSomething() {
		return _pojo.foo("FIELD");
	}

	@Inject
	private Pojo _pojo;

}
