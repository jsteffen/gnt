package caller;

import java.io.IOException;

import corpus.GNTcorpusProperties;
import trainer.GNTrainer;
import data.GNTdataProperties;

public class TrainTagger {

  public void trainer(String dataConfigFileName, String corpusConfigFileName) throws IOException {

    GNTdataProperties dataProps = new GNTdataProperties(dataConfigFileName);
    GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
    GNTrainer gnTrainer = new GNTrainer(dataProps, corpusProps);

    dataProps.copyConfigFile(dataConfigFileName);

    gnTrainer.gntTrainingWithDimensionFromConllFile(
        corpusProps.getTrainingFile(), corpusProps.getClusterIdNameFile(),
        dataProps.getGlobalParams().getDim(),
        dataProps.getGlobalParams().getNumberOfSentences());
  }


  // This one used for processing the universal dependency treebanks
  // cf. com.gn.UDlanguageGNTmodelFactory.trainLanguage(String, String)
  public void trainer(String dataConfigFileName, String corpusConfigFileName,
      String modelZipFileName, String archiveTxtName) throws IOException {

    GNTdataProperties dataProps = new GNTdataProperties(dataConfigFileName);
    GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
    GNTrainer gnTrainer = new GNTrainer(dataProps, corpusProps);

    dataProps.copyConfigFile(dataConfigFileName);

    //GN: Major difference to above method
    dataProps.getModelInfo().setModelFile(archiveTxtName);
    gnTrainer.getArchivator().setArchiveName(modelZipFileName);


    gnTrainer.gntTrainingWithDimensionFromConllFile(
        corpusProps.getTrainingFile(), corpusProps.getClusterIdNameFile(),
        dataProps.getGlobalParams().getDim(),
        dataProps.getGlobalParams().getNumberOfSentences());
  }
}
