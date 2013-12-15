package summary.lexrank;

import summary.lexrank.LexRank;
import java.util.ArrayList;

import summary.lexrank.Vector;
import summary.snippets.tfidf.SentenceVector;
import summary.lexrank.LexRank;

public class Main {
	public static void main(String[] args) {
		double [] ls1 = {1,2,1};
		double [] ls2 = {0,3,1};
		double [] ls3 = {1,0,0};
		double [] ls4 = {1,1,1};
		ArrayList<SentenceVector> sv = new ArrayList<SentenceVector>();
		sv.add(new SentenceVector(ls1));
		sv.add(new SentenceVector(ls2));
		sv.add(new SentenceVector(ls3));
		sv.add(new SentenceVector(ls4));
		if (0 == ls4[0]){
			
		}
		
		LexRank lr = new LexRank(sv, 0.001);
		
		System.out.println("\nResult: ");
		Vector.printVector(lr.getLexScore());
	}
}
