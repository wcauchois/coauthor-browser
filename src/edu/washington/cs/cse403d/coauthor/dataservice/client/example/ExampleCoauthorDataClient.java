package edu.washington.cs.cse403d.coauthor.dataservice.client.example;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import edu.washington.cs.cse403d.coauthor.shared.CoauthorDataServiceInterface;
import edu.washington.cs.cse403d.coauthor.shared.model.Publication;

/**
 * Example client to the data provider.
 * 
 * This example makes every request to the DataProdiver in its own thread. This
 * is to demonstrate the multi-threaded nature of the backend solution.
 * 
 * This is NOT how you should be using threads to query the backend async. Talk
 * to me if you want pointers/ideas
 * 
 * @author Jeff Prouty
 */
public class ExampleCoauthorDataClient {
	public static final String HOSTNAME = "attu2.cs.washington.edu";

	// public static final String HOSTNAME = "localhost";

	public static void main(String[] args) {
		final CoauthorDataServiceInterface c;
		try {
			c = (CoauthorDataServiceInterface) Naming.lookup("rmi://" + HOSTNAME + "/"
					+ CoauthorDataServiceInterface.SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		final long start = System.currentTimeMillis();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(c
							.getPublication("Zone-based virtual backbone formation in wireless ad hoc networks"));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(printPubList(c
							.getPublications("Zone-based virtual backbone formation wireless ad hoc networks")));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(c.getAuthors("Thomas E. Anderson"));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(c.getCoauthors("Thomas E. Anderson"));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(c.getCoauthors("Vinton G. Cerf"));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(printPubList(c.getPublicationsForAllAuthors("Brian N. Bershad",
							"Edward D. Lazowska", "Henry M. Levy", "Thomas E. Anderson")));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(printPubList(c.getPublicationsForAnyAuthor("Brian N. Bershad",
							"Edward D. Lazowska", "Henry M. Levy", "Thomas E. Anderson")));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(c.getPublicationTitleSuggestions("Wireless Networks"));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(c.getOneShortestPathBetweenAuthors("Brian N. Bershad", "David D. Clark", false));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(c.getOneShortestPathBetweenAuthors("Brian N. Bershad", "David D. Clark", true));
					System.out.println((System.currentTimeMillis() - start));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();
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
			if (p.hasAuthors()) {
				sb.append("numAuthors:");
				sb.append(p.getAuthors().size());
			}
			sb.append(", \t");
		}
		return sb.toString();
	}
}
