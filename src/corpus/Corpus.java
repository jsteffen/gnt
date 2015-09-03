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

	private void setLabeledData(){
		trainingLabeledData.add("/Users/gune00/data/MLDP/english/english-train");

		devLabeledData.add("/Users/gune00/data/BioNLPdata/CoNLL2007/pbiotb/dev/english_pbiotb_dev");
		devLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-answers-dev");
		devLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-dev");
		devLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev");
		devLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-dev");
		devLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-dev");
		devLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev");

		testLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-answers-test");
		testLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-test");
		testLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-test");
		testLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-test");
		testLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-test");
		testLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/ontonotes-wsj-test");
	}

	private void setUnLabeledData(){
		trainingUnLabeledData.add("/Users/gune00/data/MLDP/english/english-train-sents.txt");
		trainingUnLabeledData.add("/Users/gune00/data/BioNLPdata/CoNLL2007/pbiotb/dev/english_pbiotb_dev-sents.txt");

		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-answers-dev-sents.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-dev-sents.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev-sents.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-dev-sents.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-dev-sents.txt");
		// Unlabeled data
		devUnLabeledData.add("/Users/gune00/data/BioNLPdata/CoNLL2007/ptb/unlab/english_ptb_unlab");
		devUnLabeledData.add("/Users/gune00/data/BioNLPdata/CoNLL2007/pbiotb/unlab/all-unlab.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.all/gweb-answers.unlabeled.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.all/gweb-emails.unlabeled.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.all/gweb-newsgroups.unlabeled.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.all/gweb-reviews.unlabeled.txt");
		devUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.all/gweb-weblogs.unlabeled.txt");

		testUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-answers-test-sents.txt");
		testUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-test-sents.txt");
		testUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-test-sents.txt");
		testUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-test-sents.txt");
		testUnLabeledData.add("/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-test-sents.txt");
	}
	
	public Corpus(){
		this.setLabeledData();
		this.setUnLabeledData();
	}

}
