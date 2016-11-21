package org.osgi.service.cdi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.inject.Scope;

/**
 * When the component is registered as a service, it must be registered as a
 * prototype scope service and an instance of the component must be created
 * for each distinct request for the service.
 */
@Qualifier
@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Scope
public @interface PrototypeScoped {
}
