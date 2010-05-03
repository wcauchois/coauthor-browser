package edu.washington.cs.cse403d.coauthor.dataservice;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.neo4j.graphalgo.shortestpath.FindPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import edu.washington.cs.cse403d.coauthor.dataservice.datasources.dblp.Parse;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * The data bindings for the data provider. Performs the lookups on the actual
 * graph.
 * 
 * @author Jeff Prouty
 * @version 2
 */
public class CoauthorDataService extends UnicastRemoteObject implements CoauthorDataServiceInterface {
	private static final long serialVersionUID = -5396719662952291707L;
	private static final int SEARCH_DEPTH = 20;

	private final PrintStream logStream;
	private final Connection sqlConnection;
	private final EmbeddedGraphDatabase graphDb;

	private PreparedStatement ps_queryAuthorsFulltext;
	private PreparedStatement ps_getAuthor;
	private PreparedStatement ps_getAuthorName;
	private PreparedStatement ps_getCoauthors;

	public CoauthorDataService(Connection sqlConnection, EmbeddedGraphDatabase graphDb) throws RemoteException,
			SQLException {
		super();
		this.sqlConnection = sqlConnection;
		this.graphDb = graphDb;
		logStream = System.out;

		prepareStatements();
	}

	private void prepareStatements() throws SQLException {
		ps_queryAuthorsFulltext = sqlConnection
				.prepareStatement("SELECT [name] FROM [Authors] WHERE CONTAINS(name, ?) ORDER BY [name] ASC");
		ps_getAuthor = sqlConnection.prepareStatement("SELECT [id] FROM [Authors] WHERE [name] = ?");
		ps_getAuthorName = sqlConnection.prepareStatement("SELECT [name] FROM [Authors] WHERE [id] = ?");
		ps_getCoauthors = sqlConnection.prepareStatement("(SELECT a.name "
				+ "FROM [Authors] as a, [CoauthorRelations] as c " + "WHERE c.aid1 = ? AND c.aid2 = a.id) " + "UNION "
				+ "(SELECT a.name " + "FROM [Authors] as a, [CoauthorRelations] as c "
				+ "WHERE c.aid2 = ? AND c.aid1 = a.id)");
	}

