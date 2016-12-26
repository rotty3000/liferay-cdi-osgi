/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.cdi.container.internal.container;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;

import org.osgi.framework.Bundle;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.ServiceProperty;

public class ServiceDeclaration {

	@SuppressWarnings("unchecked")
	public ServiceDeclaration(Service service, @SuppressWarnings("rawtypes") Bean bean, BeanManager beanManager) {
		_service = service;
		_bean = bean;
		_beanManager = beanManager;

		Dictionary<String, Object> properties = new Hashtable<>();

		for (ServiceProperty serviceProperty : _service.properties()) {

			// TODO convert value to typed

			properties.put(serviceProperty.key(), serviceProperty.value());
		}

		// TODO also read properties via Qualifiers

		_properties = properties;

		_creationalContext = _beanManager.createCreationalContext(_bean);
	}

	@SuppressWarnings("rawtypes")
	public Bean getBean() {
		return _bean;
	}

	public String[] getClassNames() {
		List<String> classNames = new ArrayList<>();

		Class<?>[] types = _service.type();

		if (types.length > 0) {
			for (Type type : types) {
				classNames.add(type.getTypeName());
			}
		}
		else {
			types = _bean.getBeanClass().getInterfaces();

			if (types.length > 0) {
				for (Type type : types) {
					classNames.add(type.getTypeName());
				}
			}
			else {
				classNames.add(_bean.getBeanClass().getName());
			}
		}

		return classNames.toArray(new String[0]);
	}

	public Dictionary<String, Object> getProperties() {
		return _properties;
	}

	@SuppressWarnings({ "unchecked" })
	public Object getServiceInstance() {
		if (Singleton.class.isInstance(_bean.getScope())) {
			return _bean.create(_creationalContext);
		}
		else if (ApplicationScoped.class.isInstance(_bean.getScope())) {
			return new BundleScopeWrapper();
		}

		return new PrototypeScopeWrapper();
	}

	@SuppressWarnings("rawtypes")
	private final Bean _bean;
	private final BeanManager _beanManager;
	@SuppressWarnings("rawtypes")
	private final CreationalContext _creationalContext;
	private final Dictionary<String, Object> _properties;
	private final Service _service;

	@SuppressWarnings({"rawtypes", "unchecked"})
	private class BundleScopeWrapper implements ServiceFactory {

		@Override
		public Object getService(Bundle bundle, ServiceRegistration registration) {
			return _bean.create(_creationalContext);
		}

		@Override
		public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
			_bean.destroy(service, _creationalContext);
		}

	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private class PrototypeScopeWrapper implements PrototypeServiceFactory {

		@Override
		public Object getService(Bundle bundle, ServiceRegistration registration) {
			return _bean.create(_creationalContext);
		}

		@Override
		public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
			_bean.destroy(service, _creationalContext);
		}

	}

}
