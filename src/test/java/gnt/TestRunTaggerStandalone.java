package gnt;

import java.io.IOException;

import caller.GNTaggerStandalone;

public class TestRunTaggerStandalone {
	
public static void main(String[] args) throws IOException{
	GNTaggerStandalone runner = new GNTaggerStandalone();
	runner.initRunner("resources/models/model_ENPOS_2_0iw-1sent_FTTT_MCSVM_CS.zip");
		
	System.out.println("Tag text: ");
	runner.tagStringRunner(
			"Direction Blinking Left: When moving the pitman arm in position turn left " 
			+ "the vehicle flashes all left direction indicators (front left, exterior mirror left, rear left) "
			+ "synchronically with pulse ratio bright to dark 1:1."
			+ "We describe the WikiQA dataset, a new publicly available set of question and sentence pairs, "
			+ "collected and annotated for research on open-domain question answering. Most previous work on answer sentence "
			+ "selection focuses on a dataset created using the TREC-QA data, which includes editor-generated questions and "
			+ "candidate answer sentences selected by matching content words in the question. WikiQA is constructed using a "
			+ "more natural process and is more than an order of magnitude larger than the previous dataset. In addition, the "
			+ "WikiQA dataset also includes questions for which there are no correct sentences, enabling researchers to work on "
			+ "answer triggering, a critical component in any QA system. We compare several systems on the task of answer sentence "
			+ "selection on both datasets and also describe the performance of a system on the problem of answer triggering using the WikiQA dataset.");		
	}

}
