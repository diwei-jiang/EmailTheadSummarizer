package gate.example;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;

import static gate.util.persistence.PersistenceManager.loadObjectFromFile;

public class Main {
	 public static String HOME_PATH = "";
	 public Main() throws Exception{
			Gate.init();
			File gateHome = Gate.getGateHome();
			HOME_PATH = gateHome.getCanonicalPath() + "/";
			if (Gate.getGateHome() == null)
				Gate.setGateHome(gateHome);
						
			File pluginsHome = new File(gateHome, "plugins");
			//Register all the plugins that your program will need
			Gate.getCreoleRegister().registerDirectories(
					new File(pluginsHome, "ANNIE")
							.toURI().toURL());
	 }
	 
	 public static void main(String[] ars)throws Exception
	 {
		new Main();

		// Point it to where your gapp file resides on your hard drive
		gate.CorpusController c = ((gate.CorpusController) loadObjectFromFile(new java.io.File(
				"/Users/fenghhk/Documents/nlp/GATE-example/GATE-example/huiHW2.gapp")));
		
		Corpus corpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
//		// Point it to whichever folder contains your documents
		URL dir = new File("/Users/fenghhk/Documents/nlp/GATE-example/GATE-example/corpus/").toURI().toURL();
		System.out.println(dir.toString());
		
		corpus.populate(dir, null, "UTF-8", false); // set the encoding to
													// whatever is the encoding
									// of your files
		c.setCorpus(corpus);
		
		c.execute();

		gate.Document tempDoc = null;
		// This is how you can access the annotations created by your gate
		// application
		System.out.println(corpus.getDocumentNames().size() + corpus.size());
		for (int i = 0; i < corpus.size(); i++) {
			tempDoc = (gate.Document) corpus.get(i);
			for (gate.Annotation a : tempDoc.getAnnotations().get("TitleOrg")) {
				FeatureMap featMap = a.getFeatures();
				for(Object o :featMap.keySet()) System.out.println(o.toString());
				System.out.print("Org: " + a.getFeatures().get("hw2orgs"));
				System.out.print("  Title: " + a.getFeatures().get("hw2titles"));
				System.out.println();
			}

			// Really important to release these resources or you'll soon run
			// out of working memory
			Factory.deleteResource(tempDoc);

		}

		// Release resource--important
		Factory.deleteResource(corpus);
		System.out.println("done");
	}
}
