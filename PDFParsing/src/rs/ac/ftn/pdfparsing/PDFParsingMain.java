package rs.ac.ftn.pdfparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class PDFParsingMain {
	
	int abstractRSStartIndx = -1;
	int abstractRSEndIndx = -1;
	int abstractRSAuthorsLength = -1;
	
	int abstractENStartIndx = -1;
	int abstractENEndIndx = -1;
	int abstractENAuthorsLength = -1;
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
		int preAbstractEndIndx = -1;
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

		for (String abstractAuthors : new String[] { "Sadržaj", "Apstrakt", "Садржај" }) {
			if (content.contains(abstractAuthors)) {
				abstractRSStartIndx = content.indexOf(abstractAuthors) + abstractAuthors.length();
				abstractRSAuthorsLength = abstractAuthors.length();
			}
		}
		for (String abstractAuthors : new String[] { "Abstract" }) {
			if (content.contains(abstractAuthors)) {
				abstractENStartIndx = content.indexOf(abstractAuthors) + abstractAuthors.length();
				abstractENAuthorsLength = abstractAuthors.length();
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
	public static void main(String[] args) throws IOException {
		PDFParsingMain pdfParsingMain = new PDFParsingMain();
		pdfParsingMain.run();
	}
	
	private void run() throws IOException {
		File dir = new File("../data/");	
		int refCount = 0;
		int titleENCount = 0;
		int titleRSCount = 0;
		int abstractENCount = 0;
		int abstractRSCount = 0;
		int authorCount = 0;
		int totalFiles = 0;
		
		for (File file : dir.listFiles()) {			
			String filePath = file.getAbsolutePath();
			String fileName = file.getName();
			if (Pattern.matches(".*.txt", fileName)) {				
				abstractRSStartIndx = -1;
				abstractRSEndIndx = -1;
				abstractRSAuthorsLength = -1;
				abstractENStartIndx = -1;
				abstractENEndIndx = -1;
				abstractENAuthorsLength = -1;
				
				totalFiles++;
				if (totalFiles >= 100) {
					break;
				}
				System.out.println("Parsing plaintext file: " + filePath);
				String content = FileUtils.readFileToString(file);
			//	System.out.println(content);

				String titleEN = null;
				String titleRS = null;
				String[] titles = parseTitles(content);
				titleEN = titles[0];
				titleRS = titles[1];
			//	if (true) continue;
								
				
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
		     //   if (true) break;
			}
		}
		System.out.println("Successfully parsed references: " + refCount + "/" + totalFiles);
		System.out.println("Successfully parsed title EN: " + titleENCount + "/" + totalFiles);
		System.out.println("Successfully parsed title RS: " + titleRSCount + "/" + totalFiles);
		System.out.println("Successfully parsed authors: " + authorCount + "/" + totalFiles);
		System.out.println("Successfully parsed abstract EN: " + abstractENCount + "/" + totalFiles);
		System.out.println("Successfully parsed abstract RS: " + abstractRSCount + "/" + totalFiles);
	}

}