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

	private void setLabeledPosData(){
		// It is assumed that all these files have suffix .conll
		trainingLabeledData.add("resources/data/english/ptb3-training");
		devLabeledData.add("resources/data/english/ptb3-devel");
		testLabeledData.add("resources/data/english/ptb3-test");
		
		devLabeledData.add("resources/data/pbiotb/dev/english_pbiotb_dev");
		
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-answers-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-emails-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-reviews-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-dev");
		devLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev");
		
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-answers-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-emails-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-newsgroups-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-reviews-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/gweb-weblogs-test");
		testLabeledData.add("resources/data/sancl-2012/sancl.labeled/ontonotes-wsj-test");
		
	}

	private void setUnLabeledPosData(){
		// It is assumed that these filenames are complete
		trainingUnLabeledData.add("resources/data/english/ptb3-training-sents.txt");
		devUnLabeledData.add("resources/data/english/ptb3-devel-sents.txt");
		testUnLabeledData.add("resources/data/english/ptb3-test-sents.txt");
		
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
	
	private void setLabeledNerData(){
		// It is assumed that all these files have suffix .conll
		trainingLabeledData.add("resources/data/ner/eng-testa");
		devLabeledData.add("resources/data/ner/eng-testb");
		testLabeledData.add("resources/data/ner/eng-train");	
	}

	private void setUnLabeledNerData(){
		// It is assumed that these filenames are complete
		trainingUnLabeledData.add("resources/data/ner/eng-testa-sents.txt");
		devUnLabeledData.add("resources/data/ner/eng-testb-sents.txt");
		testUnLabeledData.add("resources/data/ner/eng-train-sents.txt");
			
		// Unlabeled data
		devUnLabeledData.add("resources/data/ptb/unlab/english_ptb_unlab");		
	}

	public Corpus(){
		this.setLabeledPosData();
		this.setUnLabeledPosData();
	}
	
	public Corpus(String taggerName){
		if (taggerName.equals("POS")){
			this.setLabeledPosData();
			this.setUnLabeledPosData();
		}
		else
			if (taggerName.equals("NER")){
				this.setLabeledNerData();
				this.setUnLabeledNerData();
			}
			else{
				System.err.println("unknown taggername used: " + taggerName);
				System.exit(0);
			}
		
	}
}
