package com.liferay.cdi.container.internal.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {

	@SafeVarargs
	public static <T> Set<T> hashSet(T ... elements) {
		Set<T> set = new HashSet<>();

		for (T t : elements) {
			set.add(t);
		}

		return set;
	}

	@SafeVarargs
	public static <T> Set<T> immutableHashSet(T ... elements) {
		Set<T> set = new HashSet<>();

		for (T t : elements) {
			set.add(t);
		}

		return Collections.unmodifiableSet(set);
	}

}