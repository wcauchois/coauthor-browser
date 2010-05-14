package edu.washington.cs.cse403d.coauthor.dataservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Turns out the MS SQL Server JDBC driver allows multiple threads to execute
 * different PreparedStatements on the same connections. However, using the same
 * PreparedStatement at the same time causes real issues. Therefore, the problem
 * is solved by creating PreparedStatements as they are needed and pooling
 * unused statements.
 * 
 * IMPORTANT: When using this class, it is important to never close a
 * PreparedStatement acquired from the lease method. It is equally important to
 * return all preparedStatements when done.
 * 
 * @author Jeff Prouty
 */
public class PreparedStatementPool {
	public static final int DEFAULT_POOL_SIZE = 8;

	private final String sql;
	private final Connection conn;
	private final int maxPoolSize;
	private int currentPoolSize;
	private final BlockingQueue<PreparedStatement> queue;

	/**
	 * Create a PreparedStatementPool with the given sql statement, the
	 * connection, and the default max pool size.
	 * 
	 * @param sql
	 * @param conn
	 */
	public PreparedStatementPool(String sql, Connection conn) {
		this(sql, conn, DEFAULT_POOL_SIZE);
	}

	/**
	 * Create a PreparedStatementPool with the given sql statement, the
	 * connection, and max pool size.
	 * 
	 * @param sql
	 * @param conn
	 */
	public PreparedStatementPool(String sql, Connection conn, int maxSize) {
		if (maxSize < 1) {
			throw new IllegalArgumentException("maxSize must be > 0");
		}
		this.sql = sql;
		this.queue = new ArrayBlockingQueue<PreparedStatement>(maxSize);
		this.conn = conn;
		this.maxPoolSize = maxSize;
		this.currentPoolSize = 0;
	}

	/**
	 * @return The number of PreparedStatements that have been created by this
	 *         PreparedStatementPool
	 */
	public int getPoolSize() {
		return currentPoolSize;
	}

	/**
	 * Gets an unused PreparedStatement if any exist. If not, a new
	 * PreparedStatement is created if the max pool size hasn't been reached. If
	 * the max limit has been reached, then this call blocks until another
	 * thread returns a leased PreparedStatement
	 * 
	 * @return An unused PreparedStatement.
	 */
	public PreparedStatement leasePreparedStatement() {
		synchronized (this) {
			if (queue.isEmpty() && currentPoolSize < maxPoolSize) {
				currentPoolSize++;
				try {
					return conn.prepareStatement(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getLocalizedMessage());
		}
	}

	/**
	 * Returns a leased PreparedStatement to the pool.
	 * 
	 * IMPORTANT: Only return a PreparedStatement after any ResultSet has been
	 * used and closed. BUT DO NOT CLOSE THE PreparedStatement
	 * 
	 * @param ps
	 */
	public void returnPreparedStatement(PreparedStatement ps) {
		if (ps == null) {
			return;
		}
		try {
			if (ps.isClosed()) {
				throw new IllegalStateException("NEVER CLOSE YOUR PreparedStatements!!!");
			} else {
				queue.offer(ps);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}