package de.dfki.mlt.gnt.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple tokenizer without sentence recognition.
 *
 * @author Günter Neumann, DFKI
 */
public final class GntSimpleTokenizer {

  private GntSimpleTokenizer() {

    // private constructor to enforce noninstantiability
  }


  public static List<String> tokenize(String string) {

    String delims = "[ |\\,|\\:|\\.|\\\"|\\(|\\)|\\!|\\?]+";
    String[] tokensArray = string.split(delims);
    // collect non-empty tokens
    List<String> tokens = new ArrayList<>();
    for (int i = 0; i < tokensArray.length; i++) {
      if (tokensArray[i].length() > 0) {
        tokens.add(tokensArray[i]);
      }
    }
    return tokens;
  }
}
