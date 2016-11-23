package com.liferay.cdi.weld.container.test.beans;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.ReferenceScope;
import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.weld.container.test.DSServiceBundleScope;
import com.liferay.cdi.weld.container.test.DSServicePrototypeScope;

@ApplicationScoped
@Service
@SuppressWarnings("rawtypes")
public class FieldInjectedReference {

	@ApplicationScoped
	@Inject
	@Reference
	private DSServiceBundleScope bundlescopped;

	@Inject
	@Reference(target = "(key=value)")
	private DSServicePrototypeScope service;

	@Inject
	@Reference(target = "(key=value)")
	private ServiceReference<DSServicePrototypeScope> reference1;

	@Inject
	@Reference(
		scope = ReferenceScope.PROTOTYPE_REQUIRED,
		service = DSServicePrototypeScope.class,
		target = "(key=value)"
	)
	private ServiceReference reference2;

	@Inject
	@Reference(service = DSServicePrototypeScope.class, target = "(key=value)")
	private Map<String, Object> properties;

	public Map<String, Object> getProperties() {
		return properties;
	}

	public ServiceReference<DSServicePrototypeScope> getReference1() {
		return reference1;
	}

	public ServiceReference getReference2() {
		return reference2;
	}

	public DSServicePrototypeScope getService() {
		return service;
	}

	@PostConstruct
	private void postConstructed() {
		System.out.println("postConstructed");
	}

	@PreDestroy
	private void preDestroyed() {
		System.out.println("preDestroyed");
	}

}