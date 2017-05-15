package caller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import tagger.GNTagger;
import corpus.EvalConllFile;
import corpus.GNTcorpusProperties;

/**
 * A test method for running GNT on same data set as FLORS and computing accuracies.
 * Actually, the first file is run in order to initialize compilation of all java objects.
 * @author gune00
 *
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

    EvalConllFile evalFile =
        new EvalConllFile(posTagger.getDataProps().getGlobalParams().getFeatureFilePathname(),
            posTagger.getDataProps().getGlobalParams().getTaggerName());
    System.out.println("\n++++\nLoad known vocabulary from archive training for evaluating OOV: "
        + evalFile.getData().getWordMapFileName());

    evalFile.getData().readWordSet(posTagger.getArchivator());
    System.out.println(evalFile.getData().toString());

    for (String fileName : posTagger.getCorpus().getDevLabeledData()) {
      String evalFileName = posTagger.getCorpus().makeEvalFileName(fileName);
      posTagger.tagAndWriteFromConllDevelFile(fileName + ".conll", evalFileName, -1);
      System.out.println("Create eval file: " + evalFileName);
      evalFile.computeAccuracy(evalFileName, true);
    }
    for (String fileName : posTagger.getCorpus().getTestLabeledData()) {
      String evalFileName = posTagger.getCorpus().makeEvalFileName(fileName);
      posTagger.tagAndWriteFromConllDevelFile(fileName + ".conll", evalFileName, -1);
      System.out.println("Create eval file: " + evalFileName);
      evalFile.computeAccuracy(evalFileName, true);
    }
  }


  // Used for running universal dependency treebanks as defined in project UniversalDepedencyBuilder
  public static void runner(String archiveZipName, String corpusConfigFileName, String archiveTxtName,
      boolean debugTest) throws IOException {

    GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
    GNTagger posTagger = new GNTagger(archiveZipName, corpusProps);
    //GN: Major difference with above.
    posTagger.getModelInfo().setModelFile(archiveTxtName);
    System.out.println("ModelFile: " + posTagger.getModelInfo().getModelFile());

    posTagger.initGNTagger(
        posTagger.getDataProps().getGlobalParams().getWindowSize(),
        posTagger.getDataProps().getGlobalParams().getDim());

    EvalConllFile evalFile =
        new EvalConllFile(posTagger.getDataProps().getGlobalParams().getFeatureFilePathname(),
            posTagger.getDataProps().getGlobalParams().getTaggerName());
    System.out.println("\n++++\nLoad known vocabulary from archive training for evaluating OOV: "
        + evalFile.getData().getWordMapFileName());

    evalFile.getData().readWordSet(posTagger.getArchivator());
    System.out.println(evalFile.getData().toString());

    for (String fileName : posTagger.getCorpus().getDevLabeledData()) {
      String evalFileName = posTagger.getCorpus().makeEvalFileName(fileName);
      posTagger.tagAndWriteFromConllDevelFile(fileName + ".conll", evalFileName, -1);
      System.out.println("Create eval file: " + evalFileName);
      evalFile.computeAccuracy(evalFileName, true);
    }
    for (String fileName : posTagger.getCorpus().getTestLabeledData()) {
      String evalFileName = posTagger.getCorpus().makeEvalFileName(fileName);
      posTagger.tagAndWriteFromConllDevelFile(fileName + ".conll", evalFileName, -1);
      System.out.println("Create eval file: " + evalFileName);
      evalFile.computeAccuracy(evalFileName, debugTest);
    }
  }


  public static void folderRunner(String archiveName, String corpusDir, String inEncode, String outEncode)
      throws IOException {

    GNTaggerStandalone runner = new GNTaggerStandalone();
    runner.initRunner(archiveName);

    long time1;
    long time2;

    Path dir = Paths.get(corpusDir);
    try (DirectoryStream<Path> stream =
        Files.newDirectoryStream(dir, "*.{txt}")) {
      for (Path entry : stream) {
        time1 = System.currentTimeMillis();

        System.out.println("Tagging file ... " + entry.toString());

        runner.tagFileRunner(entry.toString(), inEncode, outEncode);

        time2 = System.currentTimeMillis();
        System.out.println("System time (msec): " + (time2 - time1));
      }
    } catch (IOException e) {
      // IOException can never be thrown by the iteration.
      // In this snippet, it can only be thrown by newDirectoryStream.
      e.printStackTrace();
    }
  }
}
