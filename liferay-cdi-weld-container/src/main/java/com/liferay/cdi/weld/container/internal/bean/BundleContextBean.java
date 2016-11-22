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

package com.liferay.cdi.weld.container.internal.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.osgi.framework.BundleContext;

public class BundleContextBean implements Bean<BundleContext> {

	public BundleContextBean(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	public BundleContext create(CreationalContext<BundleContext> creationalContext) {
		return _bundleContext;
	}

	@Override
	public void destroy(BundleContext instance, CreationalContext<BundleContext> creationalContext) {
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(BundleContext.class);
	}

	@Override
	public Set<Annotation> getQualifiers() {
		return Collections.singleton(new DefaultQualifier());
	}

	@Override
	public Class<? extends Annotation> getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAlternative() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<?> getBeanClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNullable() {
		// TODO Auto-generated method stub
		return false;
	}

	private final BundleContext _bundleContext;

}