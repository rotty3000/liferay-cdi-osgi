package com.liferay.cdi.weld.container.internal;

import java.util.List;

import javax.enterprise.inject.spi.Extension;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class FilterBuilder {

	public static Filter createExtensionFilter(List<ExtensionDependency> extentionDependencies) {
		try {
			StringBuilder sb = new StringBuilder("(&(objectClass=" + Extension.class.getName() + ")");

			if (extentionDependencies.size() > 1) sb.append("(|");

			for (ExtensionDependency dependency : extentionDependencies) {
				sb.append(dependency.toString());
			}

			if (extentionDependencies.size() > 1) sb.append(")");

			sb.append(")");

			return FrameworkUtil.createFilter(sb.toString());
		}
		catch (InvalidSyntaxException ise) {
			throw new RuntimeException(ise);
		}
	}

	public static Filter createReferenceFilter(List<ReferenceDependency> referenceDependencies) {
		try {
			StringBuilder sb = new StringBuilder();

			if (referenceDependencies.size() > 1) sb.append("(|");

			for (ReferenceDependency dependency : referenceDependencies) {
				sb.append(dependency.toString());
			}

			if (referenceDependencies.size() > 1) sb.append(")");

			return FrameworkUtil.createFilter(sb.toString());
		}
		catch (InvalidSyntaxException ise) {
			throw new RuntimeException(ise);
		}
	}

}
