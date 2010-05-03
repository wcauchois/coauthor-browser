package edu.washington.cs.cse403d.coauthor.dataservice.client.example;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * Example client to the data provider. Localhost should be replaced with the
 * appropriate location of the remote data source.
 * 
 * The error handling can obviously be improved.
 * 
 * @author Jeff Prouty
 */
public class ExampleCoauthorDataClient {
	 public static final String HOSTNAME = "attu2.cs.washington.edu";

	public static void main(String[] args) {
		// TODO(prouty): Adding security manager and reflection on the received
		// class would allow the client and server to be out of sync. This
		// feature will turn into an anti-feature if implemented. However, a
		// security manager might be needed for over the web RMI

		CoauthorDataServiceInterface c;
		try {
			c = (CoauthorDataServiceInterface) Naming.lookup("rmi://" + HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		long start = System.currentTimeMillis();
		try {
			System.out.println(c.getAuthors("Thomas E. Anderson"));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(c.getCoauthors("Thomas E. Anderson"));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(c.getCoauthors("Vinton G. Cerf"));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(printPubList(c.getPublicationsForAllAuthors("Brian N. Bershad", "Edward D. Lazowska",
					"Henry M. Levy", "Thomas E. Anderson")));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(printPubList(c.getPublicationsForAnyAuthor("Brian N. Bershad", "Edward D. Lazowska",
					"Henry M. Levy", "Thomas E. Anderson")));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(c.getOneShortestPathBetweenAuthors("Brian N. Bershad", "David D. Clark", false));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(c.getAllShortestPathsBetweenAuthors("Brian N. Bershad", "David D. Clark", false));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(c.getOneShortestPathBetweenAuthors("Brian N. Bershad", "David D. Clark", true));
			System.out.println((System.currentTimeMillis() - start));
			System.out.println(c.getAllShortestPathsBetweenAuthors("Brian N. Bershad", "David D. Clark", true));
			System.out.println((System.currentTimeMillis() - start));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private static String printPubList(List<Publication> pubs) {
		StringBuilder sb = new StringBuilder();
		sb.append("PublicationList: ");
		for (Publication p : pubs) {
			sb.append(p.getTitle());
			if (p.hasYear()) {
				sb.append(':');
				sb.append(p.getYear());
			}
			sb.append(", \t");
		}
		return sb.toString();
	}
}
