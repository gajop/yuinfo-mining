package rs.ac.uns.ftn.informatika.bibliography.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.RAMDirectory;

import rs.ac.uns.ftn.informatika.bibliography.textsrv.SerbianStemmer;


/**
 * Croation Serbian translation.
 * 
 * @author chenejac@uns.ac.rs
 */
public class CroSerUtils {

	public static Searcher searcher;
	
	public static RAMDirectory directory;
	
	public static String dictionaryPath;

    public static String translateFromCroationToSerbian(String term){
		String retVal = term;
		if(term!=null){
			retVal = search("CS", term, "SS");
		}
		return retVal;
	}
	
	private static String search(String fieldSearched, String fieldValue, String fieldReturned) {
	     String retVal = null;
		 try { 
			 BooleanQuery booleanQuery = new BooleanQuery(); 
			 Query query1 = new TermQuery(new Term(fieldSearched, fieldValue));
			 Query query2 = new TermQuery(new Term(fieldReturned, fieldValue));
			 booleanQuery.add(query1, Occur.MUST);
			 booleanQuery.add(query2, Occur.MUST_NOT);
			  TopDocCollector collector = new TopDocCollector(1);
			  CroSerUtils.searcher.search(booleanQuery, collector);
		      ScoreDoc[] hits = collector.topDocs().scoreDocs;
		      int hitCount = collector.getTotalHits();
		      if (hitCount > 0) {
	            ScoreDoc scoreDoc = hits[0];
	            int docId = scoreDoc.doc;
	            Document doc = searcher.doc(docId);
	            retVal = doc.get(fieldReturned);           
	          }
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	     return retVal;
	}
	
	public static boolean createStemDictionary(){
		boolean retVal = false;
		try {
			
			File fileDir = new File(dictionaryPath + "/dictionary.txt");
			 
			PrintWriter printWriter = new PrintWriter(new File(dictionaryPath + "/stemDictionary.txt"), "UTF8");
			
			BufferedReader in = new BufferedReader(
			   new InputStreamReader(
	                      new FileInputStream(fileDir), "UTF8"));
	 
			String str = in.readLine();
			
			HashSet<String> croationStemmedWords = new HashSet<String>();
			
			while (str  != null) {
				if(str.contains("=")){
					String[] split = str.split("=");
					String serbian = split[0];
					String croation = split[1].split(" ")[0];
					SerbianStemmer ss = new SerbianStemmer();
					ss.setCurrent(serbian);
					ss.stem();
					String serbianStemmed = ss.getCurrent();
					ss.setCurrent(croation);
					ss.stem();
					String croationStemmed = ss.getCurrent();
					str = in.readLine();
					if((!croationStemmed.equals(serbianStemmed)) && (!croationStemmedWords.contains(croationStemmed))){
						printWriter.append(serbianStemmed + "=" + croationStemmed + ((str!=null)?"\n":""));
						croationStemmedWords.add(croationStemmed);
					}
				} else {
					str = in.readLine();
				}
			}
			printWriter.flush();
			printWriter.close();
	         
			retVal = true;
	        log.debug("Croation Serbian Dictionary sucessfully created!");
	      }
	      catch (IOException ioe) {
	    	  log.debug("Croation Serbian Dictionary creation unsuccessful!");
	    	  log.error(ioe);
	      }
	      return retVal;
	}
	
	public static boolean loadStemDictionary(){
		boolean retVal = false;
		try {
		 	if(CroSerUtils.directory != null){
		 		try{
		 			CroSerUtils.directory.close();
		 			CroSerUtils.searcher.close();
		 		} catch (Exception e){
		 			
		 		}
		 	} 
			CroSerUtils.directory = new RAMDirectory();
			
			IndexWriter writer =
	                 new IndexWriter(CroSerUtils.directory,
	                         new StandardAnalyzer(),
	                         IndexWriter.MaxFieldLength.LIMITED);
			
			File fileDir = new File(dictionaryPath + "/stemDictionary.txt");
			 
			if(! fileDir.exists()){
				System.out.println("Proba");
				if(! createStemDictionary()){
					return false;
				}
				fileDir = new File(dictionaryPath + "/stemDictionary.txt");
			}
			BufferedReader in = new BufferedReader(
			   new InputStreamReader(
	                      new FileInputStream(fileDir), "UTF8"));
	 
			String str;
			
			while ((str = in.readLine()) != null) {
				String[] split = str.split("=");
				String serbian = split[0];
				String croation = split[1];
				Document doc = new Document();
			    doc.add(new Field("SS", serbian, Field.Store.YES, Field.Index.ANALYZED));
			    doc.add(new Field("CS", croation, Field.Store.YES, Field.Index.ANALYZED));
	        	writer.addDocument(doc);
			}
	         writer.optimize();
	         writer.close();
	 
	         CroSerUtils.searcher = new IndexSearcher(CroSerUtils.directory);
	         
	         retVal = true;
	         log.debug("Croation Serbian Dictionary sucessfully loaded!");
	      }
	      catch (IOException ioe) {
	    	  log.debug("Croation Serbian Dictionary load unsuccessful!");
	    	  log.error(ioe);
	      }
	      return retVal;
	}
	
	private static Log log = LogFactory.getLog(CroSerUtils.class.getName());
}
