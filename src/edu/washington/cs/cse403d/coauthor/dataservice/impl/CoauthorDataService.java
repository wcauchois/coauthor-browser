package edu.washington.cs.cse403d.coauthor.dataservice.impl;

import java.io.FileNotFoundException;
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
import edu.washington.cs.cse403d.coauthor.dataservice.impl.asyn.GetAuthorId;
import edu.washington.cs.cse403d.coauthor.dataservice.impl.asyn.GetAuthorName;
import edu.washington.cs.cse403d.coauthor.dataservice.impl.asyn.GetPublicationAuthors;
import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * The data bindings for the data provider. Performs the lookups on the actual
 * graph.
 * 
 * @author Jeff Prouty
 * @version 3
 */
public class CoauthorDataService extends UnicastRemoteObject implements CoauthorDataServiceInterface {
	private static final long serialVersionUID = -5396719662952291707L;
	private static final int SEARCH_DEPTH = 20;

	private PrintStream logStream;
	private final Connection sqlConnection;
	private final EmbeddedGraphDatabase graphDb;

	private PreparedStatementPool psp_queryAuthorFulltextSuggestions;
	private PreparedStatementPool psp_queryAuthorsFulltext;
	private PreparedStatementPool psp_getAuthorId;
	private PreparedStatementPool psp_getAuthorName;
	private PreparedStatementPool psp_getCoauthors;

	private PreparedStatementPool psp_queryPublicationsFulltext;
	private PreparedStatementPool psp_queryPublicationsFulltextSuggestions;
	private PreparedStatementPool psp_getPublication;
	private PreparedStatementPool psp_getPublications;

	private PreparedStatementPool psp_getPublicationAuthors;

	public CoauthorDataService(Connection sqlConnection, EmbeddedGraphDatabase graphDb) throws RemoteException,
			SQLException {
		super();
		this.sqlConnection = sqlConnection;
		this.graphDb = graphDb;
		try {
			logStream = new PrintStream("dataProvider.log");
		} catch (FileNotFoundException e) {
			System.err.println("could not create log!");
			e.printStackTrace();
			logStream = System.out;
		}

		initPreparedStatementPools();
	}

	private void initPreparedStatementPools() throws SQLException {
		psp_queryAuthorsFulltext = new PreparedStatementPool(
				"SELECT [name] FROM [Authors] WHERE CONTAINS(name, ?) ORDER BY [name] ASC", sqlConnection);
		psp_queryPublicationsFulltext = new PreparedStatementPool(
				"SELECT [title] FROM [Publications] WHERE CONTAINS(title, ?) ORDER BY [title] ASC", sqlConnection);
		psp_queryAuthorFulltextSuggestions = new PreparedStatementPool(
				"SELECT TOP 50 [name] FROM [Authors] WHERE CONTAINS(name, ?)", sqlConnection);
		psp_queryPublicationsFulltextSuggestions = new PreparedStatementPool(
				"SELECT TOP 50 [title] FROM [Publications] WHERE CONTAINS(title, ?)", sqlConnection);
		psp_getAuthorId = new PreparedStatementPool("SELECT [id] FROM [Authors] WHERE [name] = ?", sqlConnection);
		psp_getAuthorName = new PreparedStatementPool("SELECT [name] FROM [Authors] WHERE [id] = ?", sqlConnection);
		psp_getCoauthors = new PreparedStatementPool("(SELECT a.name "
				+ "FROM [Authors] as a, [CoauthorRelations] as c " + "WHERE c.aid1 = ? AND c.aid2 = a.id) " + "UNION "
				+ "(SELECT a.name " + "FROM [Authors] as a, [CoauthorRelations] as c "
				+ "WHERE c.aid2 = ? AND c.aid1 = a.id)", sqlConnection);
		psp_getPublication = new PreparedStatementPool("SELECT * FROM [Publications] WHERE title = ?", sqlConnection);
		psp_getPublications = new PreparedStatementPool(
				"SELECT TOP 500 * FROM [Publications] WHERE CONTAINS(title, ?) ORDER BY [title] ASC", sqlConnection);
		psp_getPublicationAuthors = new PreparedStatementPool(
				"SELECT a.name FROM Authors AS a, PublicationAuthors AS pa WHERE pa.pid = ? AND pa.aid = a.id ORDER BY a.name ASC",
				sqlConnection);
	}

