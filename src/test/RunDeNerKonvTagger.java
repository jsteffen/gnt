package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import corpus.EvalConllFile;
import tagger.GNTagger;
import data.Alphabet;
import data.Data;
import data.ModelInfo;
import data.Pair;
import features.WordSuffixFeatureFactory;

public class RunDeNerKonvTagger {
	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("DENERKONV");
		Data.wordFormIndex = 1;
		// For konvens 2014 data labels are at column 2 (outer NE labels) or 3 (inner NE label)
		Data.posTagIndex = 2;

		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 50;
		Alphabet.withWordFeats=true;
		Alphabet.withShapeFeats=true;
		Alphabet.withSuffixFeats=true;
		Alphabet.withClusterFeats=true;
		System.out.println(Alphabet.toActiveFeatureString());

		WordSuffixFeatureFactory.ngram = false;
		WordSuffixFeatureFactory.ngramSize = 1;

		modelInfo.createModelFileName(windowSize, dim, numberOfSentences);
		System.out.println(modelInfo.toString());

		GNTagger nerTagger = new GNTagger(modelInfo);	


		nerTagger.initGNTagger(windowSize, dim);

		List<Pair<String, String>> fileList = new ArrayList<Pair<String, String>>();

		fileList.add(new Pair<String, String>(
				"resources/data/ner/dekonvens/deu.konvens.dev.conll", "resources/eval/deu-konv-dev.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/ner/dekonvens/deu.konvens.test.conll", "resources/eval/deu-konv-test.txt"));

		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile evalFile = new EvalConllFile();
		evalFile.getData().readWordSet(modelInfo.getTaggerName());
		System.out.println(evalFile.getData().toString());

		for (Pair<String, String> pair : fileList){
			nerTagger.tagAndWriteFromConllDevelFile(pair.getL(), pair.getR(), -1);
			System.out.println("Create eval file: " + pair.getR());
			evalFile.computeAccuracy(pair.getR());
		}
	}
}
