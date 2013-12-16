package summary.snippets.tfidf;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.james.mime4j.MimeException;

import summary.mboxUtil.MboxReader;
import summary.structure.Thread;
import summary.lexrank.LexRank;
import summary.snippets.sentiment.SentimentWraper;


public class Main {
	
	public static void main(String[] args) throws IOException, MimeException {

		String [] filePath = new String[1];
		filePath[0] = "/Users/diweijiang/Documents/class/NLP/mbox/permathreads.mbox/permathreads.mbox/mbox";
		ArrayList<Thread> at = MboxReader.parseThreads(filePath);
		
		Snippet snippet = new Snippet();
		snippet.run(at);
		System.out.println("results:");
		for(ThreadVector tv: snippet.getAllThreads()){
			
			// tf-idf 
			System.out.println(tv.getSubject());
			System.out.println("********Greedy Add TFIDF Result*******");
			for(SentenceVector sv: tv.getSelectedSentences()){
				System.out.println(sv.getScore() + "\t" + sv.getText() + "\t");
				// System.out.println(Arrays.toString(sv.getVector()));
			}
			
			// sentiment
			snippet.resetPipline();
			System.out.println("*************Sentiment***************");
			double scores = (new SentimentWraper()).getThreadScores(tv);
			System.out.println(scores);
			System.out.println("*************************************");
			
			// lex page rank
			LexRank lrank = new LexRank(tv.getSentenceVectors(), 0.0);
			ArrayList<SentenceVector> lexRankedSentence = lrank.getSentenceVector();
			System.out.println("**********Lex Rank Result************");
			for (SentenceVector sv : lexRankedSentence) {
				System.out.printf("%f\t%s\n", sv.getScore(), sv.getText());
			}
			System.out.println("*************************************");
		}

	}

}
