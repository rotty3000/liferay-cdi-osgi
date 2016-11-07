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

import org.osgi.framework.ServiceReference;


/**
 * The event sent by the CDI extender whenever a service that matches an injection point is registered or unregistered
 * from the OSGi registry.
 *
 * @author  Neil Griffin
 */
public class ServiceEvent<T> {

	private ServiceReference<T> reference;
	private T service;

	public ServiceEvent(ServiceReference<T> reference, T service) {
		this.reference = reference;
		this.service = service;
	}

	public ServiceReference<T> getReference() {
		return reference;
	}

	public T getService() {
		return service;
	}
}
