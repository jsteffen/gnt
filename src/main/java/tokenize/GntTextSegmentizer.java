package tokenize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A reimplementation of our morphix-reader which is a very simple but effective and fast automaton for mapping a string into a list of tokens.
 * It handles cardinals and ordinals and special chars between words.
 * Punctuation are also isolated as single strings so can be used to split the token list into a list of sentences.
 * Output tokens can be lowCased or not.
 * Currently, developed for EN/DE like languages.
 * @author gune00
 *
 */
public class GntTextSegmentizer {

  // the last one should be #\^D, the Fill Down character
  private List<Character> specialChars = 
      Arrays.asList('.', ',', ';', '!', '?', ':', '(', ')', '{', '}', '[', ']', '$', '€', '\'', '\b'); 

  private List<Character> eosChars = 
      Arrays.asList('.', '!', '?'); 

  private List<Character> delimiterChars = 
      Arrays.asList('-', '_');

  private List<Character> tokenSepChars = 
      Arrays.asList(' ', '\n', '\t');

  private boolean splitString = false;
  private boolean lowerCase = false;
  private boolean createSentence = false;
  private boolean isCandidateAbrev = false;

  private String inputString = "";
  private List<String> tokenList = new ArrayList<String>();
  private List<List<String>> sentenceList = new ArrayList<List<String>>();

  // getters and setters
  public List<Character> getSpecialChars() {
    return specialChars;
  }
  public void setSpecialChars(List<Character> specialChars) {
    this.specialChars = specialChars;
  }
  public List<Character> getEosChars() {
    return eosChars;
  }
  public void setEosChars(List<Character> eosChars) {
    this.eosChars = eosChars;
  }
  public List<Character> getDelimiterChars() {
    return delimiterChars;
  }
  public void setDelimiterChars(List<Character> delimiterChars) {
    this.delimiterChars = delimiterChars;
  }
  public List<Character> getTokenSepChars() {
    return tokenSepChars;
  }
  public void setTokenSepChars(List<Character> tokenSepChars) {
    this.tokenSepChars = tokenSepChars;
  }
  public boolean isSplitString() {
    return splitString;
  }
  public void setSplitString(boolean splitString) {
    this.splitString = splitString;
  }
  public boolean isLowerCase() {
    return lowerCase;
  }
  public void setLowerCase(boolean lowerCase) {
    this.lowerCase = lowerCase;
  }
  public boolean isCreateSentence() {
    return createSentence;
  }
  public void setCreateSentence(boolean createSentence) {
    this.createSentence = createSentence;
  }
  public boolean isCandidateAbrev() {
    return isCandidateAbrev;
  }
  public void setCandidateAbrev(boolean isCandidateAbrev) {
    this.isCandidateAbrev = isCandidateAbrev;
  }
  public String getInputString() {
    return inputString;
  }
  public void setInputString(String inputString) {
    this.inputString = inputString;
  }
  public List<String> getTokenList() {
    return tokenList;
  }
  public void setTokenList(List<String> tokenList) {
    this.tokenList = tokenList;
  }
  public List<List<String>> getSentenceList() {
    return sentenceList;
  }
  public void setSentenceList(List<List<String>> sentenceList) {
    this.sentenceList = sentenceList;
  }

  // Init classes
  public GntTextSegmentizer(){
  }

  public GntTextSegmentizer(boolean lowerCase, boolean splitString){
    this.lowerCase = lowerCase;
    this.splitString = splitString;
  }

  /*
   * The idea is to define two points s and e which define a possible span over the input string vector.
   * Depending on the type of char, a substring is extracted using current span information and a token is created.
   * By making a new string form the substring and eventually lower-case the char or not.
   * Thus the input string should be processed as a global variable
   */

