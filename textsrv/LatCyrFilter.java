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

public class LatCyrFilter extends TokenFilter {

	
	public LatCyrFilter(TokenStream input) {
		super(input);
	}

	@Override
	public Token next(Token reusableToken) throws IOException {
		if (reusableToken == null)
			return null;
		Token nextToken = input.next(reusableToken);
		if (nextToken == null)
			return null;
		String term = nextToken.term();
		nextToken.setTermBuffer(LatCyrUtils.toLatin(term));
		return nextToken;
	}
	
	

}
