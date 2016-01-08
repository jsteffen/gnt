package trainer;

import data.Pair;
import data.Window;
import de.bwaldvogel.liblinear.FeatureNode;
import features.WordFeatures;

/*
 * If I understand MDP correctly, then first all training instances are collected
 * in two parallel list yList and xList, where yList keeps the label of instance i, and
 * xList keeps the feature vector of i which is a FeatureNode[]; 
 * together with the max feature size
 * a problem is actually created; 
 * From de.dfki.lt.mdparser.parser.Trainer.constructProblem(List<Integer>, List<FeatureNode[]>, int)
 * problem.y is a array of size problem.l and each element keeps the label index of that training instance i
 * problem.x is a parallel array where each element keeps the FeatureNode[]
 * the size of each  FeatureNode[] depends on non-zero values; each element is a feature node.
 * so, in order to use a similar approach, I would need to collect all labels and feature vectors of
 * the training examples in some variables, and then create the problem.
 * At least, it seems that I cannot do it online without knowing prob.l and prob.n in advance.
 */

public class ProblemInstance {
	public static int cumLength = 0;
	private FeatureNode[] featureVector;

	// Setters and getters

	public FeatureNode[] getFeatureVector() {
		return featureVector;
	}
	public void setFeatureVector(FeatureNode[] featureVector) {
		this.featureVector = featureVector;
	}

	// Instance

	public ProblemInstance() {
	}

	// Methods
	/**
	 * Given a tokenWindow (which is a list of Wordfeatures (which each is a list of feature-value pairs)),
	 * compute a feature vector which is a naturally ordered enumeration of all feature values nodes of a problem instance
	 * @param tokenWindow
	 */
	public void createProblemInstanceFromWindow(Window tokenWindow) {
		// This means that the feature vector has size windows length
		// and windows length is the number of non-zero features with relative feature index and value
		this.setFeatureVector(new FeatureNode[tokenWindow.getWindowLength()]);
		// Add to cumulative length: only needed for computing average length of window
		ProblemInstance.cumLength+=featureVector.length;

		int offSet = 0;
		
		for (WordFeatures wordFeats : tokenWindow.getElements()){
			// Add left word embedding length
			for (int i = 0; i < wordFeats.getLeft().size(); i++){
				Pair<Integer, Double> pair = wordFeats.getLeft().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), pair.getR());
			}
			offSet += wordFeats.getLeft().size();
			// Add right word embedding length
			for (int i = 0; i < wordFeats.getRight().size(); i++){
				Pair<Integer, Double> pair = wordFeats.getRight().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), pair.getR());
			}
			offSet += wordFeats.getRight().size();
			// Add shape length
			for (int i = 0; i < wordFeats.getShape().size(); i++){
				Pair<Integer, Boolean> pair = wordFeats.getShape().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), 1);
			}
			offSet += wordFeats.getShape().size();
			// Add suffix length
			for (int i = 0; i < wordFeats.getSuffix().size(); i++){
				Pair<Integer, Boolean> pair = wordFeats.getSuffix().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), 1);
			}
			offSet += wordFeats.getSuffix().size();
			// Add cluster length
			for (int i = 0; i < wordFeats.getCluster().size(); i++){
				Pair<Integer, Boolean> pair = wordFeats.getCluster().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), 1);
			}
			offSet += wordFeats.getCluster().size();
		}

		//this.normalizeFeatureVectorToUnitLenght();
		
		if (TrainerInMem.debug) this.checkFeatureVector(tokenWindow);

	}

	private void normalizeFeatureVectorToUnitLenght(){
		double vecLength = computeUnitLength();
		for (FeatureNode node : this.featureVector) {
			node.setValue(node.getValue()/vecLength);
		}
	}
	
	
	private double computeUnitLength() {
		double vecLength = 0.0;
		for (FeatureNode node : this.featureVector){
			vecLength += node.getValue()*node.getValue();	
		}
		return Math.sqrt(vecLength);
	}
	private void checkFeatureVector(Window tokenWindow){
		int lastValue = 0;
		int fLen = this.featureVector.length-1;
		for (int i = 0; i < fLen;i++){
			FeatureNode x = this.featureVector[i];
			if (x.getIndex() <= lastValue){
				System.err.println(tokenWindow.toString());
				throw new IllegalArgumentException("GN: feature nodes must be sorted by index in ascending order: " 
						+ lastValue + "..." + x.getIndex() + " i= " + i + " value: " + x.getValue());
			}
			lastValue = x.getIndex();
		}
	}

	public String toString(){
		String output = "";
		int fLen = this.featureVector.length-1;
		for (int i = 0; i < fLen;i++){
			FeatureNode x = this.featureVector[i];
			output += x.getIndex()+":"+x.getValue()+" ";
		}
		output += this.featureVector[fLen].getIndex()
				+":"+this.featureVector[fLen].getValue();
		return output;
	}


}
