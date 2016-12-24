/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

	public static enum State {
		CREATING,
		CREATED,
		DESTROYING,
		DESTROYED,
		WAITING_FOR_EXTENSIONS,
		WAITING_FOR_SERVICES,
		SATISFIED,
		FAILURE
	}

	public CdiEvent(State type, Bundle bundle, Bundle extenderBundle) {
		this(type, bundle, extenderBundle, null, null);
	}

	public CdiEvent(State type, Bundle bundle, Bundle extenderBundle, String payload) {
		this(type, bundle, extenderBundle, payload, null);
	}

	public CdiEvent(State type, Bundle bundle, Bundle extenderBundle, String payload, Throwable cause) {
		this.type = type;
		this.bundle = bundle;
		this.extenderBundle = extenderBundle;
		this.payload = payload;
		this.cause = cause;
		this.timestamp = System.currentTimeMillis();

		StringBuilder sb = new StringBuilder();

		sb.append("{type:'");
		sb.append(this.type);
		sb.append("',timestamp:");
		sb.append(this.timestamp);
		sb.append(",bundle:'");
		sb.append(this.bundle);
		sb.append("',extenderBundle:'");
		sb.append(this.extenderBundle);
		if (this.payload != null) {
			sb.append("',payload:'");
			sb.append(this.payload);
		}
		if (this.cause != null) {
			sb.append("',cause:'");
			sb.append(this.cause.getMessage());
		}
		sb.append("'}");

		string = sb.toString();
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

	public String getPayload() {
		return payload;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public State getState() {
		return type;
	}

	@Override
	public String toString() {
		return string;
	}

	private final Bundle bundle;
	private final Throwable cause;
	private final Bundle extenderBundle;
	private final String payload;
	private final long timestamp;
	private final State type;
	private final String string;

}
