package gov.nist.healthcare.mu.mmlib.data;

import java.util.List;

import gov.nist.healthcare.mu.mmlib.data.hibernate.AppConfig;
import gov.nist.healthcare.mu.mmlib.data.hibernate.IHL7MessagesService;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DbDataSets implements IDataSets {
	public static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
			AppConfig.class);
	public List<String> getData(String element, String occurences, String datasource) {
		return ((IHL7MessagesService) context.getBean("service")).fetchData(element, occurences, datasource);
	}
}
