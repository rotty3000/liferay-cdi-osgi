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

package org.osgi.service.cdi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The @Service annotation exposes a CDI bean in the service registry.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

	/**
	 * The types under which to register this CDI bean as a service.
	 *
	 * <p>
	 * If not specified, the service types for this CDI bean are all the
	 * <i>directly</i> implemented interfaces of the class being annotated.
	 *
	 * <p>
	 * If the CDI bean does not <i>directly</i> implement any interfaces the
	 * bean class is used.
	 *
	 * @see "The types element of a Service Description."
	 */
	Class<?>[] type() default {};

	/**
	 * Properties for this Component.
	 *
	 * <p>
	 * Each property is specified as an instance of {@link ServiceProperty}.
	 *
	 * @see "The properties element of a Service Description."
	 */
	ServiceProperty[] properties() default {};

}
