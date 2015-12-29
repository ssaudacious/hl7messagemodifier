package gov.nist.healthcare.mu.mmlib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nist.healthcare.core.hl7.v2.instance.impl.Message;
import gov.nist.healthcare.core.hl7.v2.model.ProfileModel;


/* allows converting from different syntaxes of location */

/* two syntaxes are currently supported
 * 
 * Message Syntax:    SEG#n-X#m.Y#l   where numbers after # are cardinality
 * Profile based Syntax:  ./n[m]/..
 * 
 */
public class MessageLocation {
		public Message msg = null;
		public int componentNum;
		public int fieldNum;
		public String segmentName;
		public String fullSegmentName;
		public String root;
		
		//TODO
		public static String getMsgFromProfileSyntax(Message m, String s) {
			return s;
		}
		
		//TODO
		public static String getProfileFromMsgSyntax(Message m, String s) {
			return s;
		}

		public static boolean isProfileSyntax(String s) {
			return true;
		}
		
		public static String getSegmentName(Message m, String loc) {
			String msgSyntaxLoc = loc;
			
			if (isProfileSyntax(loc))
				msgSyntaxLoc = getMsgFromProfileSyntax(m, loc);
			
			int segBeginsHere = msgSyntaxLoc.lastIndexOf(":") + 1;
			msgSyntaxLoc = msgSyntaxLoc.substring(segBeginsHere);
			int len = (msgSyntaxLoc.length() >= 3 ? 3 : msgSyntaxLoc.length());
			return  msgSyntaxLoc.substring(0, len);
		}

		public static String getFullSegmentName(Message m, String loc) {
			String msgSyntaxLoc = loc;
			
			if (isProfileSyntax(loc))
				msgSyntaxLoc = getMsgFromProfileSyntax(m, loc);
			
			int segBeginsHere = msgSyntaxLoc.lastIndexOf(":") + 1;
			msgSyntaxLoc = msgSyntaxLoc.substring(segBeginsHere);
			int segEndsHere = msgSyntaxLoc.lastIndexOf('-');
			return msgSyntaxLoc.substring(0, segEndsHere == -1 ? msgSyntaxLoc.length() : segEndsHere);
		}

		public static String getFieldNum(Message m, String loc) {
			String msgSyntaxLoc = loc;
			
			if (isProfileSyntax(loc))
				msgSyntaxLoc = getMsgFromProfileSyntax(m, loc);
			
			int segBeginsHere = msgSyntaxLoc.lastIndexOf(":") + 1;
			msgSyntaxLoc = msgSyntaxLoc.substring(segBeginsHere);
			msgSyntaxLoc = msgSyntaxLoc.replace("#[0-9]+\\.", ".");
			int fieldEndsHere = msgSyntaxLoc.indexOf(".");
			if (fieldEndsHere <= 3)
				fieldEndsHere = msgSyntaxLoc.length();
			msgSyntaxLoc = msgSyntaxLoc.substring(msgSyntaxLoc.indexOf("-") + 1, fieldEndsHere);
			return msgSyntaxLoc;
		}

		public static String getComponentNum(Message m, String loc) {
			String msgSyntaxLoc = loc;
			
			if (isProfileSyntax(loc))
				msgSyntaxLoc = getMsgFromProfileSyntax(m, loc);
			
			int segBeginsHere = msgSyntaxLoc.lastIndexOf(":") + 1;
			msgSyntaxLoc = msgSyntaxLoc.substring(segBeginsHere);
			msgSyntaxLoc = msgSyntaxLoc.replace("#[0-9]+\\.", ".");
			int fieldEndsHere = msgSyntaxLoc.indexOf(".");
			if (fieldEndsHere >= 3)
				msgSyntaxLoc = msgSyntaxLoc.substring(fieldEndsHere + 1);
			else
				msgSyntaxLoc = "";
			return msgSyntaxLoc;
		}
		
		public static String getRoot(Message m, String loc) {
			String msgSyntaxLoc = loc;
			
			if (isProfileSyntax(loc))
				msgSyntaxLoc = getMsgFromProfileSyntax(m, loc);
			
			int segBeginsHere = msgSyntaxLoc.lastIndexOf(":") + 1;
			if (segBeginsHere == 0)
				return "MSH";
			else
				return msgSyntaxLoc.substring(0, segBeginsHere - 1);
		}		
		
		public MessageLocation(String element, Message m) {
			this.msg = m;
			this.componentNum = Constants.getInt(getComponentNum(m, element));
			this.fieldNum = Constants.getInt(getFieldNum(m, element));
			this.segmentName = getSegmentName(m, element);
			this.fullSegmentName = getFullSegmentName(m, element);
			this.root = getRoot(m, element);
		}
		
		
		
		/* getAllSegments: returns an array of strings with segment location information */
		public static List<String> getAllSegments(String message) {
			List<String> segs = new ArrayList<String>();
			Map<String, Integer> segcounts = new HashMap<String, Integer>();
			
			BufferedReader in = new BufferedReader(new StringReader(message));
			String line;
			try {
				while ((line = in.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty()) {
						String seg = line.substring(0, 3);
						int repeat = segcounts.containsKey(seg) ? segcounts.get(seg) : 0; // 0 if not found?
						segcounts.put(seg, ++repeat);
						System.out.println("Putting: " + seg + " " + segcounts.get(seg));
						segs.add(seg + (repeat == 1 ? "" : ("#" + repeat)));
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return segs;
		}
		
		public static void main(String[] args) {
			System.out.println("Segment should be MSH ---> " + getSegmentName(null, "ABC:MSH#4-5.4"));
			System.out.println("Full Segment should be MSH#4 ---> " + getFullSegmentName(null, "ABC:MSH#4-5.4"));
			System.out.println("Field should be 5 ---> " + getFieldNum(null, "ABC:MSH#4-5.4"));
			System.out.println("Component should be 4 ---> " + getComponentNum(null, "ABC:MSH#4-5.4"));
			
			String msg = "MSH|\nOBR|||||\nOBX|||||\nOBX|||||||\n\nOBX||||||";
			
			for (String s: getAllSegments(msg)) 
				System.out.println(s);
		}
		
}
