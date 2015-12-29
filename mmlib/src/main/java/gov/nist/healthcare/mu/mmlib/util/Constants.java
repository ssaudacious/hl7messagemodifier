package gov.nist.healthcare.mu.mmlib.util;

public class Constants {
	public static String repeater = "~";
	public static String fs = "|";
	
	
	// should we do an enum?
	// Selection values for Location 
	public final static int ALLSUBCOMPONENTS = 5;
	public final static int ALLCOMPONENTS = 4;
	public final static int ALLFIELDS = 3;
	public final static int ALLSEGMENTS = 2;
	public final static int ALLMESSAGEELEMENTS = 1;

	
	// Selection values for Cardinality parameters
	public final static int MIN = -1;
	public final static int MAX = 1;
	public final static int EXACT = 0;
	public final static int RANDOM = 1000;
	
	
	
	public final static int UNLIMITED = 65000; // should it be 65536
	
	
	public static int getInt(String s) {
		try {
			return Integer.parseInt(s);
		} 
		catch (Exception e) {
			// log message here
		}
		
		return 0;
	}
	
}
