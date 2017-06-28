package de.dfki.mlt.gnt.caller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.configuration2.ex.ConfigurationException;

import de.dfki.mlt.gnt.config.CorpusConfig;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.config.ModelConfig;
import de.dfki.mlt.gnt.trainer.GNTrainer;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class TrainTagger {

  public void trainer(String modelConfigFileName, String corpusConfigFileName)
      throws IOException, ConfigurationException {

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
      throws IOException, ConfigurationException {

    deleteFolder(GlobalConfig.getModelBuildFolder());

    InputStream in = getClass().getClassLoader().getResourceAsStream(modelConfigFileName);
    if (in == null) {
      // try file path if loading from classpath fails
      in = Files.newInputStream(Paths.get(modelConfigFileName));
    }
    copyConfigFile(in);

    ModelConfig modelConfig = ModelConfig.create(modelConfigFileName);
    CorpusConfig corpusConfig = CorpusConfig.create(corpusConfigFileName);
    GNTrainer gnTrainer = new GNTrainer(modelConfig, corpusConfig);
    if (modelArchiveName != null) {
      gnTrainer.getArchivator().setArchiveName(modelArchiveName);
    }

    gnTrainer.gntTrainingWithDimensionFromConllFile();
  }


  private static void copyConfigFile(InputStream in) {

    //Path sourceFile = new File(configFileName).toPath();
    Path targetFile = GlobalConfig.getModelBuildFolder().resolve(GlobalConfig.MODEL_CONFIG_FILE);
    try {
      Files.createDirectories(targetFile.getParent());
      Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
