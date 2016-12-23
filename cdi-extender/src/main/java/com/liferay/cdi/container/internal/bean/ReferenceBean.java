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

import static com.liferay.cdi.container.internal.util.Reflection.cast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.bean.builtin.BeanManagerProxy;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.util.Decorators;

import com.liferay.cdi.container.internal.container.ReferenceDependency;
import com.liferay.cdi.container.internal.literal.AnyLiteral;
import com.liferay.cdi.container.internal.literal.DefaultLiteral;
import com.liferay.cdi.container.internal.util.Sets;

@SuppressWarnings("rawtypes")
public class ReferenceBean implements Bean<Object> {

	public ReferenceBean(BeanManager manager, ReferenceDependency referenceDependency) {
		_manager = ((BeanManagerProxy)manager).delegate();
		_referenceDependency = referenceDependency;
		_types = Sets.immutableHashSet(_referenceDependency.getInjectionPoint().getType(), Object.class);
		_qualifiers = Sets.hashSet(DefaultLiteral.INSTANCE, AnyLiteral.INSTANCE, _referenceDependency.getReference());
	}

	@Override
	public Object create(CreationalContext<Object> creationalContext) {
		return create0(creationalContext);
	}

	@Override
	public void destroy(Object instance, CreationalContext creationalContext) {
		_referenceDependency.ungetServiceImpl(instance);
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
		return _qualifiers;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return Dependent.class;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public Set<Type> getTypes() {
		return _types;
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

	protected <T> T create0(CreationalContext<T> creationalContext) {
		InjectionPoint ip = _referenceDependency.getInjectionPoint();
		List<Decorator<?>> decorators = getDecorators(ip);
		T instance = cast(_referenceDependency.getServiceImpl());
		if (decorators.isEmpty()) {
			return instance;
		}
		return Decorators.getOuterDelegate(cast(this), instance, creationalContext, cast(getBeanClass()), ip, _manager, decorators);
	}

	protected List<Decorator<?>> getDecorators(InjectionPoint ip) {
		return _manager.resolveDecorators(Collections.singleton(ip.getType()), getQualifiers());
	}

	private final BeanManagerImpl _manager;
	private final Set<Annotation> _qualifiers;
	private final ReferenceDependency _referenceDependency;
	private final Set<Type> _types;

}