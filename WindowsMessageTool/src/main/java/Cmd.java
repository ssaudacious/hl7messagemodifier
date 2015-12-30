package main.java;

import org.immunizationsoftware.dqa.transform.ModifyMessageRequest;
import org.immunizationsoftware.dqa.transform.ModifyMessageService;

public class Cmd {

	public static void main3(String[] args) {
		// TODO Auto-generated method stub
		String transform = "select scenario NIST MU2.1.1 - Admin Child";
		String message = "";
		ModifyMessageService m = new ModifyMessageService();
		ModifyMessageRequest req = new ModifyMessageRequest();
		
		req.setMessageOriginal(message);
		req.setTransformScript(transform);
		m.transform(req);
		System.out.println("********************************\n\n");
		System.out.println(req.getMessageFinal());
		System.out.println("********************************\n\n");
	}

}
