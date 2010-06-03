package edu.washington.cs.cse403d.coauthor.dataservice.impl.asyn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.washington.cs.cse403d.coauthor.dataservice.impl.PreparedStatementPool;

public class GetAuthorName extends AsyncFetch<String> {
	private final PreparedStatementPool psp;
	private long authorId;

	public GetAuthorName(long authorId, PreparedStatementPool psp) {
		super();

		this.psp = psp;
		this.authorId = authorId;

		start();
	}

	@Override
	public void run() {
		PreparedStatement getAuthor = psp.leasePreparedStatement();
		try {
			getAuthor.setLong(1, authorId);
			ResultSet rs = getAuthor.executeQuery();

			if (rs.next()) {
				result = rs.getString(1);
			} else {
				throw new IllegalStateException("No such author for id: " + authorId);
			}
			rs.close();
		} catch (SQLException e) {
			this.e = new IllegalStateException("SQL Error: " + e.getLocalizedMessage() + " SQLSTATE: "
					+ e.getSQLState());
		} finally {
			psp.returnPreparedStatement(getAuthor);
		}
	}
}
