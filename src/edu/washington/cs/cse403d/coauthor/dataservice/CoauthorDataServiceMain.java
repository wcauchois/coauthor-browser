package edu.washington.cs.cse403d.coauthor.dataservice;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import org.neo4j.index.lucene.LuceneFulltextQueryIndexService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import edu.washington.cs.cse403d.coauthor.dataservice.graphdb.BatchCreateGraph;

/**
 * Main data service executable. Assumes that a valid neo4j database is
 * correctly initialized with data.
 * 
 * JVM Args include:
 * 
 * <pre>
 * -Djava.security.policy=server.policy
 * -Djava.rmi.server.codebase= file:/driveletter:/.../coauthor-browser/classes/
 * </pre>
 * 
 * @author Jeff Prouty
 */
public class CoauthorDataServiceMain {
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(1099);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			EmbeddedGraphDatabase graphDb = new EmbeddedGraphDatabase(BatchCreateGraph.DB_PATH);
			LuceneFulltextQueryIndexService fullTextIndexService = new LuceneFulltextQueryIndexService(graphDb);
			LuceneIndexService indexService = new LuceneIndexService(graphDb);
			indexService.enableCache(BatchCreateGraph.AUTHOR_NAME_KEY, 25000);
			BatchCreateGraph.registerShutdownHook();
			CoauthorDataServiceInterface c = new CoauthorDataService(graphDb, indexService, fullTextIndexService);
			Naming.rebind("rmi://localhost:1099/" + CoauthorDataServiceInterface.SERVICE_NAME, c);
			System.out.println("DataService Started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
