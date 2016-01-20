package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tagger.GNTagger;
import corpus.EvalConllFile;
import data.Alphabet;
import data.ModelInfo;
import data.Pair;
import features.WordSuffixFeatureFactory;

/**
 * A test method for running GNT on same data set as FLORS and computing accuracies.
 * Actually, the first file is run in order to initialize compilation of all java objects.
 * @author gune00
 *
 */
public class RunEnPosTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("MDP");
		modelInfo.setTaggerName("POS");
		
		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 0;
		Alphabet.withWordFeats=false;
		Alphabet.withShapeFeats=true;
		Alphabet.withSuffixFeats=true;
		Alphabet.withClusterFeats=true;
		System.out.println(Alphabet.toActiveFeatureString());
		
		WordSuffixFeatureFactory.ngram = false;

		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTagger posTagger = new GNTagger(modelInfo);	
		posTagger.initGNTagger(windowSize, dim);

		// TODO: define new data structure so that official results can be used to compute comparisons
		// Or fill a excel file directly
		List<Pair<String, String>> fileListDevel = new ArrayList<Pair<String, String>>();
		List<Pair<String, String>> fileListTest = new ArrayList<Pair<String, String>>();

		fileListDevel.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev.conll", "resources/eval/ontonotes-wsj-dev-flors.txt"));
		fileListDevel.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev.conll", "resources/eval/gweb-newsgroups-dev-flors.txt"));
		fileListDevel.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-reviews-dev.conll", "resources/eval/gweb-reviews-dev-flors.txt"));
		fileListDevel.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-weblogs-dev.conll", "resources/eval/gweb-weblogs-dev-flors.txt"));
		fileListDevel.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-answers-dev.conll", "resources/eval/gweb-answers-dev-flors.txt"));
		fileListDevel.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-emails-dev.conll", "resources/eval/gweb-emails-dev-flors.txt"));
		fileListDevel.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev.conll", "resources/eval/ontonotes-wsj-dev-flors.txt"));
		fileListDevel.add(new Pair<String, String>(
				"resources/data/pbiotb/dev/english_pbiotb_dev.conll", "resources/eval/english_pbiotb_dev.txt"));
		
		fileListTest.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-test.conll", "resources/eval/ontonotes-wsj-test-flors.txt"));
		fileListTest.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-test.conll", "resources/eval/gweb-newsgroups-test-flors.txt"));
		fileListTest.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-reviews-test.conll", "resources/eval/gweb-reviews-test-flors.txt"));
		fileListTest.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-weblogs-test.conll", "resources/eval/gweb-weblogs-test-flors.txt"));
		fileListTest.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-answers-test.conll", "resources/eval/gweb-answers-test-flors.txt"));
		fileListTest.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/gweb-emails-test.conll", "resources/eval/gweb-emails-test-flors.txt"));
		fileListTest.add(new Pair<String, String>(
				"resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-test.conll", "resources/eval/ontonotes-wsj-test-flors.txt"));	

		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile evalFile = new EvalConllFile();
		evalFile.getData().readWordSet(modelInfo.getTaggerName());
		System.out.println(evalFile.getData().toString());
		
		for (Pair<String, String> pair : fileListTest){
			posTagger.tagAndWriteFromConllDevelFile(pair.getL(), pair.getR(), -1);
			System.out.println("Create eval file: " + pair.getR());
			evalFile.computeAccuracy(pair.getR(), false);
		}
	}
}
