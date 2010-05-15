package edu.washington.cs.cse403d.coauthor.shared.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PathLink implements Serializable {
	private static final long serialVersionUID = 3308477967496548668L;

	private String authorAName;
	private String authorBName;
	private List<Publication> coauthoredPublications;

	public PathLink(String authorAName, String authorBName, List<Publication> coauthoredPublications) {
		setAuthorAName(authorAName);
		setAuthorBName(authorBName);
		setCoauthoredPublications(coauthoredPublications);
	}

	public PathLink(String authorAName, String authorBName) {
		setAuthorAName(authorAName);
		setAuthorBName(authorBName);
		setCoauthoredPublications(Collections.<Publication> emptyList());
	}

	public String toString() {
		return authorAName + "-(" + coauthoredPublications.size() + " pubs)->" + authorBName;
	}

	public String getAuthorAName() {
		return authorAName;
	}

	public void setAuthorAName(String authorAName) {
		this.authorAName = authorAName;
	}

	public String getAuthorBName() {
		return authorBName;
	}

	public void setAuthorBName(String authorBName) {
		this.authorBName = authorBName;
	}

	public List<Publication> getCoauthoredPublications() {
		return coauthoredPublications;
	}

	public void setCoauthoredPublications(List<Publication> coauthoredPublications) {
		this.coauthoredPublications = coauthoredPublications;
	}
}
