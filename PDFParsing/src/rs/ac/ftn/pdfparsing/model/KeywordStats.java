package rs.ac.ftn.pdfparsing.model;

import java.util.HashMap;

public class KeywordStats {
	HashMap<String, Integer> wordOccuranceMap = new HashMap<String, Integer>();
	
	public KeywordStats() {		
	}
	
	public void addWordOccurance(String word) {
		Integer wordOccurance = wordOccuranceMap.get(word);
		if (wordOccurance == null) {
			wordOccuranceMap.put(word, 1);
		} else {
			wordOccurance = wordOccurance + 1;
			wordOccuranceMap.put(word, wordOccurance + 1);
		}
	}
	public HashMap<String, Integer> getWordOccuranceMap() {
		return wordOccuranceMap;
	}
}
