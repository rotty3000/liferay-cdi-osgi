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
package com.liferay.cdi.weld.jsf;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;


/**
 * @author  Neil Griffin
 */
public class ApplicationFactoryWeldImpl extends ApplicationFactory {

	private Application application;
	private ApplicationFactory wrappedApplicationFactory;

	public ApplicationFactoryWeldImpl(ApplicationFactory applicationFactory) {
		this.wrappedApplicationFactory = applicationFactory;
	}

	@Override
	public Application getApplication() {

		if (application == null) {
			Application wrappedApplication = wrappedApplicationFactory.getApplication();
			this.application = new ApplicationWeldImpl(wrappedApplication);
		}

		return application;
	}

	@Override
	public void setApplication(Application application) {
		this.application = application;
		wrappedApplicationFactory.setApplication(application);
	}
}
