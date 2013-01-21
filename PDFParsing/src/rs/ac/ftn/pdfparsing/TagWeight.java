package rs.ac.ftn.pdfparsing;

public class TagWeight {
	String tag;
	double weight;
		
	public TagWeight(String tag, double weight) {
		super();
		this.tag = tag;
		this.weight = weight;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
