package edu.washington.cs.cse403d.coauthor.dataservice.impl.asyn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.washington.cs.cse403d.coauthor.dataservice.impl.PreparedStatementPool;

public class GetAuthorId extends AsyncFetch<Long> {
	private final PreparedStatementPool psp;
	private String authorName;

	public GetAuthorId(String authorName, PreparedStatementPool psp) {
		super();

		this.psp = psp;
		this.authorName = authorName;

		start();
	}

	@Override
	public void run() {
		if (authorName == null || authorName.isEmpty()) {
			e = new IllegalArgumentException("Must provide a non-empty author name");
			return;
		}
		
		PreparedStatement getAuthorId = psp.leasePreparedStatement();

		try {
			getAuthorId.setString(1, authorName);
			ResultSet rs = getAuthorId.executeQuery();

			if (rs.next()) {
				result = rs.getLong(1);
			} else {
				this.e = new IllegalArgumentException("No author exists in DB with name: " + authorName);
			}
			rs.close();
		} catch (SQLException e) {
			this.e = new RuntimeException(e);
		} finally {
			psp.returnPreparedStatement(getAuthorId);
		}
	}
}
