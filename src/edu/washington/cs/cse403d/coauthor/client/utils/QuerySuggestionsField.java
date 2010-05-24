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

public abstract class QuerySuggestionsField extends SuggestionsField {
	private static final long serialVersionUID = -851636475744113831L;
	
	private static final int MIN_QUERY_LENGTH = 4;
	
	protected abstract List<String> issueQuery(String part) throws Exception;
	
	private ExecutorService executor;
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
		if(filterPart.length() > 0 || text.endsWith(" ")) {
			if(!queryPart.isEmpty()
					&& queryPart.length() >= MIN_QUERY_LENGTH) {
				if(queryPart.equals(query)) {
					try {
						results.addAll(queryResults.get(0, TimeUnit.MILLISECONDS));
					} catch(Exception e) { }
				} else {
					query = queryPart;
					queryResults = executor.submit(new Callable<List<String>>() {
						public List<String> call() throws Exception {
							return issueQuery(queryPart);
						}
					});
					executor.execute(new Runnable() {
						public void run() {
							updateSuggestions();
						}
					});
				}
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
	
	public QuerySuggestionsField() {
		executor = Executors.newSingleThreadExecutor();
	}
}
