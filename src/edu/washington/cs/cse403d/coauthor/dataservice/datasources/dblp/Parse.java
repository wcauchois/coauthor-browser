package edu.washington.cs.cse403d.coauthor.dataservice.datasources.dblp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.washington.cs.cse403d.coauthor.dataservice.graphdb.BatchCreateGraph;

/**
 * Import the dblp database into a neo4j instance. This program should ONLY be
 * used to create a new neo4j database and NOT to add to an existing database.
 * 
 * First download the dblp.xml file from: http://dblp.uni-trier.de/xml/
 * 
 * For maximal efficiency, use 2gig of ram for this JVM by: -Xmx2048m This
 * program is optimized for time space and is therefore extremely expensive in
 * space.
 * 
 * @author Jeff Prouty
 */
public class Parse {
	private static final String AUTHOR = "author";
	private static final String TITLE = "title";

	private static final String XML = "dblp.xml";
	private static final int PUBLICATION_DEPTH = 2;
	private static final int NUM_RECORDS = 2179705;

	public static void main(String[] args) throws URISyntaxException {
		BatchCreateGraph.openDb();

		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new BufferedInputStream(new FileInputStream(XML));
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			double count = 0;
			int depth = 0;

			Map<String, Long> allAuthors = new HashMap<String, Long>();
			Map<String, Long> allPublications = new HashMap<String, Long>();

			StringBuilder title = new StringBuilder();
			Set<String> authors = new HashSet<String>();
			String currentAttribute = "";
			String currentPubType = "";

			XMLEvent event = null;
			while (eventReader.hasNext()) {
				event = eventReader.nextEvent();

				if (event.isStartElement()) {
					++depth;
					if (depth == PUBLICATION_DEPTH) {
						// If inside a publication, record the label (to
						// determine if its a field we want when reading its
						// data)
						StartElement startElement = event.asStartElement();
						currentPubType = startElement.getName().getLocalPart();
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
						}
					}
				}

				if (event.isEndElement()) {
					if (depth == PUBLICATION_DEPTH) {
						String titleString = title.toString();
						// www corpus is only homepages, which ALL have the same
						// title!!! Yikes
						if (!titleString.isEmpty() && authors.size() != 0 && !(currentPubType.equals("www"))) {
							long titleNodeId;
							if (allPublications.containsKey(titleString)) {
								titleNodeId = allPublications.get(titleString);
							} else {
								titleNodeId = BatchCreateGraph.createPublication(titleString);
								allPublications.put(titleString, titleNodeId);
							}

							for (String author : authors) {
								long authorNodeId;
								if (allAuthors.containsKey(author)) {
									authorNodeId = allAuthors.get(author);
								} else {
									authorNodeId = BatchCreateGraph.createAuthor(author);
									allAuthors.put(author, authorNodeId);
								}
								BatchCreateGraph.createAuthorRelationship(titleNodeId, authorNodeId);
							}
						}

						if (++count % 1000 == 0) {
							System.out.println("Progress: " + count * 100 / NUM_RECORDS);
						}

						// Cleanup for next pub:
						title = new StringBuilder();
						authors = new HashSet<String>();
						currentAttribute = "";
						currentPubType = "";
					}
					--depth;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		BatchCreateGraph.fullTextInserter.optimize();
		BatchCreateGraph.indexInserter.optimize();
		BatchCreateGraph.closeDb();
	}
}
