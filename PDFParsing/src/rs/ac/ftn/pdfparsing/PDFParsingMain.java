package rs.ac.ftn.pdfparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

import rs.ac.ftn.pdfparsing.model.Author;
import rs.ac.ftn.pdfparsing.model.KeywordStats;
import rs.ac.ftn.pdfparsing.model.Paper;
import rs.ac.ftn.pdfparsing.model.Library;
import rs.ac.ftn.pdfparsing.util.LevenshteinDistance;
import rs.ac.uns.ftn.informatika.bibliography.textsrv.CrisAnalyzer;
import rs.ac.uns.ftn.informatika.bibliography.textsrv.CroSerTranslateFilter;
import rs.ac.uns.ftn.informatika.bibliography.textsrv.LatCyrFilter;
import rs.ac.uns.ftn.informatika.bibliography.textsrv.RemoveAccentsFilter;
import rs.ac.uns.ftn.informatika.bibliography.textsrv.SerbianStemmer;
import rs.ac.uns.ftn.informatika.bibliography.utils.CroSerUtils;
import rs.ac.uns.ftn.informatika.bibliography.utils.LatCyrUtils;

public class PDFParsingMain {
	String [][] sameTopicPairs = {
		{ "vi", "ai" },
		{ "hw", "hardware" },
		{ "sw", "software" },
		{ "is", "infosys" },
		{ "pi", "applied" },
		{ "rs", "ai" },
		{ "rmit", "networks" },
		{ "esoc", "internet" },
		{ "simulation", "ai" },
		
	};
    Map<String, String> sameTopicMap = new HashMap<String, String>();
    
    private String resolveTopic(String topic) {
    	int i = 0;
		while (sameTopicMap.containsKey(topic)) {
			topic = sameTopicMap.get(topic);
			if (++i > 100) {
				throw new Error("recursive map, term: " + topic);
			}
		}
		return topic;
    }
          
    private void populateSameTopics() {
        for (String[] pair : sameTopicPairs) {
        	sameTopicMap.put(pair[0], pair[1]);
        }
    }
    
    String [][] sameWordPairsEN = {
        	{ "", "" },
    };
    
    Map<String, String> sameWordMapEN = new HashMap<String, String>();
    
    String [][] sameWordPairsRS = {
        	{ "", "" },
    };    
    Map<String, String> sameWordMapRS = new HashMap<String, String>();
    
    private String resolveWords(String word, String language) {    	
    	Map<String, String> sameWordMap;    
    	if (language.equals("EN")) {
    		sameWordMap = sameWordMapEN;
    	} else if (language.equals("RS")) {
    		sameWordMap = sameWordMapRS;
    	} else {
    		return word;
    	}
    	int i = 0;
		while (sameWordMap.containsKey(word)) {
			word = sameWordMap.get(word);
			if (++i > 100) {
				throw new Error("recursive map, term: " + word);
			}
		}
		return word;
    }
    
    private void populateSameWords() {    
        for (String[] pair : sameWordPairsEN) {
        	sameWordMapEN.put(pair[0], pair[1]);
        }
        for (String[] pair : sameWordPairsRS) {
        	sameWordMapRS.put(pair[0], pair[1]);
        }
    }

	Library library = Library.getInstance();
	int abstractRSStartIndx = -1;
	int abstractRSEndIndx = -1;
	int abstractRSAuthorsLength = -1;
	
	int abstractENStartIndx = -1;
	int abstractENEndIndx = -1;
	int abstractENAuthorsLength = -1;
	
	int authorStartIndx = Integer.MAX_VALUE;
	
	int refCount = 0;
	int titleENCount = 0;
	int titleRSCount = 0;
	int abstractENCount = 0;
	int abstractRSCount = 0;
	int authorCount = 0;
	int totalParsedFiles = 0;
	
	int tpAuthorsCount = 0;
	int fpAuthorsCount = 0;
	int fnAuthorsCount = 0;
	
	int tpTitleCount = 0;
	int fpTitleCount = 0;
	int fnTitleCount = 0;
	
	int totalFilesMatchedMeta = 0;
	
	int year;
	String topic;
	
	int startYear = Integer.MAX_VALUE;
	int endYear = Integer.MIN_VALUE;
	
	boolean debugAuthors = false;
	boolean debugTitles = false;
	boolean displayPapers = false;
	
	/** python code:
	 * #!/usr/bin/python
# -*- coding: utf8 -*- 
from __future__ import print_function
import os
import re

SCRIPT_PATH = os.path.realpath(__file__)
SCRIPT_DIR = os.path.dirname(SCRIPT_PATH)
DATA_DIR = os.path.realpath(SCRIPT_DIR + "/../data")

regLatinica = PUT LATIN  CHARS
*/