	private Publication createPublicationFromCurrentResult(ResultSet results) throws SQLException {
		return new Publication(results.getLong("id"), results.getString("title"), results.getString("pages"), results
				.getInt("year"), results.getString("url"), results.getString("ee"), results.getInt("volume"), results
				.getString("journal"), results.getString("isbn"), results.getString("publisher"), results
				.getInt("number"));
	}

	private void populatePublicationListWithAuthors(List<Publication> result) {
		GetPublicationAuthors[] async = new GetPublicationAuthors[result.size()];

		for (int i = 0; i < result.size(); ++i) {
			async[i] = new GetPublicationAuthors(result.get(i).getId(), psp_getPublicationAuthors);
		}

		for (int i = 0; i < result.size(); ++i) {
			result.get(i).setAuthors(async[i].blockForResult());
		}
	}

	private void processPath(List<Node> nodeList, List<PathLink> resultPath, boolean populatePublications) {
		GetAuthorName[] authorNameFetches = new GetAuthorName[nodeList.size()];

		for (int i = 0; i < nodeList.size(); ++i) {
			authorNameFetches[i] = new GetAuthorName(nodeList.get(i).getId(), psp_getAuthorName);
		}

		String[] authorNames = new String[nodeList.size()];
		for (int i = 0; i < nodeList.size(); ++i) {
			authorNames[i] = authorNameFetches[i].blockForResult();
		}

		long previousNodeId = nodeList.get(0).getId();
		String previousNodeName = authorNames[0];
		for (int i = 1; i < nodeList.size(); ++i) {
			long thisNodeId = nodeList.get(i).getId();
			String thisNodeName = authorNames[i];

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
		return getAuthorsHelper(searchQuery, psp_queryAuthorsFulltext);
	}

	@Override
	public List<String> getAuthorSuggestions(String searchQuery) throws RemoteException {
		List<String> results = getAuthorsHelper(searchQuery, psp_queryAuthorFulltextSuggestions);
		Collections.sort(results);
		return results;
	}

	private List<String> getAuthorsHelper(String searchQuery, PreparedStatementPool psp_pool) {
		if (searchQuery == null || searchQuery.isEmpty()) {
			throw new IllegalArgumentException("Must provide a non-empty searchQuery");
		}

		logStream.println("getAuthors(" + searchQuery + ")");

		List<String> result = new ArrayList<String>();

		StringBuilder containsParam = new StringBuilder();
		String[] queryParts = searchQuery.split("[\\s]");

		for (int i = 0; i < queryParts.length; ++i) {
			if (i != 0) {
				containsParam.append(" AND ");
			}
			containsParam.append('"' + queryParts[i] + " *\"");
		}

		PreparedStatement getAuthors = psp_pool.leasePreparedStatement();
		try {
			getAuthors.setString(1, containsParam.toString());
			ResultSet queryResults = getAuthors.executeQuery();

			while (queryResults.next()) {
				result.add(queryResults.getString(1));
			}
			queryResults.close();
		} catch (SQLException e) {
			e.printStackTrace(logStream);
			throw new IllegalStateException("Error querying data service: " + e.getMessage());
		} finally {
			psp_pool.returnPreparedStatement(getAuthors);
		}

		return result;
	}

	@Override
	public List<String> getCoauthors(String author) throws RemoteException {
		logStream.println("getCoauthors(" + author + ")");
		// Checks parameter and ensure it exists in the database. If anything is
		// wrong, exceptions are thrown
		GetAuthorId getAuthorId = new GetAuthorId(author, psp_getAuthorId);
		long authorId = getAuthorId.blockForResult();

		List<String> result = new ArrayList<String>();

		PreparedStatement getCoauthors = psp_getCoauthors.leasePreparedStatement();
		try {
			getCoauthors.setLong(1, authorId);
			getCoauthors.setLong(2, authorId);
			ResultSet queryResults = getCoauthors.executeQuery();

			while (queryResults.next()) {
				result.add(queryResults.getString(1));
			}
			queryResults.close();
		} catch (SQLException e) {
			e.printStackTrace(logStream);
			throw new IllegalStateException("Error querying data service: " + e.getMessage());
		} finally {
			psp_getCoauthors.returnPreparedStatement(getCoauthors);
		}

		Collections.sort(result);
		return result;
	}

	@Override
	public List<PathLink> getOneShortestPathBetweenAuthors(String authorA, String authorB, boolean populatePublications)
			throws RemoteException {
		logStream.println("getOneShortestPathBetweenAuthors(" + authorA + ", " + authorB + ")");

		GetAuthorId getAuthorIdA = new GetAuthorId(authorA, psp_getAuthorId);
		GetAuthorId getAuthorIdB = new GetAuthorId(authorB, psp_getAuthorId);
		long authorAId = getAuthorIdA.blockForResult();
		long authorBId = getAuthorIdB.blockForResult();

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
		} catch (Exception e) {
			e.printStackTrace(logStream);
		} finally {
			tx.finish();
		}
		return result;
	}

