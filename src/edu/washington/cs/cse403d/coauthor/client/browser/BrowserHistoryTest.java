package edu.washington.cs.cse403d.coauthor.client.browser;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.*;


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
	private static final int ARRAY_MAX = 5;
	private BrowserHistory history;
	private BrowserHistory.CircularArray array;
	@Before public void setUp() {
		history = new BrowserHistory(new TestPage(0));
		array = new BrowserHistory.CircularArray(5);
	}
	@Test public void circularArray_simple() {
		array.add(new TestPage(0));
		assertEquals(idOf(array.get(0)), 0);
		array.add(new TestPage(1));
		assertEquals(idOf(array.get(1)), 1);
		array.pop();
		assertEquals(idOf(array.get(-1)), 0);
	}
	@Test public void circularArray_wrapAround() {
		for(int i = 0; i < ARRAY_MAX; i++)
			array.add(new TestPage(i));
		assertEquals(ARRAY_MAX - 1, idOf(array.get(-1)));
		assertEquals(0, idOf(array.get(0)));
		array.add(new TestPage(ARRAY_MAX));
		assertEquals(ARRAY_MAX, idOf(array.get(-1)));
		assertEquals(1, idOf(array.get(0)));
		
		array.pop();
		assertEquals(ARRAY_MAX - 1, idOf(array.get(-1)));
		array.pop();
		assertEquals(ARRAY_MAX - 2, idOf(array.get(-1)));
	}
	@Test public void simple() {
		assertEquals(0, idOf(history.getCurrent()));
		history.push(new TestPage(1));
		assertEquals(1, idOf(history.getCurrent()));
		
		assertEquals(0, idOf(history.back()));
		assertEquals(0, idOf(history.getCurrent()));
		assertEquals(1, idOf(history.forward()));
		assertEquals(1, idOf(history.getCurrent()));
	}
	@Test public void backwardsIterator() {
		history.push(new TestPage(1));
		history.push(new TestPage(2));
		Iterator<BrowserPage> iter = history.backwardsIterator();
		assertTrue(iter.hasNext());
		assertEquals(2, idOf(iter.next()));
		assertTrue(iter.hasNext());
		assertEquals(1, idOf(iter.next()));
		assertTrue(iter.hasNext());
		assertEquals(0, idOf(iter.next()));
		assertFalse(iter.hasNext());
	}
	@Test public void hasNextPrevious() {
		assertFalse(history.hasPrevious());
		assertFalse(history.hasNext());
		history.push(new TestPage(1));
		assertTrue(history.hasPrevious());
		assertFalse(history.hasNext());
		history.back();
		assertFalse(history.hasPrevious());
		assertTrue(history.hasNext());
	}
}