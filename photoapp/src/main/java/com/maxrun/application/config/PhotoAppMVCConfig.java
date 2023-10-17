package com.maxrun.application.config;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

import com.maxrun.application.common.interceptor.AuthInterceptor;
import com.maxrun.http.message.json.XssDefenderableMapper;

@Configuration
@EnableWebMvc
@ComponentScan(	basePackages = {"com.maxrun"}, useDefaultFilters = false,
				includeFilters= @Filter(type = FilterType.ANNOTATION, classes = {Controller.class}))
public class PhotoAppMVCConfig implements WebMvcConfigurer{
	
	public PhotoAppMVCConfig() {
		System.out.println("PhotoAppMVCConfig, I was called");
	}

	private final Logger logger	= LogManager.getLogger(this.getClass());
    @Override
    public void configureViewResolvers (ViewResolverRegistry registry) {
        //by default prefix = "/WEB-INF/" and  suffix = ".jsp"
        registry.jsp().prefix("/WEB-INF/views/");
        registry.jsp().suffix(".jsp");
        System.out.println("PhotoAppMVCConfig.configureViewResolvers, I was called");
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
       CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
       multipartResolver.setMaxUploadSize(-1); // 30MB
       multipartResolver.setMaxUploadSizePerFile(-1); // 30MB
       multipartResolver.setMaxInMemorySize(0);
       return multipartResolver;
    }
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//    	System.out.println("SDWebMVCConfig.configureMessageConverters, I was called");
//    	System.out.println("SDWebMVCConfig.configureMessageConverters, converters count ===>" + converters.size());
//    	
////    	converter className ==>class org.springframework.http.converter.ByteArrayHttpMessageConverter
////		converter className ==>class org.springframework.http.converter.StringHttpMessageConverter
////		converter className ==>class org.springframework.http.converter.ResourceHttpMessageConverter
////		converter className ==>class org.springframework.http.converter.ResourceRegionHttpMessageConverter
////		converter className ==>class org.springframework.http.converter.xml.SourceHttpMessageConverter
////		converter className ==>class org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter
////		converter className ==>class org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter
////		converter className ==>class org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
//    
//    	
//    	int	i=0;
//    	
//    	for(HttpMessageConverter ctr:converters) {
//    		System.out.println("converter class Name===>" + ctr.getClass().getName());
//    		i++;
//    	}
//    	
//    	System.out.println("counter 수==>" + i);
//	}
    
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    	System.out.println("extendMessageConverters was called");
    	System.out.println("현재 설정된  HttpConverters 개수:" + converters.size());
    	
    	logger.info("HttpMessageConverter className===>" + converters.get(0).getClass().getName());
		logger.info("HttpMessageConverter CanonicalName===>" + converters.get(0).getClass().getCanonicalName());
		logger.info("HttpMessageConverter TypeName===>" + converters.get(0).getClass().getTypeName());
		logger.info("HttpMessageConverter SimpleName===>" + converters.get(0).getClass().getSimpleName());
		
		
    	logger.info("HttpMessageConverter className===>" + converters.get(1).getClass().getName());
		logger.info("HttpMessageConverter CanonicalName===>" + converters.get(1).getClass().getCanonicalName());
		logger.info("HttpMessageConverter TypeName===>" + converters.get(1).getClass().getTypeName());
		logger.info("HttpMessageConverter SimpleName===>" + converters.get(1).getClass().getSimpleName());
		//request body로 전달되는 파라미터에 대해서도 lucy-xss-servlet filter를 사용할 수 있도록 추가적인 컨버터를 등록한다
		//실제 htmlEscep 처리를 담당하는 녀석은 XssDefenderableMapper라는 objectMapper가 처리한다 신규로 추가된 converter는 단지 
		//MappingJackson2HttpMessageConverter의 기본 메퍼가 아닌 XssDefenderableMapper를 사용하여서 XSS 방지를 위해 특수문자를 처리한다
		MappingJackson2HttpMessageConverter htmlEscapingConverter = new MappingJackson2HttpMessageConverter();
		htmlEscapingConverter.setObjectMapper(new XssDefenderableMapper());
		
		converters.add(htmlEscapingConverter);
		
    	System.out.println("htmlEscapeConverter추가 후  HttpConverters 개수:" + converters.size());
    	
    	for(HttpMessageConverter<?> mv:converters) {
    		logger.info("HttpMessageConverter className===>" + mv.getClass().getName());
    		logger.info("HttpMessageConverter CanonicalName===>" + mv.getClass().getCanonicalName());
    		logger.info("HttpMessageConverter TypeName===>" + mv.getClass().getTypeName());
    		logger.info("HttpMessageConverter SimpleName===>" + mv.getClass().getSimpleName());
//    		if(mv instanceof MappingJackson2HttpMessageConverter) {
//    			logger.info("object mapper change");
//    			((MappingJackson2HttpMessageConverter) mv).setObjectMapper(new XssDefenderableMapper());
//    		}
    	}
//
//    	for(int i=0;i<converters.size();i++) {
//    		logger.info("HttpMessageConverter className===>" + converters.get(i).getClass().getName());
//    		logger.info("HttpMessageConverter CanonicalName===>" + converters.get(i).getClass().getCanonicalName());
//    		logger.info("HttpMessageConverter TypeName===>" + converters.get(i).getClass().getTypeName());
//    		logger.info("HttpMessageConverter SimpleName===>" + converters.get(i).getClass().getSimpleName());
//    	}
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**") // resource에 대한 전체 요청 발생 시
                .addResourceLocations("/resources/") // resource에 대한 위치 설정 - resources 하위로 매핑
                .setCachePeriod(60) // 리소스에 캐시 설정 가능
                .resourceChain(true) // 리졸버 체인 설정 :
                .addResolver(new EncodedResourceResolver()); // Request Header의 Accept-Encoding 정보를 보고 resource를 gzip형태로 매핑?
        
        registry.addResourceHandler("/photopath/**") // resource에 대한 전체 요청 발생 시
		        .addResourceLocations("/photopath/") // resource에 대한 위치 설정 - resources 하위로 매핑
		        .setCachePeriod(60) // 리소스에 캐시 설정 가능
		        .resourceChain(true) // 리졸버 체인 설정 :
		        .addResolver(new EncodedResourceResolver()); // Request Header의 Accept-Encoding 정보를 보고 resource를 gzip형태로 매핑?
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	InterceptorRegistration ir = registry.addInterceptor(createInterceptor(AuthInterceptor.class));
    	
    	ir.excludePathPatterns("/index");
    	ir.excludePathPatterns("/notice/list");
    	ir.excludePathPatterns("/login");
    	ir.excludePathPatterns("/logout");
    	//ir.excludePathPatterns("/repairshop/**");	//당분간 개발동안
    }
    
    @Bean
    public HandlerInterceptor createInterceptor(Class clazz) {
    	HandlerInterceptor ret = null;
    	if (clazz.equals(AuthInterceptor.class)) {
    		ret = new AuthInterceptor();
    	}
    	
    	return ret;
    }
}
