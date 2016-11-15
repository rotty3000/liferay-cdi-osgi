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

import org.osgi.framework.Bundle;

/**
 * CdiEvents are sent by the cdi extender and received by registered CdiListener services.
 */
public class CdiEvent {

	public static int CREATING = 1;
	public static int CREATED = 2;
	public static int DESTROYING = 3;
	public static int DESTROYED = 4;
	public static int WAITING_FOR_EXTENSIONS = 5;
	public static int WAITING_FOR_SERVICES = 6;
	public static int SATISFIED = 7;
	public static int FAILURE = 8;

	private Bundle bundle;
	private Throwable cause;
	private Bundle extenderBundle;
	private boolean replay;
	private int type;

	public CdiEvent(CdiEvent event, boolean replay) {
		this(event.getType(), event.getBundle(), event.getExtenderBundle(), event.getCause());
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
