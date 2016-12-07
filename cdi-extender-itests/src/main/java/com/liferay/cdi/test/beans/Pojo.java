package com.liferay.cdi.test.beans;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Pojo {

	public Pojo() {
		System.out.println("Created!!!");
	}

	public String foo(String fooInput) {
		_counter.incrementAndGet();
		return "PREFIX" + fooInput;
	}

	public int getCount() {
		return _counter.get();
	}

	private AtomicInteger _counter = new AtomicInteger();

}
