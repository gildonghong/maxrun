package com.maxrun.application.config;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.maxrun.common.ContextHolder;

@Component
@PropertySource(value="classpath:appProps/globals.properties", ignoreResourceNotFound=true)
@PropertySource(value="classpath:appProps/globals-${spring.profiles.active}.properties", ignoreResourceNotFound=true)
public class PropertyManager extends PropertySourcesPlaceholderConfigurer {
	//private final ContextHolder contextHolder;

	public static String get(String key) {
		//PropertySourcesPlaceholderConfigurer 클래스슨 FactoryBeanPostProcessor 로서 이 녀석이 생성될때는 
		//아마도 ContextHolder bean이 없을때이므로 contextHolder는 널일수 밖에 없음. 그런데 @Autowired는 기본 생성자주입인데
		//왜 바인딩시 에러가 발생하지 않았을까?
//		if(contextHolder==null) {
//			contextHolder = ContextHolder.getSpringContext().getBean(ContextHolder.class);
//			if (ContextHolder.getSpringContext().getBean(ContextHolder.class) == null)
//				System.out.println("contextHolder is null");
//		}
		
		return ContextHolder.getSpringContext().getEnvironment().getProperty(key);
		//return ev.getProperty(key);
	}
	
//	public PropertyManager(Environment ev) {
//		this.ev = ev;
//	}
	
//	//@Autowired
//	public PropertyManager(ContextHolder contextHolder) {
//		this.contextHolder = contextHolder;
//	}
	
//	public PropertyManager() {
//		this.contextHolder = null;
//	}
}
