package gov.nist.healthcare.messagemodifier.mu;

import gov.nist.healthcare.core.hl7.v2.enumeration.ElementType;
import gov.nist.healthcare.core.hl7.v2.instance.DataElement;
import gov.nist.healthcare.core.hl7.v2.instance.Element;
import gov.nist.healthcare.core.hl7.v2.instance.impl.AbstractDataElement;
import gov.nist.healthcare.core.hl7.v2.instance.impl.AbstractElement;
import gov.nist.healthcare.core.hl7.v2.instance.impl.Component;
import gov.nist.healthcare.core.hl7.v2.instance.impl.Field;
import gov.nist.healthcare.core.hl7.v2.instance.impl.Group;
import gov.nist.healthcare.core.hl7.v2.instance.impl.Message;
import gov.nist.healthcare.core.hl7.v2.instance.impl.MessageDelimiters;
import gov.nist.healthcare.core.hl7.v2.instance.impl.Segment;
import gov.nist.healthcare.core.hl7.v2.instance.impl.SubComponent;
import gov.nist.healthcare.core.hl7.v2.model.ComponentModel;
import gov.nist.healthcare.core.hl7.v2.model.ElementModel;
import gov.nist.healthcare.core.hl7.v2.model.FieldModel;
import gov.nist.healthcare.core.hl7.v2.model.GroupModel;
import gov.nist.healthcare.core.hl7.v2.model.MessageModel;
import gov.nist.healthcare.core.hl7.v2.model.SegmentModel;
import gov.nist.healthcare.core.hl7.v2.model.SubComponentModel;
import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.core.hl7.v2.parser.ParserImpl;
import gov.nist.healthcare.core.hl7.v2.parser.util.ModelFinder;
import gov.nist.healthcare.core.hl7.v2.util.LocationManager;
import gov.nist.healthcare.messagemodifier.mu.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class ModifiableMessage implements IModifiableMessage {
	/**
	 * 
	 */
	private final long serialVersionUID = -3190760638498853230L;

	ParserImpl parser = new ParserImpl();
	ModelFinder finder = new ModelFinder();
	Message m = null;
	boolean debugTrace = true;
	MessageDelimiters md = new MessageDelimiters();

	/*
	 * ModifiableMessage
	 * 
	 * @param profile
	 * 
	 * @param er7
	 * 
	 * @param isFileName determines whether the above strings are names or
	 * contents of the files
	 */
	public ModifiableMessage(String profile, String er7, boolean isFileName)
			throws ParserException {
		if (isFileName) {
			File fProfile = new File(profile);
			File m0 = new File(er7);
			m = (Message) parser.parse(m0, fProfile);
		} else
			// the strings are the contents of the profile and Er7 message
			m = (Message) parser.parse(er7, profile);

		// default values; later when the message is read, it is set from the
		// MSH
		setMessageDelimiters("^~\\&#");
		md.setFieldSeparator("|");
	}

	public String setData(String param1, String param2) {
		printEr7(m);
		apply(param1, param2,  "SET");
		return printEr7(m);
	}

	public String copyData(String param1, String param2) {
		printEr7(m);
		apply(param1, param2, "COPY");
		return printEr7(m);
	}

	public String moveData(String param1, String param2) {
		printEr7(m);
		apply(param1, param2, "MOVE");
		return printEr7(m);
	}

	public String replaceData(String param1, String param2) {
		printEr7(m);
		apply(param1, param2,  "REPLACE");
		return printModifiedEr7(m,param1,param2);
	}

	Element getElement(String param) {
		Pair<Element, String> newParam = getContext(m, param);  // 229
		Element e = LocationManager.resolve(newParam.getFirst(),
				newParam.getSecond(), null);
		return e;

	}

	void apply(String param1, String param2, String op) {
		if (op.equals("SET"))
			setData(getElement(param1), param2);  //100 - get Element, 118 - setData
		else if (op.equals("MOVE"))
			moveData(getElement(param1), getElement(param2));
		else if (op.equals("COPY"))
			copyData(getElement(param1), getElement(param2));
		else if (op.equals("REPLACE"))
			//	replaceDat(param1, param2,param3);
			searchER7(null,param1,param2);
	}

	/*void apply(String param1, String param2, String param3, String op){
		if (op.equals("REPLACE"))
			replaceDat(param1,param2,param3);
	}*/

	void setData(Element e, String param) {
		if (e != null) {
			dbgPrintln("Found the element= " + e.getName());
			if (e instanceof AbstractDataElement) {
				((AbstractDataElement) e).setValue(param);
				dbgPrintln("Setting data");
			}
		} else
			dbgPrintln("No element at " + param);
	}


	/*String replaceDat(String para1,String para2, String para3){

		CharSequence s1 = para1;
		CharSequence s2 = para2;
		// replace sequence s1 with s2
		String replaceStr = para3.replace(s1, s2);

		System.out.println(replaceStr);
		return replaceStr;

	}*/
	private void moveData(Element e1, Element e2) {
		if (e1 != null) {
			Object _par = e1.getParent();
			TreeMap<Integer, List<Element>> children = null;
			if (_par instanceof Message)
				children = ((Message) _par).getChildren();
			else if (_par instanceof Element)
				children = ((Element) _par).getChildren();
			else {
				dbgPrintln("Unknown parent type "
						+ _par.getClass().getCanonicalName());
				return;
			}

			for (List<Element> le : children.values()) {
				if (le != null)
					for (int i = 0; i < le.size(); i++)
						if (le.get(i) == e1) {// put a hole
							le.remove(i);
							break;
						}
			}

			Object _par2 = e2.getParent();
			TreeMap<Integer, List<Element>> children2 = null;
			if (_par2 instanceof Message)
				children2 = ((Message) _par2).getChildren();
			else if (_par2 instanceof Element)
				children2 = ((Element) _par2).getChildren();
			else {
				dbgPrintln("Unknown parent type "
						+ _par2.getClass().getCanonicalName());
				return;
			}

			for (List<Element> le : children2.values()) {
				if (le != null)
					for (int i = 0; i < le.size(); i++)
						if (le.get(i) == e2) {// put a hole
							le.set(i, e1);
							dbgPrintln("Setting element in the position " + i);
							break;

						}


			}
		}
	}

	private void copyData(Element e1, Element e2) {
		if (e1 != null) {
			Object _par = e1.getParent();
			TreeMap<Integer, List<Element>> children = null;
			if (_par instanceof Message)
				children = ((Message) _par).getChildren();
			else if (_par instanceof Element)
				children = ((Element) _par).getChildren();
			else {
				dbgPrintln("Unknown parent type "
						+ _par.getClass().getCanonicalName());
				return;

			}

			for (List<Element> le : children.values()) {
				if (le != null)
					for (int i = 0; i < le.size(); i++)
						if (le.get(i) == e1) {// put a hole
							le.remove(i);
							Element e3 = clone(e1);
							le.add(e3);
							break;
						}
			}

			Object _par2 = e2.getParent();
			TreeMap<Integer, List<Element>> children2 = null;
			if (_par2 instanceof Message)
				children2 = ((Message) _par2).getChildren();
			else if (_par2 instanceof Element)
				children2 = ((Element) _par2).getChildren();
			else {
				dbgPrintln("Unknown parent type "
						+ _par2.getClass().getCanonicalName());
				return;
			}



			for (List<Element> le : children2.values()) {
				if (le != null)
					for (int i = 0; i < le.size(); i++)
						if (le.get(i) == e2) {// put a hole
							le.set(i, e1);
						//	((DataElement)le.get(i)).setValue("test");
							dbgPrintln("Setting element in the position " + i);
							break;
						}


			}
		}
	}

	// Locationfinder does not operate from root; it operates relative to
	// the context
	// the following code strips the param1's initial path and makes it
	// relative to the first part
	// for instance ./1[2]/2 becomes ./2 relative to (1,2) newParam1 = ./2
	// dim1 = 1 and dim2 = 2-1 = 1
	public Pair<Element, String> getContext(Message m, String path) {
		Integer dim1 = 1, dim2 = 0;
		String newParam1 = "";

		if (path.startsWith("./")) {
			String[] parts = path.split("/");
			if (parts.length > 1) {
				String dims = parts[1];
				dim1 = Integer.parseInt(dims);
				if (dims.endsWith("]"))
					dim2 = Integer.parseInt(dims.replace(".*\\[", "")) - 1;

				for (int i = 0; i < parts.length; i++) {
					if (i != 1) {
						newParam1 += parts[i];
						if (i != parts.length - 1)
							newParam1 += "/";
					}
				}
			}

		}

		dbgPrintln(path + " parsed as  " + newParam1 + " Dim1[Dim2] = " + dim1
				+ "[" + dim2 + "]");

		return new Pair<Element, String>(m.getChildren().get(dim1).get(dim2),
				newParam1);
	}



	public String printEr7(Message m) {
		String s;
		System.out
		.println("\n\n************************* er7    ********************************************\n\n");
		System.out.println(s = generateER7(null));
		System.out
		.println("\n\n**********************************************************************\n\n");
		return s;
	}

	public String printModifiedEr7(Message m, String param1, String param2) {
		String s;
		System.out
		.println("\n\n************************* er7    ********************************************\n\n");
		System.out.println(s = searchER7(null,param1,param2));
		System.out
		.println("\n\n**********************************************************************\n\n");
		return s;
	}



	void debugPrintProfile(Message m) {
		System.out.println("Unexpected segment list: "
				+ m.getUnexpectedSegmentList());
		System.out.println(m.getModel().toString());
		MessageModel mm = (MessageModel) m.getModel();

		for (ElementModel em : mm.getChildrenModel()) {
			System.out.println("ElementModel : " + em.getName());
			for (ElementModel emm : em.getChildrenModel()) {
				System.out.println("-->ElementModel : " + emm.getName());
			}
		}
	}

	/**
	 * @param m
	 * @param el 
	 * @return
	 */

	public void setMessageDelimiters(String delim) {
		md.setComponentSeparator(delim.substring(0, 0));
		md.setRepetitionSeparator(delim.substring(1, 1));
		md.setEscapeChar(delim.substring(2, 2));
		md.setSubComponentSeparator(delim.substring(3, 3));
		md.setDecimalNomination(delim.substring(4, 4));
		md.setSegmentTerminator("\n");
	}

	public AbstractElement elementFactoryMethod(Element e) {
		AbstractElement ae = null;
		switch (e.getElementType()) {
		case SEGMENT:
			ae = new Segment((SegmentModel) e.getModel());
			ae.setParent(e.getParent());
			break;
		case GROUP:
			ae = new Group((GroupModel) e.getModel());
			ae.setParent(e.getParent());
			break;
		case FIELD:
			ae = new Field((FieldModel) e.getModel());
			ae.setParent(e.getParent());
			break;
		case COMPONENT:
			ae = new Component((ComponentModel) e.getModel());
			ae.setParent(e.getParent());
			break;
		case SUB_COMPONENT:
			ae = new SubComponent((SubComponentModel) e.getModel());
			ae.setParent(e.getParent());
			break;

		}



		if (ae instanceof AbstractDataElement) {
			((AbstractDataElement) ae).setValue(((AbstractDataElement) e)
					.getValue());
			((AbstractDataElement) ae).setColumn(((AbstractDataElement) e)
					.getColumn());
		}
		return ae;
	}

	public Element clone(Element e) {
		// System.out.println("Cloning: " + e.getName() + "\n");
		AbstractElement clonE = elementFactoryMethod(e);
		TreeMap<Integer, List<Element>> tm = new TreeMap<Integer, List<Element>>();
		if (e.getChildren() != null) {
			clonE.setChildren(tm);
			for (Integer i : e.getChildren().keySet()) {
				List<Element> el = e.getChildren().get(i);
				List<Element> elC = new ArrayList<Element>();
				for (Element elE : el) {
					elC.add(clone(elE));
				}

				tm.put(i, elC);
			}
		}
		return clonE;

	}

	/*
	 * a test use case: goes into the the structure and increases cardinality of
	 * every element (if it is not max)
	 */
	public void increaseCardinality(Element el) {
		TreeMap<Integer, List<Element>> children = el == null ? m.getChildren()
				: el.getChildren();
		// depth first navigation

		if (children == null)
			return;

		for (List<Element> es : children.values()) {
			if (es != null && es.size() > 0) {
				// if (es.get(0).getMaxOccurs() > es.size())
				es.add(clone(es.get(0)));
				for (Element e : es)
					if (e instanceof Segment || e instanceof Field
							|| e instanceof Group)
						increaseCardinality(e);
				for (Element e : es)
					if (e instanceof Group){
						increaseCardinality(e);
					}
			}
		}
	}



	public void searchElement(Element el) {
		TreeMap<Integer, List<Element>> children = el == null ? m.getChildren():el.getChildren();

	} 
	
	

	/*
	 * does a DFS in the tree to generate the ER7 message
	 */

	public String generateER7(Element el) {
		String er7 = "";

		// the ER7 segments are delimited by newlines; also the segment name is
		// not a separate field
		if (el != null && el.getElementType() == ElementType.SEGMENT)
			er7 = "\n" + el.getName() + md.getFieldSeparator();

		// if the el is null, get the children from message
		TreeMap<Integer, List<Element>> children = el == null ? m.getChildren()
				: el.getChildren();

		// if no children, it is a leaf node; if it is a dataelement return the
		// value; otheriwise nothing
		if (children == null) { // BASE CASE
			if (el != null && el.getChildren() == null
					&& el instanceof DataElement)
				er7 = ((DataElement) el).getValue();

			/*if (er7.equals(x)){
            	return y;
            }*/



			return er7;
		}

		// extract the max so that later
		Integer max = 0;
		for (Integer i : children.keySet())
			max = i > max ? i : max;

			// assert that .get(max) exists and it has at least one entry
			String currentDelim = children.get(max).size() > 0 ? delim(children
					.get(max).get(0)) : "|"; // this is used
			// to delimit
			// the ER7 of
			// the children

			// depth first navigation
			for (Integer j = 1; j <= max; j++) {
				if (el != null && el.getName().equals("MSH") && j == 1) // the first
					// field for
					// MSH is
					// the field
					// separator
				{
					md.setFieldSeparator(((DataElement) (children.get(j).get(0)))
							.getValue());
					dbgPrintln("Setting field separator as : "
							+ ((DataElement) (children.get(j).get(0))).getValue());
					continue;
				}

				if (el != null && el.getName().equals("MSH") && j == 2) // the
					// second
					// field is
					// the
					// delimiters
					setMessageDelimiters(((DataElement) (children.get(2).get(0)))
							.getValue());
				List<Element> es = children.get(j);
				if (es != null) {
					int i = es.size();
					for (Element e : es) {
						--i;
						if (e != null)
							er7 += generateER7(e)
							+ ((e instanceof Segment || i == 0) ? ""
									: (e instanceof Field) ? md
											.getRepetitionSeparator() : "");
					}
					if (j != max)
						er7 += currentDelim;
				} else
					er7 += currentDelim;

			}

			return er7;
	}


	public String searchER7(Element el, String x, String y) {
		String er7 = "";

		// the ER7 segments are delimited by newlines; also the segment name is
		// not a separate field
		if (el != null && el.getElementType() == ElementType.SEGMENT)
			er7 = "\n" + el.getName() + md.getFieldSeparator();

		// if the el is null, get the children from message
		TreeMap<Integer, List<Element>> children = el == null ? m.getChildren()
				: el.getChildren();

		// if no children, it is a leaf node; if it is a dataelement return the
		// value; otheriwise nothing
		if (children == null) { // BASE CASE
			if (el != null && el.getChildren() == null
					&& el instanceof DataElement)
				er7 = ((DataElement) el).getValue();

		}



		// extract the max so that later
		Integer max = 0;
		for (Integer i : children.keySet())
			max = i > max ? i : max;


			// assert that .get(max) exists and it has at least one entry
			String currentDelim = children.get(max).size() > 0 ? delim(children
					.get(max).get(0)) : "|"; // this is used
			// to delimit
			// the ER7 of
			// the children

			// depth first navigation
			for (Integer j = 1; j <= max; j++) {
				if (el != null && el.getName().equals("MSH") && j == 1) // the first
					// field for
					// MSH is
					// the field
					// separator
				{
					md.setFieldSeparator(((DataElement) (children.get(j).get(0)))
							.getValue());
					dbgPrintln("Setting field separator as : "
							+ ((DataElement) (children.get(j).get(0))).getValue());
					continue;
				}

				if (el != null && el.getName().equals("MSH") && j == 2) // the
					// second
					// field is
					// the
					// delimiters
					setMessageDelimiters(((DataElement) (children.get(2).get(0)))
							.getValue());
				List<Element> es = children.get(j);
				if (es != null) {
					int i = es.size();
					for (Element e : es) {
						--i;
						if (e != null)
							er7 += generateER7(e)
							+ ((e instanceof Segment || i == 0) ? ""
									: (e instanceof Field) ? md
											.getRepetitionSeparator() : "");
					}
					if (j != max)
						er7 += currentDelim;
				} else
					er7 += currentDelim;

			}
			if (er7.contains(x)){
				String m = er7.replace(x, y);
				return m;
			}
			return er7;

	}


	/*
	 * delim Uses the MessageDelimiters instance and returns the right delimiter
	 * for the ElementType
	 */
	String delim(Element el) {
		if (el == null)
			return "";
		else if (el instanceof Field)
			return md.getFieldSeparator();
		else if (el instanceof Component)
			return md.getComponentSeparator();
		else if (el instanceof SubComponent)
			return md.getSubComponentSeparator();
		else if (el instanceof Group)
			return "";



		// return "----->" + el.getName() + "<-------------";
		else if (el instanceof Segment)
			return "\n";
		else
			return el != null ? "@" + el.getElementType() + "@" : "@NULL@";
	}

	static void getInputs() {
		getInputs(true, true, true, true, true);
	}

	// default values for unit testing

	static String fProfile = "./simple_profile3.xml";
	static String fEr7 = "./simple_message3.er7";
	static String param1 = "./2/1";
	static String param2 = "TestData";
	static String op = "SET";


	static void getInputs(boolean b_profile, boolean b_er7, boolean b_op,
			boolean b_param1, boolean b_param2) {
		if (b_profile)
			fProfile = getUserInput("Profile File", fProfile);
		if (b_er7)
			fEr7 = getUserInput("ER7 file", fEr7);
		if (b_param1)
			param1 = getUserInput("Parameter 1", param1);
		if (b_param2)
			param2 = getUserInput("Parameter 2", param2);
		if (b_op)
			op = getUserInput("Operation (SET,COPY,MOVE,REPLACE)", op);
	}

	@SuppressWarnings("resource")
	static String getUserInput(String name, String defaultValue) {
		System.out.println("Enter " + name + "  (default: " + defaultValue
				+ "): ");
		String buf;
		return (!(buf = new Scanner(System.in).nextLine()).isEmpty()) ? buf
				: defaultValue;
	}

	void dbgPrintln(String s) {
		if (debugTrace)
			System.out.println(s);


	}

	/* Main: Unit tests */
	public static void main(String[] args) throws ParserException, IOException {
		while (true) {
			ModifiableMessage.getInputs(false, false, true, true, true);
			ModifiableMessage m = new ModifiableMessage(fProfile, fEr7, true);
			System.out.println("\nBEFORE:");
			m.printEr7(m.m);


			System.out.println("\nAFTER:");

			m.apply(param1, param2, op);   // 107
			if (op.equals("REPLACE"))
				m.printModifiedEr7(m.m,param1,param2);
			else {
				m.printEr7(m.m);
			}

		}
	}
}
