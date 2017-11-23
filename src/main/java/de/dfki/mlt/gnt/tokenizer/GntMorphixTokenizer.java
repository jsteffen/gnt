package de.dfki.mlt.gnt.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A reimplementation of our morphix-reader which is a very simple but effective and fast automaton for mapping a
 * string into a list of tokens.
 * It handles cardinals and ordinals and special chars between words.
 * Punctuation are also isolated as single strings so can be used to split the token list into a list of sentences.
 * Output tokens can be lowCased or not.
 * Currently, developed for EN/DE like languages.
 *
 * @author Günter Neumann, DFKI
 */
public class GntMorphixTokenizer {

  private static final Logger logger = LoggerFactory.getLogger(GntMorphixTokenizer.class);

  // the last one should be #\^D, the Fill Down character
  private static final List<Character> SPECIAL_CHARS =
      Arrays.asList('.', ',', ';', '!', '?', ':', '(', ')', '{', '}', '[', ']', '$', '€', '\'', '\b', '"', '`');

  private static final List<Character> EOS_CHARS =
      Arrays.asList('.', '!', '?');

  private static final List<Character> DELIMITER_CHARS =
      Arrays.asList('-', '_');

  private static final List<Character> TOKEN_SEP_CHARS =
      Arrays.asList(' ', '\n', '\t');

  private boolean lowerCase;
  private boolean splitString;

  private boolean createSentence;
  private boolean isCandidateAbrev;

 private String inputString;
  private List<String> tokenList;
  private List<List<String>> sentenceList;


  public GntMorphixTokenizer(boolean lowerCase, boolean splitString) {
    this.lowerCase = lowerCase;
    this.splitString = splitString;
  }


  public boolean isSplitString() {

    return this.splitString;
  }


  public void setSplitString(boolean splitString) {

    this.splitString = splitString;
  }


  public boolean isLowerCase() {

    return this.lowerCase;
  }


  public void setLowerCase(boolean lowerCase) {

    this.lowerCase = lowerCase;
  }


  // The idea is to define two points s and e which define a possible span over the input string vector.
  // Depending on the type of char, a substring is extracted using current span information and a token is created.
  // By making a new string form the substring and eventually lower-case the char or not.
  // Thus the input string should be processed as a global variable

  private String makeToken(int start, int end, boolean lowerCaseParam) {

    int sl = Math.max(1, (end - start));
    char c = '\0';
    StringBuilder newToken = new StringBuilder(sl);

    for (int i = 0; i < sl; i++) {
      c = (lowerCaseParam)
          ? (Character.toLowerCase(this.inputString.charAt(i + start))) : this.inputString.charAt(i + start);
      newToken.append(c);
    }
    //String outputString = newToken.toString()+"["+(start)+":"+(start+sl)+"]";
    String outputString = newToken.toString();
    return outputString;
  }


  private String convertToCardinal(String newToken) {

    return newToken; // +":CARD";
  }


  private String convertToOrdinal(String newToken) {

    return newToken; //+":ORD";
  }


  private String convertToCardinalAndOrdinal(String newToken) {

    //    System.out.println("AAA:" + newToken+":AAA");
        String cardinalString = newToken.substring(0, (newToken.length() - 1));
    //    String ordinalString = newToken;
    //    String outputString = cardinalString+"card:or:ord:"+ordinalString;
    String outputString = cardinalString;
    return outputString;
  }

  // TODO -> looks already not that bad
  // identify sentence boundary
  // NOTE: what to do if we have no sentence boundary but only newline ?
  // try some heuristics here
  // thus make sure to collect some look-a-head elements before deciding when to create a sentence
  // also: capture some specific HTML patterns like "HTTP/1.1", cf. GarbageFilter


