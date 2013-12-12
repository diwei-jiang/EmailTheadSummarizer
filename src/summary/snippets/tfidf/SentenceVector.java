package summary.snippets.tfidf;

import java.util.HashMap;

import summary.structure.Sentence;

public class SentenceVector {
	private String sentence;
	private HashMap<String, Integer> wordCount;
	private double[] vector;
	
	public SentenceVector(String sentence, HashMap<String, Integer> wordCount){
		this.sentence = sentence;
		this.wordCount = wordCount;
	}
	
	public void makeVector(){
		vector = new double[Snippet.dictionaryIndex.size()];
		int len = getLen();
		for(String word: wordCount.keySet()){
			double tf = (double)wordCount.get(word);
			double tfidf = tf * Snippet.dictionaryIdf.get(word);
			vector[Snippet.dictionaryIndex.get(word)] = tfidf;
		}
	}
	
	private int getLen(){
		int count = 0;
		for(String word: wordCount.keySet()){
			count += wordCount.get(word);
		}
		return count;
	}
	
	public double[] getVector(){
		return this.vector;
	}
	
	public String getText(){
		return sentence;
	}
}
