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
public class RunDeMorphTagger {

	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("MDP");
		modelInfo.setTaggerName("DEMORPH");
		
		ModelInfo.windowSize = 2;
		ModelInfo.numberOfSentences = -1;
		ModelInfo.dim = 0;
		
		Alphabet.withWordFeats=false;
		Alphabet.withShapeFeats=true;
		Alphabet.withSuffixFeats=true;
		Alphabet.withClusterFeats=true;
		System.out.println(Alphabet.toActiveFeatureString());
		
		WordSuffixFeatureFactory.ngram = false;

		modelInfo.createModelFileName(ModelInfo.windowSize, ModelInfo.dim, ModelInfo.numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTagger posTagger = new GNTagger(modelInfo);	
		posTagger.initGNTagger(ModelInfo.windowSize, ModelInfo.dim);

		List<Pair<String, String>> fileListDevel = new ArrayList<Pair<String, String>>();
		List<Pair<String, String>> fileListTest = new ArrayList<Pair<String, String>>();

		fileListDevel.add(new Pair<String, String>("resources/data/german/tiger2_morph_devel.conll", "resources/eval/tiger2_morph_devel.txt"));
		
		fileListTest.add(new Pair<String, String>("resources/data/german/tiger2_morph_test.conll", "resources/eval/tiger2_morph_test.txt"));	

		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile evalFile = new EvalConllFile();
		evalFile.getData().readWordSet(modelInfo.getTaggerName());
		System.out.println(evalFile.getData().toString());
		
		for (Pair<String, String> pair : fileListDevel){
			posTagger.tagAndWriteFromConllDevelFile(pair.getL(), pair.getR(), -1);
			System.out.println("Create eval file: " + pair.getR());
			evalFile.computeAccuracy(pair.getR(), true);
		}
		
		for (Pair<String, String> pair : fileListTest){
			posTagger.tagAndWriteFromConllDevelFile(pair.getL(), pair.getR(), -1);
			System.out.println("Create eval file: " + pair.getR());
			evalFile.computeAccuracy(pair.getR(), false);
		}
	}
}
