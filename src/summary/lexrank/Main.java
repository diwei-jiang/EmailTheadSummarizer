package summary.lexrank;

import summary.lexrank.LexRank;
import java.util.ArrayList;

import summary.lexrank.Vector;
import summary.snippets.tfidf.SentenceVector;
import summary.lexrank.LexRank;

public class Main {
	public static void main(String[] args) {
		double [] vector1 = {1,2,1};
		double [] vector2 = {0,3,1};
		double [] vector3 = {1,0,0};
		double [] vector4 = {1,1,1};
		ArrayList<SentenceVector> sv = new ArrayList<SentenceVector>();
		sv.add(new SentenceVector(vector1));
		sv.add(new SentenceVector(vector2));
		sv.add(new SentenceVector(vector3));
		sv.add(new SentenceVector(vector4));
		
		LexRank lr = new LexRank(sv, 0.001);
		
		System.out.println("\nResult: ");
		Vector.printVector(lr.getLexScore());
	}
}
