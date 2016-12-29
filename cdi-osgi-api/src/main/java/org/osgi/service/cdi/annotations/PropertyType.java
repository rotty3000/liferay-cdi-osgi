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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * The possible property types available to {@link ServiceProperty} instances.
 */
public enum PropertyType {

	Boolean("Boolean", new TypeRef<Boolean>() {}),
	Byte("Byte", new TypeRef<Byte>() {}),
	Character("Character", new TypeRef<Character>() {}),
	Double("Double", new TypeRef<Double>() {}),
	Float("Float", new TypeRef<Float>() {}),
	Integer("Integer", new TypeRef<Integer>() {}),
	Long("Long", new TypeRef<Long>() {}),
	Short("Short", new TypeRef<Short>() {}),
	String("String", new TypeRef<String>() {}),

	Boolean_Array("Boolean[]", new TypeRef<Boolean[]>() {}),
	Byte_Array("Byte[]", new TypeRef<Byte[]>() {}),
	Character_Array("Character[]", new TypeRef<Character[]>() {}),
	Double_Array("Double[]", new TypeRef<Double[]>() {}),
	Float_Array("Float[]", new TypeRef<Float[]>() {}),
	Integer_Array("Integer[]", new TypeRef<Integer[]>() {}),
	Long_Array("Long[]", new TypeRef<Long[]>() {}),
	Short_Array("Short[]", new TypeRef<Short[]>() {}),
	String_Array("String[]", new TypeRef<String[]>() {}),

	Boolean_List("List<Boolean>", new TypeRef<List<Boolean>>() {}),
	Byte_List("List<Byte>", new TypeRef<List<Byte>>() {}),
	Character_List("List<Character>", new TypeRef<List<Character>>() {}),
	Double_List("List<Double>", new TypeRef<List<Double>>() {}),
	Float_List("List<Float>", new TypeRef<List<Float>>() {}),
	Integer_List("List<Integer>", new TypeRef<List<Integer>>() {}),
	Long_List("List<Long>", new TypeRef<List<Long>>() {}),
	Short_List("List<Short>", new TypeRef<List<Short>>() {}),
	String_List("List<String>", new TypeRef<List<String>>() {}),

	Boolean_Set("Set<Boolean>", new TypeRef<Set<Boolean>>() {}),
	Byte_Set("Set<Byte>", new TypeRef<Set<Byte>>() {}),
	Character_Set("Set<Character>", new TypeRef<Set<Character>>() {}),
	Double_Set("Set<Double>", new TypeRef<Set<Double>>() {}),
	Float_Set("Set<Float>", new TypeRef<Set<Float>>() {}),
	Integer_Set("Set<Integer>", new TypeRef<Set<Integer>>() {}),
	Long_Set("Set<Long>", new TypeRef<Set<Long>>() {}),
	Short_Set("Set<Short>", new TypeRef<Set<Short>>() {}),
	String_Set("Set<String>", new TypeRef<Set<String>>() {});

	PropertyType(String value, TypeRef<?> typeRef) {
		this.value = value;
		this.typeRef = typeRef;
	}

	public Type getType() {
		return typeRef.getType();
	}

	@Override
	public java.lang.String toString() {
		return value;
	}

	private final TypeRef<?> typeRef;
	private final String value;

	private static class TypeRef<T> {

		private Type getType() {
			return ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		}

	}

}
