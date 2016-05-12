package caller;

import java.io.IOException;

import tagger.GNTagger;
import corpus.EvalConllFile;
import corpus.GNTcorpusProperties;
import data.GlobalParams;
import data.ModelInfo;

/**
 * A test method for running GNT on same data set as FLORS and computing accuracies.
 * Actually, the first file is run in order to initialize compilation of all java objects.
 * @author gune00
 *
 */
public class RunTagger {
	public static void runner(String archiveName, String corpusConfigFileName) throws IOException{
		ModelInfo modelInfo = new ModelInfo();
		GNTcorpusProperties corpusProps = new GNTcorpusProperties(corpusConfigFileName);
		GNTagger posTagger = new GNTagger(archiveName, corpusProps, modelInfo);
		posTagger.initGNTagger(GlobalParams.windowSize, GlobalParams.dim);

		EvalConllFile evalFile = new EvalConllFile();
		System.out.println("\n++++\nLoad known vocabulary from archive training for evaluating OOV: " 
		+ evalFile.getData().getWordMapFileName());
		
		evalFile.getData().readWordSet(posTagger.getArchivator());
		System.out.println(evalFile.getData().toString());
		
		for (String fileName : posTagger.getCorpus().getDevLabeledData()){
			String evalFileName = posTagger.getCorpus().makeEvalFileName(fileName);
			posTagger.tagAndWriteFromConllDevelFile(fileName+".conll", evalFileName, -1);
			System.out.println("Create eval file: " + evalFileName);
			evalFile.computeAccuracy(evalFileName, true);
		}
		for (String fileName : posTagger.getCorpus().getTestLabeledData()){
			String evalFileName = posTagger.getCorpus().makeEvalFileName(fileName);
			posTagger.tagAndWriteFromConllDevelFile(fileName+".conll", evalFileName, -1);
			System.out.println("Create eval file: " + evalFileName);
			evalFile.computeAccuracy(evalFileName, false);
		}
	}
}
