package edu.washington.cs.cse403d.coauthor.dataservice.datasources.dblp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

/**
 * Import the dblp database into a MSQL and neo4j instance. This program can be
 * used at any time to update the database with the latest dblp database while
 * keeping the two databases consistent.
 * 
 * This cannot be run while the Neo4j GraphDB is in use in another process.
 * Therefore, the graphDB must be shut down while this update processes.
 * 
 * I'm finding some versions of java use too much memory using StAX. I'm not
 * sure what's going on yet but some machines only use 100M while others will
 * try to use 4G or more.
 * 
 * First download the latest dblp.xml file from: http://dblp.uni-trier.de/xml/
 * 
 * @author Jeff Prouty
 * @version 2
 */
public class Parse {
	// parsing constants
	private static final String AUTHOR = "author";
	private static final String TITLE = "title";
	private static final String PAGES = "pages";
	private static final String YEAR = "year";
	private static final String URL = "url";
	private static final String EE = "ee";
	private static final String VOLUME = "volume";
	private static final String JOURNAL = "journal";
	private static final String ISBN = "isbn";
	private static final String PUBLISHER = "publisher";
	private static final String NUMBER = "number";

	// SQL varchar max constraint definitions
	private static final int MAX_AUTHOR_NAME_SIZE = 100;

	private static final int MAX_PUB_TITLE_SIZE = 450;
	private static final int MAX_PUB_PAGE_SIZE = 48;
	private static final int MAX_PUB_URL_SIZE = 255;
	private static final int MAX_PUB_EE_SIZE = 255;
	private static final int MAX_PUB_JOURNAL_SIZE = 192;
	private static final int MAX_PUB_ISBN_SIZE = 20;
	private static final int MAX_PUB_PUBLISHER_SIZE = 96;

	// parsing stuff
	private static final int PUBLICATION_DEPTH = 2;

	private static final int DEFAULT_DBLP_NUM_DAYS_BACK_FROM_START_ALLOWED = 5;
	private static final long ALL_DBLP_DATES = Long.MIN_VALUE;
	private static final QName MDATE = new QName("mdate");

	public static enum CoauthorRelationshipTypes implements RelationshipType {
		COAUTHOR;
	}

	/**
	 * Read the method name very carefully!!!
	 * 
	 * @param dbConnection
	 */
	private static void dropAllTables(Connection dbConnection) throws SQLException {
		String[] tablesToDrop = { "CoauthorRelations", "PublicationAuthors", "Authors", "Publications", "SystemParams" };

		for (String tableName : tablesToDrop) {
			dbConnection.createStatement().execute("DROP TABLE [" + tableName + "]");
		}
	}

	/**
	 * Creates the schema for the database. Should be executed with care.
	 * 
	 * @param dbConnection
	 */
	private static void createAllTablesWithInitialData(Connection dbConnection) throws SQLException {
		dbConnection.createStatement().execute(
				"CREATE TABLE [Authors] (" + "[id] int IDENTITY(1, 1) NOT NULL, " + "[name] nvarchar(100) NOT NULL, "
						+ "PRIMARY KEY([id]), " + "UNIQUE([name]))");
		dbConnection.createStatement().execute(
				"CREATE TABLE [Publications] (" + "[id] int IDENTITY(1, 1) NOT NULL, "
						+ "[title] nvarchar(450) NOT NULL, " + "[pages] varchar(48), " + "[year] int, "
						+ "[url] varchar(255), " + "[ee] varchar(255), " + "[volume] int, "
						+ "[journal] varchar(192), " + "[isbn] varchar(20), " + "[publisher] varchar(96), "
						+ "[number] int, " + "PRIMARY KEY([id]), " + "UNIQUE([title]))");
		dbConnection.createStatement().execute(
				"CREATE TABLE [CoauthorRelations] (" + "[aid1] int NOT NULL, " + "[aid2] int NOT NULL, "
						+ "PRIMARY KEY([aid1], [aid2]), " + "foreign key ([aid1]) references Authors ([id]), "
						+ "foreign key ([aid2]) references Authors ([id]))");
		dbConnection.createStatement().execute(
				"CREATE TABLE [PublicationAuthors] (" + "[pid] int NOT NULL, " + "[aid] int NOT NULL, "
						+ "PRIMARY KEY([pid], [aid]), " + "foreign key ([pid]) references Publications ([id]), "
						+ "foreign key ([aid]) references Authors ([id]))");
		dbConnection.createStatement().execute(
				"CREATE TABLE [SystemParams] (" + "[id] int IDENTITY(1, 1) NOT NULL, " + "[key] varchar(64), "
						+ "[value] varchar(255), " + "PRIMARY KEY([id]), " + "UNIQUE([key]))");
		populateWithInitialData(dbConnection);
	}

