package summary.snippets.tfidf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.james.mime4j.MimeException;

import summary.mboxUtil.MboxReader;
import summary.structure.Email;
import summary.structure.Sentence;
import summary.structure.Thread;
import summary.lexrank.LexRank;

public class Main {
	
	public static void main(String[] args) throws IOException, MimeException {
//		Thread t = new Thread("hello", 1);
//		for(int i = 1; i <= 18; i++){
//			Email e = new Email("me", "now");
//			e.addSendTo("you");
//			Scanner scanner = new Scanner(new FileInputStream(new File("corpus/33 5th Floor/e" + i)), "UTF-8");
//			String line;
//			while(scanner.hasNextLine()){
//				line = scanner.nextLine();
//				System.out.println(line);
//				e.addSentence(new Sentence(0, line));
//			}
//			t.addEmail(e);
//		}

		String [] filePath = new String[1];
		filePath[0] = "C:/Users/Wendong/git/goNLP/corpus/mbox";
		ArrayList<Thread> at = MboxReader.parseThreads(filePath);

		
		Snippet snippet = new Snippet();
		snippet.run(at);
		System.out.println("results:");
		for(ThreadVector tv: snippet.getAllThreads()){
			System.out.println(tv.getSubject());
			for(SentenceVector sv: tv.getSelectedSentences()){
				System.out.println(sv.getScore() + "\t" + sv.getText() + "\t");
//				System.out.println(Arrays.toString(sv.getVector()));
			}
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
