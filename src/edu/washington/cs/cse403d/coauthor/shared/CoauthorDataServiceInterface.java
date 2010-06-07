package edu.washington.cs.cse403d.coauthor.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * Defines the interface of the remote data provider.
 * 
 * @author Jeff Prouty
 * @version 2
 */
public interface CoauthorDataServiceInterface extends Remote {
	public String SERVICE_NAME = "CoauthorDataServiceV3";

	/**
	 * Get a list of authors that match a fulltext search.
	 * 
	 * The given query is split on whitespace and then only authors that match
	 * ALL terms (prefix atleast) are returned.
	 * 
	 * @param searchQuery
	 * @param limit
	 *            Number of suggestions to limit to.
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getAuthors(String searchQuery) throws RemoteException;

	/**
	 * Get a list of authors that match a fulltext search. Limits the search to
	 * only 50 to give low latency results.
	 * 
	 * The given query is split on whitespace and then only authors that match
	 * ALL terms (prefix atleast) are returned.
	 * 
	 * @param searchQuery
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getAuthorSuggestions(String searchQuery) throws RemoteException;

	/**
	 * Given a valid author, get a list of coauthors with a coauthor distance of
	 * 1 from this author. This query can probably be optimized more.
	 * 
	 * @param author
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getCoauthors(String author) throws RemoteException;

	/**
	 * Finds one shortest path (if it exists) between authorName1 and
	 * authorName2. Returns a list of PathLink objects. Only will the PathLink
	 * objects have publication lists populated if the populatePublications
	 * argument is true.
	 * 
	 * @param authorName1
	 * @param authorName2
	 * @param populatePublications
	 * @return Null if no path found.
	 * @throws RemoteException
	 */
	public List<PathLink> getOneShortestPathBetweenAuthors(String authorName1, String authorName2,
			boolean populatePublications) throws RemoteException;

	/**
	 * Finds all shortest paths (if it exists) between authorName1 and
	 * authorName2. Returns a list of the shortest paths. Each shortest path is
	 * a list of PathLink objects. Only will the PathLink objects have
	 * publication lists populated if the populatePublications argument is true.
	 * 
	 * @param authorName1
	 * @param authorName2
	 * @param populatePublications
	 * @return Null if no path(s) found.
	 * @throws RemoteException
	 */
	public List<List<PathLink>> getAllShortestPathsBetweenAuthors(String authorName1, String authorName2,
			boolean populatePublications) throws RemoteException;

	/**
	 * Given a list of authors, find the set of publications that all authors
	 * have collaborated on.
	 * 
	 * @param author
	 * @return
	 * @throws RemoteException
	 */
	public List<Publication> getPublicationsForAllAuthors(String... author) throws RemoteException;

	/**
	 * Given a list of authors, find the set of publications that any of the
	 * authors have collaborated on.
	 * 
	 * @param author
	 * @return
	 * @throws RemoteException
	 */
	public List<Publication> getPublicationsForAnyAuthor(String... author) throws RemoteException;

	/**
	 * Given a title, return the publication that matches the title exactly.
	 * 
	 * @param publicationTitle
	 * @return
	 * @throws RemoteException
	 */
	public Publication getPublication(String publicationTitle) throws RemoteException;

	/**
	 * Given a title query, return all publications that partially match title.
	 * 
	 * @param searchQuery
	 * @return
	 * @throws RemoteException
	 */
	public List<Publication> getPublications(String searchQuery) throws RemoteException;

	/**
	 * Given a String of words, search for publications whose title contains all
	 * of the given words (as prefixes). Very much like the getAuthors method.
	 * 
	 * @param searchQuery
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getPublicationTitles(String searchQuery) throws RemoteException;

	/**
	 * Given a String of words, search for publications whose title contains all
	 * of the given words (as prefixes). Very much like the getAuthors method.
	 * Limits the search to only 50 to give low latency results.
	 * 
	 * @param searchQuery
	 * @param limit
	 *            Number of suggestions to limit to.
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getPublicationTitleSuggestions(String searchQuery) throws RemoteException;
}
