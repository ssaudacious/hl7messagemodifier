package gov.nist.healthcare.mu.mmlib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SampleData {
	public static String escapeHTML(String s) {
	    StringBuilder out = new StringBuilder(Math.max(16, s.length()));
	    for (int i = 0; i < s.length(); i++) {
	        char c = s.charAt(i);
	        if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
	            out.append("&#");
	            out.append((int) c);
	            out.append(';');
	        } else {
	            out.append(c);
	        }
	    }
	    return out.toString();
	}
	public static String message = null;
	public static String profile = null; 
	static {
		try {
			ClassLoader classLoader = SampleData.class.getClassLoader();
			Scanner sc;
			message = (sc = new Scanner(new File(classLoader.getResource("samplemessage.txt").getFile()))).useDelimiter("\\Z").next().replace("\r", "").replace("\f", "");
			sc.close();
			profile = (sc = new Scanner(new File(classLoader.getResource("sampleprofile.txt").getFile()))).useDelimiter("\\Z").next().replace("\r", "").replace("\f", "");
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	public static String element = "OBX-2";
	public static int occurrences = 10;
	public static String option = "AS ENTERED BELOW";

	
	
	public static void main(String[] args) throws FileNotFoundException {
		ClassLoader classLoader = SampleData.class.getClassLoader();
		@SuppressWarnings("resource")
		String inserts[] = 
				new Scanner(new File(classLoader.getResource("ddlinserts.sql").getFile())).useDelimiter("\\Z").next().
				replace("\r", "").split("\n");
		String inserts2[] = message.replaceAll("\\|", "\", \"").replaceAll("\n", "\"\\);\n").split("\n");
		
		for (String s2 : inserts2) {
			
			System.out.println(s2);
		}
		

		
		for (String s : inserts2) {
			int lastIndex = 0;
			int count =0;
			String findStr = "\", \"";
			s = "INSERT INTO SEGMENTS VALUES (NULL, 3, \"ds3\", \"" + s;
			while(lastIndex != -1){

			       lastIndex = s.indexOf(findStr,lastIndex);

			       if( lastIndex != -1){
			             count ++;
			             lastIndex+=findStr.length();
			      }
			}
			//System.out.println(count);
			String appendString = "";
			String prefixString = "(ID, MsgId, dataSource, SegName, ";
			count = count - 1; // discounting the SegName
			for (int i = 0; i < /*80 - */count; i++) {
				prefixString += "F" + (i + 1) +  ((i == (count - 1)) ? "" : ",");
				appendString += ", NULL";
			}
			
			//prefixString = prefixString.replace("\\,$", "");
			//System.out.println(appendString);
			//System.out.println(s.replace(");", appendString + ");")); 
			System.out.println(s.replace("SEGMENTS VALUES", "segments " + prefixString + ") VALUES")); 
				
		}
	}
	
}
