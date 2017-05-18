package de.dfki.mlt.gnt.tokenizer;

import java.util.Arrays;
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
    return Arrays.asList(string.split(delims));
  }
}
