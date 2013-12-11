package summary.structure;

public class Sentence {
	private int quotationTimes;
	private String text;
	
	public Sentence(int quotationTimes, String text){
		this.quotationTimes = quotationTimes;
		this.text = text;
	}
	
	public int getQuotationTimes(){
		return this.quotationTimes;
	}
	
	public String getText(){
		return this.text;
	}
	
	public String toString(){
		return "{" + this.quotationTimes + ":" + this.text + "}";
	}

}