	private long getAuthorId(String authorName) {
		if (authorName == null || authorName.isEmpty()) {
			throw new IllegalArgumentException("Must provide a non-empty author name");
		}
		logStream.println("getAuthorId(" + authorName + ")");

		try {
			ps_getAuthor.setString(1, authorName);
			ResultSet rs = ps_getAuthor.executeQuery();

			if (!rs.next()) {
				throw new IllegalStateException("No such author: " + authorName);
			} else {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error: " + e.getLocalizedMessage() + " SQLSTATE: " + e.getSQLState());
		}
	}

	private String getAuthorName(long authorId) {
		logStream.println("getAuthorName(" + authorId + ")");
		try {
			ps_getAuthorName.setLong(1, authorId);
			ResultSet rs = ps_getAuthorName.executeQuery();

			if (!rs.next()) {
				throw new IllegalStateException("No such author for id: " + authorId);
			} else {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error: " + e.getLocalizedMessage() + " SQLSTATE: " + e.getSQLState());
		}
	}

	private Publication createPublicationFromCurrentResult(ResultSet results) throws SQLException {
		return new Publication(results.getString("title"), results.getString("pages"), results.getInt("year"), results
				.getString("url"), results.getString("ee"), results.getInt("volume"), results.getString("journal"),
				results.getString("isbn"), results.getString("publisher"), results.getInt("number"));
	}

	private void processPath(List<Node> nodeList, List<PathLink> resultPath, boolean populatePublications) {
		long previousNodeId = nodeList.get(0).getId();
		String previousNodeName = getAuthorName(previousNodeId);
		for (int i = 1; i < nodeList.size(); ++i) {
			long thisNodeId = nodeList.get(i).getId();
			String thisNodeName = getAuthorName(thisNodeId);

			PathLink thisLink;
			if (populatePublications) {
				long[] linkIds = { previousNodeId, thisNodeId };
				thisLink = new PathLink(previousNodeName, thisNodeName, getPublicationsForAllAuthors(linkIds));
			} else {
				thisLink = new PathLink(previousNodeName, thisNodeName);
			}
			resultPath.add(thisLink);

			previousNodeId = thisNodeId;
			previousNodeName = thisNodeName;
		}
	}

	@Override
	public List<String> getAuthors(String searchQuery) throws RemoteException {
		if (searchQuery == null || searchQuery.isEmpty()) {
			throw new IllegalArgumentException("Must provide a non-empty searchQuery");
		}

		logStream.println("searchQuery(" + searchQuery + ")");

		List<String> result = new ArrayList<String>();

		StringBuilder containsParam = new StringBuilder();
		String[] queryParts = searchQuery.split("[\\s]");

		for (int i = 0; i < queryParts.length; ++i) {
			if (i != 0) {
				containsParam.append(" AND ");
			}
			containsParam.append('"' + queryParts[i] + " *\"");
		}

		try {
			ps_queryAuthorsFulltext.setString(1, containsParam.toString());
			ResultSet queryResults = ps_queryAuthorsFulltext.executeQuery();

			while (queryResults.next()) {
				result.add(queryResults.getString(1));
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Error querying data service: " + e.getMessage());
		}

		return result;
	}

	@Override
	public List<String> getCoauthors(String author) throws RemoteException {
		logStream.println("getCoauthors(" + author + ")");
		// Checks parameter and ensure it exists in the database. If anything is
		// wrong, exceptions are thrown
		long authorId = getAuthorId(author);

		List<String> result = new ArrayList<String>();

		try {
			ps_getCoauthors.setLong(1, authorId);
			ps_getCoauthors.setLong(2, authorId);
			ResultSet queryResults = ps_getCoauthors.executeQuery();

			while (queryResults.next()) {
				result.add(queryResults.getString(1));
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Error querying data service: " + e.getMessage());
		}

		Collections.sort(result);
		return result;
	}

	@Override
	public List<PathLink> getOneShortestPathBetweenAuthors(String authorA, String authorB, boolean populatePublications)
			throws RemoteException {
		logStream.println("getOneShortestPathBetweenAuthors(" + authorA + ", " + authorB + ")");
		long authorAId = getAuthorId(authorA);
		long authorBId = getAuthorId(authorB);

		List<PathLink> result = new ArrayList<PathLink>();

		Transaction tx = graphDb.beginTx();
		try {
			Node authorANode = graphDb.getNodeById(authorAId);
			Node authorBNode = graphDb.getNodeById(authorBId);

			FindPath path = new FindPath(authorANode, authorBNode, SEARCH_DEPTH, Direction.BOTH,
					Parse.CoauthorRelationshipTypes.COAUTHOR);

			List<Node> results = path.getPathAsNodes();

			if (results.size() == 0) {
				return result;
			}

			processPath(results, result, populatePublications);

		} finally {
			tx.finish();
		}
		return result;
	}

	public List<List<PathLink>> getAllShortestPathsBetweenAuthors(String authorA, String authorB,
			boolean populatePublications) throws RemoteException {
		logStream.println("getAllShortestPathsBetweenAuthors(" + authorA + ", " + authorB + ")");
		long authorAId = getAuthorId(authorA);
		long authorBId = getAuthorId(authorB);

		List<List<PathLink>> result = new ArrayList<List<PathLink>>();

		Transaction tx = graphDb.beginTx();
		try {
			Node authorANode = graphDb.getNodeById(authorAId);
			Node authorBNode = graphDb.getNodeById(authorBId);

			FindPath path = new FindPath(authorANode, authorBNode, SEARCH_DEPTH, Direction.BOTH,
					Parse.CoauthorRelationshipTypes.COAUTHOR);

			List<List<Node>> results = path.getPathsAsNodes();

			if (results.size() == 0) {
				return result;
			}

			for (List<Node> onePath : results) {
				List<PathLink> thisPath = new ArrayList<PathLink>();
				processPath(onePath, thisPath, populatePublications);
				result.add(thisPath);
			}
		} finally {
			tx.finish();
		}
		return result;
	}

	@Override
	public List<Publication> getPublicationsForAllAuthors(String... authors) throws RemoteException {
		if (authors.length < 1) {
			throw new IllegalArgumentException("Number of authors must be greater than 0");
		}

		logStream.println("getPublicationsForAllAuthors(" + Arrays.toString(authors) + ")");

		long[] authorIds = new long[authors.length];
		for (int i = 0; i < authors.length; ++i) {
			// Checks parameters and ensure each author exists in the database.
			// If anything is wrong, exceptions are thrown
			authorIds[i] = getAuthorId(authors[i]);
		}

		return getPublicationsForAllAuthors(authorIds);
	}

	private List<Publication> getPublicationsForAllAuthors(long[] authorIds) {
		logStream.println("getPublicationsForAllAuthors(" + Arrays.toString(authorIds) + ")");

		List<Publication> result = new ArrayList<Publication>();

		StringBuilder preparedQuery = new StringBuilder();

		preparedQuery.append("SELECT p.* FROM [Publications] AS p");
		for (int i = 0; i < authorIds.length; ++i) {
			preparedQuery.append(", [PublicationAuthors] AS pa" + i);
		}
		preparedQuery.append(" WHERE ");
		for (int i = 0; i < authorIds.length; ++i) {
			if (i != 0) {
				preparedQuery.append(" AND ");
			}
			preparedQuery.append("pa" + i + ".aid = ? AND pa" + i + ".pid = p.id");
		}

		try {
			PreparedStatement preparedStatement = sqlConnection.prepareStatement(preparedQuery.toString());

			for (int i = 0; i < authorIds.length; ++i) {
				preparedStatement.setLong(i + 1, authorIds[i]);
			}

			ResultSet results = preparedStatement.executeQuery();

			while (results.next()) {
				result.add(createPublicationFromCurrentResult(results));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<Publication> getPublicationsForAnyAuthor(String... authors) throws RemoteException {
		if (authors.length < 1) {
			throw new IllegalArgumentException("Number of authors must be greater than 0");
		}

		logStream.println("getPublicationsForAnyAuthor(" + Arrays.toString(authors) + ")");

		long[] authorIds = new long[authors.length];
		for (int i = 0; i < authors.length; ++i) {
			// Checks parameters and ensure each author exists in the database.
			// If anything is wrong, exceptions are thrown
			authorIds[i] = getAuthorId(authors[i]);
		}
		String paramString = Arrays.toString(authorIds);

		List<Publication> result = new ArrayList<Publication>();

		String query = "SELECT DISTINCT p.* " + "FROM [Publications] AS p, [PublicationAuthors] AS pa "
				+ "WHERE pa.aid IN (" + paramString.substring(1, paramString.length() - 1) + ") AND pa.pid = p.id";

		try {
			ResultSet results = sqlConnection.createStatement().executeQuery(query);

			while (results.next()) {
				result.add(createPublicationFromCurrentResult(results));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
}
