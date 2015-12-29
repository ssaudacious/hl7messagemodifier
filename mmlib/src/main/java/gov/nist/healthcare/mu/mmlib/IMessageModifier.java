package gov.nist.healthcare.mu.mmlib;

import gov.nist.healthcare.core.hl7.v2.instance.Element;
import gov.nist.healthcare.core.hl7.v2.instance.impl.Message;
import gov.nist.healthcare.core.hl7.v2.model.ElementModel;
import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.core.hl7.v2.parser.ParserImpl;
import gov.nist.healthcare.mu.mmlib.util.Constants;
import gov.nist.healthcare.mu.mmlib.util.MessageLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


//
public abstract class IMessageModifier {
		public abstract String transformMessageUsingScript(String message, String transformScript);
		public abstract List<String> getDataFromDataSource(String element, String occurences, String datasource, String custdata);
		public String setElementFromDataSource(String message, /* the message that is being operated */
											   String profile, /* the profile that is used to get cardinality, length etc. */
											   String operation, /* cardinality? length? */
											   String mode, /* MAX? MIN? RND? EXACT? */
											   String element, /* element, ALL FIELDS, ALL SEGMENTS */ 
											   String limit,  /* EXACT or for MAX/MIN/RND limits */ 
											   String datasource,
											   String customvalue    /* in case the datasource="custom" */) throws ParserException { /* a get from datasource and setElement */
			// if element = "ALL SEGMENTS" or "ALL FIELDS" iterate over the following
			 int imode = 
					 mode.indexOf("RANDOM") != -1 || mode.equals(Constants.RANDOM + "") ? Constants.RANDOM : 
						 mode.indexOf("MAX") != -1  || mode.equals(Constants.MAX + "") ? Constants.MAX :
							 mode.indexOf("MIN") != -1 || mode.equals(Constants.MIN + "")  ?  Constants.MIN :
								 mode.indexOf("AS ENTERED") != -1 || mode.equals(Constants.EXACT + "") ? Constants.EXACT : Constants.EXACT;
			
			if (element.startsWith("All ")) 
				return setAllElements(message, profile, Constants.getInt(limit), 
						imode, 
						element.endsWith("Fields") ? Constants.ALLFIELDS : Constants.ALLSEGMENTS, 
						datasource, 
						customvalue);
			
			// if mode = max or min = extract the profile value and bound it by limit (or if random)
			int ilimit = getProfileLimit(message, profile, element, imode, limit);
			System.out.println("Got the limit as: " + ilimit);
			List<String> rs = getDataFromDataSource(element, ilimit + "", datasource, customvalue);
			for (String r : rs) {
				message = setElementInMessage(message, element, r);
			}
			return message;
		}
		
		public int getProfileLimit(String message, String profile, String element, int imode, String limit) throws ParserException {
			ParserImpl parser = new ParserImpl();
			Message m = (Message) parser.parse(message, profile);
			MessageLocation mloc = new MessageLocation(element, m);
			List<Element> seglist = m.getSegmentList();
			Map<String, Integer> segcount = new HashMap<String, Integer>();
			String root = mloc.root;
			
			// find the element by circling through the seglist
			boolean foundroot = false;
			
			for (Element seg : seglist) {
				Integer sc = segcount.get(seg.getName());
				if (sc != null && sc != 0) {
					segcount.put(seg.getName(), sc + 1);
					sc++;
				}
				else {
					sc = 1;
					segcount.put(seg.getName(), sc);
				}
				
				// we have the seg#sc
				if (!foundroot && !root.isEmpty() && !root.equals("MSH")) {
					if (root.equals(seg.getName() + "#" + sc) || (sc == 1 && root.equals(seg.getName()))) { 
						segcount = new HashMap<String, Integer>(); // reset
 						foundroot = true;
					}
					continue;
				}
				
				if (mloc.fullSegmentName.equals(seg.getName() + "#" + sc) || (sc == 1 && mloc.fullSegmentName.equals(seg.getName()))) {
					if (mloc.fieldNum == 0)
						return getLimit(seg.getMinOccurs(), seg.getMaxOccurs(), Constants.getInt(limit), imode);
					
					int index = 1;
					for (Field f : getFieldList(m, seg, "#" + sc)) {
						if (mloc.fieldNum == index)
							return getLimit(f.minOccurs, f.maxOccurs, Constants.getInt(limit), imode);
						index++;
					}
				}
			}
			return Constants.getInt(limit);
		}
	
		// what if exact == over the limit?
		public int getLimit(int min, int max, int limit, int imode) {
			Random rnd = new Random();
			limit = imode == 0 ? limit : imode == -1 ? min : imode == +1 ? (max > 65000 ? limit : max) : imode == 1000 ?
					(min + rnd.nextInt(max - min + 1)) : limit;								
			return limit;
		}
		
		
		public class Field {
			String fieldName;
			Integer minOccurs;
			Integer maxOccurs;
		}
		
		public List<Field> getFieldList(Message m, Element seg, String suffix) throws ParserException {
			List<Field> fs = new ArrayList<Field>();
			
			TreeMap<Integer, List<Element>> fields = seg.getChildren();	
			
			if (fields == null)
				return fs;
			
			Integer max = 0;
			for (Integer i : fields.keySet())
				if (i > max)
					max = i;
			System.out.println(seg.getName() + " Line: " + seg.getLineNumber());
			ElementModel segmodel = seg.getModel();
			List<ElementModel> fieldmodel = segmodel.getChildrenModel();
			
			for (Integer i = 0; i <= max; i++) {
				List<Element> val = fields.get(i);
				Field f = this.new Field();
				if (val != null) {
					Element field0 = (Element) val.get(0);
					System.out.println(seg.getName() + "-" + (i+1) + " Min: " 
							+ field0.getModel().getMinOccurs() + " Max: " 
							+ field0.getModel().getMaxOccurs());
					f.minOccurs = field0.getModel().getMinOccurs();
					f.maxOccurs = field0.getModel().getMaxOccurs();
				}
				else {
					System.out.println("From model: " + 
							" Min: " + fieldmodel.get(i).getMinOccurs() + 
							" Max: " + fieldmodel.get(i).getMaxOccurs()
							);
					
					f.minOccurs = fieldmodel.get(i).getMinOccurs();
					f.maxOccurs = fieldmodel.get(i).getMaxOccurs();
				}
				
				f.fieldName = seg.getName() + suffix + "-" + (i+1);
				fs.add(f);
			}
			return fs;
		}
		
		
		public abstract String setElementInMessage(String message, String element, String value);
		public abstract String setAllElements(String message, String profile, int limit, 
				int mode /* -1 min 0 exact +1 max +1000 random */, 
				int level /* 0: message 1: seggroup 2 - segment; 3 - field;*/,
				String datasource,
				String customValue);
		
	
}