	private static void populateWithInitialData(Connection dbConnection) throws SQLException {
		dbConnection.createStatement().execute(
				"INSERT INTO [SystemParams] ([key], [value]) VALUES ('lastDBLPDate', '0000-00-00')");
	}

	private String graphDbPath;
	private String dbUser;
	private String dbPasswd;
	private String dbDatabaseName;
	private String dbHost;
	private Integer dbPort;
	private Long dblpStartDateEpoch;
	private Integer dblpNumDaysBackFromStartAllowed;
	private String xmlFile;
	private Boolean dropTables;
	private Boolean createTables;

	private final SQLServerDataSource sqlServerDataSource;
	private Connection mainSqlConnection;
	private PreparedStatement ps_getAuthorId;
	private PreparedStatement ps_getPublicationId;
	private PreparedStatement ps_getCoauthorship;
	private PreparedStatement ps_insertAuthor;
	private PreparedStatement ps_insertPublication;
	private PreparedStatement ps_insertPublicationAuthor;
	private PreparedStatement ps_insertCoauthorRelation;
	private PreparedStatement ps_getLastDBLPDate;
	private PreparedStatement ps_updateLastDBLPDate;

	private BatchInserter graphDataSource;

	private int totalNumRecordsProcessed;
	private int totalNumRecordsAfterDateProcessed;
	private int newAuthorCount;
	private int newPublicationCount;

	private long newDblpStartDateEpoch;

	private InputStream inputXmlStream;
	private XMLEventReader xmlEventReader;

	public Parse() {
		sqlServerDataSource = new SQLServerDataSource();
		dblpNumDaysBackFromStartAllowed = DEFAULT_DBLP_NUM_DAYS_BACK_FROM_START_ALLOWED;

		dropTables = false;
		createTables = false;

		totalNumRecordsProcessed = 0;
		totalNumRecordsAfterDateProcessed = 0;
		newAuthorCount = 0;
		newPublicationCount = 0;
	}

	private void printUsage() {
		System.out.println("Usage:");
		System.out.println("java ParseToSQL -U <sql_user> -P <sql_passwd> -D <sql_database_name> -d <neo4j_graph_db>"
				+ "[-h <sql_host>] [-p <sql_port>] [-S <start_date>] [-N <num_days>] "
				+ "[--DROP_TABLES] [--CREATE_TABLES] <XMLFile>");
	}

