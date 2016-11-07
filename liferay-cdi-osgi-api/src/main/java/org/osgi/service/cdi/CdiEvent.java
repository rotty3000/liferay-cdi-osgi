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

import org.osgi.framework.Bundle;


/**
 * CdiEvents are sent by the cdi extender and received by registered CdiListener services.
 *
 * @author  Neil Griffin
 */
public class CdiEvent {

	public static int CREATING = 1;
	public static int CREATED = 2;
	public static int DESTROYING = 3;
	public static int DESTROYED = 4;
	public static int FAILURE = 5;

	private Bundle bundle;
	private Throwable cause;
	private CdiEvent cdiEvent;
	private Bundle extenderBundle;
	private boolean replay;
	private int type;

	public CdiEvent(CdiEvent event, boolean replay) {
		this.cdiEvent = event;
		this.replay = replay;
	}

	public CdiEvent(int type, Bundle bundle, Bundle extenderBundle) {
		this.type = type;
		this.bundle = bundle;
		this.extenderBundle = extenderBundle;
	}

	public CdiEvent(int type, Bundle bundle, Bundle extenderBundle, Throwable cause) {
		this(type, bundle, extenderBundle);
		this.cause = cause;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public Throwable getCause() {
		return cause;
	}

	public Bundle getExtenderBundle() {
		return extenderBundle;
	}

	public long getTimestamp() {
		return 0L; // TODO
	}

	public int getType() {
		return type;
	}

	public boolean isReplay() {
		return replay;
	}
}
