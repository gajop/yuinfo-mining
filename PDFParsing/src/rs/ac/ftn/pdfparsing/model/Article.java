package rs.ac.ftn.pdfparsing.model;

public class Article {
	String[] authors;
	String titleEN;
	String titleRS;
	String abstractEN;
	String abstractRS;
	int year;
	String topic;
	String fileName;
	
	
	public Article(String[] authors, String titleEN, String titleRS,
			String abstractEN, String abstractRS, int year, String topic,
			String fileName) {
		super();
		this.authors = authors;
		this.titleEN = titleEN;
		this.titleRS = titleRS;
		this.abstractEN = abstractEN;
		this.abstractRS = abstractRS;
		this.year = year;
		this.topic = topic;
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String[] getAuthors() {
		return authors;
	}
	public void setAuthors(String[] authors) {
		this.authors = authors;
	}
	public String getTitleEN() {
		return titleEN;
	}
	public void setTitleEN(String titleEN) {
		this.titleEN = titleEN;
	}
	public String getTitleRS() {
		return titleRS;
	}
	public void setTitleRS(String titleRS) {
		this.titleRS = titleRS;
	}
	public String getAbstractEN() {
		return abstractEN;
	}
	public void setAbstractEN(String abstractEN) {
		this.abstractEN = abstractEN;
	}
	public String getAbstractRS() {
		return abstractRS;
	}
	public void setAbstractRS(String abstractRS) {
		this.abstractRS = abstractRS;
	}	
	
}
