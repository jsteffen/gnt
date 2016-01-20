package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import corpus.EvalConllFile;
import tagger.GNTagger;
import data.Alphabet;
import data.ModelInfo;
import data.Pair;
import features.WordSuffixFeatureFactory;

public class RunEnNerTagger {
	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("NER");
		
		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 50;
		Alphabet.withWordFeats=true;
		Alphabet.withShapeFeats=true;
		Alphabet.withSuffixFeats=true;
		Alphabet.withClusterFeats=true;
		System.out.println(Alphabet.toActiveFeatureString());
		
		WordSuffixFeatureFactory.ngram = false;

		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);
		System.out.println(modelInfo.toString());
		
		GNTagger nerTagger = new GNTagger(modelInfo);	
		
		
		nerTagger.initGNTagger(windowSize, dim);

		List<Pair<String, String>> fileList = new ArrayList<Pair<String, String>>();

		fileList.add(new Pair<String, String>(
				"resources/data/ner/en/eng-testa.conll", "resources/eval/eng-testa.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/ner/en/eng-testb.conll", "resources/eval/eng-testb.txt"));

		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile evalFile = new EvalConllFile();
		evalFile.getData().readWordSet(modelInfo.getTaggerName());
		System.out.println(evalFile.getData().toString());
		
		for (Pair<String, String> pair : fileList){
			nerTagger.tagAndWriteFromConllDevelFile(pair.getL(), pair.getR(), -1);
			System.out.println("Create eval file: " + pair.getR());
			evalFile.computeAccuracy(pair.getR(), false);
		}
	}
}