	public List<List<PathLink>> getAllShortestPathsBetweenAuthors(String authorA, String authorB,
			boolean populatePublications) throws RemoteException {
		logStream.println("getAllShortestPathsBetweenAuthors(" + authorA + ", " + authorB + ")");

		GetAuthorId getAuthorIdA = new GetAuthorId(authorA, psp_getAuthorId);
		GetAuthorId getAuthorIdB = new GetAuthorId(authorB, psp_getAuthorId);
		long authorAId = getAuthorIdA.blockForResult();
		long authorBId = getAuthorIdB.blockForResult();

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
		} catch (Exception e) {
			e.printStackTrace(logStream);
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

		GetAuthorId[] getAuthorIds = new GetAuthorId[authors.length];
		for (int i = 0; i < authors.length; ++i) {
			// Checks parameters and ensure each author exists in the database.
			// If anything is wrong, exceptions are thrown
			getAuthorIds[i] = new GetAuthorId(authors[i], psp_getAuthorId);
		}

		long[] authorIds = new long[authors.length];
		for (int i = 0; i < authors.length; ++i) {
			// Checks parameters and ensure each author exists in the database.
			// If anything is wrong, exceptions are thrown
			authorIds[i] = getAuthorIds[i].blockForResult();
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
		preparedQuery.append(" ORDER BY p.[title] ASC");

		try {
			PreparedStatement preparedStatement = sqlConnection.prepareStatement(preparedQuery.toString());

			for (int i = 0; i < authorIds.length; ++i) {
				preparedStatement.setLong(i + 1, authorIds[i]);
			}

			ResultSet results = preparedStatement.executeQuery();

			while (results.next()) {
				result.add(createPublicationFromCurrentResult(results));
			}
			results.close();

			populatePublicationListWithAuthors(result);
		} catch (SQLException e) {
			e.printStackTrace(logStream);
			throw new IllegalStateException("SQL Error: " + e.getLocalizedMessage() + " SQLSTATE: " + e.getSQLState());
		}

		return result;
	}

	@Override
	public List<Publication> getPublicationsForAnyAuthor(String... authors) throws RemoteException {
		if (authors.length < 1) {
			throw new IllegalArgumentException("Number of authors must be greater than 0");
		}

		logStream.println("getPublicationsForAnyAuthor(" + Arrays.toString(authors) + ")");

		GetAuthorId[] getAuthorIds = new GetAuthorId[authors.length];
		for (int i = 0; i < authors.length; ++i) {
			// Checks parameters and ensure each author exists in the database.
			// If anything is wrong, exceptions are thrown
			getAuthorIds[i] = new GetAuthorId(authors[i], psp_getAuthorId);
		}

		long[] authorIds = new long[authors.length];
		for (int i = 0; i < authors.length; ++i) {
			// Checks parameters and ensure each author exists in the database.
			// If anything is wrong, exceptions are thrown
			authorIds[i] = getAuthorIds[i].blockForResult();
		}

		String paramString = Arrays.toString(authorIds);

		List<Publication> result = new ArrayList<Publication>();

		String query = "SELECT DISTINCT p.* " + "FROM [Publications] AS p, [PublicationAuthors] AS pa "
				+ "WHERE pa.aid IN (" + paramString.substring(1, paramString.length() - 1)
				+ ") AND pa.pid = p.id ORDER BY p.[title] ASC";

		try {
			ResultSet results = sqlConnection.createStatement().executeQuery(query);

			while (results.next()) {
				result.add(createPublicationFromCurrentResult(results));
			}
			results.close();

			populatePublicationListWithAuthors(result);
		} catch (SQLException e) {
			e.printStackTrace(logStream);
			throw new IllegalStateException("SQL Error: " + e.getLocalizedMessage() + " SQLSTATE: " + e.getSQLState());
		}

		return result;
	}

	private List<String> getPublicationTitlesHelper(String searchQuery, PreparedStatementPool psp_pool) {
		if (searchQuery == null || searchQuery.length() < 4) {
			throw new IllegalArgumentException("The search query must be non-empty and atleast 4 characters");
		}

		logStream.println("getPublicationTitles(" + searchQuery + ")");

		List<String> result = new ArrayList<String>();

		StringBuilder containsParam = new StringBuilder();
		String[] queryParts = searchQuery.split("[\\s]");

		for (int i = 0; i < queryParts.length; ++i) {
			if (i != 0) {
				containsParam.append(" AND ");
			}
			containsParam.append('"' + queryParts[i] + " *\"");
		}

		PreparedStatement getPublicationTitles = psp_pool.leasePreparedStatement();
		try {
			getPublicationTitles.setString(1, containsParam.toString());
			ResultSet queryResults = getPublicationTitles.executeQuery();

			while (queryResults.next()) {
				result.add(queryResults.getString(1));
			}
			queryResults.close();
		} catch (SQLException e) {
			e.printStackTrace(logStream);
			throw new IllegalStateException("Error querying data service: " + e.getMessage());
		} finally {
			psp_pool.returnPreparedStatement(getPublicationTitles);
		}
		return result;
	}

	@Override
	public List<String> getPublicationTitles(String searchQuery) throws RemoteException {
		List<String> result = getPublicationTitlesHelper(searchQuery, psp_queryPublicationsFulltext);
		return result;
	}

	@Override
	public List<String> getPublicationTitleSuggestions(String searchQuery) throws RemoteException {
		List<String> results = getPublicationTitlesHelper(searchQuery, psp_queryPublicationsFulltextSuggestions);
		Collections.sort(results);
		return results;
	}

	@Override
	public Publication getPublication(String publicationTitle) throws RemoteException {
		if (publicationTitle == null || publicationTitle.isEmpty()) {
			throw new IllegalArgumentException("The publicationTitle must be non-empty");
		}

		logStream.println("getPublication(" + publicationTitle + ")");

		Publication result = null;

		PreparedStatement getPublication = psp_getPublication.leasePreparedStatement();
		try {
			getPublication.setString(1, publicationTitle);
			ResultSet queryResults = getPublication.executeQuery();

			if (!queryResults.next()) {
				throw new IllegalArgumentException("No publication exists in the database with title: "
						+ publicationTitle);
			} else {
				result = createPublicationFromCurrentResult(queryResults);
			}
			queryResults.close();

			populatePublicationListWithAuthors(Arrays.asList(result));
		} catch (SQLException e) {
			e.printStackTrace(logStream);
			throw new IllegalStateException("Error querying data service: " + e.getMessage());
		} finally {
			psp_getPublication.returnPreparedStatement(getPublication);
		}
		return result;
	}

	@Override
	public List<Publication> getPublications(String searchQuery) throws RemoteException {
		if (searchQuery == null || searchQuery.length() < 4) {
			throw new IllegalArgumentException("The search query must be non-empty and atleast 4 characters");
		}

		logStream.println("getPublications(" + searchQuery + ")");

		List<Publication> result = new ArrayList<Publication>();

		StringBuilder containsParam = new StringBuilder();
		String[] queryParts = searchQuery.split("[\\s]");

		for (int i = 0; i < queryParts.length; ++i) {
			if (i != 0) {
				containsParam.append(" AND ");
			}
			containsParam.append('"' + queryParts[i] + "\"");
		}

		PreparedStatement getPublications = psp_getPublications.leasePreparedStatement();
		try {
			getPublications.setString(1, containsParam.toString());
			ResultSet queryResults = getPublications.executeQuery();

			while (queryResults.next()) {
				result.add(createPublicationFromCurrentResult(queryResults));
			}
			queryResults.close();

			populatePublicationListWithAuthors(result);
		} catch (SQLException e) {
			e.printStackTrace(logStream);
			throw new IllegalStateException("Error querying data service: " + e.getMessage());
		} finally {
			psp_getPublications.returnPreparedStatement(getPublications);
		}
		return result;
	}
}
