package com.maxrun.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;

@Import({
	PersistenceConfig.class
	})
@PropertySource("classpath:appProps/photoapp.properties")
@ComponentScan(	basePackages = {"com.maxrun"}, useDefaultFilters = true,
				excludeFilters= @Filter(type = FilterType.ANNOTATION, classes = {Controller.class}))
public class PhotoAppRootConfig {
	
	@Autowired
	ApplicationContext applicationContext;
	
	public PhotoAppRootConfig() {
		System.out.println("PhotoAppRootConfig, I was called");
	}
}
