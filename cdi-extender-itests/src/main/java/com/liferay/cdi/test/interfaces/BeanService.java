package com.liferay.cdi.test.interfaces;

public interface BeanService<T> {

	public String doSomething();

	public T get();

}
