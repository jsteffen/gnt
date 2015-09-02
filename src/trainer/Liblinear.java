package trainer;

import java.io.File;
import java.util.List;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

/**
 * General usage of API (from http://liblinear.bwaldvogel.de/ & https://github.com/bwaldvogel/liblinear-java):

	Problem problem = new Problem();
	problem.l = ... // number of training examples
	problem.n = ... // number of features
	problem.x = ... // feature nodes
	problem.y = ... // target values

	SolverType solver = SolverType.L2R_LR; // -s 0
	double C = 1.0;    // cost of constraints violation
	double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less

	Parameter parameter = new Parameter(solver, C, eps);
	Model model = Linear.train(problem, parameter);
	File modelFile = new File("model");
	model.save(modelFile);
	// load model or use it directly
	model = Model.load(modelFile);

	Feature[] instance = { new FeatureNode(1, 4), new FeatureNode(2, 2) };
	double prediction = Linear.predict(model, instance);
 * @author gune00
 *
 */

public class Liblinear {
	// GN: used in MDP
	private double    bias             = -1;
	
	// GN: default values as used in Flors
	private Parameter parameter = new Parameter(SolverType.L2R_LR, 1.0, 0.01);
	// GN: used in MDP
	private Parameter parameterMDP = new Parameter(SolverType.MCSVM_CS, 0.1, 0.3);
	
	File modelFile = new File("model");
	
	public Liblinear(String modelFile){
		this.modelFile = new File(modelFile);
	}
	
	// from de.dfki.lt.mdparser.parser.Trainer.constructProblem(List<Integer>, List<FeatureNode[]>, int)
	// Not sure I need this
	public  Problem constructProblem(List<Integer> vy, List<FeatureNode[]> vx, int max_index) {
		Problem prob = new Problem();
		prob.bias = bias;
		prob.l = vy.size();
		prob.n = max_index;
		if (bias >= 0) {
			prob.n++;
		}
		prob.x = new FeatureNode[prob.l][];
		for (int i = 0; i < prob.l; i++) {
			prob.x[i] = vx.get(i);

			if (bias >= 0) {
				assert prob.x[i][prob.x[i].length - 1] == null;
				prob.x[i][prob.x[i].length - 1] = new FeatureNode(max_index + 1, bias);
			} else {
				assert prob.x[i][prob.x[i].length - 1] != null;
			}
		}
		
		// difference: double[] instead of int[]
		prob.y = new double[prob.l];
		for (int i = 0; i < prob.l; i++)
			prob.y[i] = vy.get(i);

		return prob;
	}
}
