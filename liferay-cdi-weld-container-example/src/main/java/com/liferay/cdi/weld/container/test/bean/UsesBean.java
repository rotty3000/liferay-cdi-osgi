package com.liferay.cdi.weld.container.test.bean;

import javax.inject.Inject;

public class UsesBean {

	@Inject
	public UsesBean(SimpleBean simpleBean) {
		_simpleBean = simpleBean;
	}
	
	public void doSomething() {
		System.out.println(_simpleBean.foo("Blah!!!"));
	}

	private SimpleBean _simpleBean;
	
}
