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
 * problem.y is a array of size problem.l and each element keeps the label index that training instance i
 * problem.x is a parallel array where each element keeps the FeatureNode[]
 * the size of each  FeatureNode[] depends on non-zero values; each element is a feature node.
 * so, in order to use a similar approach, I would need to collect all labels and feature vectors of
 * the training examples in some variables, and the create the problem.
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
	public void createProblemInstanceFromWindow(Window tokenWindow) {
		this.setFeatureVector(new FeatureNode[tokenWindow.getWindowLength()]);
		// Add to cummulative lenght: only needed for computing average lenght of window
		ProblemInstance.cumLength+=featureVector.length;
		
		int offSet = 0;
		for (WordFeatures wordFeats : tokenWindow.getElements()){
			for (int i = 0; i < wordFeats.getLeft().size(); i++){
				Pair<Integer, Double> pair = wordFeats.getLeft().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), pair.getR());
			}
			offSet += wordFeats.getLeft().size();
			for (int i = 0; i < wordFeats.getRight().size(); i++){
				Pair<Integer, Double> pair = wordFeats.getRight().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), pair.getR());
			}
			offSet += wordFeats.getRight().size();
			for (int i = 0; i < wordFeats.getShape().size(); i++){
				Pair<Integer, Boolean> pair = wordFeats.getShape().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), 1);
			}
			offSet += wordFeats.getShape().size();
			for (int i = 0; i < wordFeats.getSuffix().size(); i++){
				Pair<Integer, Boolean> pair = wordFeats.getSuffix().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), 1);
			}
			offSet += wordFeats.getSuffix().size();
			for (int i = 0; i < wordFeats.getCluster().size(); i++){
				Pair<Integer, Boolean> pair = wordFeats.getCluster().get(i);
				featureVector[offSet+i] = new FeatureNode(pair.getL(), 1);
			}
			offSet += wordFeats.getCluster().size();
		}
		
		if (TrainerInMem.debug) this.checkFeatureVector(tokenWindow);

	}
	
	public void checkFeatureVector(Window tokenWindow){
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
