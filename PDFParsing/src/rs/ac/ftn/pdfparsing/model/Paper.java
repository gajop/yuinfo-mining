package rs.ac.ftn.pdfparsing.model;

import java.util.Arrays;

public class Paper {
	Author[] authors;
	String titleEN;
	String titleRS;
	String abstractEN;
	String abstractRS;
	int year;
	String topic;
	String fileName;
	String content;
	
	
	public Paper(Author[] authors, String titleEN, String titleRS,
			String abstractEN, String abstractRS, int year, String topic,
			String fileName, String content) {
		super();
		this.authors = authors;
		this.titleEN = titleEN;
		this.titleRS = titleRS;
		this.abstractEN = abstractEN;
		this.abstractRS = abstractRS;
		this.year = year;
		this.topic = topic;
		this.fileName = fileName;
		this.content = content;
	}
	
	
	
	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
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
	public Author[] getAuthors() {
		return authors;
	}
	public void setAuthors(Author[] authors) {
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



	@Override
	public String toString() {
		return "Paper [authors=" + Arrays.toString(authors) + ", titleEN="
				+ titleEN + ", titleRS=" + titleRS + ", abstractEN="
				+ abstractEN + ", abstractRS=" + abstractRS + ", year=" + year
				+ ", topic=" + topic + ", fileName=" + fileName + ", content="
				+ content + "]";
	}	
	
}
