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
	public static final String[] SERBIAN_STOP_WORDS = {
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
		"mozes", "moze", "mozemo", "mozete",
		
		//gajop added
		"se", "na", "za", "da", "sa", "od", "ili", "koji", "kao", "po", "što", "koje", "kako", "koja",
		"iz", "uz", "tako", "pri", "do", "ovaj", "ovom",
		
		//hacks
		"uspesnost", "ki",
		"rac", "nac", "unarsk", "vis", "kih", "ava", "moz", "znac", "aj", "avanj", "ena", "ajn", "zas", "eni",
		"vaz", "iti", "ke", "uvelic", "anje", "eg", "vrs", "nih", "nost", "kim", "anja", "unarodn", "aju",
		"en", "em", "nosti", "ivanj", "enem", "i:", "um", "ivanja.", "nu", "ene", "e,", "ter", "ine", "ic", "ij", "nog", "aja.",
		"aka", "bb", "itav", "oj", "inu",
	};
	public static final String[] ENGLISH_STOP_WORDS ={
	    "a", "an", "and", "are","as","at","be", "but",
	    "by", "for", "if", "in", "into", "is", "it",
	    "no", "not", "of", "on", "or", "s", "such",
	    "that", "the", "their", "then", "there","these",
	    "they", "this", "to", "was", "will", "with",
	
	    //gajop added
	    "which", "from", "can", "has", "we", "one", "its", "all", "also", "most", "some",
	    
	    //hacks
	    "uspesnost", "ki",
	    "ra", "mrez", "st", "nj", "rac", "enj", "nac", "ci", "vlj", "sm", "vizuelizacij", "va", "moz",
	    "ko", "dn", "nost", "re", "saobrac", "aja", "ava", "enja", "pr", "ku", "korisc", "ke", "nih", "linija", "ri",
	    "vi", "vo", "nosti", "omoguc*", "podatak.*", "prenos.*", "enjem", "linka", "izmedj", "ka", "moguc", "softversk.*", "odredj",
	    "okruz", "sluc.*", "prac", "aj", "pi", "sluz", "moibiln.*",
	    "cvor.*", "antena", "sloze.*", "takt*", "evidenci*", ".oko", "uredjaj.*", "lj", "devizn.*", "topologij.*", "ed", "kl",
	    "virtualni.*", "povec.*", "ponasan.*", "poruk.*", "kre", "au", "ve", "itih", "tpo", "enih", "ifra", "medj", "ina", "eno",
	    "efekt.*", "organizacij.*", "slajd.*", 
	};
	
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
