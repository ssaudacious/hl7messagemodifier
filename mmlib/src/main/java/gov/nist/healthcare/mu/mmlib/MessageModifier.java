/**
 * 
 */
package gov.nist.healthcare.mu.mmlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import gov.nist.healthcare.core.hl7.v2.instance.Element;
import gov.nist.healthcare.core.hl7.v2.instance.impl.Message;
import gov.nist.healthcare.core.hl7.v2.model.ElementModel;
import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.core.hl7.v2.parser.ParserImpl;
import gov.nist.healthcare.core.hl7.v2.parser.util.ModelFinder;
import gov.nist.healthcare.mu.mmlib.data.DbDataSets;
import gov.nist.healthcare.mu.mmlib.data.IDataSets;
import gov.nist.healthcare.mu.mmlib.util.Constants;
import gov.nist.healthcare.mu.mmlib.util.MessageLocation;
import gov.nist.healthcare.mu.mmlib.util.SampleData;

import org.immunizationsoftware.dqa.transform.ModifyMessageRequest;
import org.immunizationsoftware.dqa.transform.ModifyMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sna10
 *
 */

public class MessageModifier extends IMessageModifier {
	private static final Logger logger = LoggerFactory.getLogger(MessageModifier.class);
	
	@Override
	public String transformMessageUsingScript(String message, String transformScript) {
		//String transform = "select scenario NIST MU2.1.1 - Admin Child";
		ModifyMessageService m = new ModifyMessageService();
		ModifyMessageRequest req = new ModifyMessageRequest();
		
		req.setMessageOriginal(message);
		req.setTransformScript(transformScript);
		m.transform(req);
		return /*"\n******************* Transformed ************************** with " + transformScript + "\n" +*/ req.getMessageFinal();
	}
	
	/* get data: gets a single string for sub-segment level data */
	/* for segment level, gets a List of Strings */
	@Override 
	public List<String> getDataFromDataSource(String element, String occurrences, String datasource, String custvalue) {
		//IDataSets ds = new MockDataSets();
		//IDataSets ds = new ArrayDataSets();
		
		// if custdata replicate and return
		if (datasource.equals("cust")) {
			MessageLocation mx = new MessageLocation(element, null);
			List<String> ss = new ArrayList<String>();
			String s = "";
			
			for (int i = 0; i < Constants.getInt(occurrences); i++) {
				if (mx.fieldNum == 0) 
					ss.add(custvalue); 
				else
					s += (s.isEmpty() ? "" : Constants.repeater) + custvalue; // todo get this value form some place
			}
			
			if (mx.fieldNum != 0)
				ss.add(s);
			return ss;
		}
		IDataSets ds = new DbDataSets();
		return ds.getData(element, occurrences, datasource);
	}
	
	// sets the element in the message
	// if the element is of the type segment, inserts a segment
	// todo: parametrize the insertion of the segment
	public String setElementInMessage(String message, String element, String value) {
		MessageLocation msx = new MessageLocation(element, null);
		if (msx.fieldNum == 0) { //inserting a segment
			message = transformMessageUsingScript(message, "insert segment " + msx.segmentName + " after " + msx.root);
			logger.info("insert segment " + msx.segmentName + " after " + msx.root);
			element = msx.root + ":" + msx.segmentName + "-1";
		}
		String transformScript = element + "=" + value;
		logger.info("Transform script = " + transformScript);
		return transformMessageUsingScript(message, transformScript);
	}

	@Override
	public String setAllElements(String message, String profile, int limit, int mode, int level, String datasource, String customvalue) {
		// TODO Auto-generated method stub
		switch (level) {
			case Constants.ALLFIELDS:
			try {
				return setAllFieldElements(message, profile, limit, mode, datasource, customvalue);
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "ERR|Unable to parse message|ParserException";
			}
			case Constants.ALLSEGMENTS:
				try {
					return setAllSegmentElements(message, profile, limit, mode, datasource, customvalue);
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "ERR|Unable to parse message|ParserException";
				}
		}
		return null;
	}
	

