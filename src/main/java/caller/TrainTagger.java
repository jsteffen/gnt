package caller;

import java.io.IOException;

import corpus.GNTcorpusProperties;
import trainer.GNTrainer;
import data.GNTdataProperties;

public class TrainTagger {

	public void trainer(String dataConfigFileName, String corpusConfigFileName) throws IOException{
		GNTdataProperties dataProps = new GNTdataProperties(dataConfigFileName);
		dataProps.copyConfigFile(dataConfigFileName);
		GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
		GNTrainer gnTrainer = new GNTrainer(dataProps, corpusProps);

		gnTrainer.gntTrainingWithDimensionFromConllFile(
				corpusProps.getTrainingFile(), corpusProps.getClusterIdNameFile(), 
				dataProps.getGlobalParams().getDim(), 
				dataProps.getGlobalParams().getNumberOfSentences());
	}

	public void trainer(String dataConfigFileName, String corpusConfigFileName, 
			String modelZipFileName, String archiveTxtName) throws IOException{
		
		GNTdataProperties dataProps = new GNTdataProperties(dataConfigFileName);
		dataProps.copyConfigFile(dataConfigFileName);
		GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
		GNTrainer gnTrainer = new GNTrainer(dataProps, corpusProps);
		
		//GN: Major difference with above.
		dataProps.getModelInfo().setModelFile(archiveTxtName);
		gnTrainer.getArchivator().setArchiveName(modelZipFileName);


		gnTrainer.gntTrainingWithDimensionFromConllFile(
				corpusProps.getTrainingFile(), corpusProps.getClusterIdNameFile(), 
				dataProps.getGlobalParams().getDim(), 
				dataProps.getGlobalParams().getNumberOfSentences());
	}
}
