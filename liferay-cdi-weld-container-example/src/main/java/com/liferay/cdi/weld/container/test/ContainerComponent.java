package com.liferay.cdi.weld.container.test;

import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.InitialContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.liferay.cdi.weld.container.test.bean.SimpleBean;

@Component(immediate = true)
public class ContainerComponent {
	
	@Activate
	public void activate(BundleContext bundleContext) {
		try {
			InitialContext context = new InitialContext();
	
			BeanManager beanManager = (BeanManager)context.lookup("java:comp/BeanManager");
	
			Set<Bean<?>> beans = beanManager.getBeans(SimpleBean.class, any);
			
			System.out.println(beans);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static final AnnotationLiteral<Any> any = new AnnotationLiteral<Any>() {
		private static final long serialVersionUID = 1L;
	};

}
