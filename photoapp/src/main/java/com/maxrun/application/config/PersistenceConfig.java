package com.maxrun.application.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan(basePackages = {"com.maxrun"})
public class PersistenceConfig{

	@Autowired
	ApplicationContext applicationContext;
//	연결 URL의 일반적인 형식은 다음과 입니다.
//	jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
//	jdbc:sqlserver:// (필수) - 하위 프로토콜이라고 하며 상수입니다.
//	serverName (선택 사항) - 연결할 서버의 주소입니다. 이 주소는 DNS 또는 IP 주소이거나 로컬 컴퓨터일 수 있습니다 localhost127.0.0.1 . 연결 URL에 지정하지 않은 경우 속성 컬렉션에 서버 이름을 지정해야 합니다.
//	instanceName (선택 사항) - 연결할 serverName인스턴스입니다. 지정하지 않으면 기본 인스턴스에 대한 연결이 만들어집니다.
//	portNumber (선택 사항) - 연결할 serverName포트입니다. 기본값은 1433입니다. 기본 포트를 사용하는 경우 포트나 URL의 앞부분에 있는 포트를 : 지정할 필요가 없습니다.
	@Bean(destroyMethod="close")
	public DataSource getDataSource() {
	    HikariConfig config = new HikariConfig();
		
//	    config.setMaximumPoolSize(10);
//	    config.setDataSourceClassName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
//	    config.addDataSourceProperty("serverName","DESKTOP-J4L2EEK");
//	    config.addDataSourceProperty("portNumber", 1433);
//	    config.addDataSourceProperty("encrypt", false);	//실제 SSL인증서가 존재하는 경우 이 설정은 제거하도록 할 것
//	    config.addDataSourceProperty("databaseName", "MAX-RUN");
//	    config.addDataSourceProperty("user", "sa");
//	    config.addDataSourceProperty("password", "object_1@@4");
	    
//	    config.setMaximumPoolSize(10);
//	    config.setDataSourceClassName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
//	    config.addDataSourceProperty("serverName","sql16ssd-017.localnet.kr");
//	    config.addDataSourceProperty("portNumber", 1433);
//	    config.addDataSourceProperty("encrypt", false);	//실제 SSL인증서가 존재하는 경우 이 설정은 제거하도록 할 것
//	    config.addDataSourceProperty("databaseName", "gildonghon_sd1004");
//	    config.addDataSourceProperty("user", "gildonghon_sd1004");
//	    config.addDataSourceProperty("password", "aoekf1djrdltkd");
	    
	    config.setMaximumPoolSize(10);
	    config.setDataSourceClassName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
	    config.addDataSourceProperty("serverName","sql16ssd-006.localnet.kr");
	    config.addDataSourceProperty("portNumber", 1433);
	    config.addDataSourceProperty("encrypt", false);	//실제 SSL인증서가 존재하는 경우 이 설정은 제거하도록 할 것
	    config.addDataSourceProperty("databaseName", "maxrundb_admin");
	    config.addDataSourceProperty("user", "maxrundb_admin");
	    config.addDataSourceProperty("password", "maxrun!!");

	    return new HikariDataSource(config);  //pass in HikariConfig to HikariDataSource
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
	    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
	    factoryBean.setDataSource(getDataSource());
	    factoryBean.setConfigLocation(applicationContext.getResource("classpath:/sqlmap/config/mybatis-config.xml"));
	    factoryBean.setMapperLocations(applicationContext.getResources("classpath:/sqlmap/mappers/*.xml"));
	    return factoryBean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}