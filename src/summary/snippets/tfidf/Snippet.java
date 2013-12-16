package summary.snippets.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import summary.structure.Email;
import summary.structure.Sentence;
import summary.structure.Thread;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Snippet {

	public static HashMap<String, Integer> dictionaryIndex = new HashMap<String, Integer>();
	private HashMap<String, Integer> dictionaryDocumentCount;
	public static HashMap<String, Double> dictionaryIdf = new HashMap<String, Double>();
	private int totalSentenceNumber;
	private ArrayList<ThreadVector> allThreads;
	private StanfordCoreNLP pipeline;

	public Snippet() {
		this.dictionaryDocumentCount = new HashMap<String, Integer>();
		this.totalSentenceNumber = 0;
		this.allThreads = new ArrayList<ThreadVector>();
	}

	public void run(ArrayList<Thread> threads) {
		StanfordLemmatizer();
		parseThread(threads);

		// System.out.println(dictionaryIndex.size());
		// System.out.println(dictionaryIndex.get("-david"));

		countIdf();
		generateVectors();
		selectSentence();
	}
	
	public void StanfordLemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        this.pipeline = new StanfordCoreNLP(props);
    }

	private void selectSentence() {
		for (ThreadVector tv : allThreads) {
			tv.select();
		}
	}

	private void countIdf() {
		for (String word : Snippet.dictionaryIndex.keySet()) {
			Snippet.dictionaryIdf.put(
					word,
					Math.log((double) (totalSentenceNumber + 1)
							/ (double) dictionaryDocumentCount.get(word)));
		}
	}

	private void generateVectors() {
		for (ThreadVector t : allThreads) {
			t.makeVector();
		}
	}

	private void parseThread(ArrayList<Thread> threads) {
		for (Thread t : threads) {
			ThreadVector tv = new ThreadVector(t);
			allThreads.add(tv);
			for (Email e : t.getEmails()) {
				StringBuffer sb = new StringBuffer();
				for (Sentence s : e.getSentences()) {
					// if it's the content of this email
					if (s.getQuotationTimes() == 0) {
						sb.append(s.getText() + " ");
					}
				}
				String content = sb.toString().toLowerCase();

		        // create an empty Annotation just with the given text
		        Annotation document = new Annotation(content);

		        // run all Annotators on this text
		        this.pipeline.annotate(document);

		        // Iterate over all of the sentences found
		        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		        for(CoreMap sentence: sentences) {
					List<String> lemmas = new LinkedList<String>();
		            // Iterate over all tokens in a sentence
		            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		                // Retrieve and add the lemma for each word into the
		                // list of lemmas
		                lemmas.add(token.get(LemmaAnnotation.class));
		            }
		            
		            HashMap<String, Integer> wordCount = countWordsInSentence(lemmas);
					// if it has valid words
					if (wordCount.size() > 0) {
						totalSentenceNumber++;
						for (String word : wordCount.keySet()) {
							if (!dictionaryIndex.containsKey(word)) {
								dictionaryIndex.put(word,
										dictionaryIndex.size());
								dictionaryDocumentCount.put(word, 1);
							} else {
								dictionaryDocumentCount
										.put(word, dictionaryDocumentCount
												.get(word) + 1);
							}
						}
						SentenceVector sv = new SentenceVector(sentence.toString(), wordCount);
						tv.addSentenceVectors(sv);
					}
		        }
			}
		}
	}

	private HashMap<String, Integer> countWordsInSentence(List<String> words) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String word: words) {
			if (good(word)) {
				if (!map.containsKey(word))
					map.put(word, 1);
				else
					map.put(word, map.get(word) + 1);
			}
		}
		return map;
	}

	private boolean good(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) >= 'a' && word.charAt(i) <= 'z')
				return true;
		}
		return false;
	}

	public void resetPipline () {
		this.pipeline.clearAnnotatorPool();
	}

	public ArrayList<ThreadVector> getAllThreads() {
		return this.allThreads;
	}

}
