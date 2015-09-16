package corpus;

import java.util.ArrayList;
import java.util.List;

// The class that holds all corpus files for training, testing etc.
public class Corpus {
	public static String prefix ="/Users/gune00/dfki/gnt/data/";

	public  List<String> trainingLabeledData = new ArrayList<String>();
	public  List<String> devLabeledData = new ArrayList<String>();
	public  List<String> testLabeledData = new ArrayList<String>();

	public  List<String> trainingUnLabeledData = new ArrayList<String>();
	public  List<String> devUnLabeledData = new ArrayList<String>();
	public  List<String> testUnLabeledData = new ArrayList<String>();

	private void setLabeledData(){
		// It is assumed that all these files have suffix .conll
		trainingLabeledData.add(this.prefix+this.prefix+"english/english-train");

		devLabeledData.add(Corpus.prefix+"pbiotb/dev/english_pbiotb_dev");
		devLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-answers-dev");
		devLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-dev");
		devLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-dev");
		devLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-dev");
		devLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-dev");
		devLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/ontonotes-wsj-dev");
		devLabeledData.add(Corpus.prefix+"/Users/gune00/data/MLDP/english/english-devel");

		testLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-answers-test");
		testLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-emails-test");
		testLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-newsgroups-test");
		testLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-reviews-test");
		testLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/gweb-weblogs-test");
		testLabeledData.add(Corpus.prefix+"/Users/gune00/data/sancl-2012/sancl.labeled/ontonotes-wsj-test");
		testLabeledData.add(Corpus.prefix+"/Users/gune00/data/MLDP/english/english-test");
	}

	private void setUnLabeledData(){
		// It is assumed that these filenames are complete
		trainingUnLabeledData.add(Corpus.prefix+"english/english-train-sents.txt");
		trainingUnLabeledData.add(Corpus.prefix+"pbiotb/dev/english_pbiotb_dev-sents.txt");

		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-answers-dev-sents.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-emails-dev-sents.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-newsgroups-dev-sents.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-reviews-dev-sents.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-weblogs-dev-sents.txt");
		// Unlabeled data
		devUnLabeledData.add(Corpus.prefix+"ptb/unlab/english_ptb_unlab");
		devUnLabeledData.add(Corpus.prefix+"pbiotb/unlab/all-unlab.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.all/gweb-answers.unlabeled.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.all/gweb-emails.unlabeled.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.all/gweb-newsgroups.unlabeled.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.all/gweb-reviews.unlabeled.txt");
		devUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.all/gweb-weblogs.unlabeled.txt");

		testUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-answers-test-sents.txt");
		testUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-emails-test-sents.txt");
		testUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-newsgroups-test-sents.txt");
		testUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-reviews-test-sents.txt");
		testUnLabeledData.add(Corpus.prefix+"sancl-2012/sancl.labeled/gweb-weblogs-test-sents.txt");
	}

	public Corpus(){
		this.setLabeledData();
		this.setUnLabeledData();
	}
}
