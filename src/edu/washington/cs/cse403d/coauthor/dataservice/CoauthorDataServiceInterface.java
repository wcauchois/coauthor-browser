package edu.washington.cs.cse403d.coauthor.dataservice;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Defines the interface of the remote data provider.
 * 
 * @author Jeff Prouty
 */
public interface CoauthorDataServiceInterface extends Remote {
	public String SERVICE_NAME = "CoauthorDataService";

	/**
	 * Get a list of authors that match a fulltext search.
	 * 
	 * See http://lucene.apache.org/java/2_9_1/queryparsersyntax.html for syntax
	 * on allowable queries.
	 * 
	 * @param searchQuery
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getAuthors(String searchQuery) throws RemoteException;

	/**
	 * Given a valid author, get a list of coauthors with a coauthor distance of
	 * 1 from this author.
	 * 
	 * @param author
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getCoauthors(String author) throws RemoteException;

	/**
	 * DANGER: THIS IS EXTREMELY EXPENSIVE. Therefore it is deactivated to
	 * prevent the data provider from hanging
	 * 
	 * Given a valid author, get a list of coauthors with a coauthor distance of
	 * distance from this author.
	 * 
	 * @param author
	 * @param distance
	 * @return
	 * @throws RemoteException
	 */
	// public List<String> getCoauthors(String author, Integer distance) throws
	// RemoteException;

	/**
	 * Finds a shortest path (if it exists) between authorName1 and authorName2.
	 * Returns a chain of:
	 * 
	 * authorName1 (-> publicationI -> authorI)+
	 * 
	 * Each author and publication title is a new entry in the list.
	 * 
	 * @param authorName1
	 * @param authorName2
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getShortestPathBetweenAuthors(String authorName1, String authorName2) throws RemoteException;

	/**
	 * Given a list of authors, find the set of publications that all authors
	 * have collaborated on.
	 * 
	 * @param author
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getPublicationsForAllAuthors(String... author) throws RemoteException;

	/**
	 * Given a list of authors, find the set of publications that any of the
	 * authors have collaborated on.
	 * 
	 * @param author
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getPublicationsForAnyAuthor(String... author) throws RemoteException;
}
