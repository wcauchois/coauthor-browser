package edu.washington.cs.cse403d.coauthor.uiprototype;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates the "history" of a browser type system -- that is, which pages
 * were viewed before the current one (and if we have already gone backwards in
 * history, which pages were after the current one).
 * @see BrowserPage 
 * @author Bill Cauchois
 */
public class BrowserHistory {
	public static final int MAX = 5;
	/**
	 * A circular array of a fixed capacity where the indices wrap around.
	 */
	// Although this class is specific to the implementation of BrowserHistory,
	// it has package access so that the test fixture (BrowserHistoryTest) can
	// use it.
	static class CircularArray {
		private BrowserPage[] data;
		private int size = 0; // The current size of the array
		public int head = 0; // The index of the first element in the array
		public int capacity; // The fixed capacity of the array
		/**
		 * Initialize a circular array with the provided capacity.
		 */
		public CircularArray(int capacity) {
			this.capacity = capacity;
			data = new BrowserPage[capacity];
		}
		/**
		 * Print debug information about the array to a PrintStream.
		 */
		public void dump(PrintStream out) {
			out.println("data = " + Arrays.toString(data));
			out.println("size = " + size);
			out.println("head = " + head);
		}
		/**
		 * Get the element at index <i>i</i> in the array. Note that <i>i</i> can
		 * be negative, in which case it indicates an offset from the end of the
		 * array (i.e. -1 is the last element).
		 * 
		 * <b>Precondition</b>: The array is not empty and <i>i</i> is between
		 * 0 and size() - 1, or between -1 and -size(). 
		 */
		public BrowserPage get(int i) {
			if(i < 0) i += size;
			return data[(head + i) % capacity];
		}
		/**
		 * Add an element to the array. If the array is already full, this will
		 * overwrite the first element of the array. If the array is not full, this
		 * will cause the size to increase.
		 */
		public void add(BrowserPage elem) {
			data[(head + size) % capacity] = elem;
			if(size < MAX) size++;
			else head = (head + 1) % capacity;
		}
		/**
		 * Remove the last element from the array.
		 * <b>Postcondition:</b> The array size is decreased by 1.
		 */
		public void pop() {
			if(isEmpty())
				throw new RuntimeException("can't remove element from empty array");
			size--;
		}
		/**
		 * Returns whether the array is empty.
		 * @return true if size() == 0.
		 */
		public boolean isEmpty() {
			return size == 0;
		}
		/**
		 * Returns the size of the array (this may be less than the capacity if
		 * the array has not yet been filled).
		 */
		public int size() {
			return size;
		}
		public List<BrowserPage> list() {
			return Arrays.asList(data);
		}
	}
	// INVARIANT: This array can never be empty
	private CircularArray pages = new CircularArray(MAX);
	// INVARIANT: cursor is between -1 and -pages.size()
	private int cursor = -1;
	/**
	 * Initialize the BrowserHistory with a single page, the current page.
	 * 
	 * <b>Postcondition:</b> hasPrevious() and hasNext() are both false.
	 */
	public BrowserHistory(BrowserPage initialPage) {
		pages.add(initialPage);
	}
	/**
	 * Add a page so that it becomes the most recent one. If the history is
	 * already full, the least recent page will be forgotten. If we have already
	 * gone backwards in history (i.e. hasNext() is true), then those pages
	 * more recent than the current page will be forgotten.
	 * 
	 * <b>Postcondition</b>: hasPrevious() is true.
	 * @return the passed-in page
	 */
	public BrowserPage push(BrowserPage page) {
		if(page != getCurrent()) {
			while(cursor < -1) {
				pages.pop();
				cursor++;
			}
			pages.add(page);
		}
		return page;
	}
	/**
	 * Returns whether there is a previous page in the history. If true, then
	 * it is safe to call back().
	 */
	public boolean hasPrevious() {
		return (cursor - 1) >= -pages.size();
	}
	/**
	 * Returns whether there is a next page in the history. If true, then it
	 * is safe to call forward().
	 */
	public boolean hasNext() {
		return cursor + 1 < 0;
	}
	/**
	 * Go backwards in history.
	 * 
	 * <b>Precondition:</b> hasPrevious() is true.
	 * @return the new current page
	 * @throws RuntimeException if we can't go backwards in history
	 */
	public BrowserPage back() {
		if(!hasPrevious())
			throw new RuntimeException("can't go backwards in history");
		return pages.get(--cursor);
	}
	/**
	 * Go forwards in history.
	 * 
	 * <b>:Precondition</b>: hasNext() is true.
	 * @return the new current page
	 * @throws RuntimeException if we can't go forwards in history
	 */
	public BrowserPage forward() {
		if(!hasNext())
			throw new RuntimeException("can't go forwards in history");
		return pages.get(++cursor);
	}
	/**
	 * Gets the current page (i.e. the page presented to the user).
	 */
	public BrowserPage getCurrent() {
		return pages.get(cursor);
	}
	/**
	 * Returns whether the history is full.
	 */
	public boolean isFull() {
		return pages.size() == MAX;
	}
	
	private class BackwardsIterator implements Iterator<BrowserPage> {
		private int pos;
		public BackwardsIterator() {
			pos = cursor;
		}
		@Override public boolean hasNext() {
			return pos >= -pages.size();
		}
		@Override public BrowserPage next() {
			return pages.get(pos--);
		}
		@Override public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	public Iterator<BrowserPage> backwardsIterator() {
		return new BackwardsIterator();
	}
	public boolean contains(BrowserPage page) {
		return pages.list().contains(page);
	}
}