  // works but often not enough, e.g., when abrev is at end of sentence or token is larger than 3 chars, e.g.,
  // bzw. domainname . de -> . als sentence boundary
  // I need left/right context
  private void setCandidateAbrev(String token) {

    logger.debug("Abrev? " + token);
    if ((token.length() <= 3)) {
      this.isCandidateAbrev = true;
    } else {
      this.isCandidateAbrev = false;
      logger.debug("this.isCandidateAbrev=" + this.isCandidateAbrev);
    }
  }


  private void setCreateSentenceFlag(char c) {

    logger.debug("Create sent: " + c);
    if (EOS_CHARS.contains(c)
        && !this.isCandidateAbrev) {
      this.createSentence = true;
      logger.debug("this.createSentence=" + this.createSentence);
    }
  }


  private boolean isSingelCharSentence(List<String> tokenlist) {

    return ((tokenlist.size() == 1)
        && tokenlist.get(0).equals("\"")
        &&
        !this.sentenceList.isEmpty());
  }

  //TODO
  // adapt this to handle abbreviations:
  // if sentence starts with , or lowercase word, and last sentence last token is a small-enough word, then
  // make last word as abbreviation (adding .) and concatenate the two sentences.
  // Now, what if Sentence starts upper case or otherwise, and previous word would have been an abbreviation ?
  // At least, get ride of setCandidateAbrev.

  private void extendSentenceList() {

    PostProcessor postProcessor = new PostProcessor();
    // make a sentence
    if (!this.tokenList.isEmpty()) {
      List<String> newSentence = postProcessor.postProcessTokenList(this.tokenList);

      if (this.isSingelCharSentence(newSentence)) {
        List<String> prevSentence = this.sentenceList.get(this.sentenceList.size() - 1);
        prevSentence.add(newSentence.get(0));
      } else {

        this.sentenceList.add(newSentence);
      }

      System.out.println("Out: " + this.sentenceList.get(this.sentenceList.size() - 1));
    }
    // reset sensible class parameters

    this.createSentence = false;
    this.tokenList = new ArrayList<String>();
  }

  // ****************************** Main tokenizer based on Morphix Lisp tokenizer


