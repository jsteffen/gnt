package corpus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The class that holds all corpus files for training, testing etc.
public class Corpus {
	public static List<String> knownTaggerNames = 
			new ArrayList<String>(Arrays.asList("POS", "NER", "DEPOS", "DENER", "DEMORPH", "DEPOSMORPH"));

	private  List<String> trainingLabeledSourceFiles = new ArrayList<String>();
	private  List<String> devLabeledSourceFiles = new ArrayList<String>();
	private  List<String> testLabeledSourceFiles = new ArrayList<String>();

	private  List<String> trainingLabeledData = new ArrayList<String>();
	private  List<String> devLabeledData = new ArrayList<String>();
	private  List<String> testLabeledData = new ArrayList<String>();

	private  List<String> trainingUnLabeledData = new ArrayList<String>();
	private  List<String> devUnLabeledData = new ArrayList<String>();
	private  List<String> testUnLabeledData = new ArrayList<String>();

	public List<String> getTrainingLabeledData() {
		return trainingLabeledData;
	}
	public void setTrainingLabeledData(List<String> trainingLabeledData) {
		this.trainingLabeledData = trainingLabeledData;
	}
	public List<String> getDevLabeledData() {
		return devLabeledData;
	}
	public void setDevLabeledData(List<String> devLabeledData) {
		this.devLabeledData = devLabeledData;
	}
	public List<String> getTestLabeledData() {
		return testLabeledData;
	}
	public void setTestLabeledData(List<String> testLabeledData) {
		this.testLabeledData = testLabeledData;
	}
	public List<String> getTrainingUnLabeledData() {
		return trainingUnLabeledData;
	}
	public void setTrainingUnLabeledData(List<String> trainingUnLabeledData) {
		this.trainingUnLabeledData = trainingUnLabeledData;
	}
	public List<String> getDevUnLabeledData() {
		return devUnLabeledData;
	}
	public void setDevUnLabeledData(List<String> devUnLabeledData) {
		this.devUnLabeledData = devUnLabeledData;
	}
	public List<String> getTestUnLabeledData() {
		return testUnLabeledData;
	}
	public void setTestUnLabeledData(List<String> testUnLabeledData) {
		this.testUnLabeledData = testUnLabeledData;
	}
	public List<String> getTrainingLabeledSourceFiles() {
		return trainingLabeledSourceFiles;
	}
	public void setTrainingLabeledSourceFiles(
			List<String> trainingLabeledSourceFiles) {
		this.trainingLabeledSourceFiles = trainingLabeledSourceFiles;
	}
	public List<String> getDevLabeledSourceFiles() {
		return devLabeledSourceFiles;
	}
	public void setDevLabeledSourceFiles(List<String> devLabeledSourceFiles) {
		this.devLabeledSourceFiles = devLabeledSourceFiles;
	}
	public List<String> getTestLabeledSourceFiles() {
		return testLabeledSourceFiles;
	}
	public void setTestLabeledSourceFiles(List<String> testLabeledSourceFiles) {
		this.testLabeledSourceFiles = testLabeledSourceFiles;
	}


	// POS English
	private void setLabeledEnPosData(){
		// It is assumed that all these files have suffix .conll

		trainingLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-train");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-test");

