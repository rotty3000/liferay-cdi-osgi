package com.liferay.cdi.weld.container.internal;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.CdiEvent;
import org.osgi.service.cdi.CdiListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdiHelper {

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

		for (CdiListener listener : _listeners.values()) {
			try {
				listener.cdiEvent(event);
			}
			catch (Throwable t) {
				if (_log.isErrorEnabled()) {
					_log.error("CdiListener failed!", t);
				}
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(CdiHelper.class);

	private final Bundle _extenderBundle;
	private volatile CdiEvent _lastEvent;
	private final Map<ServiceReference<CdiListener>, CdiListener> _listeners;


}
