package edu.washington.cs.cse403d.coauthor.shared.model;

import java.io.Serializable;

public class Publication implements Serializable {
	private static final long serialVersionUID = 5221468963013037621L;

	private String title;
	private String pages;
	private Integer year;
	private String url;
	private String ee;
	private Integer volume;
	private String journal;
	private String isbn;
	private String publisher;
	private Integer number;

	public Publication() {
	}

	public Publication(String title, String pages, Integer year, String url, String ee, Integer volume, String journal,
			String isbn, String publisher, Integer number) {
		setTitle(title);
		setPages(pages);
		setYear(year);
		setUrl(url);
		setEe(ee);
		setVolume(volume);
		setJournal(journal);
		setIsbn(isbn);
		setPublisher(publisher);
		setNumber(number);
	}
	
	public String toString() {
		return getTitle();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean hasPages() {
		return pages != null;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public boolean hasYear() {
		return year != null;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public boolean hasUrl() {
		return url != null;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean hasEe() {
		return ee != null;
	}

	public String getEe() {
		return ee;
	}

	public void setEe(String ee) {
		this.ee = ee;
	}

	public boolean hasVolume() {
		return volume != null;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public boolean hasJournal() {
		return journal != null;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public boolean hasIsbn() {
		return isbn != null;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public boolean hasPublisher() {
		return publisher != null;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public boolean hasNumber() {
		return number != null;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
}
