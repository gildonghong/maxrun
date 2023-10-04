package com.maxrun.application.event;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.maxrun.http.message.json.XssDefenderableMapper;

@Component
public class AppStartedListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		ApplicationContext springContext = event.getApplicationContext();
		
//		if(!"Root WebApplicationContext".equals(springContext.getDisplayName())) {
//			return;
//		}
		
		//System.out.println("springContext event::" + springContext.getApplicationName());
		//System.out.println("springContext event::" + springContext.getDisplayName());
		
		String[] beans = springContext.getBeanDefinitionNames();
		
		System.out.println(beans);
		RequestMappingHandlerAdapter rmha = (RequestMappingHandlerAdapter)springContext.getBean(RequestMappingHandlerAdapter.class);
//		
		List<HttpMessageConverter<?>> hConverters = rmha.getMessageConverters();
//		
//		System.out.println("===================HttpMessageConverters====================");
//		System.out.println("===================size====================" + hConverters.size());
//		int delIdx=0;
		for(HttpMessageConverter<?> converter : hConverters) {
			//System.out.println("converter className ==>" + converter.getClass());
			if(converter.getClass().getName().endsWith("MappingJackson2HttpMessageConverter")) {
				
				MappingJackson2HttpMessageConverter temp = (MappingJackson2HttpMessageConverter)converter;
				
				temp.setObjectMapper(new XssDefenderableMapper());
				
				//System.out.println(((MappingJackson2HttpMessageConverter)converter).getObjectMapper().getClass().getName());

				break;

			}
		}
//			
//			delIdx++;
//		}
//		
//		hConverters.remove(delIdx);
//		
//		
//		rmha.setMessageConverters(hConverters);
//		hConverters = rmha.getMessageConverters();
//		
//		for(HttpMessageConverter<?> converter : hConverters) {
//			System.out.println(converter.getClass().getName());
//		}
//		
//		System.out.println("===================HttpMessageConverters====================");
//		System.out.println("===================size====================" + hConverters.size());
		
		/* 커스터마이징된 MappingJackson2HttpMessageConverter 으로 교체 */
//		MappingJackson2HttpMessageConverter messageConverters = springContext.getBean(MappingJackson2HttpMessageConverter.class);
//		
//		rmha.setMessageConverters(messageConverters);
	}

}
