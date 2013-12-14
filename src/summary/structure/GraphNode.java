package summary.structure;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class GraphNode {

	private ArrayList<GraphNode> parents, children;
	private Sentence sentences;
	
	private Set<String> stemSet = null; 
	private HashMap<String, HashSet<String>> senBag = null;
	private static HashSet<String> stopWordSet = null;
	private static StanfordCoreNLP pipeline = null;
	
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
	public Set<String> getStemSet() {
		if(stemSet == null) getSentencesBag();
		return stemSet;
	}
	
	public HashMap<String, HashSet<String>> getSentencesBag(){
		if(senBag == null ) senBag = new HashMap<String, HashSet<String>>();
		else return senBag;
		if(stemSet == null) stemSet = new HashSet<String>();
		if(stopWordSet == null) getStopWords();
		if(this.pipeline == null) StanfordLemmatizer();
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(this.sentences.getText());

        // run all Annotators on this text
        this.pipeline.annotate(document);

		for (CoreMap sentence: document.get(SentencesAnnotation.class)) {
            // Retrieve and add the lemma for each word into the
            // list of lemmas
			HashSet<String> wordBag = new HashSet<String>();
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
				String lemma = token.get(LemmaAnnotation.class);
				
				if(stopWordSet.contains(lemma)) continue;
                stemSet.add(lemma);
                wordBag.add(lemma);
            }
			if(!senBag.containsKey(sentence.toString()))
				senBag.put(sentence.toString(), wordBag);
		}
		return senBag;		
	}
	
	public static HashSet<String> getStopWords(){
		if(stopWordSet != null) return stopWordSet;
		String sw = "a,able,about,across,after,all,almost,also,am,among,an"
				+ ",and,any,are,as,at,be,because,been,but,by,can,cannot,could,"
				+ "dear,did,do,does,either,else,ever,every,for,from,get,got,had,"
				+ "has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,"
				+ "its,just,least,let,like,likely,may,me,might,most,must,my,neither,"
				+ "no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,"
				+ "says,she,should,since,so,some,than,that,the,their,them,then,there,"
				+ "these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where"
				+ ",which,while,who,whom,why,will,with,would,yet,you,your";
		
		stopWordSet = new HashSet<String>();
		for(String s : sw.split(",")) stopWordSet.add(s);
		
		return stopWordSet;
	}
	public GraphNode(Sentence sb){
		sentences = sb;
		parents = new ArrayList<GraphNode>();
		children = new ArrayList<GraphNode>();
	}
	
	public ArrayList<GraphNode> getParents() {
		return parents;
	}

	public void addParent(GraphNode parent) {
		this.parents.add(parent);
	}

	public ArrayList<GraphNode> getChildren() {
		return children;
	}

	public void addChild(GraphNode child) {
		this.children.add(child);
	}

	public Sentence getSentences() {
		return sentences;
	}

	public void setSentences(Sentence sb) {
		this.sentences = sb;
	}

	@Override
	public String toString() {
		StringBuilder sb =  new StringBuilder();
		sb.append("\n");
		sb.append("{");
		sb.append(parents);
		sb.append(this.getSentences());
//		sb.append(children);
		sb.append("}");
		return sb.toString();
	}

	public static void main(String[] args) {
		GraphNode root = new GraphNode(new Sentence(5, "being quoted 5 times"));
		root.addParent(new GraphNode(new Sentence(6, "a child")));
		root.addParent(new GraphNode(new Sentence(4,"a parent")));
		
		System.out.println(root);

	}

}
