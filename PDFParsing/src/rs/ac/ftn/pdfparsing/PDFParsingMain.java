package rs.ac.ftn.pdfparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rs.ac.ftn.pdfparsing.model.Article;
import rs.ac.ftn.pdfparsing.model.Library;

public class PDFParsingMain {

	Library library = Library.getInstance();
	int abstractRSStartIndx = -1;
	int abstractRSEndIndx = -1;
	int abstractRSAuthorsLength = -1;
	
	int abstractENStartIndx = -1;
	int abstractENEndIndx = -1;
	int abstractENAuthorsLength = -1;	
	
	int refCount = 0;
	int titleENCount = 0;
	int titleRSCount = 0;
	int abstractENCount = 0;
	int abstractRSCount = 0;
	int authorCount = 0;
	int totalFiles = 0;
	
	int totalFilesMatchedMeta = 0;
	
	int year;
	String topic;
	
	int startYear = Integer.MAX_VALUE;
	int endYear = Integer.MIN_VALUE;
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
	
	public String preAbstract(String content) {
		int preAbstractEndIndx = -1;		int refCount = 0;
		int titleENCount = 0;
		int titleRSCount = 0;
		int abstractENCount = 0;
		int abstractRSCount = 0;
		int authorCount = 0;
		int totalFiles = 0;
		for (String abstractAuthors : new String[] { "Sadržaj", "Apstrakt", "Садржај" }) {
			if (content.contains(abstractAuthors)) {
				preAbstractEndIndx = content.indexOf(abstractAuthors) + abstractAuthors.length();
			}
		}
		if (preAbstractEndIndx >= 0) {
			return content.substring(0, preAbstractEndIndx);
		}
		return null;
	}
	