	public String setAllFieldElements(String message, String profile, int limit, int mode, String datasource, String customvalue) throws ParserException {
		//List<String> segs = MessageLocation.getAllSegments(message);
		ParserImpl parser = new ParserImpl();
		Message m = (Message) parser.parse(message, profile);
		List<Element> seglist = m.getSegmentList();
		Map<String, Integer> segcount = new HashMap<String, Integer>();
		
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
			
			for (Field f : getFieldList(m, seg, "#" + sc)) {
				if (f.fieldName.equals("MSH-1") || f.fieldName.equals("MSH#1-1"))
					continue; // special field
				if (f.fieldName.equals("MSH-2") || f.fieldName.equals("MSH#1-2"))
					continue; // special field
				
				limit = 
						mode == Constants.EXACT ? limit :  // exact 
						mode == Constants.MIN ? 	f.minOccurs  : // min 
						mode == Constants.MAX ? (f.maxOccurs > Constants.UNLIMITED ? limit : f.maxOccurs) // use limit in case of * 
						: limit;
				if (mode ==  Constants.RANDOM) {
					Random rnd = new Random();
					Integer maxOccurs = f.maxOccurs > Constants.UNLIMITED ? limit : f.maxOccurs;
					if (maxOccurs < f.minOccurs)
						maxOccurs = f.minOccurs;
					limit = f.minOccurs + rnd.nextInt(maxOccurs - f.minOccurs + 1);
				}
					
				message = setElementFromDataSource(message, profile, "CARD" , mode + "", f.fieldName, limit + "", datasource, customvalue); 
			}
			//message = set(element, max(profilemodel.limit, limit), mode);
		}
		
		return message;
	}


	public String setAllSegmentElements(String message, String profile, int limit, int mode, String datasource, String customvalue) throws ParserException {
		//List<String> segs = MessageLocation.getAllSegments(message);
		ParserImpl parser = new ParserImpl();
		Message m = (Message) parser.parse(message, profile);
		List<Element> seglist = m.getSegmentList();
		Map<String, Integer> segcount = new HashMap<String, Integer>();
		List<String> seglistlocations = new ArrayList<String>();
		for (Element seg : seglist) {
			Integer sc = segcount.get(seg.getName());
			if (sc != null && sc != 0) {
				segcount.put(seg.getName(), ++sc);
			}
			else {
				sc = 1;
				segcount.put(seg.getName(), sc);
			}
			seglistlocations.add(seg.getName() + "#" + sc);
		}
		
		Collections.reverse(seglistlocations);
		Collections.reverse(seglist);
		
		int nseg = seglist.size();
		int i = 0, curcard = limit;
		for (String seg : seglistlocations) {
			Element curseg = seglist.get(i++);
			String loc = (i < seglist.size() ? (seglist.get(i).getName() + ":") : "") + seg.substring(0, seg.length() >= 3 ? 3 : seg.length());
			if (curseg.getInstanceNumber() == 1) {
				curcard = getCurrentCardinality(curseg);
			}
			else
				continue;
			int ilimit = getProfileLimit(message, profile, loc, mode, limit + "");
			System.out.println("Got the limit as: " + ilimit);
			System.out.println("Got the current card  as: " + curcard);
			message = setElementFromDataSource(message, profile, "CARD", Constants.EXACT + "", loc,  (ilimit - curcard) + "", datasource, customvalue);			
		}
			
		return message;
	}
	
	
	public static int getCurrentCardinality(Element seg) {
		Object par = seg.getParent();
					
		Map<Integer, List<Element>> children = 
									par instanceof Element ? ((Element)par).getChildren() :
									par instanceof Message ? ((Message)par).getChildren() :
										null;
		if (children != null) {
			for (List<Element> el : children.values()) {
				for (Element e : el)
					if (e == seg) {
						System.out.println("getCurrentCardinality: Found e: " + e.getName() + " " + el.size());
						return el.size();
					}
			}
		}
		
		return 0;
	}
	
	public static void main(String[] args) throws ParserException {
		ParserImpl parser = new ParserImpl();
		ModelFinder finder = new ModelFinder();
		Message m = (Message) parser.parse(SampleData.message, SampleData.profile);
		List<Element> seglist = m.getSegmentList();
		
		for (Element seg : seglist) {
			TreeMap<Integer, List<Element>> fields = seg.getChildren();	
			Integer max = 0;
			for (Integer i : fields.keySet())
				if (i > max)
					max = i;
			System.out.println(seg.getName() + " Line: " + seg.getLineNumber() + " Current cardinality: " + getCurrentCardinality(seg));
			if (true)
				continue;
			ElementModel segmodel = seg.getModel();
			List<ElementModel> fieldmodel = segmodel.getChildrenModel();
			for (Integer i = 0; i <= max; i++) {
				List<Element> val = fields.get(i);
				
				if (val != null) {
					Element field0 = (Element) val.get(0);
					System.out.println(seg.getName() + "-" + i + " Min: " 
							+ field0.getModel().getMinOccurs() + " Max: " 
							+ field0.getModel().getMaxOccurs() 
							+ field0.getModel().getName());
				}
				else {
					System.out.println("From model: " + 
							" Min: " + fieldmodel.get(i).getMinOccurs() + 
							" Max: " + fieldmodel.get(i).getMaxOccurs()
							+ fieldmodel.get(i).getName()
							);
					
				}
			}
		}
	}
	

}
