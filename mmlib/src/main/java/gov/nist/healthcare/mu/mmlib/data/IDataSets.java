package gov.nist.healthcare.mu.mmlib.data;

import java.util.List;

public interface IDataSets {
	public List<String> getData(String element, String occurences, String datasource);
}
