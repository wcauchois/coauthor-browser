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
	 * Given a valid author, get a list of coauthors with a coauthordistance of
	 * 1 from this author.
	 * 
	 * @param author
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getCoauthors(String author) throws RemoteException;

	/**
	 * Finds a shortest path (if it exists) between authorName1 and authorName2.
	 * 
	 * @param authorName1
	 * @param authorName2
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getShortestPathBetweenAuthors(String authorName1, String authorName2) throws RemoteException;
}
