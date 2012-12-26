package rs.ac.uns.ftn.informatika.bibliography.textsrv;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import rs.ac.uns.ftn.informatika.bibliography.utils.LatCyrUtils;

/**
 * TokenFilter for Lucene, it is latin - cyrilic insensitive
 * 
 * @author chenejac@uns.ac.rs
 * 
 */

public class RemoveAccentsFilter extends TokenFilter {

	
	private boolean replaceDj = false;

	public RemoveAccentsFilter(TokenStream input, boolean replaceDj) {
		super(input);
		replaceDj = true;
	}

	@Override
	public Token next(Token reusableToken) throws IOException {
		if (reusableToken == null)
			return null;
		Token nextToken = input.next(reusableToken);
		if (nextToken == null)
			return null;
		String term = nextToken.term();
		if(replaceDj)
			nextToken.setTermBuffer(LatCyrUtils.removeAccents(term).replace("Dj", "D").replace("dj", "d"));
		else 
			nextToken.setTermBuffer(LatCyrUtils.removeAccents(term));
		return nextToken;
	}
	

}
