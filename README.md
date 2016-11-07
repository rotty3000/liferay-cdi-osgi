# Liferay CDI OSGi

This is an implementation of [OSGi RFC 0193](https://github.com/osgi/design/blob/master/rfcs/rfc0193/rfc-0193-CDI-Integration.pdf) compatible with Liferay Portal.

## License

[GNU Lesser General Public License (LGPL), Version 2.1](http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt)

## Required OSGi Bundles

The following OSGi bundles must be downloaded and copied to $LIFERAY_HOME/osgi/modules:

- [cdi-api-1.2.jar](http://search.maven.org/remotecontent?filepath=javax/enterprise/cdi-api/1.2/cdi-api-1.2.jar)
- [javax.annotation-api-1.2.jar](http://search.maven.org/remotecontent?filepath=javax/annotation/javax.annotation-api/1.2/javax.annotation-api-1.2.jar)
- [javax.interceptor-api-1.2.jar](http://search.maven.org/remotecontent?filepath=javax/interceptor/javax.interceptor-api/1.2/javax.interceptor-api-1.2.jar)
- [jboss-classfilewriter-1.1.2.Final.jar](http://search.maven.org/remotecontent?filepath=org/jboss/classfilewriter/jboss-classfilewriter/1.1.2.Final/jboss-classfilewriter-1.1.2.Final.jar)
- [jboss-logging-3.2.1.Final.jar](http://search.maven.org/remotecontent?filepath=org/jboss/logging/jboss-logging/3.2.1.Final/jboss-logging-3.2.1.Final.jar)
- [log4j-api-2.7.jar](http://search.maven.org/remotecontent?filepath=org/apache/logging/log4j/log4j-api/2.7/log4j-api-2.7.jar)
- [org.apache.servicemix.bundles.javax-inject-1_2.jar](http://search.maven.org/remotecontent?filepath=org/apache/servicemix/bundles/org.apache.servicemix.bundles.javax-inject/1_2/org.apache.servicemix.bundles.javax-inject-1_2.jar)
- [weld-osgi-bundle-2.4.0.Final.jar](http://search.maven.org/remotecontent?filepath=org/jboss/weld/weld-osgi-bundle/2.4.0.Final/weld-osgi-bundle-2.4.0.Final.jar)

## Building From Source

*Note*: The $PORTALS_HOME environment variable must be setup prior to running any of the deploy-module.sh scripts. The Typical value for $LIFERAY_HOME would be $PORTALS_HOME/liferay-portal-7.0.3-SNAPSHOT-jsf-2.2

	cd liferay-cdi-osgi-api; ./deploy-module.sh
	cd liferay-cdi-osgi-extender; ./deploy-module.sh
	cd liferay-cdi-portable-extension; ./deploy-module.sh
	cd liferay-cdi-weld-adapter; ./deploy-module.sh
	cd demo/liferay-cdi-jsf-portlet; ./deploy-module.sh
