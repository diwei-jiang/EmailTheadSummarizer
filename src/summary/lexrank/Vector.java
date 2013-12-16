package summary.lexrank;

import java.lang.Math.*;

public class Vector {

	public static double dotProduct(double [] v1, double [] v2){
		double dotPrd = 0.0;
		for (int i = 0; i < v1.length; i++) {
			dotPrd += v1[i] * v2[i];
		}
		return dotPrd;
	}

	public static double magnitude(double[] v) {
		double mag = 0.0;
		for (int i = 0; i < v.length; i++) {
			mag += v[i] * v[i];
		}
		return Math.sqrt(mag);
	}
	
	public static double difference(double [] v1, double [] v2){
		double diff = 0.0;
		for (int i = 0; i < v1.length; i++) {
			diff += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		return Math.sqrt(diff);
	}
	
	public static void printVector(double [] v){
		System.out.print("[ ");
		for (int i = 0; i < v.length; i++) {
			System.out.print("" + v[i] + " ");
		}
		System.out.println("]");
	}
	
	
}
