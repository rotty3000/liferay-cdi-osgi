package com.liferay.cdi.test.beans;

import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;

import com.liferay.cdi.test.interfaces.BeanService;
import com.liferay.cdi.test.interfaces.SingletonScoped;

@Service(type = {BeanService.class, InstanceBean.class})
@Singleton
@SuppressWarnings("rawtypes")
public class InstanceBean implements BeanService<SingletonScoped> {

	@Override
	public String doSomething() {
		int count = 0;
		for (Iterator<?> iterator = _instance.iterator();iterator.hasNext();) {
			System.out.println(iterator.next());
			count++;
		}
		return String.valueOf(count);
	}

	@Override
	public SingletonScoped get() {
		return _instance.iterator().next();
	}

	@Inject
	@Reference
	Instance<SingletonScoped> _instance;

}