	private boolean parseArgs(String[] args) {
		if (args.length < 9) {
			return false;
		}

		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-U") && i + 2 < args.length) {
				dbUser = args[++i];
			} else if (args[i].equals("-P") && i + 2 < args.length) {
				dbPasswd = args[++i];
			} else if (args[i].equals("-D") && i + 2 < args.length) {
				dbDatabaseName = args[++i];
			} else if (args[i].equals("-h") && i + 2 < args.length) {
				dbHost = args[++i];
			} else if (args[i].equals("-p") && i + 2 < args.length) {
				try {
					dbPort = Integer.parseInt(args[++i]);
				} catch (NumberFormatException e) {
				}
			} else if (args[i].equals("-d") && i + 2 < args.length) {
				graphDbPath = args[++i];
			} else if (args[i].equals("-S") && i + 2 < args.length) {
				dblpStartDateEpoch = getEpochFromMDate(args[++i]);
			} else if (args[i].equals("-S") && i + 2 < args.length) {
				try {
					dblpNumDaysBackFromStartAllowed = Integer.parseInt(args[++i]);
				} catch (NumberFormatException e) {
				}
			} else if (args[i].equals("--DROP_TABLES") && i + 1 < args.length) {
				dropTables = true;
			} else if (args[i].equals("--CREATE_TABLES") && i + 1 < args.length) {
				createTables = true;
			} else if (i + 1 == args.length) {
				xmlFile = args[i];
			}
		}

		return dbUser != null && dbPasswd != null && dbDatabaseName != null && graphDbPath != null && xmlFile != null;
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
		graphDataSource = new BatchInserterImpl(graphDbPath);
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

	private void nullInsertPublicationPreparedStatementParameters() throws SQLException {
		ps_insertPublication.setNull(2, java.sql.Types.VARCHAR);
		ps_insertPublication.setNull(3, java.sql.Types.INTEGER);
		ps_insertPublication.setNull(4, java.sql.Types.VARCHAR);
		ps_insertPublication.setNull(5, java.sql.Types.VARCHAR);
		ps_insertPublication.setNull(6, java.sql.Types.INTEGER);
		ps_insertPublication.setNull(7, java.sql.Types.VARCHAR);
		ps_insertPublication.setNull(8, java.sql.Types.VARCHAR);
		ps_insertPublication.setNull(9, java.sql.Types.VARCHAR);
		ps_insertPublication.setNull(10, java.sql.Types.INTEGER);
	}

	private void initializePreparedStatements() throws SQLException {
		ps_getAuthorId = mainSqlConnection.prepareStatement("SELECT [id] FROM [Authors] WHERE [name] = ?");
		ps_getPublicationId = mainSqlConnection.prepareStatement("SELECT [id] FROM [Publications] WHERE [title] = ?");
		ps_getCoauthorship = mainSqlConnection
				.prepareStatement("SELECT * FROM [CoauthorRelations] WHERE [aid1] = ? AND [aid2] = ?");

		ps_insertAuthor = mainSqlConnection.prepareStatement("INSERT INTO [Authors] ([name]) VALUES (?)");
		ps_insertPublication = mainSqlConnection
				.prepareStatement("INSERT INTO [Publications] ([title], [pages], [year], [url], [ee], [volume], [journal], [isbn], [publisher], [number]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		nullInsertPublicationPreparedStatementParameters();
		ps_insertPublicationAuthor = mainSqlConnection
				.prepareStatement("INSERT INTO [PublicationAuthors] ([pid], [aid]) VALUES (?, ?)");
		ps_insertCoauthorRelation = mainSqlConnection
				.prepareStatement("INSERT INTO [CoauthorRelations] ([aid1], [aid2]) VALUES (?, ?)");

		ps_getLastDBLPDate = mainSqlConnection
				.prepareStatement("SELECT [value] FROM [SystemParams] WHERE [key] = 'lastDBLPDate'");

		ps_updateLastDBLPDate = mainSqlConnection
				.prepareStatement("UPDATE SystemParams SET [value] = ? WHERE [key] = 'lastDBLPDate'");
	}

	private void createAuthorCoauthorshipIfDoesNotExist(long a, long b) throws SQLException {
		if (a == b) {
			// Coauthor link to self
			return;
		}

		// Ordering the pairing results in the fewest possible links by auto dup
		// elimination
		long smallerId = Math.min(a, b);
		long largerId = Math.max(a, b);

		ps_getCoauthorship.setLong(1, smallerId);
		ps_getCoauthorship.setLong(2, largerId);
		try {
			ResultSet rs = ps_getCoauthorship.executeQuery();
			if (rs.next()) {
				// Link already exists
				return;
			} else {
				// Link doesn't exist. Create it in SQL AND GraphDB
				ps_insertCoauthorRelation.setLong(1, smallerId);
				ps_insertCoauthorRelation.setLong(2, largerId);
				ps_insertCoauthorRelation.executeUpdate();

				graphDataSource.createRelationship(smallerId, largerId, CoauthorRelationshipTypes.COAUTHOR, Collections
						.<String, Object> emptyMap());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private long getAuthorOrCreateIfDoesNotExist(String authorName) throws SQLException {
		ps_getAuthorId.setString(1, authorName);

		ps_insertAuthor.setString(1, authorName);
		try {
			ResultSet rs = ps_getAuthorId.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				ps_insertAuthor.executeUpdate();
				++newAuthorCount;
				rs = ps_getAuthorId.executeQuery();
				if (!rs.next()) {
					throw new IllegalStateException("Weird!");
				} else {
					long id = rs.getLong(1);

					// Insert into the GraphDB
					graphDataSource.createNode(id, Collections.<String, Object> emptyMap());

					return id;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private long getPublicationOrCreateIfDoesNotExist(String titleString, String pages, Integer year, String url,
			String ee, Integer volume, String journal, String isbn, String publisher, Integer number)
			throws SQLException {
		titleString = truncateString(titleString, MAX_PUB_TITLE_SIZE);
		if (titleString.charAt(titleString.length() - 1) == '.') {
			titleString = titleString.substring(0, titleString.length() - 1);
		}

		ps_getPublicationId.setString(1, titleString);

		ps_insertPublication.setString(1, titleString);
		nullInsertPublicationPreparedStatementParameters();
		if (pages != null) {
			ps_insertPublication.setString(2, truncateString(pages, MAX_PUB_PAGE_SIZE));
		}
		if (year != null) {
			ps_insertPublication.setInt(3, year);
		}
		if (url != null) {
			ps_insertPublication.setString(4, truncateString(url, MAX_PUB_URL_SIZE));
		}
		if (ee != null) {
			ps_insertPublication.setString(5, truncateString(ee, MAX_PUB_EE_SIZE));
		}
		if (volume != null) {
			ps_insertPublication.setInt(6, volume);
		}
		if (journal != null) {
			ps_insertPublication.setString(7, truncateString(journal, MAX_PUB_JOURNAL_SIZE));
		}
		if (isbn != null) {
			ps_insertPublication.setString(8, truncateString(isbn, MAX_PUB_ISBN_SIZE));
		}
		if (publisher != null) {
			ps_insertPublication.setString(9, truncateString(publisher, MAX_PUB_PUBLISHER_SIZE));
		}
		if (number != null) {
			ps_insertPublication.setInt(10, number);
		}
		try {
			ResultSet rs = ps_getPublicationId.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				ps_insertPublication.executeUpdate();
				++newPublicationCount;
				rs = ps_getPublicationId.executeQuery();
				if (!rs.next()) {
					throw new IllegalStateException("Weird!");
				} else {
					return rs.getLong(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void getDblpStartDateFromSQLIfNeeded() throws SQLException {
		if (dblpStartDateEpoch == null) {
			// The start date isn't given. Try to find it from SQL
			ResultSet rs = ps_getLastDBLPDate.executeQuery();

			if (!rs.next()) {
				// Key not found. Use the default
				dblpStartDateEpoch = ALL_DBLP_DATES;
			} else {
				dblpStartDateEpoch = getEpochFromMDate(rs.getString("value"));
			}

			if (dblpStartDateEpoch == ALL_DBLP_DATES) {
				// Starting at lowest date. No need to worry about the
				// startAllowed grace
				dblpNumDaysBackFromStartAllowed = 0;
			}
		}
	}

	private void dropTables() throws SQLException {
		dropAllTables(mainSqlConnection);
	}

	private void createTables() throws SQLException {
		createAllTablesWithInitialData(mainSqlConnection);
	}

	public void run(String[] args) {
		if (!parseArgs(args)) {
			printUsage();
			return;
		}

		try {
			connectToSqlDataSource();
		} catch (SQLServerException e) {
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
			initializePreparedStatements();
		} catch (SQLException e) {
			System.err.println("Could not create prepared statements.");
			e.printStackTrace();
			return;
		}

		try {
			if (dropTables) {
				dropTables();
			}
		} catch (SQLException e) {
			System.err.println("Could not drop existing tables.");
			e.printStackTrace();
			return;
		}

		try {
			if (createTables) {
				createTables();
			}
		} catch (SQLException e) {
			System.err.println("Could not create tables from schema");
			e.printStackTrace();
			return;
		}

		try {
			getDblpStartDateFromSQLIfNeeded();
		} catch (SQLException e) {
			System.err.println("Error getting DBLP Start Date from SQL");
			e.printStackTrace();
			return;
		}

		newDblpStartDateEpoch = dblpStartDateEpoch;

		try {
			inputXmlStream = new FileInputStream(xmlFile);
		} catch (FileNotFoundException e) {
			System.err.println("Could not find the specified xml file.");
			e.printStackTrace();
			return;
		}

		try {
			xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputXmlStream);
		} catch (XMLStreamException e) {
			System.err.println("Could not create the XMLEventReader.");
			e.printStackTrace();
			return;
		} catch (Exception e) {
			System.err.println("Other error while creating XMLEventReader.");
			e.printStackTrace();
			return;
		}

		try {
			parseXMLFile();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			updateDblpStartDate();
		} catch (SQLException e) {
			System.err.println("Unable to update the dblp start date property.");
			e.printStackTrace();
			return;
		}

		try {
			mainSqlConnection.close();
			graphDataSource.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateDblpStartDate() throws SQLException {
		String newDate = getMDateFromEpoch(newDblpStartDateEpoch);

		ps_updateLastDBLPDate.setString(1, newDate);
		ps_updateLastDBLPDate.execute();
	}

	private static String truncateString(String str, int maxLength) {
		if (str.length() > maxLength) {
			return str.substring(0, maxLength);
		} else {
			return str;
		}
	}

	@SuppressWarnings("deprecation")
	private static long getEpochFromMDate(String mdate) {
		Date result = new Date(0);
		int year = Integer.parseInt(mdate.substring(0, 4)) - 1900;
		int month = Integer.parseInt(mdate.substring(5, 7)) - 1;
		int day = Integer.parseInt(mdate.substring(8, 10));
		result.setHours(0);
		result.setYear(year);
		result.setDate(day);
		result.setMonth(month);
		return result.getTime();
	}

	@SuppressWarnings("deprecation")
	private static String getMDateFromEpoch(long epoch) {
		Date result = new Date(epoch);
		String yearStr = Integer.toString(result.getYear() + 1900);
		while (yearStr.length() != 4) {
			yearStr = '0' + yearStr;
		}

		String monthStr = Integer.toString(result.getMonth() + 1);
		while (monthStr.length() != 2) {
			monthStr = '0' + monthStr;
		}

		String dayStr = Integer.toString(result.getDate());
		while (dayStr.length() != 2) {
			dayStr = '0' + dayStr;
		}

		return yearStr + "-" + monthStr + "-" + dayStr;
	}

	private void parseXMLFile() throws XMLStreamException, SQLException {
		int depth = 0;

		final long dblpDayGracePeriod = (long) dblpNumDaysBackFromStartAllowed * 1000 * 60 * 60 * 24;
		StringBuilder title = new StringBuilder();
		Set<String> authors = new HashSet<String>();
		String currentAttribute = "";
		String currentPubType = "";
		String pages = null;
		Integer year = null;
		String url = null;
		String ee = null;
		Integer volume = null;
		String journal = null;
		String isbn = null;
		String publisher = null;
		Integer number = null;
		Long mdate = null;

		boolean hasBatchUpdate = false;

		XMLEvent event = null;
		while (xmlEventReader.hasNext()) {
			event = xmlEventReader.nextEvent();

			if (event.isStartElement()) {
				++depth;
				if (depth == PUBLICATION_DEPTH) {
					// If inside a publication, record the label (to
					// determine if its a field we want when reading its
					// data)
					StartElement startElement = event.asStartElement();
					currentPubType = startElement.getName().getLocalPart();

					String mdateString = startElement.getAttributeByName(MDATE).getValue();
					if (mdateString != null && mdateString.length() == 10) {
						mdate = getEpochFromMDate(mdateString);
					}
				} else if (depth == PUBLICATION_DEPTH + 1) {
					// If inside a publication, record the label (to
					// determine if its a field we want when reading its
					// data)
					StartElement startElement = event.asStartElement();
					currentAttribute = startElement.getName().getLocalPart();
				}
			}

			if (event.isCharacters()) {
				if (depth > PUBLICATION_DEPTH) {
					String data = event.asCharacters().getData();

					if (currentAttribute.equals(AUTHOR)) {
						authors.add(data);
					} else if (currentAttribute.equals(TITLE)) {
						title.append(data);
					} else if (currentAttribute.equals(PAGES)) {
						pages = data.trim();
					} else if (currentAttribute.equals(YEAR)) {
						try {
							year = Integer.parseInt(data.trim());
						} catch (NumberFormatException e) {
						}
					} else if (currentAttribute.equals(URL)) {
						url = data.trim();
					} else if (currentAttribute.equals(EE)) {
						ee = data.trim();
					} else if (currentAttribute.equals(VOLUME)) {
						try {
							volume = Integer.parseInt(data.trim());
						} catch (NumberFormatException e) {
						}
					} else if (currentAttribute.equals(JOURNAL)) {
						journal = data.trim();
					} else if (currentAttribute.equals(ISBN)) {
						isbn = data.trim();
					} else if (currentAttribute.equals(PUBLISHER)) {
						publisher = data.trim();
					} else if (currentAttribute.equals(NUMBER)) {
						try {
							number = Integer.parseInt(data.trim());
						} catch (NumberFormatException e) {
						}
					}
				}
			}

			if (event.isEndElement()) {
				if (depth == PUBLICATION_DEPTH) {
					String titleString = title.toString().trim();
					// www corpus is only homepages, which ALL have the same
					// title!!! Yikes
					if (!titleString.isEmpty() && authors.size() != 0 && !(currentPubType.equals("www"))
							&& mdate != null && dblpStartDateEpoch <= mdate + dblpDayGracePeriod) {
						++totalNumRecordsAfterDateProcessed;
						newDblpStartDateEpoch = Math.max(mdate, newDblpStartDateEpoch);
						long publicationId = getPublicationOrCreateIfDoesNotExist(titleString, pages, year, url, ee,
								volume, journal, isbn, publisher, number);

						int i = 0;
						long[] authorIds = new long[authors.size()];
						for (String author : authors) {
							if (!author.isEmpty() && author.length() > MAX_AUTHOR_NAME_SIZE) {
								author = author.substring(0, MAX_AUTHOR_NAME_SIZE);
							}
							long authorId = getAuthorOrCreateIfDoesNotExist(author);
							authorIds[i++] = authorId;

							ps_insertPublicationAuthor.setLong(1, publicationId);
							ps_insertPublicationAuthor.setLong(2, authorId);
							ps_insertPublicationAuthor.addBatch();
							hasBatchUpdate = true;
						}

						for (i = 0; i < authorIds.length - 1; ++i) {
							for (int j = i + 1; j < authorIds.length; ++j) {
								createAuthorCoauthorshipIfDoesNotExist(authorIds[i], authorIds[j]);
							}
						}
					}

					if (++totalNumRecordsProcessed % 1000 == 0) {
						System.out.println("Processed " + totalNumRecordsProcessed + " records ("
								+ totalNumRecordsAfterDateProcessed + " after start date); " + newAuthorCount
								+ " new authors and " + newPublicationCount + " new publications.");

						if (hasBatchUpdate) {
							hasBatchUpdate = false;
							try {
								ps_insertPublicationAuthor.executeBatch();
							} catch (SQLException e) {
								// Ignore duplicates (non dups ARE being
								// added)
							} finally {
								ps_insertPublicationAuthor.clearBatch();
							}
							try {
								ps_insertCoauthorRelation.executeBatch();
							} catch (SQLException e) {
								// Ignore duplicates (non dups ARE being
								// added)
							} finally {
								ps_insertCoauthorRelation.clearBatch();
							}
						}
					}

					// Cleanup for next pub:
					title = new StringBuilder();
					authors.clear();
					authors = new HashSet<String>();
					currentAttribute = "";
					currentPubType = "";
					mdate = null;
					pages = null;
					year = null;
					url = null;
					ee = null;
					volume = null;
					journal = null;
					isbn = null;
					publisher = null;
					number = null;
				}
				--depth;
			}
		}
	}

	public static void main(String[] args) throws URISyntaxException, FileNotFoundException {
		new Parse().run(args);
	}
}
