package de.dfki.mlt.gnt.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple tokenizer without sentence recognition.
 *
 * @author Günter Neumann, DFKI
 */
public final class GntSimpleTokenizer {

  private static final Pattern delims = Pattern.compile("[ |\\,|\\:|\\.|\\\"|\\(|\\)|\\!|\\?]+");


  private GntSimpleTokenizer() {

    // private constructor to enforce noninstantiability
  }


  public static List<String> tokenize(String string) {

    List<String> tokens = new ArrayList<String>();

    Matcher m = delims.matcher(string);

    int lastEnd = 0;
    while (m.find()) {
      int start = m.start();
      if (lastEnd != start) {
        String nonDelim = string.substring(lastEnd, start);
        tokens.add(nonDelim);
      }
      String delim = m.group().trim();
      if (delim.length() > 0) {
        tokens.add(delim);
      }

      int end = m.end();
      lastEnd = end;
    }

    if (lastEnd != string.length()) {
      String nonDelim = string.substring(lastEnd);
      tokens.add(nonDelim);
    }

    return tokens;
  }
}
