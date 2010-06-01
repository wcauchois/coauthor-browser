package edu.washington.cs.cse403d.coauthor.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Specializes a SuggestionsField by adding the ability to populate the
 * suggestions from a backend query that is automatically run in a separate
 * thread (to ensure no lag from a user's perspective). To use this class:
 * instead of overloading getSuggestions(), overload issueQuery() -- which
 * has pretty much the same specification as getSuggestions() (i.e. it returns
 * a list of suggestions for a given fragment), except that it is expected to
 * take on the order of ~500 milliseconds to complete.
 * 
 * Issuing queries in the background is accomplished using an ExecutorService.
 * If you don't provide an ExecutorService in the constructor,
 * QuerySuggestionsField will by default construct a new single threaded
 * Executor for itself. If you expect the ExecutorService to change over
 * the lifetime of your QuerySuggestionsField instance, you should override
 * getExecutorService() instead (the default implementation returns the
 * internal executor service).
 * 
 * @author William Cauchois
 */
public abstract class QuerySuggestionsField extends SuggestionsField {
	private static final long serialVersionUID = -851636475744113831L;
	
	private static final int MIN_QUERY_LENGTH = 3;
	
	protected abstract List<String> issueQuery(String part) throws Exception;
	
	private ExecutorService myExecutor = null;
	private String query;
	private Future<List<String>> queryResults;
	
	protected List<String> getSuggestions(String text) {
		List<String> parts = new LinkedList<String>(Arrays.asList(text.split("[ .,]+")));
		
		// Remove any parts (like initials) that are shorter than 2 characters
		if(parts.size() > 2) {
			Iterator<String> iter = parts.iterator();
			while(iter.hasNext()) {
				if(iter.next().length() <= 1)
					iter.remove();
			}
		}
		
		final String queryPart = (parts.size() > 0) ? parts.get(0) : "";
		final String filterPart = (parts.size() > 1) ? parts.get(1) : "";
		
		List<String> results = new ArrayList<String>();
		if(!queryPart.isEmpty()
				&& queryPart.length() >= MIN_QUERY_LENGTH) {
			if(queryPart.equals(query)) {
				try {
					results.addAll(queryResults.get(0, TimeUnit.MILLISECONDS));
				} catch(Exception e) { }
			} else {
				query = queryPart;
				queryResults = getExecutorService().submit(new Callable<List<String>>() {
					public List<String> call() throws Exception {
						return issueQuery(queryPart);
					}
				});
				getExecutorService().execute(new Runnable() {
					public void run() {
						updateSuggestions();
					}
				});
			}
		}
		
		if(results.size() > 0 && filterPart.length() > 0) {
			List<String> filteredResults = new ArrayList<String>();
			String filter = filterPart.toLowerCase();
			for(String result : results) {
				if(result.toLowerCase().contains(filter))
					filteredResults.add(result);
			}
			results = filteredResults;
		}
		
		return results;
	}
	
	protected ExecutorService getExecutorService() {
		return myExecutor;
	}
	public QuerySuggestionsField(ExecutorService executorToUse) {
		myExecutor = executorToUse;
	}
	public QuerySuggestionsField() {
		myExecutor = Executors.newSingleThreadExecutor();
	}
}
