package rs.ac.ftn.pdfparsing.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import rs.ac.uns.ftn.informatika.bibliography.utils.LatCyrUtils;

public class SerbianStemmer {
	HashMap<String, String> stemmingMap = new HashMap<String, String>();
	
	private void load() throws IOException {
		String content = LatCyrUtils.toLatin(FileUtils.readFileToString(new File("metadata/stemDictionary.txt")));
		for (String line : content.split("\n")) {
			String [] parts = line.split("=");
			String root = parts[0];
			//String 
		}
	}
}
