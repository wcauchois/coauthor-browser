package edu.washington.cs.cse403d.coauthor.dataservice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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

	@Override
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

	private void growCoauthorsOneLevel(Set<String> coauthors, Set<Node> frontier, Set<Long> visited) {
		Set<Node> oldFrontier = new HashSet<Node>(frontier);
		frontier.clear();

		for (Node frontierNode : oldFrontier) {
			for (Relationship r : frontierNode.getRelationships()) {
				if (!visited.contains(r.getId())) {
					// Mark this relation as visited
					visited.add(r.getId());
					for (Relationship co : r.getStartNode().getRelationships()) {
						if (!visited.contains(co.getId())) {
							// Mark this relation as visited
							visited.add(co.getId());
							Node coNode = co.getEndNode();
							frontier.add(coNode);
							coauthors.add((String) coNode.getProperty(BatchCreateGraph.AUTHOR_NAME_KEY));
						}
					}
				}
			}
		}
	}

	@Override
	public List<String> getCoauthors(String author) throws RemoteException {
		return getCoauthors(author, 1);
	}

	public List<String> getCoauthors(String author, Integer distance) throws RemoteException {
		if (distance < 0) {
			throw new IllegalArgumentException("Distance must be greater than 0");
		}

		List<String> result = new ArrayList<String>();

		Transaction tx = graphDb.beginTx();
		try {
			Node authorNode = findAuthorNode(author);

			if (authorNode == null) {
				// TODO(prouty): Test to ensure the transaction is exiting here.
				throw new IllegalArgumentException("Cannot find node for author: " + author);
			}

			Set<String> coauthors = new TreeSet<String>();
			Set<Node> frontier = new HashSet<Node>();
			Set<Long> visited = new HashSet<Long>();

			frontier.add(authorNode);

			for (int i = 0; i < distance; i++) {
				growCoauthorsOneLevel(coauthors, frontier, visited);
			}

			result.addAll(coauthors);
		} finally {
			tx.finish();
		}
		return result;
	}

	@Override
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

	@Override
	public List<String> getPublicationsForAllAuthors(String... authors) throws RemoteException {
		if (authors.length < 1) {
			throw new IllegalArgumentException("Number of authors must be greater than 0");
		}

		Set<String> authorPubs = null;

		Transaction tx = graphDb.beginTx();
		try {
			for (String author : authors) {
				Node authorNode = findAuthorNode(author);

				if (authorNode == null) {
					// TODO(prouty): Test to ensure the transaction is exiting
					// here.
					throw new IllegalArgumentException("Cannot find node for author: " + author);
				}

				Set<String> thisAuthor = new HashSet<String>();
				for (Relationship r : authorNode.getRelationships()) {
					Node coNode = r.getStartNode();
					thisAuthor.add((String) coNode.getProperty(BatchCreateGraph.PUBLICATION_TITLE_KEY));
				}

				if (authorPubs == null) {
					// This is the first set of publications
					authorPubs = thisAuthor;
				} else {
					// Intersect this author's set with the previous author's
					// sets
					authorPubs.retainAll(thisAuthor);
				}
			}
		} finally {
			tx.finish();
		}

		List<String> result = new ArrayList<String>();

		if (authorPubs != null) {
			result.addAll(authorPubs);
			Collections.sort(result);
		}

		return result;
	}

	@Override
	public List<String> getPublicationsForAnyAuthor(String... authors) throws RemoteException {
		if (authors.length < 1) {
			throw new IllegalArgumentException("Number of authors must be greater than 0");
		}

		Set<String> authorPubs = new TreeSet<String>();

		Transaction tx = graphDb.beginTx();
		try {
			for (String author : authors) {
				Node authorNode = findAuthorNode(author);

				if (authorNode == null) {
					// TODO(prouty): Test to ensure the transaction is exiting
					// here.
					throw new IllegalArgumentException("Cannot find node for author: " + author);
				}

				for (Relationship r : authorNode.getRelationships()) {
					Node coNode = r.getStartNode();
					authorPubs.add((String) coNode.getProperty(BatchCreateGraph.PUBLICATION_TITLE_KEY));
				}
			}
		} finally {
			tx.finish();
		}

		List<String> result = new ArrayList<String>();
		result.addAll(authorPubs);
		return result;
	}
}
