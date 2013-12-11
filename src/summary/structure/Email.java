package summary.structure;

import java.util.ArrayList;

public class Email {
	private String sendBy;
	private String time;
	private ArrayList<String> sendTo;
	private ArrayList<Sentence> sentences;
	
	public Email(String sendBy, String time){
		this.sendBy = sendBy;
		this.time = time;
		this.sendTo = new ArrayList<String>();
		this.sentences = new ArrayList<Sentence>();
	}
	
	public void addSendTo(String sendTo){
		this.sendTo.add(sendTo);
	}
	
	public void addSentence(Sentence s){
		this.sentences.add(s);
	}
	
	public String getSendBy(){
		return this.sendBy;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public ArrayList<String> getSendTo(){
		return this.sendTo;
	}
	
	public ArrayList<Sentence> getSentences(){
		return this.sentences;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(this.sendBy + ", ");
		sb.append(this.time + ", ");
		sb.append(this.sendTo + ", ");
		sb.append(this.sentences);
		sb.append("}");
		return sb.toString();
	}
}
