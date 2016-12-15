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

/**
 * Defines CDI Extender constants.
 */
public class CdiExtenderConstants {
	private CdiExtenderConstants() {
		// non-instantiable
	}

	public static final String CDI_EXTENDER = "osgi.cdi";

	public static final String CDI_EXTENDER_CONTAINER_STATE = "osgi.cdi.container.state";

	public static final String CDI_EXTENSION = "osgi.cdi.extension";

	public static final String CDI_EXTENSION_ATTRIBUTE = "extensions";
}
