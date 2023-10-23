package com.maxrun.common;

import javax.servlet.ServletContext;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Component
public class ContextHolder implements ApplicationContextAware, ServletContextAware {
	
	private static ApplicationContext springContext;
	private static ServletContext servletContext;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.servletContext = servletContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		System.out.println("===================spring context====================");
		
		this.springContext = applicationContext;
		
		if(applicationContext==null) {
			System.out.println("################ spring context is null");
		}
		
		String[] beans = springContext.getBeanDefinitionNames();
		
//		RequestMappingHandlerAdapter rma = (RequestMappingHandlerAdapter) springContext.getBean("RequestMappingHandlerAdapter");
//		
//		rma.setMessageConverters(messageConverters);

        for (String bean : beans) {
            System.out.println("bean : " + bean);
        }
        
//        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
//        
//        Configuration conf = sqlSessionFactory.getConfiguration();
//        
//        conf.isCallSettersOnNulls();
//        conf.setCallSettersOnNulls(true);
//        
//        sqlSessionFactory.openSession().getConfiguration().isCallSettersOnNulls();
        
	}
	
	public static ApplicationContext getSpringContext() {
		return springContext;
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}
}
