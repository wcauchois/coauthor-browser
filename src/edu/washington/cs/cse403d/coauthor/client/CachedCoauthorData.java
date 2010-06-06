package edu.washington.cs.cse403d.coauthor.client;

import java.rmi.RemoteException;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.PathLink;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;

@SuppressWarnings("unchecked")
public class CachedCoauthorData implements CoauthorDataServiceInterface {
	private Map<Method, Map<List<Object>, Object>> memoize;
	private class CacheEntry {
		public List<Object> key;
		public Map<List<Object>, Object> table;
		public CacheEntry(List<Object> theKey, Map<List<Object>, Object> theTable) {
			key = theKey;
			table = theTable;
		}
	}
	private final int MAX_CACHE = 50;
	private Queue<CacheEntry> entries = new LinkedList<CacheEntry>();
	private CoauthorDataServiceInterface wrappedCDSI;
	public CachedCoauthorData(CoauthorDataServiceInterface cdsi) {
		wrappedCDSI = cdsi;
		memoize = new HashMap<Method, Map<List<Object>, Object>>();
		for(Method meth : CoauthorDataServiceInterface.class.getMethods())
			memoize.put(meth, new HashMap<List<Object>, Object>());
	}
	private Object getResult(Method meth, Object... args) throws RemoteException {
		while(entries.size() > MAX_CACHE) {
			CacheEntry toRemove = entries.poll();
			toRemove.table.remove(toRemove.key);
		}
		Map<List<Object>, Object> methTable = memoize.get(meth);
		List<Object> argsList = Arrays.asList(args);
		if(methTable.containsKey(argsList)) {
			return methTable.get(argsList);
		} else {
			try {
				Object result = meth.invoke(wrappedCDSI, args);
				
				methTable.put(argsList, result);
				entries.add(new CacheEntry(argsList, methTable));
				return result;
			} catch(InvocationTargetException e) {
				if(e.getCause() instanceof RemoteException)
					throw (RemoteException)e.getCause();
				else
					throw new RuntimeException("Failed to invoke wrapped method", e);
			} catch(IllegalAccessException e) {
				throw new RuntimeException("Failed to invoke wrapped method", e);
			}
		}
	}
	private Method getCDSIMethodUnsafe(String name, Class<?>... args) {
		try {
			return CoauthorDataServiceInterface.class.getMethod(name, args);
		} catch(NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<List<PathLink>> getAllShortestPathsBetweenAuthors(
			String authorName1, String authorName2, boolean populatePublications)
			throws RemoteException {
		return (List<List<PathLink>>)getResult(
				getCDSIMethodUnsafe("getAllShortestPathsBetweenAuthors",
						String.class, String.class, boolean.class),
				authorName1, authorName2, populatePublications);
	}

	@Override
	public List<String> getAuthorSuggestions(String searchQuery)
			throws RemoteException {
		return (List<String>)getResult(
				getCDSIMethodUnsafe("getAuthorSuggestions", String.class),
				searchQuery);
	}

	@Override
	public List<String> getAuthors(String searchQuery) throws RemoteException {
		return (List<String>)getResult(
				getCDSIMethodUnsafe("getAuthors", String.class),
				searchQuery);
	}

	@Override
	public List<String> getCoauthors(String author) throws RemoteException {
		return (List<String>)getResult(
				getCDSIMethodUnsafe("getCoauthors", String.class),
				author);
	}

	@Override
	public List<PathLink> getOneShortestPathBetweenAuthors(String authorName1,
			String authorName2, boolean populatePublications)
			throws RemoteException {
		return (List<PathLink>)getResult(
				getCDSIMethodUnsafe("getOneShortestPathBetweenAuthors",
						String.class, String.class, boolean.class),
				authorName1, authorName2, populatePublications);
	}
	@Override
	public Publication getPublication(String publicationTitle)
			throws RemoteException {
		return wrappedCDSI.getPublication(publicationTitle);
	}
	@Override
	public List<String> getPublicationTitleSuggestions(String searchQuery)
			throws RemoteException {
		return wrappedCDSI.getPublicationTitleSuggestions(searchQuery);
	}
	@Override
	public List<String> getPublicationTitles(String searchQuery)
			throws RemoteException {
		return wrappedCDSI.getPublicationTitles(searchQuery);
	}
	@Override
	public List<Publication> getPublications(String searchQuery)
			throws RemoteException {
		return wrappedCDSI.getPublications(searchQuery);
	}
	@Override
	public List<Publication> getPublicationsForAllAuthors(String... author)
			throws RemoteException {
		return (List<Publication>)getResult(
				getCDSIMethodUnsafe("getPublicationsForAllAuthors", String[].class),
				(Object[])author);
	}
	@Override
	public List<Publication> getPublicationsForAnyAuthor(String... author)
			throws RemoteException {
		return wrappedCDSI.getPublicationsForAnyAuthor(author);
	}
}
