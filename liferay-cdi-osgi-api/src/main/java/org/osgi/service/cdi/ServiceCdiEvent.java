/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.cdi;

import org.osgi.framework.ServiceReference;

/**
 * A holder for observer methods to receive both the ServiceReferences and the service.
 */
public class ServiceCdiEvent<T> {

	private final ServiceReference<T> reference;
	
	private final T service;
	
	public ServiceCdiEvent(ServiceReference<T> reference, T service) {
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
