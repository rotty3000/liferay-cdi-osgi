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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;


/**
 * The @Component annotation exposes a CDI bean in the OSGi registry.
 *
 * @author  Neil Griffin
 */
@Qualifier
@Target(value = { ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER, ElementType.CONSTRUCTOR })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

	Class<?>[] interfaces() default {};

	ComponentProperty[] properties() default {};

	String requireConfiguration() default "";
}
