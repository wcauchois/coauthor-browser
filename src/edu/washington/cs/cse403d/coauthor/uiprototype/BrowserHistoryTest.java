package edu.washington.cs.cse403d.coauthor.uiprototype;

import static org.junit.Assert.*;

import org.junit.Test;

public class BrowserHistoryTest {
	private static class TestPage extends BrowserPage {
		public int id;
		public TestPage(int id) {
			this.id = id;
		}
		public String toString() {
			return "" + id;
		}
	}
	private int idOf(BrowserPage page) {
		return ((TestPage)page).id;
	}
	private static final int MAX = 5;
	@Test public void circularArray_simple() {
		BrowserHistory.CircularArray array = new BrowserHistory.CircularArray(MAX);
		array.add(new TestPage(0));
		assertEquals(idOf(array.get(0)), 0);
		array.add(new TestPage(1));
		assertEquals(idOf(array.get(1)), 1);
		array.pop();
		assertEquals(idOf(array.get(-1)), 0);
	}
	@Test public void circularArray_wrapAround() {
		BrowserHistory.CircularArray array = new BrowserHistory.CircularArray(MAX);
		for(int i = 0; i < MAX; i++)
			array.add(new TestPage(i));
		assertEquals(MAX - 1, idOf(array.get(-1)));
		assertEquals(0, idOf(array.get(0)));
		array.add(new TestPage(MAX));
		assertEquals(MAX, idOf(array.get(-1)));
		assertEquals(1, idOf(array.get(0)));
		
		array.pop();
		assertEquals(MAX - 1, idOf(array.get(-1)));
		array.pop();
		assertEquals(MAX - 2, idOf(array.get(-1)));
	}
	@Test public void simple() {
		BrowserHistory history = new BrowserHistory(new TestPage(0));
		assertEquals(0, idOf(history.getCurrent()));
		history.push(new TestPage(1));
		assertEquals(1, idOf(history.getCurrent()));
		
		assertEquals(0, idOf(history.back()));
		assertEquals(0, idOf(history.getCurrent()));
		assertEquals(1, idOf(history.forward()));
		assertEquals(1, idOf(history.getCurrent()));
	}
}