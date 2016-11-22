package com.liferay.cdi.weld.container.internal.jndi;

import java.util.Hashtable;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class JndiObjectFactory implements ObjectFactory {

	public JndiObjectFactory(BeanManager beanManager) {
		_jndiContext = new JNDIContext(beanManager);
	}

	@Override
	public Object getObjectInstance(
			Object obj, Name name, javax.naming.Context context, Hashtable<?, ?> environment)
		throws Exception {

		if (obj == null) {
			return _jndiContext;
		}

		return null;
	}

	private final JNDIContext _jndiContext;

}