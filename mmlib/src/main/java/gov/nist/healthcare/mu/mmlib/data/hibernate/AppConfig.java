package gov.nist.healthcare.mu.mmlib.data.hibernate;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
 
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:db/db.properties")
@ComponentScan(basePackages = "gov.nist.healthcare.mu.mmlib.data", excludeFilters = { @ComponentScan.Filter(Configuration.class) })
public class AppConfig {
	@Autowired
	Environment env;
 
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(env.getProperty("jdbc.databaseurl"));
		dataSource.setUsername(env.getProperty("jdbc.username"));
		dataSource.setPassword(env.getProperty("jdbc.password"));
		return dataSource;
	}
 
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory() throws Exception {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", env.getProperty("jdbc.dialect"));
		properties.put("hibernate.show_sql", "true");
		properties.put("current_session_context_class", "thread");
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setPackagesToScan(new String[] { "gov.nist.healthcare.mu.mmlib.data" });
		factory.setDataSource(getDataSource());
		factory.setHibernateProperties(properties);
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager() throws Exception {
		return new HibernateTransactionManager(getSessionFactory());
	}
	@Bean(name="service")
	public IHL7MessagesService service(){
		return new HL7MessagesService();
	}	
		
}