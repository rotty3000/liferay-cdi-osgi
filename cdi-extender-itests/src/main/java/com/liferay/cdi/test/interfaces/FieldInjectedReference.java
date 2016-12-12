package com.liferay.cdi.test.interfaces;

import java.util.Map;

import org.osgi.framework.ServiceReference;

import com.liferay.cdi.test.components.DSServicePrototypeScope;

public interface FieldInjectedReference {

	public Map<String, Object> getProperties();

	public ServiceReference<DSServicePrototypeScope> getReference1();

	@SuppressWarnings("rawtypes")
	public ServiceReference getReference2();

	public DSServicePrototypeScope getService();

}