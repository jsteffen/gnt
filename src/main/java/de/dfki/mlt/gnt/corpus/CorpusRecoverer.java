package de.dfki.mlt.gnt.corpus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.CorpusConfig;

/**
 * A class for recovering corpus files which have been nomrlaized by class CorpusNormalizer.
 * That class saves the orig files to files with extension ".orig" before normalization is done.
 * The task of the CorpusRecoverer is to restore the original .conll and -sents.txt files and
 * deleting the .orig files afterwards.
 *
 * @author Günter Neumann, DFKI
 */
public final class CorpusRecoverer {

  private CorpusRecoverer() {

    // private constructor to enforce noninstantiability
  }


  private static void recoverCopyConllFile(String fileNameOrig) throws IOException {

    File fileOrig = new File(fileNameOrig);

    String fileNameCopy =
        (String)fileNameOrig.subSequence(0, (fileNameOrig.length() - ".orig".length()));
    File fileCopyConll = new File(fileNameCopy);

    if (!fileOrig.exists()) {
      System.out.println(fileOrig.toString() + " already recovered!");
    } else {
      Files.copy(fileOrig.toPath(), fileCopyConll.toPath(), StandardCopyOption.REPLACE_EXISTING);
      System.out.println(fileCopyConll.toString() + " recovered!");
    }
    if (!fileOrig.exists()) {
      System.out.println(fileOrig.toString() + " already recovered!");
    } else {
      Files.deleteIfExists(fileOrig.toPath());
      System.out.println(fileOrig.toString() + " deleted!");
    }
  }


  public static void recoverCopiedFilesFromCorpus(CorpusConfig corpusConfig)
      throws IOException {

    // Labeled data
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.TRAINING_LABELED_DATA, Collections.emptyList())) {
      String fileNameComplete = fileName + ".orig";
      recoverCopyConllFile(fileNameComplete);
    }
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.DEV_LABELED_DATA, Collections.emptyList())) {
      String fileNameComplete = fileName + ".orig";
      recoverCopyConllFile(fileNameComplete);
    }
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.TEST_LABELED_DATA, Collections.emptyList())) {
      String fileNameComplete = fileName + ".orig";
      recoverCopyConllFile(fileNameComplete);
    }

    // Unlabeled data
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.TRAINING_UNLABELED_DATA, Collections.emptyList())) {
      String fileNameComplete = fileName + ".orig";
      recoverCopyConllFile(fileNameComplete);
    }
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.DEV_UNLABELED_DATA, Collections.emptyList())) {
      String fileNameComplete = fileName + ".orig";
      recoverCopyConllFile(fileNameComplete);
    }
    for (String fileName : corpusConfig.getList(
        String.class, ConfigKeys.TEST_UNLABELED_DATA, Collections.emptyList())) {
      String fileNameComplete = fileName + ".orig";
      recoverCopyConllFile(fileNameComplete);
    }
  }
}
