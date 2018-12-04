package de.dfki.mlt.gnt.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostProcessor {

  // TODO use hashtables
  // cf. https://www.learnenglish.de/grammar/shortforms.html

  private static List<String> englishPositiveForms =
      Arrays.asList("i", "he", "she", "it", "we", "you", "what", "they", "them", "that", "there");

  private static List<String> englishNegativeForms =
      Arrays.asList("wasn", "doesn", "don", "weren", "didn", "hasn", "hadn", "can", "couldn",
          "mustn", "shan", "shouldn", "won", "wouldn", "isn", "haven");

  private static List<String> englishCliticSuffix =
      Arrays.asList("s", "d", "re", "m", "ve", "ll");

  private boolean postProcess = true;


  public boolean isPostProcess() {

    return this.postProcess;
  }


  public void setPostProcess(boolean postProcess) {

    this.postProcess = postProcess;
  }


  /**
   * A post processor, which additionally checks groups of tokens and eventually pust them together.
   * Currently, design for English, but should work for German as well.
   * tokens follow penn ike style since grammars are learned with such tokens
   */


  private int handleEnglishClitics(
      String token, List<String> sentence, List<String> newSentence,
      int tokenId, int sentenceLength) {

    if (tokenId > 0 && tokenId < sentenceLength) {
      String leftToken = sentence.get(tokenId - 1);
      String rightToken = sentence.get(tokenId + 1);

      //      System.out.println("In: " + sentence);
      //      System.out.println(tokenId + ": " + token);
      //      System.out.println(leftToken + " # " + rightToken);

      if (rightToken.equalsIgnoreCase("t")) {
        // handle n't
        if (leftToken.equalsIgnoreCase("n")) {
          // since "n" is already in newSentence we have to delete it.
          newSentence.remove((newSentence.size() - 1));
          newSentence.add("n't");
          tokenId++;
        } else if (englishNegativeForms.contains(leftToken.toLowerCase())) {
          // handle wouldn't etc.
          newSentence.remove((newSentence.size() - 1));
          newSentence.add(leftToken.substring(0, leftToken.length() - 1));
          newSentence.add("n't");
          tokenId++;
        }
      } else if (englishCliticSuffix.contains(rightToken.toLowerCase())) {
        // handle 's 'd 're etc.
        String suffix = "'" + rightToken;
        if (englishPositiveForms.contains(leftToken.toLowerCase())) {
          newSentence.add(suffix);
          // skip "s" in the input
          tokenId++;
        } else {
          // handle possessives; basically the them as above,
          // but I leave it here for logical reasons
          newSentence.add(suffix);
          tokenId++;
        }
      } else if (rightToken.equals("'")) {
        // closing parenthesis ',', -> '',
        newSentence.add("''");
        tokenId++;

      } else {
        // TODO plural possessives
        newSentence.add(token);
      }
    } else {
      newSentence.add(token);
    }
    return tokenId;
  }


  // opening parenthesis `, `, -> ``
  private int handleOpenPara(
      String token, List<String> sentence, List<String> newSentence,
      int tokenId, int sentenceLength) {

    if (tokenId > 0 && tokenId < sentenceLength) {
      String rightToken = sentence.get(tokenId + 1);

      if (rightToken.equals("`")) {
        newSentence.add("``");
        tokenId++;
      } else {
        newSentence.add(token);
      }
    } else {
      newSentence.add(token);
    }
    return tokenId;
  }


  private List<Integer> handleCandidateAbreviation(
      String token, List<String> sentence, List<String> newSentence,
      int tokenId, int sentenceLength) {

    List<Integer> result = new ArrayList<Integer>();
    if (tokenId > 0 && tokenId < sentenceLength) {
      String leftToken = sentence.get(tokenId - 1);
      String rightToken = sentence.get(tokenId + 1);

      //      System.out.println("In: " + sentence);
      //      System.out.println(tokenId + ": " + token);
      //      System.out.println(leftToken + " # " + rightToken);

      // Cases like M . T , but not M . ? or M . string
      if (Character.isAlphabetic(leftToken.charAt(leftToken.length() - 1))
          &&
          (rightToken.length() == 1)
          &&
          Character.isAlphabetic(rightToken.charAt(0))) {
        String newToken = newSentence.get(newSentence.size() - 1) + "." + rightToken;
        newSentence.remove((newSentence.size() - 1));
        newSentence.add(newToken);
        tokenId = tokenId + 1;
      } else if (Character.isAlphabetic(leftToken.charAt(leftToken.length() - 1))
          &&
          (rightToken.length() >= 1)) {
        // Cases like A . string
        String newToken = newSentence.get(newSentence.size() - 1) + ".";
        newSentence.remove((newSentence.size() - 1));
        newSentence.add(newToken);
      } else {
        newSentence.add(token);
      }

    } else {
      newSentence.add(token);
    }
    result.add(tokenId);
    result.add(sentenceLength);
    //System.out.println("New: " + newSentence);
    return result;
  }


  public List<String> postProcessTokenList(List<String> sentence) {

    List<String> newSentence = new ArrayList<String>();
    if (this.postProcess) {
      int tokenId = -1;
      int sentenceLength = sentence.size() - 1;
      while (tokenId < sentenceLength) {
        tokenId++;
        String token = sentence.get(tokenId);

        // Handle English clitics
        if (token.equals("'")) {
          tokenId = this.handleEnglishClitics(
              token, sentence, newSentence, tokenId, sentenceLength);
        } else if (token.equals(".")) {
          // Handle non-eof dot sign
          List<Integer> result = this.handleCandidateAbreviation(
              token, sentence, newSentence, tokenId, sentenceLength);
          tokenId = result.get(0);
          sentenceLength = result.get(1);
        } else if (token.equals("`")) {
          tokenId = this.handleOpenPara(token, sentence, newSentence, tokenId, sentenceLength);
        } else if ((token.length() > 1) && ((token.charAt(0) == '$') || (token.charAt(0) == '€'))
            && Character.isDigit(token.charAt(1))) {
          // if token is of form $dY or €dY split into $ dY
          // TODO handle also US $ 10 -> US$ 10
          newSentence.add(token.substring(0, 1));
          newSentence.add(token.substring(1));
          tokenId = tokenId - 1;
        } else {
          newSentence.add(token);
        }
      }
    } else {
      newSentence = sentence;
    }
    return newSentence;
  }
}
