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
package com.liferay.cdi.jsf.weld;

import java.util.Map;

import javax.el.ELContextListener;
import javax.el.ExpressionFactory;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.el.WeldELContextListener;


/**
 * @author  Neil Griffin
 */
public class ApplicationWeldImpl extends ApplicationWrapper {

	private Application wrappedApplication;
	private ExpressionFactory expressionFactory;

	public ApplicationWeldImpl(Application application) {

		this.wrappedApplication = application;

		BeanManager beanManager = getBeanManager();

		if (beanManager != null) {
			ELContextListener elContextListener = new WeldELContextListener();
			application.addELContextListener(elContextListener);
			application.addELResolver(beanManager.getELResolver());
		}
	}

	@Override
	public ExpressionFactory getExpressionFactory() {

		if (this.expressionFactory == null) {
			BeanManager beanManager = getBeanManager();
			ExpressionFactory wrappedExpressionFactory = getWrapped().getExpressionFactory();

			if (beanManager != null) {
				this.expressionFactory = beanManager.wrapExpressionFactory(wrappedExpressionFactory);
			}
			else {
				this.expressionFactory = getWrapped().getExpressionFactory();
			}
		}

		return expressionFactory;
	}

	@Override
	public Application getWrapped() {
		return wrappedApplication;
	}

	private BeanManager getBeanManager() {

		BeanManager beanManager = null;

		try {
			InitialContext context = new InitialContext();

			beanManager = (BeanManager) context.lookup("java:comp/BeanManager");
		}
		catch (NamingException e) {
			// ignore
		}

		if (beanManager == null) {
			FacesContext startupFacesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = startupFacesContext.getExternalContext();
			Map<String, Object> applicationMap = externalContext.getApplicationMap();
			beanManager = (BeanManager) applicationMap.remove("beanManagerHack");
		}

		return beanManager;
	}
}
