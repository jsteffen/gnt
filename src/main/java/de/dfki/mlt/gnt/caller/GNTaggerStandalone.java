package de.dfki.mlt.gnt.caller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.dfki.mlt.gnt.tagger.GNTagger;
import de.dfki.mlt.gnt.tokenizer.GntSimpleTokenizer;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class GNTaggerStandalone {

  private GNTagger posTagger = null;


  public GNTagger getPosTagger() {

    return this.posTagger;
  }


  public void initRunner(String archiveName) throws IOException {

    this.posTagger = new GNTagger(archiveName);
    this.posTagger.initGNTagger(
        this.posTagger.getDataProps().getGlobalParams().getWindowSize(),
        this.posTagger.getDataProps().getGlobalParams().getDim());
  }


  /**
   * Receives a string and calls GNT tagger. Then splits resulting tagged strings into a line-oriented format.
   * @param inputString
   */
  public void tagStringRunner(String inputString) {

    List<String> tokens = GntSimpleTokenizer.tokenize(inputString);

    this.posTagger.tagUnlabeledTokens(tokens);

    String taggedString = this.posTagger.taggedSentenceToString();

    for (String token : taggedString.split(" ")) {
      System.out.println(token);
    }
  }


  /**
   * Receives the name of a file, reads it line-wise, calls GNT tagger on each line, and
   * saves resulting tagged string in output file. Output file is build from sourceFilename by
   * adding suffix .GNT
   * @param sourceFileName
   * @param inEncode
   * @param outEncode
   * @throws IOException
   */
  public void tagFileRunner(Path sourcePath, String inEncode, String outEncode) throws IOException {

    Path resultPath = Paths.get(sourcePath.toString() + ".GNT");
    try (BufferedReader in = Files.newBufferedReader(
            sourcePath, Charset.forName(inEncode));
        PrintWriter out = new PrintWriter(Files.newBufferedWriter(
            resultPath, Charset.forName(outEncode)))) {

      String line;
      while ((line = in.readLine()) != null) {
        if (!line.isEmpty()) {
          List<String> tokens = GntSimpleTokenizer.tokenize(line);
          this.posTagger.tagUnlabeledTokens(tokens);
          String taggedString = this.posTagger.taggedSentenceToString();
          for (String token : taggedString.split(" ")) {
            out.println(token);
          }
        }
      }
    }
  }
}
