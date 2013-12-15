package summary.snippets.sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;
import java.util.HashMap;

import summary.structure.Email;
import summary.structure.Sentance;

public class EmailWithSentiment extends Email {

  private HashMap<Sentence, Double> scoresHash;

  public EmailWithSentiment (String _sendBy, String _time) {
    super (_sendBy, _time);
    scoresHash = new HashMap<Sentence, Double>();
  }

  public void addSentenceWithSentiment (Sentence s) {
    addSentence(s);
    scoresHash.put(s, sentimentScores(s));
  }

  public double getSentenceSentiment (Sentence s) {
    return scoresHash.get(s);
  }

  public double getEmailAverageSentiment () {
    if (scoresHash.isEmpty())
      return 0;
    else
      return sentimentScores(scoresHash);
  }

  
  /**
   * Scores
   * negative ....... positive
   * -2, -1,     0    , 1, 2
   */
  private double sentimentScores (Sentence s) {

    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    int mainSentiment = 0;
    int sentanceCount = 0;

    if (s.getText() != null && s.getText().length() > 0) {
      Annotation annotation = pipeline.process(s.getText());
      for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
        Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
        int sentiment = RNNCoreAnnotations.getPredictedClass(tree) - 2;
        mainSentiment += sentiment;
        sentanceCount++;
      }
    } else { return 0; }

    return mainSentiment / sentanceCount;
  }

  private double sentimentScores (HashMap<Sentence, Double> _scoresHash) {
    double avgScores = 0;
    for(Sentence s : _scoresHash.keySet()){
      avgScores += _scoresHash.get(s);
    }
    return avgScores / _scoresHash.size();
  }
  
  
  /**
   * test case
   */
  public static void main (String[] args) {
    
    String text1 = "I don't agree with you.";
    String text2 = "Lets get our taskrabbits fired up.";
    String text3 = "Just buy two power drills and some beers for Friday.";
    Sentence sen1 = new Sentence(0, text1);
    Sentence sen2 = new Sentence(0, text2);
    Sentence sen3 = new Sentence(0, text3);
    
    EmailWithSentiment es = new EmailWithSentiment("me", "0");
    es.addSentenceWithSentiment(sen1);
    es.addSentenceWithSentiment(sen2);
    es.addSentenceWithSentiment(sen3);
    
    System.out.println(es.getSentenceSentiment(sen1));
    System.out.println(es.getEmailAverageSentiment());

  }

}
