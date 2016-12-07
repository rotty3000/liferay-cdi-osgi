package com.liferay.cdi.test.beans;

import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.beans.Pojo;

@Service
public class FieldInjectedService {

	public String doSomething() {
		return _pojo.foo("FIELD");
	}

	@Inject
	private Pojo _pojo;

}
