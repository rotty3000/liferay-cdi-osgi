package com.liferay.cdi.weld.container.test;

import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	property = {
		"key=value"
	},
	service = DSService.class
)
public class DSService {

}
