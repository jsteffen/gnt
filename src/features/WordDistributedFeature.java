package features;

import java.util.Map;

public class WordDistributedFeature {
	// leftContext and rightContext both corresponds to a vector constructed from the indicator words
	// leftContext[0] == indicator word with rank 1
	// leftContext[n] == indicator word with rank n+1
	// n == rank n+1 - 1
	// leftContext[n+1] counts for non-indicator context elements

	// The cells keep the frequency information which is finally transformed to a weight by computing
	// 1+log(freq); this is why I am using double
	private double[] leftContext;
	private double[] rightContext;

	public double[] getLeftContext() {
		return leftContext;
	}
	public void setLeftContext(double[] leftContext) {
		this.leftContext = leftContext;
	}
	public double[] getRightContext() {
		return rightContext;
	}
	public void setRightContext(double[] rightContext) {
		this.rightContext = rightContext;
	}

	WordDistributedFeature(int n){
		leftContext = new double[n+1];
		rightContext = new double[n+1];
	}

	public WordDistributedFeature(int n, int leftWordIndex, int rightWordIndex) {
		// when a new word vector is initialized, all elements should be initialized with 0 -> in java the default
		leftContext = new double[n+1];
		rightContext = new double[n+1];
		this.updateWordVector(leftWordIndex, rightWordIndex);
	}

	public void updateWordVector(int leftWordIndex, int rightWordIndex) {
		// increment frequencies
		leftContext[leftWordIndex] = leftContext[leftWordIndex] + 1;
		rightContext[rightWordIndex] = rightContext[rightWordIndex] + 1;
	}

	private void computeWeights(double[] context){
		for (int i=0; i < context.length;i++){
			if (context[i] != 0.0) context[i]= 1 + Math.log(context[i]);
		}
	}

	public void computeContextWeights(){
		this.computeWeights(this.leftContext);
		this.computeWeights(this.rightContext);
	}

	public void initializeContext(String[] contextVector, String direction){
		// Insert non-zero weights for given index
		for (int i=0; i < contextVector.length;i++){
			String[] indexWeightPair = contextVector[i].split(":");
			int index = Integer.parseInt(indexWeightPair[0]);
			double weight = Double.parseDouble(indexWeightPair[1]);
			if (direction.equals("left"))
				leftContext[index] = weight;
			else
				if (direction.equals("right"))
					rightContext[index] = weight;
		}
		// Finally set weights of not seen index to zero
		// BASICALLY I assume that 0 is the default !
	}
	
	// This is a self-made function that concatenates the left and right vector to a single one
	// TODO Could also be done offline, finally, and then left and right could be deleted
	public double[] concatenateleftAndRightVector(){
		double[] lrVector = new double[this.getLeftContext().length+this.getRightContext().length];
		for (int i=0; i < this.getLeftContext().length;i++){
			lrVector[i]=this.getLeftContext()[i];
		}
		for (int j=this.getLeftContext().length+1; j < lrVector.length;j++){
			lrVector[j]=this.getLeftContext()[lrVector.length-j];
		}
		return lrVector;
	}

	public String toLeftContext(){
		String outputString = "";
		for (int i = 0; i <  leftContext.length; i++){
			outputString = outputString + leftContext[i] + "\t";
		}
		return outputString;
	}

	public String toRightContext(){
		String outputString = "";
		for (int i = 0; i <  rightContext.length; i++){
			outputString = outputString + rightContext[i] + "\t";
		}
		return outputString;
	}

	public String toLeftContextIndex(){
		String outputString = "";
		for (int i = 0; i <  leftContext.length; i++){
			if (leftContext[i] != 0)
				outputString = outputString + i + ":" + leftContext[i] + "\t";
		}
		return outputString;
	}

	public String toRightContextIndex(){
		String outputString = "";
		for (int i = 0; i <  rightContext.length; i++){
			if (rightContext[i] != 0)
				outputString = outputString + i + ":" + rightContext[i] + "\t";
		}
		return outputString;
	}

	public String toString (){
		String outputString = "";
		for (int i = 0; i <  leftContext.length; i++){
			outputString = outputString + (i+1) + "\t";
		}
		outputString = outputString + "\n";
		for (int i = 0; i <  leftContext.length; i++){
			outputString = outputString + leftContext[i] + "\t";
		}
		outputString = outputString + "\n";
		for (int i = 0; i <  rightContext.length; i++){
			outputString = outputString + rightContext[i] + "\t";
		}
		outputString = outputString + "\n";
		return outputString;
	}

	public String toStringEncoded (Map<Integer, String> num2iw){
		String outputString = "";
		for (int i = 0; i <  leftContext.length; i++){
			outputString = outputString + num2iw.get(i+1) + "\t";
		}
		outputString = outputString + "\n";
		for (int i = 0; i <  leftContext.length; i++){
			outputString = outputString + leftContext[i] + "\t";
		}
		outputString = outputString + "\n";
		for (int i = 0; i <  rightContext.length; i++){
			outputString = outputString + rightContext[i] + "\t";
		}
		outputString = outputString + "\n";
		return outputString;
	}


}
