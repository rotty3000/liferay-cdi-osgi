package com.liferay.cdi.weld.container.test.beans;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.weld.container.test.DSService;

@ApplicationScoped
@Service
@SuppressWarnings("rawtypes")
public class FieldInjectedReference {

	@Inject
	@Reference(target = "(key=value)")
	private DSService service;

	@Inject
	@Reference(target = "(key=value)")
	private ServiceReference<DSService> reference1;

	@Inject
	@Reference(service = DSService.class, target = "(key=value)")
	private ServiceReference reference2;

	@Inject
	@Reference(service = DSService.class, target = "(key=value)")
	private Map<String, Object> properties;

	public Map<String, Object> getProperties() {
		return properties;
	}

	public ServiceReference<DSService> getReference1() {
		return reference1;
	}

	public ServiceReference getReference2() {
		return reference2;
	}

	public DSService getService() {
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