package edu.washington.cs.cse403d.coauthor.dataservice.datasources.dblp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 * This is a one off class that is used for reconstructing the neo4j graph
 * database from only the SQL data base.
 * 
 * Note: Be sure to change the hard coded paths when using this utility
 * 
 * @author Jeff Prouty
 */
public class CreateGraphFromSql {
	public static enum CoauthorRelationshipTypes implements RelationshipType {
		COAUTHOR;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SQLServerDataSource sqlServerDataSource = new SQLServerDataSource();

			sqlServerDataSource.setUser("cse403d");
			sqlServerDataSource.setPassword("DI9u4ICOQPjaumFB");
			sqlServerDataSource.setDatabaseName("403d");
			sqlServerDataSource.setServerName("iprojsrv.cs.washington.edu");

			BatchInserter batchInserter = new BatchInserterImpl("graphDB");
			registerShutdownHook(batchInserter);

			Connection mainSqlConnection = sqlServerDataSource.getConnection();

			PreparedStatement selectAuthors = mainSqlConnection.prepareStatement("SELECT id FROM Authors");

			ResultSet rs = selectAuthors.executeQuery();

			while (rs.next()) {
				batchInserter.createNode(rs.getLong(1), Collections.<String, Object> emptyMap());
			}

			PreparedStatement selectRelation = mainSqlConnection.prepareStatement("SELECT * FROM CoauthorRelations");

			rs = selectRelation.executeQuery();

			while (rs.next()) {
				batchInserter.createRelationship(rs.getLong(1), rs.getLong(2), CoauthorRelationshipTypes.COAUTHOR,
						Collections.<String, Object> emptyMap());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void registerShutdownHook(final BatchInserter batchInserter) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (batchInserter != null) {
					batchInserter.shutdown();
				}
			}
		});
	}
}
