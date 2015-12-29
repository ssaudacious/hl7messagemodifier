package gov.nist.healthcare.mu.mmlib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ArrayDataSets implements IDataSets {
	HashMap<String, HashMap<String, String>> dss = new HashMap<String, HashMap<String, String>>();
	HashMap<String, String> ds1 = new HashMap<String, String>();
	HashMap<String, String> ds2 = new HashMap<String, String>();
	HashMap<String, String> ds3 = new HashMap<String, String>();
	HashMap<String, String> ds4 = new HashMap<String, String>();
	
	{
		dss.put("ds1", ds1);
		dss.put("ds2", ds2);
		dss.put("ds3", ds3);
		dss.put("ds4", ds4);
		
		
		ds1.put("OBX-2", "ds1OBX2");
		ds2.put("OBX-2", "ds2OBX2");
		ds3.put("OBX-2", "ds3OBX2");
		ds4.put("OBX-2", "ds4OBX2");
	}
	
	public List<String> getData(String element, String occurences, String datasource) {
		List<String> rs = new ArrayList<String>();
		rs.add(new String(new char[Integer.parseInt(occurences)]).replace("\0", dss.get(datasource).get(element) + "~"));
		return rs;
	}
}
