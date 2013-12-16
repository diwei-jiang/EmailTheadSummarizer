package summary.snippets.sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

import summary.snippets.tfidf.*;

public class SentimentWraper {

	
	public SentimentWraper () { }

  public double getThreadScores (ThreadVector tv) {
    double scores = 0.0;
    for (SentenceVector sv : tv.getSentenceVectors())
      scores += this.sentimentScores(sv.getText());

    if(tv.getSentenceVectors().isEmpty())
      return 0.0;
    else
      return scores/tv.getSentenceVectors().size();
  }

  /**
   * Scores
   * negative ....... positive
   * -2, -1,     0    , 1, 2
   */
  public double sentimentScores (String s) {

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    int mainSentiment = 0;
    int sentanceCount = 0;

    if (s != null && s.length() > 0) {
      Annotation annotation = pipeline.process(s);
      for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
        Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
        int sentiment = RNNCoreAnnotations.getPredictedClass(tree) - 2;
        mainSentiment += sentiment;
        sentanceCount++;
      }
    } else { return 0; }

    return mainSentiment / sentanceCount;
  }
}