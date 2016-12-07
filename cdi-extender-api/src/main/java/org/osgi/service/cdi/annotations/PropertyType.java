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

/**
 * The possible property types available to {@link ServiceProperty} instances.
 */
public enum PropertyType {

	Boolean("Boolean"),
	Byte("Byte"),
	Character("Character"),
	Double("Double"),
	Float("Float"),
	Integer("Integer"),
	Long("Long"),
	Short("Short"),
	String("String"),

	Boolean_Array("Boolean[]"),
	Byte_Array("Byte[]"),
	Character_Array("Character[]"),
	Double_Array("Double[]"),
	Float_Array("Float[]"),
	Integer_Array("Integer[]"),
	Long_Array("Long[]"),
	Short_Array("Short[]"),
	String_Array("String[]"),

	Boolean_List("List<Boolean>"),
	Byte_List("List<Byte>"),
	Character_List("List<Character>"),
	Double_List("List<Double>"),
	Float_List("List<Float>"),
	Integer_List("List<Integer>"),
	Long_List("List<Long>"),
	Short_List("List<Short>"),
	String_List("List<String>"),

	Boolean_Set("Set<Boolean>"),
	Byte_Set("Set<Byte>"),
	Character_Set("Set<Character>"),
	Double_Set("Set<Double>"),
	Float_Set("Set<Float>"),
	Integer_Set("Set<Integer>"),
	Long_Set("Set<Long>"),
	Short_Set("Set<Short>"),
	String_Set("Set<String>");

	PropertyType(String value) {
		this.value = value;
	}

	@Override
	public java.lang.String toString() {
		return value;
	}

	private final String value;

}
