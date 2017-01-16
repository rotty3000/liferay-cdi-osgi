package com.liferay.cdi.test.beans;

import java.util.Iterator;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanService;
import com.liferay.cdi.test.interfaces.SingletonScoped;

@Service(type = {BeanService.class, InstancePropertiesBean.class})
@Singleton
public class InstancePropertiesBean implements BeanService<Map<String, Object>> {

	@Override
	public String doSomething() {
		int count = 0;
		for (Iterator<?> iterator = _instance.iterator();iterator.hasNext();) {
			iterator.next();
			count++;
		}
		return String.valueOf(count);
	}

	@Override
	public Map<String, Object> get() {
		return _instance.iterator().next();
	}

	@Inject
	@Reference(service = SingletonScoped.class)
	Instance<Map<String, Object>> _instance;

}
