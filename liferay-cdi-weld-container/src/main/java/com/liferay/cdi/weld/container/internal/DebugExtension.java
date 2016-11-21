package com.liferay.cdi.weld.container.internal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DebugExtension implements Extension {

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		if (_log.isDebugEnabled()) {
			_log.debug("After bean discovery {}", abd);
		}
	}

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		if (_log.isDebugEnabled()) {
			_log.debug("Before bean discovery {}", bbd);
		}
	}

	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		if (_log.isDebugEnabled()) {
			_log.debug("Processing bean {}", pat);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(DebugExtension.class);

}
