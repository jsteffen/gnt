package corpus;

import java.util.ArrayList;
import java.util.List;

// The class that holds all corpus files for training, testing etc.
public class Corpus {
	public  List<String> trainingLabeledData = new ArrayList<String>();
	public  List<String> devLabeledData = new ArrayList<String>();
	public  List<String> testLabeledData = new ArrayList<String>();

	public  List<String> trainingUnLabeledData = new ArrayList<String>();
	public  List<String> devUnLabeledData = new ArrayList<String>();
	public  List<String> testUnLabeledData = new ArrayList<String>();

	// POS English
	private void setLabeledEnPosData(){
		// It is assumed that all these files have suffix .conll
		//		trainingLabeledData.add("resources/data/english/ptb3-training");
		//		devLabeledData.add("resources/data/english/ptb3-devel");
		//		testLabeledData.add("resources/data/english/ptb3-test");

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
	private void setLabeledEnNerData(){
		// It is assumed that all these files have suffix .conll
		trainingLabeledData.add("resources/data/ner/en/eng-testa");
		devLabeledData.add("resources/data/ner/en/eng-testb");
		testLabeledData.add("resources/data/ner/en/eng-train");	
	}

	private void setUnLabeledEnNerData(){
		// It is assumed that these filenames are complete
		trainingUnLabeledData.add("resources/data/ner/en/eng-testa-sents.txt");
		devUnLabeledData.add("resources/data/ner/en/eng-testb-sents.txt");
		testUnLabeledData.add("resources/data/ner/en/eng-train-sents.txt");

		// Unlabeled data
		devUnLabeledData.add("resources/data/ptb/unlab/english_ptb_unlab");		
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
	}

	// German NER
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
		devUnLabeledData.add("resources/data/german/unlab/de-wikidump-sents500000.txt");		
	}


	public Corpus(String taggerName){
		if (taggerName.equals("POS")){
			this.setLabeledEnPosData();
			this.setUnLabeledEnPosData();
		}
		else
			if (taggerName.equals("NER")){
				this.setLabeledEnNerData();
				this.setUnLabeledEnNerData();
			}
			else
				if (taggerName.equals("DENER")){
					this.setLabeledDeNerData();
					this.setUnLabeledDeNerData();
				}
				else
					if (taggerName.equals("DEPOS")){
						this.setLabeledDePosData();
						this.setUnLabeledDePosData();
					}
					else{
						System.err.println("unknown taggername used: " + taggerName);
						System.exit(0);
					}	
	}
}
