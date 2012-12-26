package rs.ac.uns.ftn.informatika.bibliography.textsrv;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ISOLatin1AccentFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.tartarus.snowball.ext.PorterStemmer;


/**
 * Analyser for Lucene, it is latin - cyrilic insensitive
 * 
 * @author chenejac@uns.ac.rs
 * 
 */
public class CrisAnalyzer extends Analyzer {

	private Set<String> serbianStopSet;
	private Set<String> englishStopSet;
	private static final String[] SERBIAN_STOP_WORDS = {
		"biti", "ne", 
		"jesam", "sam", "jesi", "si", "je", "jesmo", "smo", "jeste", "ste", "jesu", "su",
		"nijesam", "nisam", "nijesi", "nisi", "nije", "nijesmo", "nismo", "nijeste", "niste", "nijesu", "nisu",
		"budem", "budeš", "bude", "budemo", "budete", "budu",
		"budes",
		"bih",  "bi", "bismo", "biste", "biše", 
		"bise",
		"bio", "bili", "budimo", "budite", "bila", "bilo", "bile", 
		"ću", "ćeš", "će", "ćemo", "ćete", 
		"neću", "nećeš", "neće", "nećemo", "nećete", 
		"cu", "ces", "ce", "cemo", "cete",
		"necu", "neces", "nece", "necemo", "necete",
		"mogu", "možeš", "može", "možemo", "možete",
		"mozes", "moze", "mozemo", "mozete"};
	private static final String[] ENGLISH_STOP_WORDS ={
	    "a", "an", "and", "are","as","at","be", "but",
	    "by", "for", "if", "in", "into", "is", "it",
	    "no", "not", "of", "on", "or", "s", "such",
	    "that", "the", "their", "then", "there","these",
	    "they", "this", "to", "was", "will", "with" };
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public CrisAnalyzer() {
		super();
		serbianStopSet = StopFilter.makeStopSet(SERBIAN_STOP_WORDS);
		englishStopSet = StopFilter.makeStopSet(ENGLISH_STOP_WORDS);
	}



	/**
	 * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String,
	 *      java.io.Reader)
	 */
	public TokenStream tokenStream(String fieldname, Reader reader) {
		if(fieldname.endsWith("_SRP")){
			return new RemoveAccentsFilter(
					new CroSerTranslateFilter(
						new SnowballFilter(
							new StopFilter(
								new LatCyrFilter(
									new LowerCaseFilter(
											new WhitespaceTokenizer(reader))), serbianStopSet), new SerbianStemmer())), true);
		} else if(fieldname.endsWith("_ENG")){
			return new RemoveAccentsFilter(
					new SnowballFilter(
						new StopFilter(
							new LatCyrFilter(
								new LowerCaseFilter(
										new WhitespaceTokenizer(reader))), englishStopSet), new PorterStemmer()), true);
		} else {
			return new RemoveAccentsFilter(
						new LatCyrFilter(
								new LowerCaseFilter(
										new WhitespaceTokenizer(reader))), false);
		}
	}
}
