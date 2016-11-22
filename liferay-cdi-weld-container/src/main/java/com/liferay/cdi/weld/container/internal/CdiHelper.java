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

package com.liferay.cdi.weld.container.internal;

import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.CdiListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdiHelper {

	public static final AnnotationLiteral<Any> ANY = new AnnotationLiteral<Any>() {
		private static final long serialVersionUID = 1L;
	};

	public CdiHelper(Bundle extenderBundle, Map<ServiceReference<CdiListener>, CdiListener> listeners) {
		_extenderBundle = extenderBundle;
		_listeners = listeners;
	}

	public Bundle getExtenderBundle() {
		return _extenderBundle;
	}

	public CdiEvent getLastEvent() {
		return _lastEvent;
	}

	public void fireCdiEvent(CdiEvent event) {
		_lastEvent = event;

		if (_log.isDebugEnabled()) {
			_log.debug("CDIe - Event {}", event);
		}

		for (CdiListener listener : _listeners.values()) {
			try {
				listener.cdiEvent(event);
			}
			catch (Throwable t) {
				if (_log.isErrorEnabled()) {
					_log.error("CDIe - CdiListener failed", t);
				}
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(CdiHelper.class);

	private final Bundle _extenderBundle;
	private volatile CdiEvent _lastEvent;
	private final Map<ServiceReference<CdiListener>, CdiListener> _listeners;

}