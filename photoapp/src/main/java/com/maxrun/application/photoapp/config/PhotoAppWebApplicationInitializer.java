package com.maxrun.application.photoapp.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;

/* web.xml */
/* WebApplicationInitializer 인터페이스는 servlet3 스텍을 지원하는 서블릿 컨테이너의 코드기반 설정을 찾아서 이를 사용하여 해당 컨테이너를 초기화해주는 MVC에서 제공하는 인터페이스이다*/
//public class SDWebApplicationInitializer implements WebApplicationInitializer {
/* AbstractAnnotationConfigDispatcherServletInitializer 는 WebApplicationInitializer를 구현한 기본구현체로서 단순히 서블릿매핑과 설정클래스만 리스트업해서 Dispatcher Servlet을 등록
 * 할 수 있기때문에 좀 더 권장되는 방법이다 */
public class PhotoAppWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	public PhotoAppWebApplicationInitializer() {
		System.out.println("SDWebApplicationInitializer created");
	}
	@Override
	protected Class<?>[] getRootConfigClasses() {
		// TODO Auto-generated method stub
		return new Class[] {PhotoAppRootConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		// TODO Auto-generated method stub
		return new Class[] {PhotoAppMVCConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		// TODO Auto-generated method stub
		return new String[] {"/"};
	}
	
	@Override
    protected FrameworkServlet createDispatcherServlet (WebApplicationContext wac) {
        DispatcherServlet ds = new DispatcherServlet(wac);
        ds.setDetectAllHandlerExceptionResolvers(false);

        
        return ds;
    }
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		
		/* web.xml없이  java config로 필터 등록*/
		FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
		encodingFilter.setInitParameter("encoding", "UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(null, true, "/*");
		
		FilterRegistration.Dynamic lucyXssFilter = servletContext.addFilter("lucyXssFilter", new XssEscapeServletFilter());
		lucyXssFilter.addMappingForUrlPatterns(null, true, "/*");
	}

//	@Override
//	public void onStartup(ServletContext servletContext) throws ServletException {
//		System.out.println("################## WebApplicationInitializer ####################");
//		System.out.println("################## WebApplicationInitializer ####################");
//		System.out.println("################## WebApplicationInitializer ####################");
//		System.out.println("################## WebApplicationInitializer ####################");
//		
//		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
//		rootContext.register(SDRootConfig.class);
//		//rootContext.setConfigLocation("com.sabang.application.sd.config.SDRootConfig");
//		
//		//rootContext.refresh();
//		Collection<ApplicationListener<?>> cols = rootContext.getApplicationListeners();
//
//		System.out.println("listner count==>" + cols.size());
//		System.out.println("listner count==>" + cols.size());
//		System.out.println("listner count==>" + cols.size());
//		System.out.println("listner count==>" + cols.size());
//		System.out.println("listner count==>" + cols.size());
//		System.out.println("listner count==>" + cols.size());
//		
//		servletContext.addListener(new ContextLoaderListener(rootContext));
//		
//		
//		AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
//      	ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(webContext));
//      	webContext.register(SDWebMVCConfig.class);
//      
//      	dispatcher.setLoadOnStartup(1);
//      	dispatcher.addMapping("/");
//	}
}
