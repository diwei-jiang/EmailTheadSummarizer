package summary.snippets.clueword;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.apache.james.mime4j.MimeException;

import summary.mboxUtil.MboxReader;
import summary.structure.GraphNode;
import summary.structure.Thread;
public class CWSummarizer {
	private int topk = 4;
	private HashMap<String, ArrayList<SentenceWrapper>> topkSentences;
	private HashMap<String, GraphNode> graphs;

	public static void main(String[] args) throws FileNotFoundException, IOException, MimeException{
		ArrayList<Thread> threads =  MboxReader.parseThreads(args);
//		System.out.println(threads);
		
		CWSummarizer cws = new CWSummarizer(10);
		cws.run(threads);
		HashMap<String, ArrayList<SentenceWrapper>> result = cws.getAllThreadsSummary();
		for(String header : result.keySet()){
			System.out.println("Subject:\t" + header);
			System.out.println(result.get(header));
		}
	}
	public CWSummarizer(int topk){
		this.topk = topk;
	}
	public void run(ArrayList<Thread> threads){
		
		if(graphs == null) graphs = new HashMap<String, GraphNode>();
		for(Thread th : threads){
			graphs.put(th.getHeader(), new QuotationGraph().buildQuotationGraph(th));
		}
		getTopkSentences();
	}
	public void getTopkSentences(){
		for(String threadHeader : graphs.keySet()){
//			System.out.println(threadHeader);
			PriorityQueue<SentenceWrapper> ts = new PriorityQueue<SentenceWrapper>();
			graphTraverse(graphs.get(threadHeader), ts);
			
			ArrayList<SentenceWrapper> topList = new ArrayList<SentenceWrapper>();
			while(ts.size() > 0) topList.add(0, ts.poll());
			if(topkSentences == null){ 
				topkSentences = new HashMap<String, ArrayList<SentenceWrapper>>();
			}
			topkSentences.put(threadHeader, topList);
		}
	}
	public void graphTraverse(GraphNode node, PriorityQueue<SentenceWrapper> ts){
		// bfs 
		if (node == null){
			return;
		}
		HashSet<GraphNode> visited = new HashSet<GraphNode>();
		LinkedList<GraphNode> visiting = new LinkedList<GraphNode>();
		visiting.add(node);
		
		
		while (visiting.size() > 0) {
			GraphNode curNode = visiting.pop();
			if(visited.contains(curNode)) continue;
			visited.add(curNode);
			visiting.addAll(curNode.getParents());
			visiting.addAll(curNode.getChildren());
			
			HashMap<String, HashSet<String>> senBag = curNode.getSentencesBag();
			for (String sentenceStr : senBag.keySet()) {
				HashSet<String> wordSet = senBag.get(sentenceStr);
				SentenceWrapper sw = new SentenceWrapper(sentenceStr, getFreq(
						curNode.getChildren(), wordSet)
						+ getFreq(curNode.getParents(), wordSet));
				inQueue(ts, sw);
			}
		}
		
	}
	public int getFreq(ArrayList<GraphNode> nodes, HashSet<String> wordBag){
		Iterator<String> iter = wordBag.iterator();
		int senScore = 0; // sentence score
		
		while(iter.hasNext()){
			String word = iter.next();
			for(GraphNode node : nodes){
				if(node.getStemSet().contains(word)) senScore ++;
			}
		}
		return senScore;
	}
	public void inQueue(PriorityQueue<SentenceWrapper> ts, SentenceWrapper sw){
		ts.add(sw);
		if(ts.size() > topk) ts.poll();
	}
	public HashMap<String, ArrayList<SentenceWrapper>> getAllThreadsSummary(){
		return this.topkSentences;
	}
}


class SentenceWrapper implements Comparable<SentenceWrapper>{
	private String sen;
	private int clueScore;
	public SentenceWrapper(String sen, int cs){
		this.sen = sen;
		clueScore = cs;
	}
	public String getSen() {
		return sen;
	}
	public void setSen(String sen) {
		this.sen = sen;
	}
	public int getClueScore() {
		return clueScore;
	}
	public void setClueScore(int clueScore) {
		this.clueScore = clueScore;
	}
	@Override
	public int compareTo(SentenceWrapper arg0) {
		// TODO Auto-generated method stub
		return this.clueScore - arg0.clueScore;
	}
	@Override
	public String toString() {
		return "" + clueScore + "\t" + sen + "\n";
	}
	
}
