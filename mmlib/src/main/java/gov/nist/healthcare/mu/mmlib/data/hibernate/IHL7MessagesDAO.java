package gov.nist.healthcare.mu.mmlib.data.hibernate;

import java.util.List;

public interface IHL7MessagesDAO {
	@SuppressWarnings("rawtypes")
	public List fetchElementsFromDataSource(String element, String occurences, String datasource);
}