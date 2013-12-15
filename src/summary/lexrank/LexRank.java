package summary.lexrank;

import java.util.ArrayList;


import summary.snippets.tfidf.SentenceVector;
import summary.lexrank.Vector;

/*
 * Implement the LexRank algorithm of Erkan and Radev. 
 * The LexRank class takes the tf-idf vectors of sentences to compute 
 * their lexical centrality. It uses the epsilon value to determine
 * the convergence of the algorithm. 
 */

public class LexRank {
	
	private double epsilon = 0.0;
	private double dampingFactor = 0.85;
	private ArrayList<SentenceVector> sentences;
	private double [][] cosineMatrix;
	private int [] degree;
	private double [] lexScore;
	
	public LexRank(ArrayList<SentenceVector> sentences, double epsilon) {
		this.sentences = sentences;
		this.epsilon = epsilon; 
		init();;
		buildCosineMatrix();
		powerMethod(this.dampingFactor);
	}
	
	public double[] getLexScore() {
		return this.lexScore;
	}
	
	public void setDampingFactor(double dampingFactor) {
		this.dampingFactor = dampingFactor;
	}
	
	private void init() {
		cosineMatrix = new double [sentences.size()][];
		for (int i = 0; i < sentences.size(); i++) {
			cosineMatrix[i] = new double [sentences.size()];
		}
		degree = new int[sentences.size()];
		lexScore = new double[sentences.size()];
	}
	
	private void buildCosineMatrix() {
		for (int i = 0; i < sentences.size(); i++) {
			for (int j = 0; j < sentences.size(); j++) {
				cosineMatrix[i][j] = idfModifiedCosine(sentences.get(i), sentences.get(j));
				degree[i] += cosineMatrix[i][j];
			}
		}
		for (int i = 0; i < sentences.size(); i++) {
			for (int j = 0; j < sentences.size(); j++) {
				cosineMatrix[i][j] = cosineMatrix[i][j] / degree[j];
			}
		}
	}
	
	private double idfModifiedCosine(SentenceVector x, SentenceVector y) {
		return Vector.dotProduct(x.getVector(), y.getVector());
	}
	
	private void powerMethod(double dampFactor) {
		double magDiff = epsilon + 100000;
		double size = (double)sentences.size();
		lexScore = new double[sentences.size()];
		double [] lexScoreNext = new double[sentences.size()];
		for (int i = 0; i < sentences.size(); i++) {
			lexScore[i] = 1 / (double)sentences.size();
		}
		while (magDiff > epsilon) {
			for (int i = 0; i < sentences.size(); i++) {
				lexScoreNext[i] = dampFactor / size 
								+ (1 - dampFactor) 
								* Vector.dotProduct(cosineMatrix[i], lexScore);
			}
			magDiff = Vector.difference(lexScoreNext, lexScore);
			Vector.printVector(lexScoreNext);
			System.arraycopy(lexScoreNext, 0, lexScore, 0, sentences.size());
		}
	}
	
}
