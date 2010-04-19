package edu.washington.cs.cse403d.coauthor.dataservice.client.example;

import java.rmi.Naming;
import java.rmi.RemoteException;

import edu.washington.cs.cse403d.coauthor.dataservice.CoauthorDataServiceInterface;

/**
 * Example client to the data provider. Localhost should be replaced with the
 * appropriate location of the remote data source.
 * 
 * The error handling can obviously be improved.
 * 
 * @author Jeff Prouty
 */
public class ExampleCoauthorDataClient {
	public static final String HOSTNAME = "localhost";

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
			System.out.println(c.getShortestPathBetweenAuthors("Thomas E. Anderson", "David D. Clark"));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println((System.currentTimeMillis() - start));
	}
}
