/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.osgi.service.cdi;

import org.osgi.framework.ServiceRegistration;


/**
 * CDI Event sent by the CDI extender whenever a component has been registered or unregistered to/from the OSGi
 * registry.
 *
 * @author  Neil Griffin
 */
public class ComponentEvent<T> {

	private ServiceRegistration<T> registration;
	private T service;

	public ComponentEvent(ServiceRegistration<T> registration, T service) {
		this.registration = registration;
		this.service = service;
	}

	public ServiceRegistration<T> getRegistration() {
		return registration;
	}

	public T getService() {
		return service;
	}
}
