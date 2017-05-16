package tokenize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public final class GntSplitTokenizer {

  private GntSplitTokenizer() {

    // private constructor to enforce noninstantiability
  }


  public static String[] splitTokenizer(String string) {
    //System.out.println(">>>"+string+"<<<");

    String delims = "[ |\\,|\\:|\\.|\\\"|\\(|\\)|\\!|\\?]+";
    //delims = "[\\W]+";

    String[] tokens = string.split(delims);

    // GN: This is a cheap trick to remove empty strings
    final List<String> list = new ArrayList<String>();
    Collections.addAll(list, tokens);
    list.remove("");
    tokens = list.toArray(new String[list.size()]);
    return tokens;
  }
}
