package gov.nist.healthcare.messagemodifier.mu;

public interface IModifiableMessage {
	public String setData(String param1, String param2);
	public String copyData(String param1, String param2);	
	public String moveData(String param1, String param2);
}
