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
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

import org.jboss.weld.exceptions.UnsupportedOperationException;

public class JNDIContext implements Context {

	private final BeanManager _beanManager;

	public JNDIContext(BeanManager beanManager) {
		_beanManager = beanManager;
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	@Override
	public Object lookup(String name) throws NamingException {
		if (name.length() == 0) {
			return new JNDIContext(_beanManager);
		}
		if (name.equals("java:comp/BeanManager")) {
			return _beanManager;
		}
		throw new NamingException("Could not find " + name);
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unbind(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String composeName(String name, String prefix) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws NamingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		throw new OperationNotSupportedException();
	}

}
