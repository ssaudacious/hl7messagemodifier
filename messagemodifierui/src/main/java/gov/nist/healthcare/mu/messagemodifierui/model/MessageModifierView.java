package gov.nist.healthcare.mu.messagemodifierui.model;

import gov.nist.healthcare.core.hl7.v2.parser.ParserException;
import gov.nist.healthcare.messagemodifier.mu.ModifiableMessage;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@SessionScoped
public class MessageModifierView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fProfileName = "Select Profile";
	private String fMessageName = "Select Message";
	private String fProfileText;
	private String fMessageText;
	private UploadedFile fMessage;
	private UploadedFile fProfile;
	private String operation = "SET";
	private String operand1 = "./2/1";
	private String operand2 = "TestData";
	private boolean uploadFiles = true;


	public UploadedFile getfProfile() {
		return null;
	}

	public UploadedFile getfMessage() {
		return null;
	}
	
	public String getfMessageName() {
		return fMessageName;
	}

	public String getfMessageText()  {                        
		return fMessageText;
	}
	
	public void setfMessage(UploadedFile _fMessage) throws IOException {
		if (uploadFiles && _fMessage != null) {
			fMessage = _fMessage;
			fMessageText = new String(IOUtils.toCharArray(fMessage.getInputstream()));
			fMessageName = fMessage.getFileName();
			//printMessage("Message added!!!");
			fMessage = null;
		}
	}


	public String getfProfileName() {
		return fProfileName;
	}

	public String getfProfileText()  {
		return fProfileText;
	}

	public void setfProfileText(String profileText) throws IOException{
		if (!uploadFiles)
			fProfileText = profileText;
	}

	public void setfMessageText(String messageText) throws IOException  {
		if (!uploadFiles)
			fMessageText = messageText;
	}
	
	public void setfProfile(UploadedFile _fProfile) throws IOException {
		if (uploadFiles && _fProfile != null) {
			fProfile = _fProfile;
			fProfileText = new String(IOUtils.toCharArray(fProfile.getInputstream()));
			fProfileName = fProfile.getFileName();
			//printMessage("Profile added! " + fProfileName);
			fProfile = null;
		}
	}
	
    public void printMessage(String x) {
            FacesMessage message = new FacesMessage("Success: " + x);
            FacesContext.getCurrentInstance().addMessage(null, message);
    }
  
    public void uploadFiles() {
            printMessage(fProfile == null ? "Fprofile is null " : "Fprofile is not null");
            uploadFiles = true;
            printMessage(fProfileName);
    }

    public void uploadMessage(FileUploadEvent e) throws IOException {
    	setfMessage(e.getFile());
        printMessage("UploadMessage");
        uploadFiles = true;
        printMessage(fProfileName);
    }
    
    public void uploadProfile(FileUploadEvent e) throws IOException {
    	setfProfile(e.getFile());
        printMessage("UploadProfile");
        uploadFiles = true;
        printMessage(fProfileName);
    }
    
    public void uploadText() {
        printMessage(fProfile == null ? "Fprofile is null " : "Fprofile is not null");
        uploadFiles = false;
        printMessage(fProfileName);
    }
    
    public void setData() throws ParserException {
        ModifiableMessage m = new ModifiableMessage(fProfileText, fMessageText, false);
        fMessageText = m.setData(operand1, operand2);
        printMessage("Successfully set the data");
    }

    public void copyData() throws ParserException {
        ModifiableMessage m = new ModifiableMessage(fProfileText, fMessageText, false);
        fMessageText = m.copyData(operand1, operand2);
        printMessage("Successfully copied the data");
    }

    public void moveData() throws ParserException {
        ModifiableMessage m = new ModifiableMessage(fProfileText, fMessageText, false);
        fMessageText = m.moveData(operand1, operand2);
        printMessage("Successfully moved the data");
    }

    public void replaceData() throws ParserException {
        ModifiableMessage m = new ModifiableMessage(fProfileText, fMessageText, false);
        fMessageText = m.replaceData(operand1, operand2);
        printMessage("Successfully replaced the data");
    }

    public void incCardinality() throws ParserException {
        ModifiableMessage m = new ModifiableMessage(fProfileText, fMessageText, false);
        fMessageText = m.increaseCardinality(operand1, operand2);
        printMessage("Successfully increased the cardinality");
    }
    
    public void incCardinalityALL() throws ParserException {
        ModifiableMessage m = new ModifiableMessage(fProfileText, fMessageText, false);
        m.increaseCardinality(null);
        fMessageText = m.generateER7(null);
        printMessage("Successfully increased the cardinality!");
    }

	public void apply() throws ParserException {
		if (operation.equals("SET"))
			setData();
		else if (operation.equals("MOVE"))
			moveData();
		else if (operation.equals("COPY"))
			copyData();
		else if (operation.equals("REPLACE"))
			replaceData();
		else if (operation.equals("INC Cardinality ALL"))
			incCardinality();
	}

	public String getOperand1() {
		return operand1;
	}

	public void setOperand1(String operand1) {
		this.operand1 = operand1;
	}

	public String getOperand2() {
		return operand2;
	}

	public void setOperand2(String operand2) {
		this.operand2 = operand2;
	}

	
	public List<String> getOperations() {
		List<String> s = new ArrayList<String>();
		s.add("MOVE");
		s.add("COPY");
		s.add("SET");
		s.add("REPLACE");
		s.add("INC Cardinality");
		s.add("INC Cardinality ALL");
		
		return s;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String s) {
		operation = s;
	}
    
}
