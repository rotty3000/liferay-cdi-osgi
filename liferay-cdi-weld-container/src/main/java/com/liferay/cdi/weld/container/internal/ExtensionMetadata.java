package com.liferay.cdi.weld.container.internal;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.spi.Metadata;

public class ExtensionMetadata implements Metadata<Extension> {

	public ExtensionMetadata(Extension extension, String location) {
		_extension = extension;
		_location = location;
	}

	@Override
	public Extension getValue() {
		return _extension;
	}

	@Override
	public String getLocation() {
		return _location;
	}

	private final Extension _extension;
	private final String _location;

}