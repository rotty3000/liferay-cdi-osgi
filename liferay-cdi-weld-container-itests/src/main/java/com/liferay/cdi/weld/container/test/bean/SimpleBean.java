package com.liferay.cdi.weld.container.test.bean;

public class SimpleBean {
	
	public SimpleBean() {
		System.out.println("Created!!!");
	}

	public String foo(String fooInput) {
		return "Foo " + fooInput;
	}
	
}
