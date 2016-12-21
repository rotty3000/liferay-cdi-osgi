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

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.ReferenceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.cdi.container.internal.bean.ReferenceBean;

public class ReferenceDependency {

	static enum BindType {
		SERVICE, SERVICE_PROPERTIES, SERVICE_REFERENCE
	}

	public ReferenceDependency(
			Reference reference, BeanManager manager, InjectionPoint injectionPoint, BundleContext bundleContext)
		throws InvalidSyntaxException {

		_reference = reference;
		_manager = manager;
		_injectionPoint = injectionPoint;
		_bundleContext = bundleContext;

		Class<? extends Annotation> scope = ApplicationScoped.class;

		if (reference.scope() == ReferenceScope.PROTOTYPE) {
			scope = Dependent.class;
		}
		else if (reference.scope() == ReferenceScope.SINGLETON) {
			scope = Singleton.class;
		}

		_scope = scope;
		_bindType = getBindType(_injectionPoint.getType());

		_serviceClass = getServiceType();

		_string = buildFilter(_serviceClass);
		_filter = FrameworkUtil.createFilter(_string);
	}

	public void addBean(AfterBeanDiscovery abd) {
		abd.addBean(new ReferenceBean(_manager, this));
	}

	public Class<?> getBeanClass() {
		if (_bindType == BindType.SERVICE_REFERENCE) {
			return ServiceReference.class;
		}
		else if (_bindType == BindType.SERVICE_PROPERTIES) {
			return Map.class;
		}

		return _serviceClass;
	}

	public Class<? extends Annotation> getScope() {
		return _scope;
	}

	@SuppressWarnings("unchecked")
	public Object getServiceImpl() {
		if (_bindType == BindType.SERVICE_REFERENCE) {
			return _serviceReference;
		}
		else if (_bindType == BindType.SERVICE_PROPERTIES) {
			// TODO should this be a wrapper around the ServiceReference rather than a copy?

			Map<String, Object> properties = new HashMap<>();

			for (String key : _serviceReference.getPropertyKeys()) {
				properties.put(key, _serviceReference.getProperty(key));
			}

			return properties;
		}

		_serviceObjects = _bundleContext.getServiceObjects(_serviceReference);

		return _serviceObjects.getService();
	}

	public Set<Type> getTypes() {
		return Collections.singleton(_injectionPoint.getType());
	}

	public boolean isResolved() {
		return _serviceReference != null;
	}

	public boolean matches(ServiceReference<?> reference) {
		return _filter.match(reference);
	}

	public void resolve(ServiceReference<?> reference) {
		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Binding {} to injection point {}", reference, _injectionPoint);
		}

		_serviceReference = reference;
	}

	@Override
	public String toString() {
		return _string;
	}

	@SuppressWarnings("unchecked")
	public void ungetServiceImpl(Object service) {
		if (_serviceObjects == null) {
			return;
		}

		try {
			_serviceObjects.ungetService(service);
		}
		catch (Throwable t) {
			if (_log.isWarnEnabled()) {
				_log.warn("CDIe - UngetService resulted in error", t);
			}
		}
	}

	private String buildFilter(Class<?> serviceType) throws InvalidSyntaxException {
		String targetFilter = _reference.target();

		int targetFilterLength = targetFilter.length();

		if (targetFilterLength > 0) {
			FrameworkUtil.createFilter(targetFilter);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("(&(");
		sb.append(Constants.OBJECTCLASS);
		sb.append("=");
		sb.append(serviceType.getName());
		sb.append(")");

		if (_reference.scope() == ReferenceScope.PROTOTYPE) {
			sb.append("(");
			sb.append(Constants.SERVICE_SCOPE);
			sb.append("=");
			sb.append(Constants.SCOPE_PROTOTYPE);
			sb.append(")");
		}
		else if (_reference.scope() == ReferenceScope.SINGLETON) {
			sb.append("(");
			sb.append(Constants.SERVICE_SCOPE);
			sb.append("=");
			sb.append(Constants.SCOPE_SINGLETON);
			sb.append(")");
		}

		if ((targetFilterLength > 0)) {
			sb.append(targetFilter);
		}

		sb.append(")");

		return sb.toString();
	}

	private BindType getBindType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)type;

			Type rawType = parameterizedType.getRawType();

			if (Map.class.isAssignableFrom((Class<?>)rawType)) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

				Type first = actualTypeArguments[0];
				Type second = actualTypeArguments[1];

				if (!(first instanceof ParameterizedType) &&
					String.class.isAssignableFrom((Class<?>)first)) {

					if ((!(second instanceof ParameterizedType) && (second == Object.class)) ||
						(second instanceof WildcardType)) {

						return BindType.SERVICE_PROPERTIES;
					}
				}

				return BindType.SERVICE;
			}
			else if (ServiceReference.class.isAssignableFrom((Class<?>)rawType)) {
				return BindType.SERVICE_REFERENCE;
			}

			return BindType.SERVICE;
		}
		else if (ServiceReference.class.isAssignableFrom((Class<?>)type)) {
			return BindType.SERVICE_REFERENCE;
		}

		return BindType.SERVICE;
	}

	private Class<?> getServiceType() {
		if (_reference.service() != Object.class) {
			return _reference.service();
		}

		Type type = _injectionPoint.getType();

		if (_bindType == BindType.SERVICE_PROPERTIES) {
			throw new IllegalArgumentException(
				"A @Reference cannot bind service properties to a Map<String, Object> without " +
					"specifying the @Reference.service property: " + _injectionPoint);
		}
		else if ((_bindType == BindType.SERVICE_REFERENCE) && !(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException(
				"A @Reference cannot bind a ServiceReference without specifying either the " +
					"@Reference.service property or a generic type argument (e.g. ServiceReference<Foo>: " +
						_injectionPoint);
		}

		if (!(type instanceof ParameterizedType)) {
			return (Class<?>)type;
		}

		ParameterizedType parameterizedType = (ParameterizedType)type;

		Type rawType = parameterizedType.getRawType();

		if (!ServiceReference.class.isAssignableFrom((Class<?>)rawType)) {
			return (Class<?>)rawType;
		}

		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

		Type first = actualTypeArguments[0];

		if (first instanceof ParameterizedType) {
			return (Class<?>)((ParameterizedType)first).getRawType();
		}

		return (Class<?>)first;
	}

	private static final Logger _log = LoggerFactory.getLogger(ReferenceDependency.class);

	private final BeanManager _manager;
	private final BindType _bindType;
	private final BundleContext _bundleContext;
	private final Filter _filter;
	private final InjectionPoint _injectionPoint;
	private final Reference _reference;
	private final Class<? extends Annotation> _scope;
	private volatile ServiceObjects _serviceObjects;
	@SuppressWarnings("rawtypes")
	private volatile ServiceReference _serviceReference;
	private final Class<?> _serviceClass;
	private final String _string;

}