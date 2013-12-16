package summary.snippets.clueword;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.james.mime4j.MimeException;

import summary.mboxUtil.MboxReader;
import summary.structure.Email;
import summary.structure.GraphNode;
import summary.structure.Sentence;
import summary.structure.Thread;

public class QuotationGraph {

	public GraphNode buildQuotationGraph(Thread thread){
		GraphNode threadGraph = null;
		Email largestEmail = null;
		for(Email em : thread.getEmails()){
			
			// a simple test graph
//			if(largestEmail == null) largestEmail = em;
//			else{
//				if(largestEmail.getSentences().size() < em.getSentences().size()){
//					largestEmail = em;
//				}
//			}
			GraphNode emailGraph = buildEmailGraph(em);
			if (emailGraph == null) 
				continue;
			if(threadGraph == null)  threadGraph = emailGraph;
			else{
				// merge threadGraph and emailGraph
				MergeByGraphIntersection(threadGraph, emailGraph);
			}
		}
		return buildEmailGraph(largestEmail);
		//return threadGraph;
	}
	
	// a target GraphNode is an email graph so it's a doubly linked list and
	// a base graphNode is the thread graph so it's a direct graph  
	// so if we can find a node in both graphs we can add the child of the node in the email graph
	// to the children array list of the node in the thread graph.
	public void MergeByGraphIntersection(GraphNode base, GraphNode target){
		if(EditDistance.isSimilar(base.getSentences().getText(),
				target.getSentences().getText())) {
			System.out.println("Same quotationTimes and similar block? No way"
					+ base.getSentences() + target.getSentences());
			return;
		}
		
		HashSet<GraphNode> visited = new HashSet<GraphNode>();
		LinkedList<GraphNode> visiting = new LinkedList<GraphNode>();
		GraphNode curNode=base, nextTarget=target;
		
		visiting.add(base);
//		System.out.println(base.getSentences());
//		visiting.addAll(base.getChildren());
//		visiting.addAll(base.getParents());
		
		while(visiting.size() > 0){
			
			curNode = visiting.pop();
			if(visited.contains(curNode)) continue;
			visited.add(curNode);
			nextTarget = target;
			System.out.println(curNode.getSentences());
			
			// traverse target to see if there's an intersection
			while(nextTarget.getParents().size() > 0){
				boolean r = EditDistance.isSimilar(curNode.getSentences().getText(),
						nextTarget.getSentences().getText());
				
				if(r) System.out.println(curNode.getSentences().getQuotationTimes() +":"+
							curNode.getSentences().getText()	+"\t\t" + 
							nextTarget.getSentences().getQuotationTimes() +":"+ nextTarget.getSentences().getText() + "\t\t" + r);
				
				if(r)	break;
	
				nextTarget = nextTarget.getParents().get(0);
			}
			
			//have not found any similar sentences and try next sentence in the queue
			if(nextTarget.getParents().size() == 0){
				visiting.addAll(curNode.getChildren());
				visiting.addAll(curNode.getParents());
				continue;
			}
			break;
		}
		
		// merge the children list
		if(nextTarget.getChildren().size() > 0){
//			for(GraphNode node : nextTarget.getChildren()){
				GraphNode gnode = nextTarget.getChildren().get(0);
				curNode.addChild(gnode);
				gnode.addParent(curNode);
//			}
			
			System.out.println("after merging: ");
			for(GraphNode node : curNode.getChildren())
				System.out.println(node.getSentences());
		}
		
	}
	
	public GraphNode buildEmailGraph(Email email){
		if(email == null) {
			System.out.println("Broken email: "+email);
			return null;
		}
		if(email.getSentences().size() == 0){
			System.out.println("Broken email: "+email);
			return null;
		}
		GraphNode root = null;
		//Suppose in one email, the user only replies to another email and no quotation level jump
		for(Sentence sen : email.getSentences()){
			if(root == null) root = new GraphNode(sen);
			else{
				if(sen.getQuotationTimes() == 
						root.getSentences().getQuotationTimes()){
					System.out.println("Should not has same quotTimes"
							+"new="+sen+"root="+root.getSentences().getQuotationTimes());
					System.exit(0);
				}
				else if(sen.getQuotationTimes() - 1 == 
						root.getSentences().getQuotationTimes()){
					root.addParent(new GraphNode(sen));
					root.getParents().get(0).addChild(root);
					root = root.getParents().get(0);
				}
				else if(sen.getQuotationTimes() + 1 ==
						root.getSentences().getQuotationTimes()){
					root.addChild(new GraphNode(sen));
					root.getChildren().get(0).addParent(root);
					root = root.getChildren().get(0);
				}
				else{
					System.out.println("Non continuous qutationTimes occurs");
					System.exit(0);
				}
			}
		}
		while(root.getSentences().getQuotationTimes() > 0){
			if(root.getChildren().size() > 0) root = root.getChildren().get(0);
			else break;
		}
		return root;
	}
	public static void main(String[] args) throws FileNotFoundException, IOException, MimeException {
		ArrayList<Thread> threads =  MboxReader.parseThreads(args);
		Thread athread = threads.get(0);
//		System.out.println(athread);
		System.out.println(new QuotationGraph().buildQuotationGraph(athread));
				
	}

}
