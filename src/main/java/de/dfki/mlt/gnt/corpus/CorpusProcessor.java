package de.dfki.mlt.gnt.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods to prepare a corpus for model training.
 *
 * @author Günter Neumann, DFKI
 * @author Jörg Steffen, DFKI
 */
public final class CorpusProcessor {

  private static final Logger logger = LoggerFactory.getLogger(CorpusProcessor.class);


  private CorpusProcessor() {

    // private constructor to enforce noninstantiability
  }


  /**
   * Prepares the corpus specified in the given corpus config for mode training.
   *
   * @param corpusProps
   *          the corpus config
   */
  public static void prepreCorpus(GNTcorpusProperties corpusProps) {

    // transcode NER source files into proper CoNLL format
    transcodeNerSourceFilesToConllFiles(corpusProps);
    // transcode CoNLL files into plain text sentence files
    transcodeConllFilesToSentenceFiles(corpusProps);
  }


  /**
   * Transcodes NER source files to CoNLL files; currently, assuming CoNLL 2003 format.
   *
   * @param corpusProps
   *          the corpus config
   */
  private static void transcodeNerSourceFilesToConllFiles(GNTcorpusProperties corpusProps) {

    // collect all NER source files to transcode
    String[] propKeys = new String[] {
        "trainingLabeledSourceFiles", "devLabeledSourceFiles", "testLabeledSourceFiles" };
    List<String> nerSourceFilesToTranscode = new ArrayList<>();
    for (String oneKey : propKeys) {
      if (corpusProps.containsKey(oneKey)) {
        String[] nerSourceFiles = corpusProps.getProperty(oneKey).split(",");
        nerSourceFilesToTranscode.addAll(Arrays.asList(nerSourceFiles));
      }
    }

    if (nerSourceFilesToTranscode.isEmpty()) {
      return;
    }

    logger.info("transcoding NER source files to CoNLL files...");

    // language is required to distinguish between different NER source file formats
    String lang = "EN";
    if (corpusProps.getProperty("taggerName").equals("DENER")) {
      lang = "DE";
    }

    // transcode NER source files
    for (String fileName : nerSourceFilesToTranscode) {
      try {
        logger.info(fileName.trim());
        transcodeNerSourceFileToConllFile(fileName.trim(), "ISO-8859-1", "utf-8", lang);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * Reads in a file in NER source format and converts it into CoNLL format.
   *
   * @param nerSourceFileName
   * @param sourceEncoding
   * @param targetEncoding
   * @param lang
   * @throws IOException
   */
  private static void transcodeNerSourceFileToConllFile(
      String nerSourceFileName, String sourceEncoding, String targetEncoding, String lang)
      throws IOException {

    // init reader for CONLL style file
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(nerSourceFileName),
            sourceEncoding));

    // init writer for line-wise file
    String conllFileName = nerSourceFileName.substring(0, nerSourceFileName.lastIndexOf(".")) + ".conll";
    PrintWriter writer = new PrintWriter(
        new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(conllFileName),
                targetEncoding)));

    String line = "";
    int tokenCnt = 0;
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        tokenCnt = 0;
        writer.println();
      } else {
        //if (!line.equals("-DOCSTART- -X- O O"))
        tokenCnt++;
        String[] tokenizedLine = line.split(" ");
        writer.println(nerTokenToString(tokenizedLine, tokenCnt, lang));
      }
    }

    reader.close();
    writer.close();
  }


  private static String nerTokenToString(String[] tokenizedLine, int index, String lang) {

    StringBuilder output = new StringBuilder();
    if (lang.equals("EN")) {
      // EN
      // West NNP I-NP I-MISC
      // index West NNP I-NP I-MISC
      output.append(index + "\t")
          .append(tokenizedLine[0] + "\t")
          .append(tokenizedLine[1] + "\t")
          .append(tokenizedLine[2] + "\t")
          .append(tokenizedLine[3]);
    } else if (lang.equals("DE")) {
      // DE
      // Nordendler <unknown> NN I-NC I-ORG
      // index Nordendler NN I-NC I-ORG
      output.append(index + "\t")
          .append(tokenizedLine[0] + "\t")
          .append(tokenizedLine[2] + "\t")
          .append(tokenizedLine[3] + "\t")
          .append(tokenizedLine[4]);
    }
    return output.toString();
  }


  /**
   * Transcodes NER source files to CoNLL files.
   *
   * @param corpusProps
   *          the corpus config
   */
  private static void transcodeConllFilesToSentenceFiles(GNTcorpusProperties corpusProps) {

    // collect all CoNLL files to transcode
    String[] propKeys = new String[] {
        "trainingLabeledData", "devLabeledData", "testLabeledData" };
    List<String> conllFilesToTranscode = new ArrayList<>();
    for (String oneKey : propKeys) {
      if (corpusProps.containsKey(oneKey)) {
        String[] conllFiles = corpusProps.getProperty(oneKey).split(",");
        conllFilesToTranscode.addAll(Arrays.asList(conllFiles));
      }
    }

    if (conllFilesToTranscode.isEmpty()) {
      return;
    }

    logger.info("transcoding CoNLL files to sentence files...");

    for (String fileName : conllFilesToTranscode) {
      try {
        logger.info(fileName.trim());
        // transcode ALL files -> -1 as last parameter
        transcodeConllFileToSentenceFile(fileName.trim(), "utf-8", "utf-8", -1);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * <pre>
   * {@code
   * Reads in a file of sentences in CoNLL format and writes out each sentence lines-wise in a output file.
   * CoNLL format:
   * - each word a line, sentence ends with newline
   * - word is at second position:
   * 1       The     _       DT      DT      _       2       NMOD
   * }
   * </pre>
   *
   * @param conllSourceFileName
   * @param sourceEncoding
   * @param targetEncoding
   * @param maxSent
   *          max number of sentences to process; use -1 to process all sentences
   * @throws IOException
   */
  private static void transcodeConllFileToSentenceFile(
      String conllSourceFileName, String sourceEncoding, String targetEncoding, int maxSent)
      throws IOException {

    // init reader for CoNLL style file
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(conllSourceFileName),
            sourceEncoding));

    // init writer for line-wise sentences file
    String sentenceFileName =
        conllSourceFileName.substring(0, conllSourceFileName.lastIndexOf(".")) + "-sents.txt";
    PrintWriter writer = new PrintWriter(
        new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(sentenceFileName),
                targetEncoding)));

    String line = "";
    int sentCnt = 0;
    List<String> tokens = new ArrayList<String>();
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        // if we read a newline it means we know we have just extracted the words
        // of a sentence, so write them to file
        if (!tokens.isEmpty()) {
          writer.println(sentenceToString(tokens));
          tokens = new ArrayList<String>();
          // increase sentence counter
          sentCnt++;
          // stop if maxSent sentences has been processed;
          // if maxSent is < 0 this means: read until end of file
          if ((maxSent > 0) && (sentCnt >= maxSent)) {
            break;
          }
        }
      } else {
        // extract the word from each CoNLL token line
        String[] tokenizedLine = line.split("\t");
        tokens.add(tokenizedLine[1]);
      }
    }
    reader.close();
    writer.close();
  }


  private static String sentenceToString(List<String> tokens) {

    StringBuilder sentence = new StringBuilder();
    for (int i = 0; i < tokens.size(); i++) {
      sentence.append(tokens.get(i) + " ");
    }
    return sentence.toString().trim();
  }
}
