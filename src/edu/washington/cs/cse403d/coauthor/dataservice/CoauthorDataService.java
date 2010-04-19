package edu.washington.cs.cse403d.coauthor.dataservice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.search.Sort;
import org.neo4j.graphalgo.shortestpath.FindPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexHits;
import org.neo4j.index.lucene.LuceneFulltextQueryIndexService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import edu.washington.cs.cse403d.coauthor.dataservice.graphdb.BatchCreateGraph;

/**
 * The data bindings for the data provider. Performs the lookups on the actual
 * graph.
 * 
 * @author Jeff Prouty
 */
public class CoauthorDataService extends UnicastRemoteObject implements CoauthorDataServiceInterface {
	private static final long serialVersionUID = -5396719662952291707L;
	private static final int SEARCH_DEPTH = 20;

	private final EmbeddedGraphDatabase graphDb;
	private final LuceneIndexService indexService;
	private final LuceneFulltextQueryIndexService fulltextIndexService;

	public CoauthorDataService(EmbeddedGraphDatabase graphDb, LuceneIndexService indexService,
			LuceneFulltextQueryIndexService fulltextIndexService) throws RemoteException {
		super();
		this.graphDb = graphDb;
		this.indexService = indexService;
		this.fulltextIndexService = fulltextIndexService;
	}

	private Node findAuthorNode(String author) {
		try {
			return indexService.getSingleNode(BatchCreateGraph.AUTHOR_NAME_KEY, author);
		} catch (Exception e) {
			return null;
		}
	}

	public List<String> getAuthors(String searchQuery) throws RemoteException {
		List<String> result = new ArrayList<String>();

		Transaction tx = graphDb.beginTx();
		try {
			IndexHits<Node> hits = fulltextIndexService.getNodes(BatchCreateGraph.AUTHOR_NAME_KEY, searchQuery,
					Sort.RELEVANCE);
			for (Node n : hits) {
				result.add((String) n.getProperty(BatchCreateGraph.AUTHOR_NAME_KEY));
			}
			tx.success();
		} finally {
			tx.finish();
		}
		return result;
	}

	public List<String> getCoauthors(String author) throws RemoteException {
		List<String> result = new ArrayList<String>();

		Transaction tx = graphDb.beginTx();
		try {
			Node authorNode = findAuthorNode(author);

			if (authorNode == null) {
				// TODO(prouty): Test to ensure the transaction is exiting here.
				throw new IllegalArgumentException("Cannot find node for author: " + author);
			}

			Set<String> coauthors = new TreeSet<String>();
			for (Relationship r : authorNode.getRelationships()) {
				for (Relationship co : r.getStartNode().getRelationships()) {
					Node coNode = co.getEndNode();
					coauthors.add((String) coNode.getProperty(BatchCreateGraph.AUTHOR_NAME_KEY));
				}
			}

			coauthors.remove(authorNode.getProperty(BatchCreateGraph.AUTHOR_NAME_KEY));
			result.addAll(coauthors);
		} finally {
			tx.finish();
		}
		return result;
	}

	public List<String> getShortestPathBetweenAuthors(String a, String b) throws RemoteException {
		List<String> result = new ArrayList<String>();

		Transaction tx = graphDb.beginTx();
		try {
			Node aAuthorNode = findAuthorNode(a);
			Node bAuthorNode = findAuthorNode(b);

			if (aAuthorNode == null) {
				throw new IllegalArgumentException("Cannot find node for author: " + a);
			}
			if (bAuthorNode == null) {
				throw new IllegalArgumentException("Cannot find node for author: " + b);
			}

			FindPath path = new FindPath(aAuthorNode, bAuthorNode, SEARCH_DEPTH, Direction.BOTH,
					BatchCreateGraph.CoauthorRelationshipTypes.AUTHOR);

			for (Node n : path.getPathAsNodes()) {
				if (((Integer) n.getProperty(BatchCreateGraph.NODE_TYPE)) == BatchCreateGraph.NodeTypes.AUTHOR
						.ordinal()) {
					result.add((String) n.getProperty(BatchCreateGraph.AUTHOR_NAME_KEY));
				} else if (((Integer) n.getProperty(BatchCreateGraph.NODE_TYPE)) == BatchCreateGraph.NodeTypes.PUBLICATION
						.ordinal()) {
					result.add((String) n.getProperty(BatchCreateGraph.PUBLICATION_TITLE_KEY));
				}
			}
		} finally {
			tx.finish();
		}
		return result;
	}
}
