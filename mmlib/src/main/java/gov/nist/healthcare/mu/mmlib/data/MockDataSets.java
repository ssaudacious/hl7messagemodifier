package gov.nist.healthcare.mu.mmlib.data;

import java.util.ArrayList;
import java.util.List;

class MockDataSets implements IDataSets {
	public List<String> getData(String element, String occurences, String datasource) {
		List<String> rs = new ArrayList<String>();
		rs.add(datasource + "@" + new String(new char[Integer.parseInt(occurences)]).replace("\0", element + "~"));
		return rs;
	}
}
