package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tagger.GNTagger;
import corpus.EvalConllFile;
import data.ModelInfo;
import data.Pair;
import features.WordFeatures;

/**
 * A test method for running GNT on same data set as FLORS and computing accuracies.
 * Actually, the first file is run in order to initialize compilation of all java objects.
 * @author gune00
 *
 */
public class RunPosTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("POS");
		
		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 0;
		WordFeatures.withWordFeats=false;
		WordFeatures.withShapeFeats=true;
		WordFeatures.withSuffixFeats=true;
		System.out.println(WordFeatures.toActiveFeatureString());

		modelInfo.createModelFileName(dim, numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTagger posTagger = new GNTagger(modelInfo);	
		posTagger.initGNTagger(windowSize, dim);

		List<Pair<String, String>> fileList = new ArrayList<Pair<String, String>>();

		fileList.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev.conll", "resources/eval/ontonotes-wsj-dev-flors.txt"));

		fileList.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev.conll", "resources/eval/gweb-newsgroups-dev-flors.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-reviews-dev.conll", "resources/eval/gweb-reviews-dev-flors.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-weblogs-dev.conll", "resources/eval/gweb-weblogs-dev-flors.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-answers-dev.conll", "resources/eval/gweb-answers-dev-flors.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-emails-dev.conll", "resources/eval/gweb-emails-dev-flors.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/english/ptb3-devel.conll", "resources/eval/ptb3-devel-flors.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/english/ptb3-test.conll", "resources/eval/ptb3-test-flors.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/pbiotb/dev/english_pbiotb_dev.conll", "resources/eval/english_pbiotb_dev.txt"));

		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile.data.readWordSet(modelInfo.getTaggerName());
		System.out.println(EvalConllFile.data.toString());
		
		for (Pair<String, String> pair : fileList){
			posTagger.tagAndWriteFromConllDevelFile(pair.getL(), pair.getR());
		}
	}
}
