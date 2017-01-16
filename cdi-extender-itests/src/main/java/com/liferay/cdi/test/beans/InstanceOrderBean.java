package com.liferay.cdi.test.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanService;
import com.liferay.cdi.test.interfaces.SingletonScoped;

@Service(type = {BeanService.class, InstanceOrderBean.class})
@Singleton
@SuppressWarnings("rawtypes")
public class InstanceOrderBean implements BeanService<List<ServiceReference>> {

	@Override
	public String doSomething() {
		int count = 0;
		for (Iterator<ServiceReference> iterator = _instance.iterator();iterator.hasNext();) {
			ServiceReference serviceReference = iterator.next();
			System.out.println(serviceReference);
			count++;
		}
		return String.valueOf(count);
	}

	@Override
	public List<ServiceReference> get() {
		List<ServiceReference> list = new ArrayList<>();
		for (Iterator<ServiceReference> iterator = _instance.iterator();iterator.hasNext();) {
			list.add(iterator.next());
		}
		return list;
	}

	@Inject
	@Reference(service = SingletonScoped.class)
	Instance<ServiceReference> _instance;

}
