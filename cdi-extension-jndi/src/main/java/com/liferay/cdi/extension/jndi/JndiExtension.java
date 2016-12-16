package com.liferay.cdi.extension.jndi;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.CdiExtenderConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jndi.JNDIConstants;

@Component(property = {CdiExtenderConstants.CDI_EXTENSION + "=jndi"})
public class JndiExtension implements Extension {
	
	@Activate
	void activate(BundleContext bundleContext) {
		_bundle = bundleContext.getBundle();
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(JNDIConstants.JNDI_URLSCHEME, "java");

		_objectFactoryRegistration = _bundle.getBundleContext().registerService(
			ObjectFactory.class, new JndiObjectFactory(beanManager), properties);
	}

	@PreDestroy
	void destroy() {
		_objectFactoryRegistration.unregister();
	}

	private Bundle _bundle;
	private ServiceRegistration<ObjectFactory> _objectFactoryRegistration;

}
