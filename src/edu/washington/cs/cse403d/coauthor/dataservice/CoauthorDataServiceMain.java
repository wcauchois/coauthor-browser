package edu.washington.cs.cse403d.coauthor.dataservice;

import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.sql.Connection;
import java.sql.SQLException;

import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;

/**
 * Main data service executable. Assumes that a valid neo4j database is
 * correctly initialized with data.
 * 
 * JVM Args include (optional, sometimes):
 * 
 * <pre>
 * -Djava.security.policy=server.policy
 * -Djava.rmi.server.codebase= file:/driveletter:/.../coauthor-browser/classes/
 * </pre>
 * 
 * @author Jeff Prouty
 * @version 2
 */
public class CoauthorDataServiceMain {
	private EmbeddedGraphDatabase graphDataSource;
	private final SQLServerConnectionPoolDataSource sqlServerDataSource;
	private Connection mainSqlConnection;

	private String graphDbPath;
	private String dbUser;
	private String dbPasswd;
	private String dbDatabaseName;
	private String dbHost;
	private Integer dbPort;
	private Boolean noReg;

	public CoauthorDataServiceMain() {
		sqlServerDataSource = new SQLServerConnectionPoolDataSource();
	}

	public void run(String[] args) {
		if (!parseArgs(args)) {
			printUsage();
			return;
		}

		if (noReg == null || noReg == false) {
			try {
				LocateRegistry.createRegistry(1099);
			} catch (Exception e) {
				System.err.println("Could not create the registry. Could already be running. "
						+ "Try running this again with option -noReg");
				e.printStackTrace();
			}
		}

		try {
			connectToSqlDataSource();
		} catch (SQLException e) {
			System.err.println("Could not connect to the given SQL database.");
			e.printStackTrace();
			return;
		}

		try {
			connectToGraphDataSource();
		} catch (Exception e) {
			System.err.println("Could not connect to the given Graph database.");
			e.printStackTrace();
			return;
		}

		try {
			CoauthorDataServiceInterface c = new CoauthorDataService(mainSqlConnection, graphDataSource);
			Naming.rebind("rmi://localhost:1099/" + CoauthorDataServiceInterface.SERVICE_NAME, c);
			System.out.println("DataService Started Successfully");
		} catch (Exception e) {
			System.err.println("Error starting/binding RMI service");
			e.printStackTrace();
		}
	}

	private void connectToSqlDataSource() throws SQLServerException {
		sqlServerDataSource.setUser(dbUser);
		sqlServerDataSource.setPassword(dbPasswd);
		sqlServerDataSource.setDatabaseName(dbDatabaseName);
		if (dbHost != null) {
			sqlServerDataSource.setServerName(dbHost);
		}
		if (dbPort != null) {
			sqlServerDataSource.setPortNumber(dbPort);
		}

		mainSqlConnection = sqlServerDataSource.getConnection();
	}

	private void connectToGraphDataSource() {
		graphDataSource = new EmbeddedGraphDatabase(graphDbPath);
		registerNeo4jShutdownHook();
	}

	private void registerNeo4jShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (graphDataSource != null) {
					graphDataSource.shutdown();
				}
			}
		});
	}

	private boolean parseArgs(String[] args) {
		if (args.length < 8) {
			return false;
		}

		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-U") && i + 1 < args.length) {
				dbUser = args[++i];
			} else if (args[i].equals("-P") && i + 1 < args.length) {
				dbPasswd = args[++i];
			} else if (args[i].equals("-D") && i + 1 < args.length) {
				dbDatabaseName = args[++i];
			} else if (args[i].equals("-h") && i + 1 < args.length) {
				dbHost = args[++i];
			} else if (args[i].equals("-p") && i + 1 < args.length) {
				try {
					dbPort = Integer.parseInt(args[++i]);
				} catch (NumberFormatException e) {
				}
			} else if (args[i].equals("-d") && i + 1 < args.length) {
				graphDbPath = args[++i];
				if (!new File(graphDbPath).exists()) {
					System.err.println("Given neo4j path does not exist: " + graphDbPath);
					return false;
				}
			} else if (args[i].equals("-noReg")) {
				noReg = true;
			}
		}

		return dbUser != null && dbPasswd != null && dbDatabaseName != null && graphDbPath != null;
	}

	private void printUsage() {
		System.out.println("Usage:");
		System.out
				.println("java CoauthorDataServiceMain -U <sql_user> -P <sql_passwd> -D <sql_database_name> -d <neo4j_graph_db>"
						+ "[-noReg] [-h <sql_host>] [-p <sql_port>]");
	}

	public static void main(String[] args) {
		new CoauthorDataServiceMain().run(args);
	}
}
