package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import corpus.EvalConllFile;
import tagger.GNTagger;
import data.ModelInfo;
import data.Pair;
import features.WordFeatures;

public class RunNerTagger {
	public static void main(String[] args) throws IOException{
		ModelInfo modelInfo = new ModelInfo("FLORS");
		modelInfo.setTaggerName("NER");
		
		int windowSize = 2;
		int numberOfSentences = -1;
		int dim = 50;
		WordFeatures.withWordFeats=true;
		WordFeatures.withShapeFeats=true;
		WordFeatures.withSuffixFeats=true;

		modelInfo.createModelFileName(dim, numberOfSentences);

		System.out.println(modelInfo.toString());
		GNTagger nerTagger = new GNTagger(modelInfo);	
		
		
		nerTagger.initGNTagger(windowSize, dim);

		List<Pair<String, String>> fileList = new ArrayList<Pair<String, String>>();

		fileList.add(new Pair<String, String>(
				"resources/data/ner/eng-testa.conll", "resources/eval/eng-testa.txt"));
		fileList.add(new Pair<String, String>(
				"resources/data/ner/eng-testb.conll", "resources/eval/eng-testb.txt"));

		System.out.println("\n++++\nLoad known vocabulary from training for evaluating OOV: ");
		EvalConllFile.data.readWordSet(modelInfo.getTaggerName());
		
		for (Pair<String, String> pair : fileList){
			nerTagger.tagAndWriteFromConllDevelFile(pair.getL(), pair.getR());
		}
	}
}
