package rs.ac.ftn.pdfparsing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Library {
	List<Paper> papers = new LinkedList<Paper>();

	public HashMap<Integer, List<Paper>> papersByYear = new HashMap<Integer, List<Paper>>();
	public HashMap<String, List<Paper>> papersByTopic = new HashMap<String, List<Paper>>();
	public HashMap<String, List<Paper>> papersByAuthor = new HashMap<String, List<Paper>>();
	public HashMap<Integer, HashMap<String, Paper>> papersByYearAndName = new HashMap<Integer, HashMap<String, Paper>>();
	
	public Set<Author> authors = new HashSet<Author>(); 
	public HashMap<String, Author> authorsByFullName = new HashMap<String, Author>();
	static Library instance = new Library();
	
	public static Library getInstance() {
		return instance;
	}
	
	public void addPaper(Paper paper) {
		papers.add(paper);
		List<Paper> papersByYearList;
		HashMap<String, Paper> papersByYearMap;
		if (papersByYear.containsKey(paper.getYear())) {					
			papersByYearList = papersByYear.get(paper.getYear());	
			papersByYearMap = papersByYearAndName.get(paper.getYear());
		} else {			
			papersByYearList = new LinkedList<Paper>();			
			papersByYear.put(paper.getYear(), papersByYearList);
			
			papersByYearMap = new HashMap<String, Paper>();			
			papersByYearAndName.put(paper.getYear(), papersByYearMap);
		}
		papersByYearList.add(paper);
		papersByYearMap.put(paper.getFileName(), paper);

		List<Paper> papersByTopicList;
		if (papersByTopic.containsKey(paper.getTopic())) {					
			papersByTopicList = papersByTopic.get(paper.getTopic());			
		} else {
			papersByTopicList = new LinkedList<Paper>();
			papersByTopic.put(paper.getTopic(), papersByTopicList);			
		}
		papersByTopicList.add(paper);
		
		for (Author author : paper.getAuthors()) {
			List<Paper> papersByAuthorList;
			if (papersByAuthor.containsKey(author.getFullName())) {					
				papersByAuthorList = papersByAuthor.get(author.getFullName());			
			} else {
				papersByAuthorList = new LinkedList<Paper>();
				papersByAuthor.put(author.getFullName(), papersByAuthorList);			
			}
			papersByAuthorList.add(paper);
		} 
		
		for (Author author : paper.getAuthors()) {
			if (!authors.contains(author)) {
				authors.add(author);
				authorsByFullName.put(author.getFullName(), author);
			}
		}
	}
	
	public void redoMappings() {
		List<Paper> oldPapers = new ArrayList<Paper>(papers);
		papers.clear();
		papersByYear.clear();
		papersByTopic.clear();
		papersByAuthor.clear();
		papersByYearAndName.clear();
				
		for (Paper paper : oldPapers) {
			addPaper(paper);
		}
	}
	
	public List<Paper> getPapers() {
		return papers;
	}
	
	public List<Paper> getPapersByTopic(String topic) {
		return papersByTopic.get(topic);
	}
	
	public List<Paper> getPapersByYear(int year) {
		return papersByYear.get(year);
	}
	
	public List<Paper> getPapersByAuthor(String author) {
		return papersByAuthor.get(author);
	}
	
	public Paper getPaperByYearAndName(int year, String name) {
		if (papersByYearAndName.get(year) == null) { 			
			return null;
		}
		return papersByYearAndName.get(year).get(name);
	}
	
	public Author getAuthorByFullName(String fullName) {
		return authorsByFullName.get(fullName);
	}
}
