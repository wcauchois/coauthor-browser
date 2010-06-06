package edu.washington.cs.cse403d.coauthor.dataservice.impl.asyn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.dataservice.impl.PreparedStatementPool;

public class GetPublicationAuthors extends AsyncFetch<List<String>> {
	private final PreparedStatementPool psp;
	private long publicationId;

	public GetPublicationAuthors(long publicationId, PreparedStatementPool psp) {
		super();

		this.psp = psp;
		this.publicationId = publicationId;

		start();
	}

	@Override
	public void run() {
		result = new ArrayList<String>();

		PreparedStatement getPubs = psp.leasePreparedStatement();
		try {
			getPubs.setLong(1, publicationId);
			ResultSet rs = getPubs.executeQuery();

			while (rs.next()) {
				result.add(rs.getString(1));
			}
			rs.close();
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error: " + e.getLocalizedMessage() + " SQLSTATE: " + e.getSQLState());
		} finally {
			psp.returnPreparedStatement(getPubs);
		}
	}
}
