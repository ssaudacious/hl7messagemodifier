package gov.nist.healthcare.mu.mmlib.data.hibernate;

import java.util.List;


public interface IHL7MessagesService {
		public List<String> fetchData(String element, String occurences, String datasource);

}
