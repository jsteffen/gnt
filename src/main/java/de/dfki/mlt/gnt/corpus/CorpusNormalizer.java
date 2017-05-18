package de.dfki.mlt.gnt.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

/**
 * <pre>
 * {@code
 * The idea of this class is to process all files from Corpus and normalize the content.
 * Currently normalization will be restricted to numbers:
 * - map each digit to 0
 * - everything else remains as it is.
 *
 * I will also only consider labeled conll files (train/devel/test) and the unlabeled data
 *
 * Define:
 *
 * - read labeled data
 * - parse CONLL token line to get token, normalize it and return conll
 * - write labeled data
 *
 * - read unlabeled data
 * - iterate  string of sentence
 * - normalize it and return sentence
 * - write unlabeled data
 * - read
 * }
 * </pre>
 *
 * @author Günter Neumann, DFKI
 */
public class CorpusNormalizer {

  private Corpus corpus = null;


  public Corpus getCorpus() {

    return this.corpus;
  }


  public void setCorpus(Corpus corpus) {

    this.corpus = corpus;
  }


  // Code for normalization of all corpus files

  /**
   * Copy the file named fileNameOrig to fileNameOrig.orig if it does not exist.
   * It is assumed that fileNameOrig is complete name.
   * @param fileNameOrig
   * @throws IOException
   */
  private static String copyConllFile(String fileNameOrig) throws IOException {

    File fileOrig = new File(fileNameOrig);
    File fileCopy = new File(fileNameOrig + ".orig");
    if (fileCopy.exists()) {
      System.out.println(fileCopy.toString() + " already copied!");
    } else {
      Files.copy(fileOrig.toPath(), fileCopy.toPath());
      System.out.println(fileCopy.toString() + " copied!");
    }
    return fileCopy.toString();
  }


  private static String normalizeDigits(String token) {

    String normalizedDigitString = token.replaceAll("\\d", "0");
    return normalizedDigitString;
  }


  private static String makeConllNormalizedTokenString(String word,
      String[] tokenizedLine) {

    String newConllTokenString = tokenizedLine[0] + "\t" + word;
    for (int i = 2; i < tokenizedLine.length; i++) {
      newConllTokenString += "\t" + tokenizedLine[i];
    }
    return newConllTokenString;
  }


  private static void normalizeConllLabeledFile(String sourceFileName, String targetFileName)
      throws IOException {

    String sourceEncoding = "utf-8";
    String targetEncoding = "utf-8";
    // init reader for CONLL style file
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(sourceFileName),
            sourceEncoding));

    // init writer for line-wise file
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(targetFileName),
            targetEncoding));

    String line = "";
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        writer.newLine();
      } else {
        // Extract the word from each CONLL token line
        String[] tokenizedLine = line.split("\t");
        String wordNormalized = CorpusNormalizer.normalizeDigits(tokenizedLine[1]);
        writer.write(makeConllNormalizedTokenString(wordNormalized, tokenizedLine));
        writer.newLine();
      }
    }
    reader.close();
    writer.close();
  }


  public static void normalizeUnLabeledFile(String sourceFileName, String targetFileName)
      throws IOException {

    String sourceEncoding = "utf-8";
    String targetEncoding = "utf-8";
    // init reader for CONLL style file
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(sourceFileName),
            sourceEncoding));

    // init writer for line-wise file
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(targetFileName),
            targetEncoding));

    String line = "";
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        writer.newLine();
      } else {
        // Normalize line which is assumed to correspond to a sentence.
        writer.write(CorpusNormalizer.normalizeDigits(line));
        writer.newLine();
      }
    }
    reader.close();
    writer.close();
  }


  private void normalizeLabeledFilesFromCorpus() throws IOException {

    for (String fileName : this.getCorpus().getTrainingLabeledData()) {
      String fileNameComplete = fileName + ".conll";
      System.out.println("Copy and normalize: " + fileNameComplete);
      String fileNameCopyName = CorpusNormalizer.copyConllFile(fileNameComplete);
      CorpusNormalizer.normalizeConllLabeledFile(fileNameCopyName, fileNameComplete);
    }
    for (String fileName : this.getCorpus().getDevLabeledData()) {
      String fileNameComplete = fileName + ".conll";
      System.out.println("Copy and normalize: " + fileNameComplete);
      String fileNameCopyName = CorpusNormalizer.copyConllFile(fileNameComplete);
      CorpusNormalizer.normalizeConllLabeledFile(fileNameCopyName, fileNameComplete);
    }
    for (String fileName : this.getCorpus().getTestLabeledData()) {
      String fileNameComplete = fileName + ".conll";
      System.out.println("Copy and normalize: " + fileNameComplete);
      String fileNameCopyName = CorpusNormalizer.copyConllFile(fileNameComplete);
      CorpusNormalizer.normalizeConllLabeledFile(fileNameCopyName, fileNameComplete);
    }
  }


  private void normalizeUnLabeledFilesFromCorpus() throws IOException {

    for (String fileNameComplete : this.getCorpus().getTrainingUnLabeledData()) {
      System.out.println("Copy and normalize: " + fileNameComplete);
      String fileNameCopyName = CorpusNormalizer.copyConllFile(fileNameComplete);
      CorpusNormalizer.normalizeUnLabeledFile(fileNameCopyName, fileNameComplete);
    }
    for (String fileNameComplete : this.getCorpus().getDevUnLabeledData()) {
      System.out.println("Copy and normalize: " + fileNameComplete);
      String fileNameCopyName = CorpusNormalizer.copyConllFile(fileNameComplete);
      CorpusNormalizer.normalizeUnLabeledFile(fileNameCopyName, fileNameComplete);
    }
    for (String fileNameComplete : this.getCorpus().getTestUnLabeledData()) {
      System.out.println("Copy and normalize: " + fileNameComplete);
      String fileNameCopyName = CorpusNormalizer.copyConllFile(fileNameComplete);
      CorpusNormalizer.normalizeUnLabeledFile(fileNameCopyName, fileNameComplete);
    }
  }


  public void normalizeCorpus() throws IOException {

    this.normalizeLabeledFilesFromCorpus();
    this.normalizeUnLabeledFilesFromCorpus();
  }
}
