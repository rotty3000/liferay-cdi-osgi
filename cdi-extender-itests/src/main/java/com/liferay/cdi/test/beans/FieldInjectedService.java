package com.liferay.cdi.test.beans;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanService;
import com.liferay.cdi.test.interfaces.Pojo;

@Service(type = {FieldInjectedService.class, BeanService.class})
@Singleton
public class FieldInjectedService implements BeanService<Pojo> {

	@Override
	public String doSomething() {
		return _pojo.foo("FIELD");
	}

	@Override
	public Pojo get() {
		return _pojo;
	}

	@Inject
	private PojoImpl _pojo;

}
