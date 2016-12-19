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

package com.liferay.cdi.container.internal.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import com.liferay.cdi.container.internal.container.ReferenceDependency;

@SuppressWarnings("rawtypes")
public class ReferenceBean implements Bean {

	public ReferenceBean(ReferenceDependency referenceDependency) {
		_referenceDependency = referenceDependency;
	}

	@Override
	public Object create(CreationalContext creationalContext) {
		return _referenceDependency.getServiceImpl();
	}

	@Override
	public void destroy(Object service, CreationalContext creationalContext) {
		_referenceDependency.ungetServiceImpl(service);
	}

	@Override
	public Class<?> getBeanClass() {
		return _referenceDependency.getBeanClass();
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return Collections.emptySet();
	}

	@Override
	public String getName() {
		return toString() + "#" + System.identityHashCode(this);
	}

	@Override
	public Set<Annotation> getQualifiers() {
		return Collections.singleton(new DefaultQualifier());
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return _referenceDependency.getScope();
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public Set<Type> getTypes() {
		return _referenceDependency.getTypes();
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public String toString() {
		return "ReferenceBean(" + _referenceDependency + ")";
	}

	private final ReferenceDependency _referenceDependency;

}