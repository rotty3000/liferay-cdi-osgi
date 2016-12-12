package com.liferay.cdi.test.interfaces;

public interface BeanThingy<T> {

	public String doSomething();

	public T getThingy();

}
