package gov.nist.healthcare.mu.mmlib.data.hibernate;

import gov.nist.healthcare.mu.mmlib.util.Constants;
import gov.nist.healthcare.mu.mmlib.util.MessageLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HL7MessagesService implements IHL7MessagesService {
	
	@Autowired
	HL7MessagesDAO hL7MessageDAO;
	
	@Transactional
	@Override
	public List<String> fetchData(String element, String occurences, String datasource) {
		@SuppressWarnings("rawtypes")
		List cr = hL7MessageDAO.fetchElementsFromDataSource(element,  occurences, datasource);
		String sRet = "";
		
		List<String> lret = new ArrayList<String>();
		
		for (Object o : cr) {
			// result is the set of fields in the segment
			if (o instanceof Object[]) {
				Object[] i =  (Object[]) o;
				for (Object s: i) {
					if (s == null)
						s = "NULL";
					sRet += s.toString() + Constants.fs;
				}
				sRet = sRet.replaceAll("[\\|NULL]*$", ""); // trailing NULLs to empty
				lret.add(sRet);
				sRet = "";
			} 
			else {// result is a repeating field/subcomponent
				if (o == null)
					o = "";   // do we have to continue here?
				sRet += (sRet.isEmpty() ? "" : Constants.repeater) + o.toString(); // need to remove the last bit
			}
				
		}
		
		if (!sRet.isEmpty() && lret.isEmpty()) // returning the field set
			lret.add(sRet);
		
		return lret;
	}
	
	//Obsolete presently; but maybe needed later
	ArrayList<Integer> getRandomSet(int n, int max) {
		ArrayList<Integer> s = new ArrayList<Integer>();
		Random r = new Random(new Date().getTime());
		for (int i = 0; i < n; i++)
			s.add(r.nextInt(max));
		return s;
	}
}
