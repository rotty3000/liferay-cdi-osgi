package com.liferay.cdi.test.beans;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.osgi.service.cdi.CdiEvent;

import com.liferay.cdi.test.interfaces.BeanThingy;
import com.liferay.cdi.test.interfaces.CdiEventObserverQualifier;

@Singleton
@CdiEventObserverQualifier
public class CdiEventObserver implements BeanThingy<List<CdiEvent>> {

	@Override
	public String doSomething() {
		return this.toString();
	}

	@Override
	public List<CdiEvent> getThingy() {
		return events;
	}

	public void onAnyDocumentEvent(@Observes CdiEvent event) {
		events.add(event);
	}

	private final List<CdiEvent> events = new CopyOnWriteArrayList<>();

}
