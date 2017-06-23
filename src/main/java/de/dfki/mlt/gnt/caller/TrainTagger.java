package de.dfki.mlt.gnt.caller;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.corpus.GNTcorpusProperties;
import de.dfki.mlt.gnt.data.GNTdataProperties;
import de.dfki.mlt.gnt.trainer.GNTrainer;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class TrainTagger {

  public void trainer(String modelConfigFileName, String corpusConfigFileName)
      throws IOException {

    this.trainer(modelConfigFileName, corpusConfigFileName, null);
  }


  /**
   * This one used for processing the universal dependency treebanks
   * cf. com.gn.UDlanguageGNTmodelFactory.trainLanguage(String, String)
   * @param modelConfigFileName
   * @param corpusConfigFileName
   * @param modelArchiveName
   * @throws IOException
   */
  public void trainer(String modelConfigFileName, String corpusConfigFileName, String modelArchiveName)
      throws IOException {

    deleteFolder(GlobalConfig.getModelBuildFolder());

    GNTdataProperties dataProps = new GNTdataProperties(modelConfigFileName);
    GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
    GNTrainer gnTrainer = new GNTrainer(dataProps, corpusProps);

    dataProps.copyConfigFile(modelConfigFileName);

    if (modelArchiveName != null) {
      gnTrainer.getArchivator().setArchiveName(modelArchiveName);
    }

    gnTrainer.gntTrainingWithDimensionFromConllFile(
        corpusProps.getTrainingFile(), corpusProps.getClusterIdNameFile(),
        dataProps.getGlobalParams().getDim(),
        dataProps.getGlobalParams().getNumberOfSentences());
  }


  private static void deleteFolder(Path path)
      throws IOException {

    try {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {

          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }


        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
            throws IOException {

          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (NoSuchFileException e) {
      // nothing to do, file already deleted
    }
  }
}
