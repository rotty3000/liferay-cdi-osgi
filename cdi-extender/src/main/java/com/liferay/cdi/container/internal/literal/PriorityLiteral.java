package com.liferay.cdi.container.internal.literal;

import javax.annotation.Priority;
import javax.enterprise.util.AnnotationLiteral;

public class PriorityLiteral extends AnnotationLiteral<Priority> implements Priority {

	private static final long serialVersionUID = 1L;
	public static final Priority INSTANCE = new PriorityLiteral(0);

	private PriorityLiteral(int value) {
		_value = value;
	}

	public static PriorityLiteral of(int value) {
		return new PriorityLiteral(value);
	}

	@Override
	public int value() {
		return _value;
	}

	private final int _value;

}
