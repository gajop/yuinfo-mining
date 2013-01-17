package rs.ac.ftn.pdfparsing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Library {
	List<Article> articles = new LinkedList<Article>();

	public HashMap<Integer, List<Article>> articlesByYear = new HashMap<Integer, List<Article>>();
	public HashMap<String, List<Article>> articlesByTopic = new HashMap<String, List<Article>>();
	public HashMap<String, List<Article>> articlesByAuthor = new HashMap<String, List<Article>>();
	public HashMap<Integer, HashMap<String, Article>> articlesByYearAndName = new HashMap<Integer, HashMap<String, Article>>();
	static Library instance = new Library();
	
	public static Library getInstance() {
		return instance;
	}
	
	public void addArticle(Article article) {
		articles.add(article);
		List<Article> articlesByYearList;
		HashMap<String, Article> articlesByYearMap;
		if (articlesByYear.containsKey(article.getYear())) {					
			articlesByYearList = articlesByYear.get(article.getYear());	
			articlesByYearMap = articlesByYearAndName.get(article.getYear());
		} else {			
			articlesByYearList = new LinkedList<Article>();			
			articlesByYear.put(article.getYear(), articlesByYearList);
			
			articlesByYearMap = new HashMap<String, Article>();			
			articlesByYearAndName.put(article.getYear(), articlesByYearMap);
		}
		articlesByYearList.add(article);
		articlesByYearMap.put(article.getFileName(), article);

		List<Article> articlesByTopicList;
		if (articlesByTopic.containsKey(article.getTopic())) {					
			articlesByTopicList = articlesByTopic.get(article.getTopic());			
		} else {
			articlesByTopicList = new LinkedList<Article>();
			articlesByTopic.put(article.getTopic(), articlesByTopicList);			
		}
		articlesByTopicList.add(article);
				
		for (String author : article.getAuthors()) {
			List<Article> articlesByAuthorList;
			if (articlesByAuthor.containsKey(author)) {					
				articlesByAuthorList = articlesByAuthor.get(author);			
			} else {
				articlesByAuthorList = new LinkedList<Article>();
				articlesByAuthor.put(author, articlesByAuthorList);			
			}
			articlesByAuthorList.add(article);
		}		
	}
	
	public void redoMappings() {
		List<Article> oldArticles = new ArrayList<Article>(articles);
		articles.clear();
		articlesByYear.clear();
		articlesByTopic.clear();
		articlesByAuthor.clear();
		articlesByYearAndName.clear();
				
		for (Article article : oldArticles) {
			addArticle(article);
		}
	}
	
	public List<Article> getArticles() {
		return articles;
	}
	
	public List<Article> getArticlesByTopic(String topic) {
		return articlesByTopic.get(topic);
	}
	
	public List<Article> getArticlesByYear(int year) {
		return articlesByYear.get(year);
	}
	
	public List<Article> getArticlesByAuthor(String author) {
		return articlesByAuthor.get(author);
	}
	
	public Article getArticleByYearAndName(int year, String name) {
		if (articlesByYearAndName.get(year) == null) { 			
			return null;
		}
		System.out.println(articlesByYearAndName.get(year).size());
		return articlesByYearAndName.get(year).get(name);
	}
}
