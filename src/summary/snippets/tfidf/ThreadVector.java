package summary.snippets.tfidf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
//		while(sentenceVectors.size() > 0){
//			double max = Double.NEGATIVE_INFINITY;
//			SentenceVector select = null;
//			for(SentenceVector sv: sentenceVectors){
//				double score = getScore(sv);
//				sv.setScore(score);
//				if(score >= max){
//					max = score;
//					select = sv;
//				}
//			}
//			sentenceVectors.remove(select);
//			selected.add(select);
//			//if(max < 0.0001) break;
//		}
		
		for(SentenceVector sv: sentenceVectors){
			double score = getScore(sv);
			sv.setScore(score);
		}
		selected = new ArrayList<SentenceVector>(sentenceVectors);
		Collections.sort(selected, new SentenceComparator());
	}
	
	// keep both informative and novel
	private double getScore(SentenceVector s){
		double similarityToCenter = cosin(s.getVector(), vector);
		double maxSimilarityToSelectedSentences = getMaxSimilarityToSelectedSentences(s);
		double weightedScore = similarityToCenter - 0.3 * maxSimilarityToSelectedSentences;
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
//		System.out.println("center vector: " + Arrays.toString(vector));
	}

	public String getSubject(){
		return "header: " + this.t.getHeader();
	}
	
	public ArrayList<SentenceVector> getSelectedSentences(){
		return this.selected;
	}
	
	public ArrayList<SentenceVector> getSentenceVectors() {
		return this.sentenceVectors;
	}
}

class SentenceComparator implements Comparator<SentenceVector> {
	@Override
	public int compare(SentenceVector s1, SentenceVector s2) {
		if (s1.getScore() < s2.getScore()) {
			return 1;
		} else if (s1.getScore() > s2.getScore()) {
			return -1;
		} else {
			return 0;
		}
	}
}