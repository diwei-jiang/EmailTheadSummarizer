package summary.snippets.tfidf;

import java.util.ArrayList;
import java.util.Arrays;

import summary.structure.Thread;

public class ThreadVector {
	private Thread t;
	private ArrayList<SentenceVector> sentenceVectors;
	private double[] vector;
	private ArrayList<SentenceVector> selected;
	
	ThreadVector(Thread t){
		this.t = t;
		sentenceVectors = new ArrayList<SentenceVector>();
		vector = null;
		selected = new ArrayList<SentenceVector>();
	}
	
	public void select(){
		while(true){
			double max = 0;
			SentenceVector select = null;
			for(SentenceVector sv: sentenceVectors){
				double score = getScore(sv);
				if(score >= max){
					max = score;
					select = sv;
				}
			}
			if(max > 0){
				sentenceVectors.remove(select);
				selected.add(select);
			}
			if(max < 0.0001) break;
		}
	}
	
	private double getScore(SentenceVector s){
		double similarityToCenter = cosin(s.getVector(), vector);
		double maxSimilarityToSelectedSentences = getMaxSimilarityToSelectedSentences(s);
		double weightedScore = similarityToCenter - maxSimilarityToSelectedSentences;
		return weightedScore;
	}
	
	private double getMaxSimilarityToSelectedSentences(SentenceVector s){
		double max = 0;
		for(SentenceVector sv: selected){
			double score = cosin(s.getVector(), sv.getVector());
			if(score > max) max = score;
		}
		return max;
	}
	
	private double cosin(double[] a, double[] b){
		double score = 0;
		double lena = 0;
		double lenb = 0;
		double dot = 0;
		for(int i = 0; i < a.length; i++){
			lena += a[i] * a[i];
			lenb += b[i] * b[i];
			dot += a[i] * b[i];
		}
		lena = Math.sqrt(lena);
		lenb = Math.sqrt(lenb);
		score = dot / lena / lenb;
		return score;
	}
	
	public void addSentenceVectors(SentenceVector v){
		this.sentenceVectors.add(v);
	}
	
	public void makeVector(){
		vector = new double[Snippet.dictionaryIndex.size()];
		for(SentenceVector sv: sentenceVectors){
			sv.makeVector();
			for(int i = 0 ; i < vector.length; i++){
				vector[i] += sv.getVector()[i];
			}
		}
		for(int i = 0 ; i < vector.length; i++){
			vector[i] /= sentenceVectors.size();
		}
		System.out.println("center vector: " + Arrays.toString(vector));
	}

	public String getSubject(){
		return "header: " + this.t.getHeader();
	}
	
	public ArrayList<SentenceVector> getSelectedSentences(){
		return this.selected;
	}
}
