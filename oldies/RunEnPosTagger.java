package test;

import java.io.IOException;

import tagger.GNTagger;
import corpus.EvalConllFile;
import data.GNTProperties;
import data.ModelInfo;

/**
 * A test method for running GNT on same data set as FLORS and computing accuracies.
 * Actually, the first file is run in order to initialize compilation of all java objects.
 * @author gune00
 *
 */
public class RunEnPosTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo();
		GNTProperties props = new GNTProperties("resources/props/EnPosTagger.xml");
		GNTagger posTagger = new GNTagger(modelInfo, props);
		posTagger.initGNTagger(ModelInfo.windowSize, ModelInfo.dim);

		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile evalFile = new EvalConllFile();
		evalFile.getData().readWordSet(modelInfo.getTaggerName());
		System.out.println(evalFile.getData().toString());

		for (String fileName : posTagger.getCorpus().getTestLabeledData()){
			String evalFileName = posTagger.getCorpus().makeEvalFileName(fileName);
			posTagger.tagAndWriteFromConllDevelFile(fileName+".conll", evalFileName, -1);
			System.out.println("Create eval file: " + posTagger.getCorpus().makeEvalFileName(fileName));
			evalFile.computeAccuracy(evalFileName, false);
		}
	}
}
