package data;

import de.bwaldvogel.liblinear.SolverType;

public class ModelInfo {
	
	private SolverType solver = SolverType.L2R_LR; // -s 0
	private double C = 1.0;    // cost of constraints violation
	private double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less
	
	private int windowSize = 2;
	
	private String modelFilePrefix = "/Users/gune00/data/wordVectorTests/testModel10k_";
	private String modelFile = "";

	public SolverType getSolver() {
		return solver;
	}
	public void setSolver(SolverType solver) {
		this.solver = solver;
	}
	public double getC() {
		return C;
	}
	public void setC(double c) {
		C = c;
	}
	public double getEps() {
		return eps;
	}
	public void setEps(double eps) {
		this.eps = eps;
	}
	public String getModelFilePrefix() {
		return modelFilePrefix;
	}
	public void setModelFilePrefix(String modelFilePrefix) {
		this.modelFilePrefix = modelFilePrefix;
	}
	public String getModelFile() {
		return modelFile;
	}
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}
	public int getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}
	
	//
	public ModelInfo(){
	}
	
	public ModelInfo(String type){
		if (type.equalsIgnoreCase("FLORS")) this.initFlorsInfo();
		else
			if (type.equalsIgnoreCase("MDP")) this.initMDPInfo();
			else
			{
				System.err.println("Unknown model info type: " + type);
				System.exit(0);
			}
		this.setModelFile(this.getModelFilePrefix()+this.getSolver()+".txt");
	}
	
	public ModelInfo(String type, int windowSize){
		if (type.equalsIgnoreCase("FLORS")) this.initFlorsInfo();
		else
			if (type.equalsIgnoreCase("MDP")) this.initMDPInfo();
			else
			{
				System.err.println("Unknown model info type: " + type);
				System.exit(0);
			}
		this.setModelFile(this.getModelFilePrefix()+this.getSolver()+".txt");
		this.setWindowSize(windowSize);
	}
	
	public void initFlorsInfo(){
		this.setSolver(SolverType.L2R_LR);
		this.setC(1.0);
		this.setEps(0.01);
	}
	
	public void initMDPInfo(){
		this.setSolver(SolverType.MCSVM_CS);
		this.setC(0.1);
		this.setEps(0.3);
	}
	
	public String toString(){
		String output ="ModelInfo:\n";
		output += "Solver: " + this.getSolver()+"\n";
		output += "C: " + this.getC()+"\n";
		output += "Eps: " + this.getEps()+"\n";
		output += "ModelFilePrefix: " + this.getModelFilePrefix()+"\n";
		output += "ModelFileNames: " + this.getModelFile()+"\n";
		output += "WindowSize: " + this.getWindowSize()+"\n";
		return output;
		
	}

}
