package de.dfki.mlt.gnt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import de.dfki.mlt.gnt.caller.TrainTagger;
import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.tagger.GNTagger;

/**
 *
 *
 * @author Jörg Steffen, DFKI
 */
public class TestGNT {

  @Before
  public void setUp() throws IOException {

    Utils.deleteFolder(GlobalConfig.getModelBuildFolder());
    Utils.deleteFolder(GlobalConfig.getPath(ConfigKeys.EVAL_FOLDER));
    List<Path> filesToDelete = Utils.getAllFilesFromFolder(Paths.get("src/test/resources"), "*.{zip,txt}");
    filesToDelete.addAll(Utils.getAllFilesFromFolder(Paths.get("src/test/resources/input"), "*.GNT"));
    for (Path oneFileToDelete : filesToDelete) {
      Files.delete(oneFileToDelete);
    }
  }


  @Test
  public void testTrainEvalTag()
      throws IOException, ConfigurationException, InterruptedException {

    GlobalConfig.getInstance().setProperty(ConfigKeys.CREATE_LIBLINEAR_INPUT_FILE, false);
    GlobalConfig.getInstance().setProperty(ConfigKeys.DEBUG, true);

    testTrain();
    // for some reason the model archive is not immediately available in the file system, so we wait a moment
    TimeUnit.SECONDS.sleep(5);
    testEval();
    testTag();
  }


  private void testTrain()
      throws IOException, ConfigurationException {

    TrainTagger gntTrainer = new TrainTagger();
    gntTrainer.trainer("src/test/resources/EnPosTagger.model.conf", "src/test/resources/EnPosTagger.corpus.conf");

    String modelName = "model_ENPOS_2_0iw-1sent_FTTTF_MCSVM_CS";
    assertThat(GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER).resolve(modelName + ".zip")).exists();

    List<Path> modelFiles =
        Utils.getAllFilesFromFolder(GlobalConfig.getModelBuildFolder(), "*");
    List<Path> expectedModelFiles =
        Utils.getAllFilesFromFolder(Paths.get("src/test/resources/expected/model"), "*");

    assertThat(modelFiles).hasSameSizeAs(expectedModelFiles);
    modelFiles.sort(null);
    expectedModelFiles.sort(null);
    for (int i = 0; i < modelFiles.size(); i++) {
      assertThat(modelFiles.get(i).getFileName()).isEqualTo(expectedModelFiles.get(i).getFileName());
      assertThat(modelFiles.get(i)).usingCharset(StandardCharsets.UTF_8)
          .hasSameContentAs(expectedModelFiles.get(i), StandardCharsets.UTF_8);
    }
  }


  private void testEval()
      throws IOException, ConfigurationException {

    GNTagger tagger = new GNTagger("model_ENPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip");
    tagger.eval("src/test/resources/EnPosTagger.corpus.conf");

    List<Path> evalFiles =
        Utils.getAllFilesFromFolder(GlobalConfig.getPath(ConfigKeys.EVAL_FOLDER), "*");
    List<Path> expectedEvalFiles =
        Utils.getAllFilesFromFolder(Paths.get("src/test/resources/expected/eval"), "*");
    assertThat(evalFiles).hasSameSizeAs(expectedEvalFiles);
    evalFiles.sort(null);
    expectedEvalFiles.sort(null);
    for (int i = 0; i < evalFiles.size(); i++) {
      assertThat(evalFiles.get(i).getFileName()).isEqualTo(expectedEvalFiles.get(i).getFileName());
      assertThat(evalFiles.get(i)).usingCharset(StandardCharsets.UTF_8)
          .hasSameContentAs(expectedEvalFiles.get(i), StandardCharsets.UTF_8);
    }
  }


  private void testTag()
      throws IOException, ConfigurationException {

    GNTagger tagger = new GNTagger("model_ENPOS_2_0iw-1sent_FTTTF_MCSVM_CS.zip");
    tagger.tagFolder("src/test/resources/input", "UTF-8", "UTF-8");

    List<Path> taggedFiles =
        Utils.getAllFilesFromFolder(Paths.get("src/test/resources/input"), "*.GNT");
    List<Path> expectedTaggedFiles =
        Utils.getAllFilesFromFolder(Paths.get("src/test/resources/expected/tagged"), "*.GNT");
    assertThat(taggedFiles).hasSameSizeAs(expectedTaggedFiles);
    taggedFiles.sort(null);
    expectedTaggedFiles.sort(null);
    for (int i = 0; i < taggedFiles.size(); i++) {
      assertThat(taggedFiles.get(i).getFileName()).isEqualTo(expectedTaggedFiles.get(i).getFileName());
      assertThat(taggedFiles.get(i)).usingCharset(StandardCharsets.UTF_8)
          .hasSameContentAs(expectedTaggedFiles.get(i), StandardCharsets.UTF_8);
    }
  }


  @Test
  public void testTrainLiblinearInput()
      throws IOException, ConfigurationException {

    GlobalConfig.getInstance().setProperty(ConfigKeys.CREATE_LIBLINEAR_INPUT_FILE, true);

    TrainTagger gntTrainer = new TrainTagger();
    gntTrainer.trainer("src/test/resources/EnPosTagger.model.conf", "src/test/resources/EnPosTagger.corpus.conf");
    String modelName = "model_ENPOS_2_0iw-1sent_FTTTF_MCSVM_CS";
    assertThat(GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER).resolve(modelName + ".zip")).exists();
    String liblinearInputFileName = "liblinear_input_" + modelName + ".txt";
    assertThat(GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER).resolve(liblinearInputFileName))
        .usingCharset(StandardCharsets.UTF_8).hasSameContentAs(
            Paths.get("src/test/resources/expected").resolve(liblinearInputFileName), StandardCharsets.UTF_8);
  }
}
