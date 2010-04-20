package edu.washington.cs.cse403d.coauthor.dataservice.graphdb;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.index.lucene.LuceneFulltextIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserter;
import org.neo4j.index.lucene.LuceneIndexBatchInserterImpl;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;

/**
 * This file contains a bunch of static things related to the graph database. I
 * currently don't like this setup. It will be refactored soon.
 * 
 * @author Jeff Prouty
 */
public class BatchCreateGraph {
	public static final String DB_PATH = "neo4j-store";
	public static final String NODE_TYPE = "node_type";
	public static final String AUTHOR_NAME_KEY = "author_name";
	public static final String PUBLICATION_TITLE_KEY = "publication_title";
	public static final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();

	public static BatchInserter batchInserter;
	public static LuceneFulltextIndexBatchInserter fullTextInserter;
	public static LuceneIndexBatchInserter indexInserter;

	public static enum NodeTypes implements RelationshipType {
		AUTHOR, PUBLICATION;
	}

	public static enum CoauthorRelationshipTypes implements RelationshipType {
		AUTHOR;
	}

	public static long createAuthor(String name) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(NODE_TYPE, NodeTypes.AUTHOR.ordinal());
		properties.put(AUTHOR_NAME_KEY, name);
		long id = batchInserter.createNode(properties);
		indexInserter.index(id, AUTHOR_NAME_KEY, name);
		fullTextInserter.index(id, AUTHOR_NAME_KEY, name);
		return id;
	}

	public static long createPublication(String title) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(NODE_TYPE, NodeTypes.PUBLICATION.ordinal());
		properties.put(PUBLICATION_TITLE_KEY, title);
		long id = batchInserter.createNode(properties);
		indexInserter.index(id, PUBLICATION_TITLE_KEY, title);
		fullTextInserter.index(id, PUBLICATION_TITLE_KEY, title);
		return id;
	}

	public static void createAuthorRelationship(long publicationNodeId, long authorNodeId) {
		batchInserter.createRelationship(publicationNodeId, authorNodeId, CoauthorRelationshipTypes.AUTHOR, EMPTY_MAP);
	}

	public static void openDb() {
		batchInserter = new BatchInserterImpl(DB_PATH);
		fullTextInserter = new LuceneFulltextIndexBatchInserter(batchInserter);
		indexInserter = new LuceneIndexBatchInserterImpl(batchInserter);
		registerShutdownHook();
	}

	public static void closeDb() {
		batchInserter.shutdown();
		fullTextInserter.shutdown();
		indexInserter.shutdown();
	}

	public static void registerShutdownHook() {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (batchInserter != null) {
					batchInserter.shutdown();
					fullTextInserter.shutdown();
					indexInserter.shutdown();
				}
			}
		});
	}
}