	public String[] parseTitles(String content) {
		String titleRS = null;
		String titleEN = null;
		String preAbstractContent = preAbstract(content);
		System.out.println(preAbstractContent);
		//String[] titles = preAbstractContent.split("\n");
		//titleRS = titles[0];
		//titleEN = titles[1];		
		
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
				if (line.matches(".*([a-zčćšđž " + cyrLower + "]){2,}.*")) {
		//			System.out.println("1: " + line);
					authorsString = line;
					break;
				}
			}			
			for (String author : authorsString.split(",")) {
				authors.add(author);
			}
		}
		///System.out.println("DONE!!!!!!!!!");
		return authors.toArray(new String[authors.size()]);
	}
	
	public String [] parseAbstract(String content) {
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
					if (line.matches("[\\d\\sA-ZČĆŠĐŽ\\p{Punct}]*")) {						
						//System.out.println("MATCH: " + line);
						found = true;
						break;
					}
					indx = indx + line.length() + 1;
				}
				if (found) {
					if (i == 0) {
						abstractRS = abstractRS.substring(0, indx);
					} else {
						abstractEN = abstractEN.substring(0, indx);
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
	
	Article parseFile(File file) throws IOException {
		String filePath = file.getAbsolutePath();
		abstractRSStartIndx = -1;
		abstractRSEndIndx = -1;
		abstractRSAuthorsLength = -1;
		abstractENStartIndx = -1;
		abstractENEndIndx = -1;
		abstractENAuthorsLength = -1;
		
		totalFiles++;
		System.out.println("Parsing plaintext file: " + filePath);
		String content = FileUtils.readFileToString(file);
	//	System.out.println(content);
		
		String titleEN = null;
		String titleRS = null;
		String[] titles = parseTitles(content);
		titleEN = titles[0];
		titleRS = titles[1];						
		
		String abstracts [] = parseAbstract(content);
		String abstractEN = abstracts[0];
		String abstractRS = abstracts[1];
		
		String [] authors = parseAuthors(content);

		String[] references = parseReferences(content);
        if (titleEN != null || titleRS != null) {
        	System.out.println("Authorss: ");
            if (titleEN != null) {
            	titleENCount++;
                System.out.println("Titles EN: " + titleEN);
            } 
        	if (titleRS != null) {
        		titleRSCount++;
            	System.out.println("Titles RS: " + titleRS);
			}
        }
        if (abstractEN != null || abstractRS != null) {
        	System.out.println("Abstracts: ");
            if (abstractEN != null) {
            	abstractENCount++;
                System.out.println("Abstract EN: " + abstractEN);
            } 
        	if (abstractRS != null) {
        		abstractRSCount++;
            	System.out.println("Abstract RS: " + abstractRS);
			}
        }
        if ((abstractEN == null && !BlackListedFiles.getInstance().hasNoEnglish(filePath)) 
        		|| (abstractRS == null && !BlackListedFiles.getInstance().hasNoSerbian(filePath))) {
        	System.out.println(content);
        	System.err.println("Failed to parse " + ((abstractEN == null)?"English abstract ":"") + 
        			((abstractRS == null)?"Serbian abstract ":""));
        	throw new Error("abstract parsing error " + filePath); 
        }
        if (authors.length > 0) {
        	System.out.print("Authors: ");
        	authorCount++;
            for (String author : authors) {
            	System.out.print(author + ", ");
            }
            System.out.println();
        }
        if (references.length > 0) {
	        System.out.println("References: ");
	        for (int i = 0; i < references.length; i++) {
	        	String ref = references[i];
	        	System.out.println((i+1) + "." + ref);
	        }
        	refCount++;
        }
        Article article = new Article(authors, titleEN, titleRS, abstractEN, abstractRS, year, topic, file.getName());
        library.addArticle(article);
        return article;
     //   if (true) break;
	}
	
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
					topic = file.getName();
				}
				recurseDirectory(file);
			} else {
				String fileName = file.getName();
				if (Pattern.matches(".*.txt", fileName) && !fileName.contains("meta")) {
					parseFile(file);
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
							parseFile(newFile);
						} else {
							System.err.println("Newly created file doesn't exist: " + newFilePath);
						}
					}					
				}
			}
		}
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
					    JSONObject jsonArticle = (JSONObject) o;

					    String pdfName = (String) jsonArticle.get("pdfName");
					    String txtFileName = pdfName.replace(".pdf", ".txt");					    

					    String url = (String) jsonArticle.get("url");

					    String authorsStr = (String) jsonArticle.get("authors");
					    if (authorsStr.contains(":")) {
					    	authorsStr = authorsStr.substring(0, authorsStr.indexOf(":"));
					    }
					    String[] authors = authorsStr.split(",");

					    Article article = library.getArticleByYearAndName(year, txtFileName);
					    if (article != null) {					    	
					    	article.setAuthors(authors);
					    	totalFilesMatchedMeta++;
					    } else {
					    	System.err.println(year + " " + txtFileName);
					    }
					  }
				}
			}
		}
	}
	
	private void run() throws Exception {
		File dataDir = new File("../data/");
		recurseDirectory(dataDir);
		recurseMeta(dataDir);
		library.redoMappings();
				
		System.out.println("Successfully parsed references: " + refCount + "/" + totalFiles);
		System.out.println("Successfully parsed title EN: " + titleENCount + "/" + totalFiles);
		System.out.println("Successfully parsed title RS: " + titleRSCount + "/" + totalFiles);
		System.out.println("Successfully parsed authors: " + authorCount + "/" + totalFiles);
		System.out.println("Successfully parsed abstract EN: " + abstractENCount + "/" + totalFiles);
		System.out.println("Successfully parsed abstract RS: " + abstractRSCount + "/" + totalFiles);
		System.out.println("Total files matched meta: " + totalFilesMatchedMeta + "/" + totalFiles);
		
		
		System.out.println("Total articles by year:");
		for (int year = startYear; year <= endYear; year++) {
			System.out.println(year + ": " + library.getArticlesByYear(year).size());
		}
		
		List<Entry<String, List<Article>>> articles = new ArrayList<Map.Entry<String,List<Article>>>(library.articlesByAuthor.entrySet());

		Collections.sort(articles, new Comparator<Map.Entry<String, List<Article>>>(){
			@Override
		    public int compare(Map.Entry<String, List<Article>> o1, Map.Entry<String, List<Article>> o2) {
		        return o2.getValue().size() - o1.getValue().size();
		    }
		});
		
		int topAuthorsCount = 50;
		System.out.println("Top " + topAuthorsCount + " most published authors: ");
		{
			int i = 0;
			for (Entry<String, List<Article>> entry : articles) {
				if (i >= topAuthorsCount) {
					break;
				}
				System.out.println((i + 1) + ". " + entry.getKey() + " " + entry.getValue().size());
				i++;
			}
		}
	}

}