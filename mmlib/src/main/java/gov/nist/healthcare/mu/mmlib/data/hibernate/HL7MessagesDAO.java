package gov.nist.healthcare.mu.mmlib.data.hibernate;

import gov.nist.healthcare.mu.mmlib.util.Constants;
import gov.nist.healthcare.mu.mmlib.util.MessageLocation;

import java.sql.CallableStatement;
import java.util.List;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;

@Repository
public class HL7MessagesDAO implements IHL7MessagesDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	@SuppressWarnings("rawtypes")
	@Override
	public List fetchElementsFromDataSource(String element, String occurrences, String datasource) {
		MessageLocation mx = new MessageLocation(element, null);
		Random r = new Random();
		String cols = mx.fieldNum == 0 ? "*" : ("f" + mx.fieldNum);
		if (mx.fieldNum != 0) {
			return sessionFactory.getCurrentSession().createSQLQuery(
					//"select f1 from segments")
					"CALL getfields('" + cols + "', '" + mx.segmentName + "', "  + occurrences + "," + r.nextInt() + ",'" + datasource + "')")
					//" call getfields();")
					//.setParameter("segName", mx.segmentName)
					//.setParameter("limit", occurrences)
					.list();
		}
		else
			return sessionFactory.getCurrentSession().createSQLQuery(
					"call getsegments('" + mx.segmentName + "', "  + occurrences + "," + r.nextInt() + ",'" + datasource + "');")
					//.setParameter("segName", mx.segmentName)
					//.setParameter("limit", occurrences)
					.list();
	}
}
