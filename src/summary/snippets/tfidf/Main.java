package summary.snippets.tfidf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import summary.structure.Email;
import summary.structure.Sentence;
import summary.structure.Thread;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		Thread t = new Thread("hello", 1);
		
		for(int i = 1; i <= 18; i++){
			Email e = new Email("me", "now");
			e.addSendTo("you");
			Scanner scanner = new Scanner(new FileInputStream(new File("corpus/33 5th Floor/e" + i)), "UTF-8");
			String line;
			while(scanner.hasNextLine()){
				line = scanner.nextLine();
				//System.out.println(line);
				e.addSentence(new Sentence(0, line));
			}
			t.addEmail(e);
		}

		ArrayList<Thread> at = new ArrayList<Thread>();
		at.add(t);
		
		Snippet snippet = new Snippet();
		snippet.run(at);
		System.out.println("results:");
		for(ThreadVector tv: snippet.getAllThreads()){
			System.out.println(tv.getSubject());
			for(SentenceVector sv: tv.getSelectedSentences()){
				System.out.print(sv.getScore() + "\t" + sv.getText() + "\t");
				System.out.println(Arrays.toString(sv.getVector()));
			}
		}
	}

}
