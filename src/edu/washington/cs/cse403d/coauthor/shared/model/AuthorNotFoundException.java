package edu.washington.cs.cse403d.coauthor.shared.model;

/**
 * This exception is added for ease of handling author not found errors.
 * 
 * @author Jeff Prouty
 */
public class AuthorNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 6435161186114140016L;

	public AuthorNotFoundException() {
	}

	public AuthorNotFoundException(String msg) {
		super(msg);
	}
}