	String regLatinica = "[A-Za-zČčĆćŠšĐđŽž]";
	String cyrLower = "љњертзуиопшђасдфгхјклчћжжџцвбнж";
	public String[] parseReferences(String content) {
		List<String> references = new LinkedList<String>();
		for (String refStart : new String[] {"LITERATURA", "ЛИТЕРАТУРА"}) {
			//Pattern.matches("LITERATURA.*", content);
			if (content.contains(refStart)) {
				int refStartIndx = content.indexOf(refStart);
				String referencePart = content.substring(refStartIndx + refStart.length());
		//		System.out.println(referencePart);
				
				String [] referenceStrings = referencePart.split("\\[\\d*\\]");
				for (String part : referenceStrings) {
					String reference = "";
					for (String line : part.split("\n")) {
						if (line.trim().equals("")) {
							break;
						}
						reference = reference + " " + line;
					}
					reference = reference.trim();
					if (!reference.equals("")) {
						references.add(reference);
					}
				}
			}
		}
		return references.toArray(new String[references.size()]);
	}
	
	public String preAuthor(String content) {
		int authorEndIndx = Math.min(authorStartIndx, Math.min(abstractRSStartIndx, abstractENStartIndx));
		if (authorEndIndx >= 0) {
			return content.substring(0, authorEndIndx);
		}
		return null;
	}
	
	public String[] parseTitles(String content) {
		String titleRS = null;
		String titleEN = null;
		String preAbstractContent = preAuthor(content);
		if (preAbstractContent != null) {		
			if (displayPapers) System.out.println(preAbstractContent);
			String[] titles = preAbstractContent.split("\n");
			if (titles.length > 0) {
				if (titles.length <= 2) {
					titleRS = titles[0];			
					if (titles.length > 1) {
						titleEN = titles[1];
					}
				} else {
					int linesForRS = titles.length / 2;
					titleRS = "";
					for (int i = 0; i < linesForRS; i++) {
						titleRS = titleRS + " " +  titles[i];
					}
					titleEN = "";
					for (int i = linesForRS; i < titles.length; i++) {
						titleEN = titleEN + " " + titles[i];
					}
				}
			}
		}		
		return new String[] { titleEN, titleRS };
	}
	
	public String[] parseAuthors(String content) {
		List<String> authors = new LinkedList<String>();
		//System.out.println("Parsing authors...!!!!!!!!!!!!");
		int authorEndIndx = Math.min(abstractRSStartIndx, abstractENStartIndx);
		if (authorEndIndx >= 0) {
			String authorsString = content.substring(0, authorEndIndx);
//			System.out.println(authorsString);
			for (String line : authorsString.split("\n")) {
		//		System.out.println("Line: " + line);
				if (line.matches(".*([a-zčćšđž " + cyrLower + "]){2,}.*") && (line.contains(",") || line.matches(".*" + regLatinica + "{2,}i[cć].*"))) {
		//			System.out.println("1: " + line);
					authorsString = line;
					authorStartIndx = content.indexOf(line);
					break;
				}
			}
			for (String author : authorsString.split(",")) {
				String [] removalRegexes = new String[] {"prof.", "dr", "Dr", "Mr", "mr", "prof", "[0-9]+", "\\."};
				for (String removalRegex : removalRegexes) {
					author = author.replaceAll(removalRegex, "");
				}
				author = author.trim();
				author.replaceAll("[^ A-Za-zČčĆćŠšĐđŽžљњертзуиопшђасдфгхјклчћжжџцвбнжЉЊЕРТЗУИООШЂАСДФГХЈКЛЧЋЖЅЏЦВБНМ]*", "");
				authors.add(author);
			}
		}
		///System.out.println("DONE!!!!!!!!!");
		return authors.toArray(new String[authors.size()]);
	}
	
