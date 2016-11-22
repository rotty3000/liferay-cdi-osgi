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

package com.liferay.cdi.weld.container.internal.container;

import java.util.HashSet;
import java.util.Set;

import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Service;

public class SimpleEnvironment implements Environment {

	private Set<Class<? extends Service>> requiredBeanDeploymentArchiveServices;
	private Set<Class<? extends Service>> requiredDeploymentServices;

	public SimpleEnvironment() {
		this.requiredBeanDeploymentArchiveServices = new HashSet<Class<? extends Service>>();
		this.requiredDeploymentServices = new HashSet<Class<? extends Service>>();
	}

	@Override
	public Set<Class<? extends Service>> getRequiredBeanDeploymentArchiveServices() {
		return requiredBeanDeploymentArchiveServices;
	}

	@Override
	public Set<Class<? extends Service>> getRequiredDeploymentServices() {
		return requiredDeploymentServices;
	}
}