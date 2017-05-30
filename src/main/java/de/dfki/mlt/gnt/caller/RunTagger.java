package de.dfki.mlt.gnt.caller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.corpus.ConllEvaluator;
import de.dfki.mlt.gnt.corpus.GNTcorpusProperties;
import de.dfki.mlt.gnt.data.Data;
import de.dfki.mlt.gnt.tagger.GNTagger;

/**
 * A test method for running GNT on same data set as FLORS and computing accuracies.
 * Actually, the first file is run in order to initialize compilation of all java objects.
 *
 * @author Günter Neumann, DFKI
 */
public final class RunTagger {

  private RunTagger() {

    // private constructor to enforce noninstantiability
  }


  public static void runner(String archiveName, String corpusConfigFileName) throws IOException {

    GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
    GNTagger posTagger = new GNTagger(archiveName, corpusProps);
    posTagger.initGNTagger(
        posTagger.getDataProps().getGlobalParams().getWindowSize(),
        posTagger.getDataProps().getGlobalParams().getDim());

    Data data = new Data();
    data.readWordSet(posTagger.getArchivator());
    System.out.println("\n++++\nLoad known vocabulary from archive training for evaluating OOV: "
        + data.getWordMapPath());
    System.out.println(data.toString());
    ConllEvaluator evaluator = new ConllEvaluator(data);

    for (String fileName : posTagger.getCorpus().getDevLabeledData()) {
      Path evalPath = posTagger.tagAndWriteFromConllDevelFile(fileName, -1);
      evaluator.computeAccuracy(evalPath, GlobalConfig.getBoolean(ConfigKeys.DEBUG));
    }
    for (String fileName : posTagger.getCorpus().getTestLabeledData()) {
      Path evalPath = posTagger.tagAndWriteFromConllDevelFile(fileName, -1);
      evaluator.computeAccuracy(evalPath, GlobalConfig.getBoolean(ConfigKeys.DEBUG));
    }
    posTagger.getArchivator().close();
  }


  public static void folderRunner(String archiveName, String inputDir, String inEncode, String outEncode)
      throws IOException {

    GNTaggerStandalone runner = new GNTaggerStandalone();
    runner.initRunner(archiveName);

    long time1;
    long time2;

    Path dir = Paths.get(inputDir);
    try (DirectoryStream<Path> stream =
        Files.newDirectoryStream(dir, "*.{txt}")) {
      for (Path entry : stream) {
        time1 = System.currentTimeMillis();

        System.out.println("Tagging file ... " + entry.toString());

        runner.tagFileRunner(entry, inEncode, outEncode);

        time2 = System.currentTimeMillis();
        System.out.println("System time (msec): " + (time2 - time1));
      }
    } catch (IOException e) {
      // IOException can never be thrown by the iteration.
      // In this snippet, it can only be thrown by newDirectoryStream.
      e.printStackTrace();
    }

    runner.getPosTagger().getArchivator().close();
  }
}