	public String [] parseAbstract(String content) throws IOException {
		String abstractRS = null;
		String abstractEN = null;

		//System.out.println(Pattern.quote("Sadržaj|Apstrakt|Садржај"));
		/*Pattern serbianApstract = Pattern.compile(Pattern.quote(".*(Sadržaj)|(Apstrakt)|(Садржај).*"), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);		
			Matcher m = serbianApstract.matcher(content);
			if (m.find()) {
				System.out.println("Found pattern!");
				abstractRSStartIndx = m.start();
				abstractRSAuthorsLength = m.end() - m.start();
			}*/
		for (String abstractAuthors : new String[] { 
				"Sadržaj", "Apstrakt", "Садржај",
				"Rezime", "SAŽETAK", //alternative spellings...
				"Sadr aj", "Sadrž aj", "SAŽ ETAK", "С адр ж ај", "Sadrž", "A P S T R A K T", "С адр ај", "С а ржа", "С ад р ж ај", "С а држ ј", "Sadrž aj", "Sadrž aj", "Sadžaj", "Sadrˇaj", "Sadrţaj", /* due to PDF parsing errors */ 
				"Sadrzaj", "ABSTRAKT" /* spelling errors */
				}) {
			if (content.toLowerCase().contains(abstractAuthors.toLowerCase())) {
				abstractRSStartIndx = content.toLowerCase().indexOf(abstractAuthors.toLowerCase()) + abstractAuthors.length();
				abstractRSAuthorsLength = abstractAuthors.length();
				break;
			}
		}
		for (String abstractAuthors : new String[] { 
				"Abstract",
				"Summary", //alternative spellings...
				"A B S T R A C T", //PDF parsing errors
				"Abstrac", "APSTRACT", "Abstact", "Abscract", /* because some people can't spell Abstract right */ }) {
			if (content.toLowerCase().contains(abstractAuthors.toLowerCase())) {
				abstractENStartIndx = content.toLowerCase().indexOf(abstractAuthors.toLowerCase()) + abstractAuthors.length();
				abstractENAuthorsLength = abstractAuthors.length();
				break;
			}
		}
		
		if (abstractRSStartIndx >= 0) {
			abstractRS = content.substring(abstractRSStartIndx);
		}
		if (abstractENStartIndx >= 0) {
			abstractEN = content.substring(abstractENStartIndx);
		}
		
		//abstracts come in order		
		if (abstractRSStartIndx >= 0 && abstractENStartIndx >= 0) {
			if (abstractRSStartIndx > abstractENStartIndx) {
				abstractENEndIndx = abstractRSStartIndx - abstractRSAuthorsLength;
				abstractEN = content.substring(abstractENStartIndx, abstractENEndIndx);
			} else {
				abstractRSEndIndx = abstractENStartIndx - abstractENAuthorsLength;
				abstractRS = content.substring(abstractRSStartIndx, abstractRSEndIndx);
			}
		}
		
		for (int i = 0; i <= 1; i++) {
			String abstractContent = null;
			int abstractStartIndx = -1;
			int abstractEndIndx = -1;
			if (i == 0) {
				abstractContent = abstractRS;
				abstractStartIndx = abstractRSStartIndx;
				abstractEndIndx = abstractRSEndIndx;
			} else {
				abstractContent = abstractEN;
				abstractStartIndx = abstractENStartIndx;
				abstractEndIndx = abstractENEndIndx;
			}
			if (abstractStartIndx >= 0 && abstractEndIndx == -1) {
				int indx = 0;
				boolean found = false;
				for (String line : abstractContent.split("\n")) {
					if (line.matches("[\\dI]*\\.?\\s*([A-ZČĆŠĐŽ\\p{Punct}0-9][a-zčćšđž]*\\s?)+") ||
							line.matches("Ključne re.*") || line.startsWith("Index Terms") ||
							line.trim().equals("") && indx != 0) {						
						//System.out.println("MATCH: " + line);
						found = true;
						break;
					}
					indx = indx + line.length() + 1;
				}
				//System.out.println("Abstract size: " + indx);				
				if (found) {
					if (i == 0) {
						abstractRS = abstractRS.substring(0, indx);
						
					} else {
						abstractEN = abstractEN.substring(0, indx);
					
						//if (indx > 1000) { System.out.println(abstractEN); System.in.read(); }
					}
				}
			}
		}
		return new String[] { abstractEN, abstractRS };
	}
	/*
def preAbstract(content):
    abstractStart = int(10e50)

    #todo add checks for cyrillic 
    abstractRSMatch = re.search(u"", content)
    //TODO: Sadrzaj itd.
    
    if abstractRSMatch:
        abstractStart = min(abstractRSMatch.span()[0], abstractStart)
    abstractENMatch = re.search(r"Abstract", content)
    if abstractENMatch:
        abstractStart = min(abstractENMatch.span()[0], abstractStart)

    return content[:abstractStart]
    
     */
	/*
def parseAuthors(content):
    titleRS = None
    titleEN = None
    preAbstractContent = preAbstract(content)
    titles = preAbstractContent.split("\n")
    titleRS = titles[0]
    titleEN = titles[1]
#    titleENMatch = re.search(r"[A-Z\s]*", preAbstractContent)
#
#    if titleENMatch:
#        begin, end = titleENMatch.span()
#        titleEN = preAbstractContent[begin:end]
    print(titleRS)
    print(titleEN)
    return [titleEN, titleRS]

	 */
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		PDFParsingMain pdfParsingMain = new PDFParsingMain();
		pdfParsingMain.run();
	}
	
	Paper parseFile(File file) throws IOException {
		String filePath = file.getAbsolutePath();
		abstractRSStartIndx = -1;
		abstractRSEndIndx = -1;
		abstractRSAuthorsLength = -1;
		abstractENStartIndx = -1;
		abstractENEndIndx = -1;
		abstractENAuthorsLength = -1;
		
		totalParsedFiles++;
		if (displayPapers) System.out.println("Parsing plaintext file: " + filePath);
		String content = LatCyrUtils.toLatin(FileUtils.readFileToString(file));
		if (totalParsedFiles < 100) {
			System.out.println(content);
			System.in.read();
		}
	//	System.out.println(content);		
		
		String abstracts [] = parseAbstract(content);
		String abstractEN = abstracts[0];
		String abstractRS = abstracts[1];
		
		
		String [] authorFullNames = parseAuthors(content);
		
		String titleEN = null;
		String titleRS = null;
		String[] titles = parseTitles(content);
		titleEN = titles[0];
		titleRS = titles[1];

		String[] references = parseReferences(content);
		
		
        if (titleEN != null || titleRS != null) {
        	if (displayPapers) System.out.println("Titles: ");
            if (titleEN != null) {
            	titleENCount++;
            	if (displayPapers) System.out.println("Titles EN: " + titleEN);
            } 
        	if (titleRS != null) {
        		titleRSCount++;
        		if (displayPapers) System.out.println("Titles RS: " + titleRS);
			}
        }
        if (abstractEN != null || abstractRS != null) {
        	if (displayPapers) System.out.println("Abstracts: ");
            if (abstractEN != null) {
            	abstractENCount++;
            	if (displayPapers) System.out.println("Abstract EN: " + abstractEN);
            } 
        	if (abstractRS != null) {
        		abstractRSCount++;
        		if (displayPapers) System.out.println("Abstract RS: " + abstractRS);
			}
        }
        if ((abstractEN == null && !BlackListedFiles.getInstance().hasNoEnglish(filePath)) 
        		|| (abstractRS == null && !BlackListedFiles.getInstance().hasNoSerbian(filePath))) {
        	if (displayPapers) System.out.println(content);
        	if (displayPapers) System.err.println("Failed to parse " + ((abstractEN == null)?"English abstract ":"") + 
        			((abstractRS == null)?"Serbian abstract ":""));
        	//throw new Error("abstract parsing error " + filePath); 
        }
        if (authorFullNames.length > 0) {
        	if (displayPapers) System.out.print("Authors: ");
        	authorCount++;
            for (String author : authorFullNames) {
            	if (displayPapers) System.out.print(author + ", ");
            }
            if (displayPapers) System.out.println();
        }
        if (references.length > 0) {
        	if (displayPapers) System.out.println("References: ");
	        for (int i = 0; i < references.length; i++) {
	        	String ref = references[i];
	        	if (displayPapers) System.out.println((i+1) + "." + ref);
	        }
        	refCount++;
        }
        
        Author[] authors = new Author[authorFullNames.length];
        {
        	int i = 0;
	        
	        for (String authorFullName: authorFullNames) {
	        	Author author = library.getAuthorByFullName(authorFullName);
	        	if (author == null) {
	        		author = new Author(authorFullName);
	        	}
	        	authors[i++] = author;
	        }
        }
        Paper paper = new Paper(authors, titleEN, titleRS, abstractEN, abstractRS, year, topic, file.getName(), content);
        library.addPaper(paper);
        return paper;
     //   if (true) break;
	}
	
	class ToParseFile {
		File file;
		int year;
		String topic;
		public ToParseFile(File file, int year, String topic) {
			super();
			this.file = file;
			this.year = year;
			this.topic = topic;
		}		
	}
	ArrayList<ToParseFile> toParseFiles = new ArrayList<ToParseFile>(); 
	
	private void recurseDirectory(File dir) throws Exception {
		File[] files = dir.listFiles();
		Vector<String> fileNames = new Vector<String>();
		//populate list of .pdf files that have been transformed to .txt files
		for (File file : files) {
			String fileName = file.getName();
			if (Pattern.matches(".*.txt", fileName) && !fileName.contains("meta")) {				
				fileNames.add(fileName);
			}
		}
		for (File file : files) {
			String filePath = file.getAbsolutePath();
			if (BlackListedFiles.getInstance().isBlackListed(filePath)) {
				continue;
			}
			if (file.isDirectory()) {
				if (file.getName().matches("\\d+")) {
					year = Integer.valueOf(file.getName());
					startYear = Math.min(year, startYear);
					endYear = Math.max(year, endYear);
				} else if (!file.getName().equals("data")) {
					topic = resolveTopic(file.getName());
				}
				recurseDirectory(file);
			} else {
				String fileName = file.getName();
				if (Pattern.matches(".*.txt", fileName) && !fileName.contains("meta")) {
					toParseFiles.add(new ToParseFile(file, year, topic));
				}
				if (Pattern.matches(".*.pdf", fileName)) {
					String txtFilePath = fileName.replace(".pdf", ".txt");
					if (!fileNames.contains(txtFilePath)) {
						System.out.println("PDF doesn't exist: " + fileName);
						Process process = Runtime.getRuntime().exec("pdftotext " + filePath);
						process.waitFor();
						String newFilePath = dir.getAbsolutePath() + File.separator + txtFilePath;
						File newFile = new File(newFilePath);
						if (newFile.exists()) {
							toParseFiles.add(new ToParseFile(file, year, topic));
						} else {
							System.err.println("Newly created file doesn't exist: " + newFilePath);
						}
					}					
				}
			}
		}
	}
	
	String[] permute(String[] parts) {
		if (parts.length == 1) {
			return new String[] {parts[0]};
		}
		Vector<String> permutations = new Vector<String>();
		for (int i = 0; i < parts.length; i++) {
			String[] newPermutation = new String[parts.length - 1];
			String permutation = parts[i];
			for (int j = 0; j < i; j++) {
				newPermutation[j] = parts[j];
			}
			for (int j = i+1; j < parts.length; j++) {
				newPermutation[j-1] = parts[j];
			}
			{
				String [] subPermutations = permute(newPermutation);
				for (String subPermutation : subPermutations) {
					permutations.add(permutation + " " + subPermutation);
				}
			}
		}
		return permutations.toArray(new String[permutations.size()]);
	}
	
	String[] generateAllNamePermutations(String fullName) {
		String[] permutations = permute(fullName.split("\\s+"));
		return permutations;
	}
	
	private void recurseMeta(File dir) throws Exception, ParseException {
		File[] files = dir.listFiles();
		for (File file : files) {
			String filePath = file.getAbsolutePath();
			if (file.isDirectory()) {
				if (file.getName().matches("\\d+")) {
					year = Integer.valueOf(file.getName());
				}
				recurseMeta(file);
			} else {
				String fileName = file.getName();
				if (Pattern.matches(".*.txt", fileName) && fileName.contains("meta")) {
					JSONParser parser = new JSONParser();
					JSONArray a = (JSONArray) parser.parse(new FileReader(file));
					for (Object o : a)
					  {
					    JSONObject jsonPaper = (JSONObject) o;

					    String pdfName = (String) jsonPaper.get("pdfName");
					    String txtFileName = pdfName.replace(".pdf", ".txt");					    

					    Paper paper = library.getPaperByYearAndName(year, txtFileName);
					    if (paper != null) {					    	
					    	totalFilesMatchedMeta++;
					    } else {
					    	System.err.println("No paper: " + year + " " + txtFileName);
					    	continue;
					    }
					    
					    String title = (String) jsonPaper.get("title");
					    title = title.toUpperCase();
					    {
					    	int distance = Integer.MAX_VALUE;
						    if (paper.getTitleEN() != null) {
						    	distance = Math.min(LevenshteinDistance.computeLevenshteinDistance(paper.getTitleEN(), title),
						    			distance);
						    }
						    if (paper.getTitleRS() != null) {
						    	distance = Math.min(LevenshteinDistance.computeLevenshteinDistance(paper.getTitleRS(), title),
						    			distance);
						    }
						    if (distance <= 2) {						    	
						    	tpTitleCount++;
						    } else {
						    	if (paper.getTitleEN() == null || paper.getTitleRS() == null) {
						    		fnTitleCount++;	
						    	} else {
						    		fpTitleCount++;
						    	}
						    	
						    	if (totalFilesMatchedMeta < 50 && debugTitles) {
						    		System.err.println(paper);
						    		System.err.println("Failed to match: " + title + " to either of the two titles: ");
						    		System.err.println(paper.getTitleEN());
						    		System.err.println(paper.getTitleRS());
						    		System.in.read();
						    	}
						    }
					    }
					    
					    String url = (String) jsonPaper.get("url");

					    String authorsStr = (String) jsonPaper.get("authors");
					    if (authorsStr.contains(":")) {
					    	authorsStr = authorsStr.substring(0, authorsStr.indexOf(":"));
					    }
					    String[] authorFullNames = authorsStr.split(",");
					    
					    //check for matching authors
				        Author[] authors = new Author[authorFullNames.length];
				        {				        	
				        	for (String authorFullName : authorFullNames) {
				        		int closestDistance = Integer.MAX_VALUE;
				        		int index = -1;
				        		Author[] parsedAuthors = paper.getAuthors();
				        		for (int i = 0; i < parsedAuthors.length; i++) {
				        			Author parsedAuthor = parsedAuthors[i];
				        			String parsedAuthorFullName = parsedAuthor.getFullName();
				        			int distance = LevenshteinDistance.computeLevenshteinDistance(parsedAuthorFullName, authorFullName);
				        			
				        			String[] parts = parsedAuthorFullName.split("\\s+");
				        			if (parts.length >= 2 && parts.length < 6) {
				        				for (String namePermutation : generateAllNamePermutations(parsedAuthorFullName)) {
				        				//String authorFullNameReversed = "";
				        				//for (int j = parts.length - 1; j >= 0; j--) {
				        			//		authorFullNameReversed = authorFullNameReversed + " " + parts[j];
				        			//	}
					        				distance = Math.min(distance, 
					        						LevenshteinDistance.computeLevenshteinDistance(namePermutation, authorFullName));
				        				}
				        			}	
				        			if (distance < closestDistance) {
				        				closestDistance = distance;
				        				index = i;
				        			}
				        		}
				        		if (closestDistance <= 5) {
				        			tpAuthorsCount++;
				        		} else {
				        			if (parsedAuthors.length > 0) {
				        				fpAuthorsCount++;	
				        			} else {
				        				fnAuthorsCount++;
				        			}
				        			
				        			if (totalFilesMatchedMeta < 50 && debugAuthors) {
					        			System.err.println("In paper: ");
					        			System.err.println(paper);
					        			System.err.print("Failed to match: " + authorFullName + " to any of the authors: ");
						        		for (int i = 0; i < parsedAuthors.length; i++) {
						        			Author parsedAuthor = parsedAuthors[i];
						        			System.err.println(parsedAuthor.getFullName());
						        		}
						        		if (parsedAuthors.length > 0) {
						        			System.err.println("Closest match: " + parsedAuthors[index].getFullName() + " with accurancy of " + closestDistance);
						        		}

					        			System.in.read();
					        		}
				        		}
				        	}
				        }
				        {
				        	int i = 0;
					        
					        for (String authorFullName: authorFullNames) {
					        	Author author = library.getAuthorByFullName(authorFullName);
					        	if (author == null) {
					        		author = new Author(authorFullName);
					        	}
					        	authors[i++] = author;
					        }
				        }
				        paper.setAuthors(authors);
					  }
				}
			}
		}
	}
	
	private boolean isStopWord(String term) {
		for (String word : term.split("\\s+")) {
			for (String[] stopWordSets : new String[][] { CrisAnalyzer.ENGLISH_STOP_WORDS, CrisAnalyzer.SERBIAN_STOP_WORDS}) {
				for (String stopWord : stopWordSets) {				
					if (word.matches(stopWord)) {
						return true;
					}
				}
			}
			try {
				Integer.valueOf(word);
				return true;
			} catch (Exception ex) {						
			}
			if (word.matches("\\d+\\.") || word.length() < 2) {
				return true;
			}
		}
		return false;
	}
	
	String [] lowInfoWordsEN = new String[] {
			"paper", "present", "system", "us", "sistema", "data", "implement", "sto", "describ", "develop", "sto", "problem", "have", "base", "method", "solut.*",
			"softwar.*", "model", "word", "words", "result", "show", "more", "new", "process", "applic", "possibl", "provid", "been", "basic", "enabl", "give", "perform", "analysi", "through",
			"differ", "veri",
	};
	String [] lowInfoWordsRS = new String[] {
			"procentualn", "delaju", "razvoj", "sistem", "podatk", "proces", "osnovn", "njihov", "analiz", "metod", "vajd", "slika", "vec", "sve", "samo", "ta",
			"jedn", "cilj", "resenj", "enje", "sve", "pristup", "osnov", "prim", "inform.*", "mode", "model", "dat", "prikaz", "velik", "broj", "pregled", "rec", "reci",
			"uvod", "process", "servi", "zbog", "toga", "bez", "opisan", "softver", "sta", "predocen", "ovog", "nacin", "aplikacij", "imac",
	};
	
	private boolean isLowInfoTerm(String term, String language) {
		/*String [] lowInfoWords;
		if (language.equals("EN")) {
			lowInfoWords = lowInfoWordsEN;
		} else if (language.equals("RS")) {
			lowInfoWords = lowInfoWordsRS;
		} else {
			throw new Error("no such language <(!_!)? " + language);
		}*/
		for (String word : term.split("\\s+")) {
			word = word.trim();
			if (word.equals("")) {
				continue;
			}
			boolean notLowInfo = true;
			for (String [] lowInfoWords : new String [][] { lowInfoWordsEN, lowInfoWordsRS }) {				
				for (String lowInfoWord : lowInfoWords) {
					if (word.matches(lowInfoWord)) {
						notLowInfo = false;
						break;
					}
				}
				if (!notLowInfo) {
					break;
				}				
			}
			if (notLowInfo) {
				return false;
			}
		}
		return true;
	}
	
	private void printTags(List<Tag> tags, String topicOfInterest, String language, int year, int totalWords, int totalLowInfoWords, int ngram, boolean trackTermsPerPaper) {
		System.out.println("Total : " + totalWords + " , contain info count: " + (totalWords - totalLowInfoWords));
		Cloud cloud = new Cloud();
		Collections.sort(tags, new Comparator<Tag>(){
			@Override
			public int compare(Tag o1, Tag o2) {
		        if (o1.getScore() < o2.getScore()) return 1;
		        if (o1.getScore() > o2.getScore()) return -1;
		        return 0;
			}
		});
		int topWords = 30;		
		System.out.println("Top " + topWords + " most used words: " + year + " " + topicOfInterest + " " + language + " ngram: " + ngram + " limit one per paper: " + trackTermsPerPaper);
		{
			int i = 0;
			for (Tag tag : tags) {
				if (i >= topWords) {
					break;
				}
				cloud.addTag(tag);
				System.out.println((i + 1) + ". " + tag.getName() + " " + tag.getScoreInt());
				i++;
			}
		}
		//TestOpenCloud toc = new TestOpenCloud();
		//toc.show(cloud);
		if (year == -1) {
		//	toc.saveImage(topicOfInterest + "-" + language + ".png");
		} else {
		//	toc.saveImage(year + "-" + language + ".png");
		}
	}
	
	//ok this is ugly now: FIXME: we check the year if it's not -1, otherwise we check the topicOfInterest
	//it calculates frequency now
	private void parsePapersForKeywords(String topicOfInterest, String language, int year, int ngram, boolean trackTermsPerPaper) throws Exception {
		CrisAnalyzer crisAnalyzer = new CrisAnalyzer();		

		HashMap<String, KeywordStats> topicStats = new HashMap<String, KeywordStats>();
		HashMap<Integer, KeywordStats> yearStats = new HashMap<Integer, KeywordStats>();
		int totalWords = 0;
		int totalLowInfoWords = 0;
		for (Paper paper : library.getPapers()) {
			HashMap<String, Boolean> addedTerms = new HashMap<String, Boolean>(); 
			KeywordStats ks;
			if (topicStats.containsKey(paper.getTopic())) {
				ks = topicStats.get(paper.getTopic());
			} else {
				ks = new KeywordStats();
				topicStats.put(paper.getTopic(), ks);
			}
			KeywordStats ksYear;
			if (yearStats.containsKey(paper.getYear())) {
				ksYear = yearStats.get(paper.getYear());
			} else {
				ksYear = new KeywordStats();
				yearStats.put(paper.getYear(), ksYear);
			}
			String abstractContent;
			if (language.equals("EN")) {
				abstractContent = paper.getAbstractEN();
			} else if (language.equals("RS")) {
				abstractContent = paper.getAbstractRS();
			} else {
				throw new Exception("Unsupported language");
			}
										
			if (abstractContent != null) {
				TokenStream ts;
				if (language.equals("EN")) {
					ts = crisAnalyzer.tokenStream("abstract_ENG", new StringReader(abstractContent));
				} else if (language.equals("RS")) {
					ts = crisAnalyzer.tokenStream("abstract_SRP", new StringReader(abstractContent));
				} else {			
					throw new Exception("Unsupported language");
				}
				/*for (String word : new HashSet<String>(Arrays.asList(abstractContent.split("\\s+")))) {
					ks.addWordOccurance(word.toLowerCase());
				}*/
				Token token;
				LinkedList<String> queue = new LinkedList<String>();
				for (int i = 0; i < ngram; i++) {
					token = ts.next();
					if (token == null) { 
						break;
					}
					String word = token.term().toLowerCase();
					queue.add(word);
				}
				while ((token = ts.next()) != null) {										
					String term = queue.poll();
					for (int i = 0; i < ngram - 1; i++) {
						term = term + " " + queue.get(i);
					}
					if (!trackTermsPerPaper || !addedTerms.containsKey(term)) {
						ks.addWordOccurance(term);
						ksYear.addWordOccurance(term);
						addedTerms.put(term, true);
					}
					
					String word = token.term().toLowerCase();
					queue.add(word);
				}
			}
		}
		KeywordStats ks;
		if (year == -1) {
			ks = topicStats.get(topicOfInterest);
		} else {
			ks = yearStats.get(year);
		}
		List<Entry<String, Integer>> wordOccurances = new ArrayList<Map.Entry<String,Integer>>(ks.getWordOccuranceMap().entrySet());

		Collections.sort(wordOccurances, new Comparator<Map.Entry<String, Integer>>(){
			@Override
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
		        return o2.getValue() - o1.getValue();
		    }
		});
			
		List<Entry<String, Integer>> tmpWordOccurances = new ArrayList<Map.Entry<String,Integer>>();
		for (Entry<String, Integer> entry : wordOccurances) {
			if (isStopWord(entry.getKey())) {
				continue;
			}
			totalWords += entry.getValue();
			if (isLowInfoTerm(entry.getKey(), language)) {
				totalLowInfoWords+= entry.getValue();
				continue;
			}
			tmpWordOccurances.add(entry);
		}
		wordOccurances = tmpWordOccurances;
			
		List<Tag> tags = new ArrayList<Tag>();
		{
			for (Entry<String, Integer> entry : wordOccurances) {
				String word = entry.getKey();
				double tf = entry.getValue();// * 1.0 / totalWords;
				

				double occurInTopicCount = 0;
				if (year == -1) {
					for (Entry<String, KeywordStats> ksEntry : topicStats.entrySet()) {				
						Integer value = ksEntry.getValue().getWordOccuranceMap().get(word);
						if (value != null) {
							occurInTopicCount++;
						}				
					}
				} else {
					for (Entry<Integer, KeywordStats> ksEntry : yearStats.entrySet()) {				
						Integer value = ksEntry.getValue().getWordOccuranceMap().get(word);
						if (value != null) {
							occurInTopicCount++;
						}				
					}
				}
				double idf;
				if (year == -1) {
					idf = Math.log(topicStats.size() * 1.0 / occurInTopicCount);
				} else {
					idf = Math.log(yearStats.size() * 1.0 / occurInTopicCount);
				}
				double tfidf = tf * idf;
				
				
				Tag tag = new Tag(entry.getKey(), tf);
				tags.add(tag);
			}
		}
		printTags(tags, topicOfInterest, language, year, totalWords, totalLowInfoWords, ngram, trackTermsPerPaper);
	}
	
	private void showAuthorsByPublishedPapers() {
		List<Entry<String, List<Paper>>> papers = new ArrayList<Map.Entry<String,List<Paper>>>(library.papersByAuthor.entrySet());

		Collections.sort(papers, new Comparator<Map.Entry<String, List<Paper>>>(){
			@Override
		    public int compare(Map.Entry<String, List<Paper>> o1, Map.Entry<String, List<Paper>> o2) {
		        return o2.getValue().size() - o1.getValue().size();
		    }
		});
		
		int topAuthorsCount = 50;
		System.out.println("Top " + topAuthorsCount + " most published authors: ");
		{
			int i = 0;
			for (Entry<String, List<Paper>> entry : papers) {
				if (i >= topAuthorsCount) {
					break;
				}
				System.out.println((i + 1) + ". " + entry.getKey() + " " + entry.getValue().size());
				i++;
			}
		}
	}
	
	private void parseAllFiles() throws Exception {
		Collections.shuffle(toParseFiles, new Random(587641272));		
		for (ToParseFile file : toParseFiles) {
			year = file.year;
			topic = file.topic;
			parseFile(file.file);
		}
	}
	
	private double getRecall(double tp, double fp, double fn) {
		return tp / (tp + fn);
	}
	
	private double getPrecision(double tp, double fp, double fn) {
		return tp / (tp + fp);
	}
	
	private double getAccurancy(double tp, double fp, double fn) {
		return tp / (tp + fp + fn);
	}
	
	private double getFMeasure(double tp, double fp, double fn) {
		double precision = getPrecision(tp, fp, fn);
		double recall = getRecall(tp, fp, fn);
		return 2 * (precision * recall) / (precision + recall);
	}
	
	private void run() throws Exception {
		CroSerUtils.loadStemDictionary();
		populateSameTopics();
		populateSameWords();
		File dataDir = new File("../data/");
		recurseDirectory(dataDir);
		parseAllFiles();
		recurseMeta(dataDir);		
		library.redoMappings();
				
		System.out.println("Successfully parsed references: " + refCount + "/" + totalParsedFiles);
		System.out.println("Successfully parsed title EN: " + titleENCount + "/" + totalParsedFiles);
		System.out.println("Successfully parsed title RS: " + titleRSCount + "/" + totalParsedFiles);
		System.out.println("Successfully parsed authors: " + authorCount + "/" + totalParsedFiles);
		System.out.println("Successfully parsed abstract EN: " + abstractENCount + "/" + totalParsedFiles);
		System.out.println("Successfully parsed abstract RS: " + abstractRSCount + "/" + totalParsedFiles);
		System.out.println("Matching authors: " + tpAuthorsCount + ", different authors fp: " + fpAuthorsCount + ", fn: " + fnAuthorsCount);
		System.out.println("Authors stats: recall  " + getRecall(tpAuthorsCount, fpAuthorsCount, fnAuthorsCount) + " precision " + getPrecision(tpAuthorsCount, fpAuthorsCount, fnAuthorsCount) + 
				" accurancy  " + + getAccurancy(tpAuthorsCount, fpAuthorsCount, fnAuthorsCount) + " f1 " + getFMeasure(tpAuthorsCount, fpAuthorsCount, fnAuthorsCount));
		System.out.println("Matching titles: " + tpTitleCount + ", different titles fp " + fpTitleCount + ", fn: " + fnTitleCount);
		System.out.println("Title stats: recall  " + getRecall(tpTitleCount, fpTitleCount, fnTitleCount) + " precision " + getPrecision(tpTitleCount, fpTitleCount, fnTitleCount) + 
				" accurancy  " + + getAccurancy(tpTitleCount, fpTitleCount, fnTitleCount) + " f1 " + getFMeasure(tpTitleCount, fpTitleCount, fnTitleCount));
		System.out.println("Total files matched meta: " + totalFilesMatchedMeta + "/" + totalParsedFiles);
		
		System.out.println("Total unqiue authors: " + library.papersByAuthor.size());
		System.out.println("Total papers by year:");
		for (String language : new String[] { "EN", "RS"}) {
			for (int year = startYear; year <= endYear; year++) {
				int totalPapers = 0;
				for (Paper paper : library.getPapersByYear(year)) {
					if ((language.equals("EN") && paper.getAbstractEN() != null) || (language.equals("RS") && paper.getAbstractRS() != null)) {
						totalPapers++;
					}
				}
				System.out.println("\t" + year + ": " + totalPapers);
			}
		}
		
		System.out.println("Total papers by topic:");
		for (String language : new String[] { "EN", "RS"}) {
			for (Entry<String, List<Paper>> entrySet : Library.getInstance().papersByTopic.entrySet()) {
				int totalPapers = 0;
				for (Paper paper : entrySet.getValue()) {
					if ((language.equals("EN") && paper.getAbstractEN() != null) || (language.equals("RS") && paper.getAbstractRS() != null)) {
						totalPapers++;
					}
				}
				System.out.println(entrySet.getKey() + " "  + language + ": " + totalPapers);
			}
		}
		
		showAuthorsByPublishedPapers();
		
		for (int year = startYear; year <= endYear; year++) {
			for (String lang : new String[] { "EN", "RS"} ) {
				for (int i = 1; i <= 3; i++) {
					for (boolean trackTermsPerPaper : new boolean [] { true, false} ) {
						parsePapersForKeywords("", lang, year, i, trackTermsPerPaper);
					}
					//System.in.read();
				}
			}
		}
		
		for (String topic : Library.getInstance().papersByTopic.keySet()) {
			for (String lang : new String[] { "EN", "RS"} ) {
				for (int i = 1; i <= 3; i++) {
					for (boolean trackTermsPerPaper : new boolean [] { true, false} ) {
						parsePapersForKeywords(topic, lang, -1, i, trackTermsPerPaper);
					}
					//	System.in.read();
				}
			}
		}
		System.exit(0);
	}

}