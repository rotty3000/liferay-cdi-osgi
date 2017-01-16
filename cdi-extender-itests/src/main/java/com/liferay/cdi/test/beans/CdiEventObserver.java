package com.liferay.cdi.test.beans;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.osgi.service.cdi.CdiEvent;

import com.liferay.cdi.test.interfaces.BeanService;
import com.liferay.cdi.test.interfaces.CdiEventObserverQualifier;

@CdiEventObserverQualifier
@Singleton
public class CdiEventObserver implements BeanService<List<CdiEvent>> {

	@Override
	public String doSomething() {
		return this.toString();
	}

	@Override
	public List<CdiEvent> get() {
		return events;
	}

	public void onAnyDocumentEvent(@Observes CdiEvent event) {
		events.add(event);
	}

	private final List<CdiEvent> events = new CopyOnWriteArrayList<>();

}
