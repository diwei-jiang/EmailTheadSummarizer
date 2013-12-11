package summary.structure;

import java.util.ArrayList;

public class Thread {
	private String header;
	private int emailNumber;
	private ArrayList<Email> emails;
	
	public Thread(String header, int emailNumber){
		this.header = header;
		this.emailNumber = emailNumber;
		this.emails = new ArrayList<Email>();
	}
	
	public String getHeader(){
		return this.header;
	}
	
	public int getEmailNumber(){
		return this.emailNumber;
	}
	
	public void addEmail(Email e){
		this.emails.add(e);
	}
	
	public ArrayList<Email> getEmails(){
		return this.emails;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(this.header + ", " + this.emailNumber + ", ");
		sb.append(this.emails);
		sb.append("}");
		return sb.toString(); 
	}
	
	public static void main(String[] args){
		Thread t = new Thread("hello", 1);
		
		Email e1 = new Email("me", "now");
		e1.addSendTo("you");
		e1.addSentence(new Sentence(0, "hello~"));
		
		t.addEmail(e1);
		
		System.out.println(t);
	}
	
}
