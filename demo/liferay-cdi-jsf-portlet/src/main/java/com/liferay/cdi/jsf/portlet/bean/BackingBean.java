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
package com.liferay.cdi.jsf.portlet.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.liferay.faces.util.context.FacesContextHelperUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
@Named
@RequestScoped
public class BackingBean {

	private static final Logger logger = LoggerFactory.getLogger(BackingBean.class);

	@Inject
	private ModelBean modelBean;

	@PostConstruct
	public void postConstruct() {
		logger.info("Called BackingBean.postConstruct()");
	}

	@PreDestroy
	public void preDestroy() {
		logger.info("Called BackingBean.preDestroy()");
	}

	public void submit() {
		logger.info("Called BackingBean.submit()");
		FacesContextHelperUtil.addGlobalInfoMessage("hello", modelBean.getFullName());
	}
}
