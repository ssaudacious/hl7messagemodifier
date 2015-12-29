package gov.nist.healthcare.mu.messagemodifierui.model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "test")
public class TestBean {
	private String instVar = "This is  the Test string from TestBean122";

	
	private TreeNode root;
	public TestBean() {
		root = new DefaultTreeNode("Root", null);
		TreeNode node0 = new DefaultTreeNode("Tree node 0", root);
		node0.setExpanded(true);
		TreeNode node1 = new DefaultTreeNode("Tree node 1", root);
		TreeNode node2 = new DefaultTreeNode("Tree node 2", root);
		node1.getChildren().add(node2);
	}
	public TreeNode getRoot() {
		return root;
	}	
	public String getInstVar() {
		return instVar;
	}

	public void setInstVar(String instVar) {
		this.instVar = instVar;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String country = "USA";
	public String city;
	public String value = "0.0";
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getCountries() {
		List<String> a = new ArrayList<String>();
		a.add("India");
		a.add("USA");
		return a;
	}
	
	public List<String> getCities() {
		List<String> a = new ArrayList<String>();
		if (country.equals("USA")) {
			a.add("MD");
			a.add("DC");
		} 
		else {
			a.add("TN");
			a.add("AP");

		}
		return a;
	}
	
	public String updateValue() {
		value = String.valueOf(System.currentTimeMillis());
		country = country.equals("USA") ? "India" : "USA";
		return null;
	}	
}
