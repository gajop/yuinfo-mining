package rs.ac.ftn.pdfparsing.model;

public class Topic {
	String name;
	String id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Topic(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}
	
	
}
