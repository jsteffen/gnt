package tagger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public final class PostProcessor {

  private static Set<String> emoticon =
      new HashSet<String>(
          Arrays.asList(":)", ";)", "^^", ":D", ";)", ";-)", ":)", "♥", "^^", ";-)", "^^", "*-*", ":D",
              ":P", "^^", "<3", ":((", ";-)", ":-)", ";)", "*-*", ":-)", ":)", ":-P", ":(", ":-)", ":D",
              "-.-", "^^", ":-)", ":D", ":-P", ";)", ";-)", ":D", "O.o", ":D", ";)", "o.O", ";)", ":D",
              "xD", "\\o/", ":'o", "<3", ";)", "XD", ":-)", ":-*", ":D", "♥", ":D", ";))))", ">__<", ";-)",
              ":-)", ":-)))", ":D", "<3", "^^", ":D", ";-)", ";)", "xD", ":)", ";)", ":'(", ":(", ":€", ":D",
              ".__.", ":-)", ":P", ":D", ":P", ":D", "<3", ":)", "^^", "D:", "xD", ":-/", ";-)", ":D", ":(", ">",
              ":D", "<3", ";-)", ";)", "*_*", ":)", ":-)", "^^", ":o)", ":D", "<3", "=>", ":)", ":P", ":/",
              ":P", "-.-", ":D", ":/", "<3", ":)", "♥", ":)", ";)", ";-)", ":)", ":(", ":D", ";)", ";oD", ":)",
              ":-))", ":-)", ";)", ":-(", ":)", ";-)", ";)", ":/", ":O", ";)", ":)))", "o_O", "-_-",
              ":(", ":D", ";)", "oO", ":(", "-.-", ";)", ":D", ";-)", "<3", ">:D", ":D"));

  private static Set<String> pause =
      new HashSet<String>(
          Arrays.asList("..", "...", "....", ".....", "........", "..........."));


  private PostProcessor() {

    // private constructor to enforce noninstantiability
  }


  public static String determineTwitterLabel(String word, String label) {

    // missing PAUSE and COMMENTS
    if (word.charAt(0) == '@') {
      return "ADDRESS";
    } else if (word.charAt(0) == '#') {
      // very ambiguous
      return "HASH";
    } else if (word.startsWith("http://")) {
      return "URL";
    } else if (emoticon.contains(word)) {
      return "EMO";
    } else if (pause.contains(word)) {
      return "PAUSE";
    } else {
      return label;
    }
  }
}