  /*
   * NOTE:
   * in state 2, the FST jumps to state 3 and 4
   * to handle delimiters in numbers, as in 23.456 or 3,4e
   *.
   * The original morphix-reader jumped from 3 and 4 to state 5
   * if the char followed the delimiter is a number.
   * I think this is wrong, because it leads to a separation of the numbers after all 1+ delimiters, e.g.,
   * 1,500,000 etc.
   * For that reason, I changed the FST so that it jumps back to state 2.
   * This will then keep all digits together, e.g., also in date like tokens of form:
   * 23.10.2016 or 1.2.3.4.5. or 3,4,5,6,6
   * and will assigned them type :card or :ord
   *
   *
   * NOTE: this further means that state 5 is not used anymore.
   *
   * TODO
   * I think it might not be so difficult to extend the FST to further subclassify
   * such "complex" numbers.
   *
   * check e.g.,
   * 13 : 55 -> 13:55, 2001 / 2002 -> 2001/2002
   */
  public List<List<String>> tokenize(String inputStringParam) {

    // Initialization
    this.createSentence = false;
    this.isCandidateAbrev = false;
    this.inputString = inputStringParam;
    this.tokenList = new ArrayList<>();
    this.sentenceList = new ArrayList<>();
    int il = this.inputString.length();
    int state = 0;
    int start = 0;
    int delimCnt = 0;
    int end = 0;
    char c = '\0'; // used as dummy instead of nil or null

    logger.debug("Input (#" + il + "): " + this.inputString);

    // This will be a loop which is terminated inside
    while (true) {
      logger.debug("Start: " + start + " end: " + end + " State " + state + " c: " + c);
      //System.out.println("Start: " + start + " end: " + end + " State " + state + " c: " + c);

      if (end > il) {
        //if (this.createSentence) {
        // always collect remaining tokens in a last sentence
        this.extendSentenceList();
        //}
        break;
      }

      if (end == il) {
        c = '\0';
      } else {
        c = this.inputString.charAt(end);
      }

      if (this.createSentence) {
        this.extendSentenceList();
      }


      switch (state) {
        // state actions

        case 1: // 1 is the character state, so most likely
          if ((c == '\0') || TOKEN_SEP_CHARS.contains(c)) {
            String newToken = this.makeToken(start, end, this.lowerCase);
            this.tokenList.add(newToken);
            state = 0;
            start = (1 + end);
          } else {
            if (this.splitString && DELIMITER_CHARS.contains(c)) {
              state = 6;
              delimCnt++;
            } else {
              if (SPECIAL_CHARS.contains(c)) {
                String newToken = this.makeToken(start, end, this.lowerCase);
                this.tokenList.add(newToken);
                this.setCandidateAbrev(newToken);
                this.tokenList.add(Character.toString(c));
                this.setCreateSentenceFlag(c);
                state = 0;
                start = (1 + end);
              }
            }
          }
          break;

        case 0: // state zero covers: space, tab, specials
          if (TOKEN_SEP_CHARS.contains(c)) {
            start++;
          } else if ((c == '\0')) {
            this.createSentence = true;
          } else {
            if (SPECIAL_CHARS.contains(c)) {
              String newToken = this.makeToken(start, end, this.lowerCase);
              this.tokenList.add(newToken);
              // newToken is a char-string like "!"
              this.isCandidateAbrev = false;
              this.setCreateSentenceFlag(c);
              start++;
            } else {
              if (Character.isDigit(c)) {
                state = 2;
              } else {
                state = 1;
              }
            }
          }
          break;

        case 2: // state two: integer part of digit
          if ((c == '\0') || TOKEN_SEP_CHARS.contains(c)) {
            String newToken = this.makeToken(start, end, this.lowerCase);
            String cardinalString = convertToCardinal(newToken);
            this.tokenList.add(cardinalString);
            state = 0;
            start = (1 + end);
          } else {
            if (c == '.') {
              state = 4;
            } else if (c == ',') {
              state = 3;
            } else if (SPECIAL_CHARS.contains(c)) {
              String newToken = this.makeToken(start, end, this.lowerCase);
              String cardinalString = convertToCardinal(newToken);
              this.tokenList.add(cardinalString);
              this.tokenList.add(Character.toString(c));
              this.setCreateSentenceFlag(c);
              state = 0;
              start = (1 + end);
            } else if (Character.isDigit(c)) {
              // do nothing

            } else {
              state = 1;
            }

          }
          break;

        case 3: // state three: floating point designated by #\,

          if ((c == '\0') || TOKEN_SEP_CHARS.contains(c)) {
            String newToken = this.makeToken(start, (end - 1), this.lowerCase);
            String cardinalString = convertToCardinal(newToken);
            this.tokenList.add(cardinalString);
            this.tokenList.add(",");
            state = 0;
            start = (1 + end);
          } else {
            if (SPECIAL_CHARS.contains(c)) {
              String newToken = this.makeToken(start, (end - 1), this.lowerCase);
              String cardinalString = convertToCardinal(newToken);
              this.tokenList.add(cardinalString);
              this.tokenList.add(",");
              this.tokenList.add(Character.toString(c));
              state = 0;
              start = (1 + end);
            } else {
              if (Character.isDigit(c)) {
                // Why not state = 2 ?
                // state = 5;
                state = 2;
              } else {
                String newToken = this.makeToken(start, (end - 1), this.lowerCase);
                String cardinalString = convertToCardinal(newToken);
                this.tokenList.add(cardinalString);
                this.tokenList.add(",");
                state = 1;
                start = end;
              }
            }
          }
          break;

        case 4: // state four: floating point designated by #\.

          if ((c == '\0')) {
            String newToken = this.makeToken(start, end, this.lowerCase);
            String numberString = convertToCardinalAndOrdinal(newToken);
            this.tokenList.add(numberString);
            this.tokenList.add(".");
            this.createSentence = true;
            state = 0;
            start = (1 + end);
          } else {
            if (TOKEN_SEP_CHARS.contains(c)) {
              String newToken = this.makeToken(start, end, this.lowerCase);
              String numberString = convertToOrdinal(newToken);
              this.tokenList.add(numberString);
              state = 0;
              start = (1 + end);
            } else {
              if (SPECIAL_CHARS.contains(c)) {

                String newToken = this.makeToken(start, end, this.lowerCase);
                String numberString = convertToOrdinal(newToken);
                this.tokenList.add(numberString);
                this.tokenList.add(Character.toString(c));
                this.setCreateSentenceFlag(c);
                state = 0;
                start = (1 + end);
              } else {
                if (Character.isDigit(c)) {
                  // Why not state = 2 ?
                  // state = 5;
                  state = 2;
                } else {
                  String newToken = this.makeToken(start, end, this.lowerCase);
                  String numberString = convertToOrdinal(newToken);
                  this.tokenList.add(numberString);
                  state = 1;
                  start = end;
                }
              }
            }
          }
          break;

        case 5: // state five: digits
          if ((c == '\0') || TOKEN_SEP_CHARS.contains(c)) {
            String newToken = this.makeToken(start, end, this.lowerCase);
            String cardinalString = convertToCardinal(newToken);
            this.tokenList.add(cardinalString);
            state = 0;
            start = (1 + end);
          } else {
            if (SPECIAL_CHARS.contains(c)) {
              String newToken = this.makeToken(start, end, this.lowerCase);
              String cardinalString = convertToCardinal(newToken);
              this.tokenList.add(cardinalString);
              this.tokenList.add(Character.toString(c));
              this.setCreateSentenceFlag(c);
              state = 0;
              start = (1 + end);
            } else {
              if (!Character.isDigit(c)) {
                String newToken = this.makeToken(start, end, this.lowerCase);
                String cardinalString = convertToCardinal(newToken);
                this.tokenList.add(cardinalString);
                state = 1;
                start = end;
              }
            }
          }
          break;

        case 6: // state six: handle delimiters like #\-
          if ((c == '\0') || TOKEN_SEP_CHARS.contains(c)) {
            String newToken = this.makeToken(start, (end - delimCnt), this.lowerCase);
            this.tokenList.add(newToken);
            state = 0;
            delimCnt = 0;
            start = (1 + end);
          } else {
            if (DELIMITER_CHARS.contains(c)) {
              delimCnt++;
            } else {
              if (SPECIAL_CHARS.contains(c)) {
                String newToken = this.makeToken(start, (end - delimCnt), this.lowerCase);
                this.tokenList.add(newToken);
                this.tokenList.add(Character.toString(c));
                this.setCreateSentenceFlag(c);
                state = 0;
                delimCnt = 0;
                start = (1 + end);
              } else {
                if (Character.isDigit(c)) {
                  state = 0;
                } else {
                  String newToken = this.makeToken(start, (end - delimCnt), this.lowerCase);
                  this.tokenList.add(newToken);
                  state = 1;
                  delimCnt = 0;
                  start = end;
                }
              }
            }
          }
          break;
        default:
          logger.error("unknown state " + state + ", will be ignored");
      }
      end++;
    }

    return this.sentenceList;
  }


  public static String sentenceListToString(List<List<String>> sentenceListParam) {

    StringBuilder output = new StringBuilder();
    int id = 0;
    for (List<String> tokenListOfSentence : sentenceListParam) {
      if (!tokenListOfSentence.isEmpty()) {
        output.append(String.format("%d: %s%n", id, tokenListToString(tokenListOfSentence)));
        id++;
      }
    }
    return output.toString();
  }


  private static String tokenListToString(List<String> tokenListParam) {

    StringBuilder output = new StringBuilder();
    for (String token : tokenListParam) {
      output.append(token + " ");
    }
    return output.toString().trim();
  }
}
