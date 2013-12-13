package summary.structure;


import java.util.ArrayList;

public class GraphNode {

	private ArrayList<GraphNode> parents, children;
	private Sentence sentences;
	
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
		sb.append("{");
		sb.append(parents);
		sb.append("|"+parents.size()+"|");
		sb.append(this.sentences.getQuotationTimes());
//		sb.append("\n");
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