		devLabeledData.add("resources/data/pbiotb/dev/english_pbiotb_dev");

		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-answers-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-emails-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-reviews-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-dev");

		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-answers-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-emails-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-reviews-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-test");		
	}

	private void setUnLabeledEnPosData(){
		// It is assumed that these filenames are complete
		trainingUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-train-sents.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev-sents.txt");
		testUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-test-sents.txt");

		trainingUnLabeledData.add("resources/data/pbiotb/dev/english_pbiotb_dev-sents.txt");

		devUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-answers-dev-sents.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-emails-dev-sents.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev-sents.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-reviews-dev-sents.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-dev-sents.txt");

		// Unlabeled data
		devUnLabeledData.add("resources/data/ptb/unlab/english_ptb_unlab");
		devUnLabeledData.add("resources/data/pbiotb/unlab/all-unlab.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.all/gweb-answers.unlabeled.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.all/gweb-emails.unlabeled.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.all/gweb-newsgroups.unlabeled.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.all/gweb-reviews.unlabeled.txt");
		devUnLabeledData.add("resources/data/sancl-2012/sancl.all/gweb-weblogs.unlabeled.txt");

		testUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-answers-test-sents.txt");
		testUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-emails-test-sents.txt");
		testUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-test-sents.txt");
		testUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-reviews-test-sents.txt");
		testUnLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-test-sents.txt");		
	}

	// English NER
	private void setLabeledEnNerSourceFiles(){
		// It is assumed that all these files have suffix .src
		trainingLabeledSourceFiles.add("resources/data/ner/en/eng-train");
		devLabeledSourceFiles.add("resources/data/ner/en/eng-testa");
		testLabeledSourceFiles.add("resources/data/ner/en/eng-testb");	
	}

	private void setLabeledEnNerData(){
		// It is assumed that all these files have suffix .conll
		trainingLabeledData.add("resources/data/ner/en/eng-train");
		devLabeledData.add("resources/data/ner/en/eng-testb");
		testLabeledData.add("resources/data/ner/en/eng-testa");	
	}

	private void setUnLabeledEnNerData(){
		// It is assumed that these filenames are complete
		trainingUnLabeledData.add("resources/data/ner/en/eng-train-sents.txt");
		devUnLabeledData.add("resources/data/ner/en/eng-testa-sents.txt");
		testUnLabeledData.add("resources/data/ner/en/eng-testb-sents.txt");

		// Unlabeled data
		//devUnLabeledData.add("resources/data/ptb/unlab/english_ptb_unlab");		
	}
	
	// English BILOU 
	private void setLabeledEnNerBilouData(){
		// It is assumed that all these files have suffix .conll
		trainingLabeledData.add("resources/data/ner/bilou/eng-train");
		devLabeledData.add("resources/data/ner/bilou/eng-testb");
		testLabeledData.add("resources/data/ner/bilou/eng-testa");	
	}

	private void setUnLabeledEnNerBilouData(){
		// It is assumed that these filenames are complete
		trainingUnLabeledData.add("resources/data/ner/bilou/eng-train-sents.txt");
		devUnLabeledData.add("resources/data/ner/bilou/eng-testa-sents.txt");
		testUnLabeledData.add("resources/data/ner/bilou/eng-testb-sents.txt");

		// Unlabeled data
		//devUnLabeledData.add("resources/data/ptb/unlab/english_ptb_unlab");		
	}

	// POS German
	private void setLabeledDePosData(){
		trainingLabeledData.add("resources/data/german/tiger2_train");
		devLabeledData.add("resources/data/german/tiger2_devel");
		testLabeledData.add("resources/data/german/tiger2_test");
	}

	private void setUnLabeledDePosData(){	
		trainingUnLabeledData.add("resources/data/german/tiger2_train-sents.txt");
		devUnLabeledData.add("resources/data/german/tiger2_devel-sents.txt");
		testUnLabeledData.add("resources/data/german/tiger2_test-sents.txt");
		// Unlabeled data
		devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");
	}
	
	// DE POS and Morph
	private void setLabeledDePosMorphData(){
		trainingLabeledData.add("resources/data/german/tiger2_posmorph_train");
		devLabeledData.add("resources/data/german/tiger2_posmorph_devel");
		testLabeledData.add("resources/data/german/tiger2_posmorph_test");
	}
	
	private void setUnLabeledDePosMorphData(){	
		trainingUnLabeledData.add("resources/data/german/tiger2_posmorph_train-sents.txt");
		devUnLabeledData.add("resources/data/german/tiger2_posmorph_devel-sents.txt");
		testUnLabeledData.add("resources/data/german/tiger2_posmorph_test-sents.txt");
		// Unlabeled data
		devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");
	}
	
	// DE Morph
		private void setLabeledDeMorphData(){
			trainingLabeledData.add("resources/data/german/tiger2_morph_train");
			devLabeledData.add("resources/data/german/tiger2_morph_devel");
			testLabeledData.add("resources/data/german/tiger2_morph_test");
		}
		
		private void setUnLabeledDeMorphData(){	
			trainingUnLabeledData.add("resources/data/german/tiger2_morph_train-sents.txt");
			devUnLabeledData.add("resources/data/german/tiger2_morph_devel-sents.txt");
			testUnLabeledData.add("resources/data/german/tiger2_morph_test-sents.txt");
			// Unlabeled data
			devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");
		}

	// German NER CONLL
	private void setLabeledDeNerSourceFiles(){
		// It is assumed that all these files have suffix .src
		trainingLabeledSourceFiles.add("resources/data/ner/de/deu-train");
		devLabeledSourceFiles.add("resources/data/ner/de/deu-testa");
		testLabeledSourceFiles.add("resources/data/ner/de/deu-testb");	
	}

	private void setLabeledDeNerData(){
		// It is assumed that all these files have suffix .conll
		devLabeledData.add("resources/data/ner/de/deu-testa");
		testLabeledData.add("resources/data/ner/de/deu-testb");
		trainingLabeledData.add("resources/data/ner/de/deu-train");	
	}

	private void setUnLabeledDeNerData(){
		// It is assumed that these filenames are complete
		devUnLabeledData.add("resources/data/ner/de/deu-testa-sents.txt");
		testUnLabeledData.add("resources/data/ner/de/deu-testb-sents.txt");
		trainingUnLabeledData.add("resources/data/ner/de/deu-train-sents.txt");

		// Unlabeled data
		//devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");		
	}
	
	private void setLabeledDeNerBilouData(){
		// It is assumed that all these files have suffix .conll
		devLabeledData.add("resources/data/ner/bilou/deu-testa");
		testLabeledData.add("resources/data/ner/bilou/deu-testb");
		trainingLabeledData.add("resources/data/ner/bilou/deu-train");	
	}

	private void setUnLabeledDeNerBilouData(){
		// It is assumed that these filenames are complete
		devUnLabeledData.add("resources/data/ner/bilou/deu-testa-sents.txt");
		testUnLabeledData.add("resources/data/ner/bilou/deu-testb-sents.txt");
		trainingUnLabeledData.add("resources/data/ner/bilou/deu-train-sents.txt");

		// Unlabeled data
		//devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");		
	}

	// German NER KONVENS 2014 data
	private void setLabeledDeNerKonvData(){
		// It is assumed that all these files have suffix .conll
		devLabeledData.add("resources/data/ner/dekonvens/deu.konvens.dev");
		testLabeledData.add("resources/data/ner/dekonvens/deu.konvens.test");
		trainingLabeledData.add("resources/data/ner/dekonvens/deu.konvens.train");	
	}

	private void setUnLabeledDeNerKonvData(){
		// It is assumed that these filenames are complete
		devUnLabeledData.add("resources/data/ner/dekonvens/deu.konvens.dev-sents.txt");
		testUnLabeledData.add("resources/data/ner/dekonvens/deu.konvens.test-sents.txt");
		trainingUnLabeledData.add("resources/data/ner/dekonvens/deu.konvens.train-sents.txt");

		// Unlabeled data
		//devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");		
	}
	
	// German NER Konvens BILOU
	private void setLabeledDeNerKonvBilouData(){
		// It is assumed that all these files have suffix .conll
		devLabeledData.add("resources/data/ner/bilou/deu.konvens.dev");
		testLabeledData.add("resources/data/ner/bilou/deu.konvens.test");
		trainingLabeledData.add("resources/data/ner/bilou/deu.konvens.train");	
	}

	private void setUnLabeledDeNerKonvBilouData(){
		// It is assumed that these filenames are complete
		devUnLabeledData.add("resources/data/ner/bilou/deu.konvens.dev-sents.txt");
		testUnLabeledData.add("resources/data/ner/bilou/deu.konvens.test-sents.txt");
		trainingUnLabeledData.add("resources/data/ner/bilou/deu.konvens.train-sents.txt");

		// Unlabeled data
		//devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");		
	}
	
	// Constructor;
	
	public Corpus() {
	}

	public Corpus(String taggerName){
		switch (taggerName){
		case "POS": 
			this.setLabeledEnPosData();
			this.setUnLabeledEnPosData();
			break;
		case "NER":
			this.setLabeledEnNerSourceFiles();
			this.setLabeledEnNerData();
			this.setUnLabeledEnNerData();
			break;
		case "NERBILOU":
			this.setLabeledEnNerBilouData();
			this.setUnLabeledEnNerBilouData();
			break;
		case "DENER":
			this.setLabeledDeNerSourceFiles();
			this.setLabeledDeNerData();
			this.setUnLabeledDeNerData();
			break;
		case "DENERBILOU":
			this.setLabeledDeNerBilouData();
			this.setUnLabeledDeNerBilouData();
			break;
		case "DENERKONV":
			this.setLabeledDeNerKonvData();
			this.setUnLabeledDeNerKonvData();
			break;
		case "DENERKONVBILOU":
			this.setLabeledDeNerKonvBilouData();
			this.setUnLabeledDeNerKonvBilouData();
			break;
		case "DEPOS":
			this.setLabeledDePosData();
			this.setUnLabeledDePosData();
			break;
		case "DEPOSMORPH":
			this.setLabeledDePosMorphData();
			this.setUnLabeledDePosMorphData();
			break;
		case "DEMORPH":
			this.setLabeledDeMorphData();
			this.setUnLabeledDeMorphData();
			break;
		default :
			System.err.println("unknown taggername used: " + taggerName);
			System.exit(0);
			break;
		}
	}
}