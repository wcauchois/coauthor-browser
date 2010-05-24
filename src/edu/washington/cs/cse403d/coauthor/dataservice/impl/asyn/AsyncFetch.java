package edu.washington.cs.cse403d.coauthor.dataservice.impl.asyn;

public abstract class AsyncFetch<T> extends Thread {
	protected RuntimeException e;
	protected T result;

	public T blockForResult() {
		try {
			join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (e != null) {
			throw e;
		}

		return result;
	}
}
