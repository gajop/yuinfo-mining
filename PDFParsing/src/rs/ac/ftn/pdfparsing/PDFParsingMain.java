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
		if (content.matches(".*Sadržaj|Apstrakt|Садржај.*")) {
			String abstractStrings [] = content.split("Sadržaj|Apstrakt|Садржај");
			System.out.println(abstractStrings[1]);
		}
		Pattern.matches("", content);
		return "";
	}
	
	public String [] parseAbstract(String content) {
		String abstractRS = null;
		String abstractEN = null;
		if (content.matches(".*(Sadržaj|Apstrakt|Садржај).*")) {
			String abstractStrings [] = content.split("Sadržaj|Apstrakt|Садржај");
			System.out.println(abstractStrings[1]);
			abstractRS = abstractStrings[1];
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
	
	public String[] parseTitle(String content) {
		String titleRS = null;
		String titleEN = null;
		String preAbstractContent = preAbstract(content);
		//String[] titles = preAbstractContent.split("\n");
		//titleRS = titles[0];
		//titleEN = titles[1];		
		
		return new String[] { titleEN, titleRS };
	}
	/*
def parseTitle(content):
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
				totalFiles++;
				if (totalFiles >= 100) {
					break;
				}
				System.out.println("Parsing plaintext file: " + filePath);
				String content = FileUtils.readFileToString(file);
			//	System.out.println(content);

				String titleEN = null;
				String titleRS = null;
				String[] titles = parseTitle(content);
				titleEN = titles[0];
				titleRS = titles[1];
			//	if (true) continue;
				String authorsEN = null;
				String authorsRS = null;
				
				String abstracts [] = parseAbstract(content);
				String abstractEN = abstracts[0];
				String abstractRS = abstracts[1];

				String[] references = parseReferences(content);
		        if (titleEN != null || titleRS != null) {
		        	System.out.println("Titles: ");
		            if (titleEN != null) {
		                System.out.println("Title EN: " + titleEN);
		            } 
		        	if (titleRS != null) {
		            	System.out.println("Title RS: " + titleRS);
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
		        System.out.println("References: ");
		        for (int i = 0; i < references.length; i++) {
		        	String ref = references[i];
		       // 	System.out.println((i+1) + "." + ref);
		        }
		        if (references.length > 0) {
		        	refCount++;
		        }
		        //if (true) break;
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