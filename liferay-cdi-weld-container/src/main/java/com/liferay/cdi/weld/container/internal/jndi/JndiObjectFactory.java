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

package com.liferay.cdi.weld.container.internal.jndi;

import java.util.Hashtable;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class JndiObjectFactory implements ObjectFactory {

	public JndiObjectFactory(BeanManager beanManager) {
		_jndiContext = new JNDIContext(beanManager);
	}

	@Override
	public Object getObjectInstance(
			Object obj, Name name, javax.naming.Context context, Hashtable<?, ?> environment)
		throws Exception {

		if (obj == null) {
			return _jndiContext;
		}

		return null;
	}

	private final JNDIContext _jndiContext;

}