  private String makeToken(int start, int end, boolean lowerCase){
    int sl = Math.max(1, (end - start));
    char c = '\0';
    StringBuilder newToken = new StringBuilder(sl);

    for (int i = 0; i < sl; i++){
      c = (lowerCase)?
          (Character.toLowerCase(this.inputString.charAt(i+start))):
            this.inputString.charAt(i+start);
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
    //    String cardinalString = newToken.substring(0, (newToken.length() - 1));
    //    String ordinalString = newToken;
    //    String outputString = cardinalString+"card:or:ord:"+ordinalString;
    String outputString = newToken ; //+":CARDORD";
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
  private void setCandidateAbrev(String token){
    //System.err.println("Abrev? " + token);
    if ((token.length() <= 3)
        ){
      this.isCandidateAbrev = true;
    }
    else
      this.isCandidateAbrev = false;
    //System.err.println("this.isCandidateAbrev=" + this.isCandidateAbrev);
  }

  private void setCreateSentenceFlag(char c){
    //    System.err.println("Create sent: " + c);
    if (this.eosChars.contains(c) &&
        !this.isCandidateAbrev)
      this.createSentence = true;
    //    System.err.println("this.createSentence=" + this.createSentence);
  }
  private void extendSentenceList(){
    // make a sentence
    this.sentenceList.add(this.tokenList);
    // reset sensible class parameters
    this.createSentence = false;
    this.tokenList = new ArrayList<String>();

  }
  /*
   * This will be a loop which is terminated inside;
   */

  /*
   * NOTE:
   * in state 2, the FST jumbs to state 3 and 4
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
  public void scanText (String inputString){
    // Initialization
    this.inputString = inputString;
    int il = this.inputString.length();
    int state = 0;
    int start = 0;
    int delimCnt = 0;
    int end = 0;
    char c = '\0'; // used as dummy instead of nil or null

    // System.err.println("Input (#" + il + "): " + inputString);

    while(true){
      //System.err.println("Start: " + start + " end: " + end + " State " + state +  " c: " + c);

      if (end > il) {
        if (this.createSentence) {
          this.extendSentenceList();
        }
        break;
      }

      if (end == il) {
        c  = '\0';
      }
      else {
        c = this.inputString.charAt(end);
      }

      if (this.createSentence) {
        this.extendSentenceList();
      }


      switch (state) {
      // state actions

      case 1: // 1 is the character state, so most likely
        if ((c == '\0') || this.tokenSepChars.contains(c)) {
          String newToken = this.makeToken(start, end, lowerCase);
          this.tokenList.add(newToken);
          state = 0; start = (1+ end);
        }
        else {
          if (this.splitString && this.delimiterChars.contains(c)){
            state = 6; delimCnt++;
          }
          else {
            if (this.specialChars.contains(c)) {
              String newToken = this.makeToken(start, end, lowerCase);
              this.tokenList.add(newToken);
              this.setCandidateAbrev(newToken);
              this.tokenList.add(Character.toString(c));
              this.setCreateSentenceFlag(c);
              state = 0; start = (1+ end);
            }
          }
        }
        break;

      case 0: // state zero covers: space, tab, specials
        if (this.tokenSepChars.contains(c)) {
          start++;
        }
        else
          if ((c == '\0')) {
            this.createSentence=true;
          }
          else {
            if (this.specialChars.contains(c)){
              String newToken = this.makeToken(start, end, lowerCase);
              this.tokenList.add(newToken);
              // newToken is a char-string like "!"
              this.isCandidateAbrev = false;
              this.setCreateSentenceFlag(c);
              start++;
            }
            else {
              if (Character.isDigit(c)) {
                state = 2;
              }
              else {
                state = 1;
              }
            }
          }
        break;

      case 2: // state two: integer part of digit
        if ((c == '\0') || this.tokenSepChars.contains(c)){
          String newToken = this.makeToken(start, end, lowerCase);
          String cardinalString = convertToCardinal(newToken);
          this.tokenList.add(cardinalString);
          state = 0; start = (1+ end);  
        }
        else{
          if (c == '.') {
            state = 4;
          }
          else
            if (c == ',') {
              state = 3;
            }
            else
              if (this.specialChars.contains(c)) {
                String newToken = this.makeToken(start, end, lowerCase);
                String cardinalString = convertToCardinal(newToken);
                this.tokenList.add(cardinalString);
                this.tokenList.add(Character.toString(c));
                this.setCreateSentenceFlag(c);
                state = 0; start = (1+ end);
              } 
              else
                if (Character.isDigit(c)) {
                }
                else
                {
                  state = 1;
                }

        }
        break;

      case 3: // state three: floating point designated by #\,
        if ((c == '\0') || this.tokenSepChars.contains(c)){
          String newToken = this.makeToken(start, (1- end), lowerCase);
          String cardinalString = convertToCardinal(newToken);
          this.tokenList.add(cardinalString);
          this.tokenList.add(",");
          state = 0; start = (1+ end);  
        }
        else {
          if (this.specialChars.contains(c)) {
            String newToken = this.makeToken(start, (1- end), lowerCase);
            String cardinalString = convertToCardinal(newToken);
            this.tokenList.add(cardinalString);
            this.tokenList.add(",");
            this.tokenList.add(Character.toString(c));
            state = 0; start = (1+ end);
          } 
          else {
            if (Character.isDigit(c)) {
              // Why not state = 2 ?
              // state = 5;
              state = 2;
            }
            else 
            {
              String newToken = this.makeToken(start, (1- end), lowerCase);
              String cardinalString = convertToCardinal(newToken);
              this.tokenList.add(cardinalString);
              this.tokenList.add(",");
              state = 1; start = end;
            }
          }
        }
        break;

      case 4: // state four: floating point designated by #\.

        if ((c == '\0')){
          String newToken = this.makeToken(start, end, lowerCase);
          String numberString = convertToCardinalAndOrdinal(newToken);
          this.tokenList.add(numberString);
          this.tokenList.add(".");
          this.createSentence = true;
          state = 0; start = (1+ end);
        }
        else {
          if (this.tokenSepChars.contains(c)){
            String newToken = this.makeToken(start, end, lowerCase);
            String numberString = convertToOrdinal(newToken);
            this.tokenList.add(numberString);
            state = 0; start = (1+ end);  
          }
          else {
            if (this.specialChars.contains(c)) {

              String newToken = this.makeToken(start, end, lowerCase);
              String numberString = convertToOrdinal(newToken);
              this.tokenList.add(numberString);
              this.tokenList.add(Character.toString(c));
              this.setCreateSentenceFlag(c);
              state = 0; start = (1+ end);
            }
            else {
              if (Character.isDigit(c)) {
                // Why not state = 2 ?
                // state = 5;
                state = 2;
              }
              else {
                String newToken = this.makeToken(start, end, lowerCase);
                String numberString = convertToOrdinal(newToken);
                this.tokenList.add(numberString);
                state = 1; start = end;
              }
            }
          }
        }
        break;

      case 5: // state five: digits
        if ((c == '\0') || this.tokenSepChars.contains(c)){
          String newToken = this.makeToken(start, end, lowerCase);
          String cardinalString = convertToCardinal(newToken);
          this.tokenList.add(cardinalString);
          state = 0; start = (1+ end);  
        }
        else {
          if (this.specialChars.contains(c)) {
            String newToken = this.makeToken(start, end, lowerCase);
            String cardinalString = convertToCardinal(newToken);
            this.tokenList.add(cardinalString);
            this.tokenList.add(Character.toString(c));
            this.setCreateSentenceFlag(c);
            state = 0; start = (1+ end);
          } 
          else {
            if (Character.isDigit(c)) {
            }
            else {
              String newToken = this.makeToken(start, end, lowerCase);
              String cardinalString = convertToCardinal(newToken);
              this.tokenList.add(cardinalString);
              state = 1; start = end;
            }  
          }
        }
        break;

      case 6: // state six: handle delimiters like #\-
        if ((c == '\0') || this.tokenSepChars.contains(c)){
          String newToken = this.makeToken(start, (end - delimCnt), lowerCase);
          this.tokenList.add(newToken);
          state = 0; delimCnt = 0; start = (1+ end);  
        }
        else {
          if (this.delimiterChars.contains(c)){
            delimCnt++;
          }
          else {
            if (this.specialChars.contains(c)) {
              String newToken = this.makeToken(start, (end - delimCnt), lowerCase);
              this.tokenList.add(newToken);
              this.tokenList.add(Character.toString(c));
              this.setCreateSentenceFlag(c);
              state = 0; delimCnt = 0; start = (1+ end);
            }
            else {
              if (Character.isDigit(c)) {
                state = 0;
              }
              else {
                String newToken = this.makeToken(start, (end - delimCnt), lowerCase);
                this.tokenList.add(newToken);
                state = 1; delimCnt = 0; start = end;
              }
            }
          }
        }
        break;
      }
      end++;
    }
  }

  public String tokenListToString(List<String> tokenList){
    String outputString = "";
    for (String token : tokenList){
      outputString += token + " " ;
    }
    return outputString;
  }

  public String sentenceListToString(){
    String outputString = "";
    int id = 0;
    for (List<String> tokenList : this.sentenceList){
      if (!tokenList.isEmpty()){
        outputString += id + ": " + this.tokenListToString(tokenList) + "\n";
        id++;
      }
    }
    return outputString;
  }

  public void reset (){
    tokenList = new ArrayList<String>();
    sentenceList = new ArrayList<List<String>>();
  }
}
