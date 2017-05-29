package de.dfki.mlt.gnt.trainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.data.Alphabet;
import de.dfki.mlt.gnt.data.Data;
import de.dfki.mlt.gnt.data.GlobalParams;
import de.dfki.mlt.gnt.data.ModelInfo;
import de.dfki.mlt.gnt.data.OffSets;
import de.dfki.mlt.gnt.data.Window;

/**
 * <pre>
 * <code>
 * General usage of API (from http://liblinear.bwaldvogel.de/ & https://github.com/bwaldvogel/liblinear-java):
 *
 * Problem problem = new Problem();
 * problem.l = ... // number of training examples
 * problem.n = ... // number of features
 * problem.x = ... // feature nodes
 * problem.y = ... // target values
 *
 * SolverType solver = SolverType.L2R_LR; // -s 0
 * double cost = 1.0;    // cost of constraints violation
 * double eps = 0.01; // stopping criteria; influences number of iterations performed, the higher the less
 *
 * Parameter parameter = new Parameter(solver, cost, eps);
 * Model model = Linear.train(problem, parameter);
 * File modelFile = new File("model");
 * model.save(modelFile);
 * // load model or use it directly
 * model = Model.load(modelFile);
 *
 * Feature[] instance = { new FeatureNode(1, 4), new FeatureNode(2, 2) };
 * double prediction = Linear.predict(model, instance);
 *
 * My idea is to create directly a FeatureNode list from a training instance
 * by using the relative indices from the alphabet and using corresponding offsets.
 * In order to do so, I need the tokenVectorSize in advance (non-incremental version) or
 * I need to create an intermediate representation with window-size many sublists of sublist (for the token
 * feature parts) with relative indices, for which I then create the final one (incremental version);
 * such a intermediate representation should be useful for testing anyway.
 * </code>
 * </pre>
 *
 * @author Günter Neumann, DFKI
 */
public class TrainerInMem {

  private static boolean debug = false;
  private Data data = new Data();
  private Alphabet alphabet = new Alphabet();
  private Archivator archivator;
  private OffSets offSets = new OffSets();
  private GlobalParams globalParams = new GlobalParams();
  private int windowSize = 2;
  private ModelInfo modelInfo = new ModelInfo();

  // API/Values for Liblinear
  // GN: biased -> used in Problem() -> if => 0 add extra feature
  private double bias = -1;
  // GN: default values as used in Flors
  // cost -> cost of constraints violation
  // eps -> stopping criteria; influences number of iterations performed, the higher the less

  private Parameter parameter = new Parameter(SolverType.L2R_LR, 1.0, 0.01);

  private Problem problem = new Problem();

  //  public TrainerInMem (){
  //  }
  //
  //  public TrainerInMem (int windowSize){
  //    this.setWindowSize(windowSize);
  //  }
  //
  //  public TrainerInMem (ModelInfo modelInfo, int windowSize){
  //    this.setWindowSize(windowSize);
  //    this.setModelInfo(modelInfo);
  //    this.setData(new Data());
  //
  //    this.setParameter(new Parameter(
  //        modelInfo.getSolver(),
  //        modelInfo.getCost(),
  //        modelInfo.getEps()));
  //  }


  public TrainerInMem(Archivator archivator, ModelInfo modelInfo, Alphabet alphabet, GlobalParams globals,
      int windowSize) {
    this.setGlobalParams(globals);
    this.setWindowSize(windowSize);
    this.setModelInfo(modelInfo);
    this.setAlphabet(alphabet);
    this.setData(new Data());
    this.setArchivator(archivator);

    this.setParameter(new Parameter(
        modelInfo.getSolver(),
        modelInfo.getCost(),
        modelInfo.getEps()));
  }


  public static void setDebug(boolean debug) {

    TrainerInMem.debug = debug;
  }


  public static boolean getDebug() {

    return TrainerInMem.debug;
  }


  public Data getData() {

    return this.data;
  }


  public void setData(Data data) {

    this.data = data;
  }


  public ModelInfo getModelInfo() {

    return this.modelInfo;
  }


  public void setModelInfo(ModelInfo modelInfo) {

    this.modelInfo = modelInfo;
  }


  public Alphabet getAlphabet() {

    return this.alphabet;
  }


  public void setAlphabet(Alphabet alphabet) {

    this.alphabet = alphabet;
  }


  public Archivator getArchivator() {

    return this.archivator;
  }


  public void setArchivator(Archivator archivator) {

    this.archivator = archivator;
  }


  public OffSets getOffSets() {

    return this.offSets;
  }


  public void setOffSets(OffSets offSets) {

    this.offSets = offSets;
  }


  public int getWindowSize() {

    return this.windowSize;
  }


  public void setWindowSize(int windowSize) {

    this.windowSize = windowSize;
  }


  public double getBias() {

    return this.bias;
  }


  public void setBias(double bias) {

    this.bias = bias;
  }


  public Parameter getParameter() {

    return this.parameter;
  }


  public void setParameter(Parameter parameter) {

    this.parameter = parameter;
  }


  public Problem getProblem() {

    return this.problem;
  }


  public void setProblem(Problem problem) {

    this.problem = problem;
  }


  public GlobalParams getGlobalParams() {

    return this.globalParams;
  }


  public void setGlobalParams(GlobalParams globalParams) {

    this.globalParams = globalParams;
  }


  /*
   * training steps
   * - init corpus
   * - init alphabet
   * - init window
   * - init offsets
   * - init liblinear (non-incremental version, since needs feature vector size)
   *
   * - readConllTrainingFile
   * - create sentence object
   *
   * - loop through sentence object
   * - create training instance frames and store
   * - after all frames have been computed
   * - create feature vectors
   * - add to problem
   * - do training
   * - save model file
   */


  /**
   * For each token of a sentence create a window frame, add the label of the current sentence token wrt the window
   * and store it in Data instances.
   * The latter is a global storage and stores all frames first, before the windows are filled.
   * I do this because I do not know in advance the number of sentences and hence, the number of tokens in a file.
   * @throws IOException
   */
  private void createWindowFramesFromSentence() {

    // for each token t_i of current training sentence do
    // System.out.println("Sentence no: " + data.getSentenceCnt());
    int mod = 100000;
    for (int i = 0; i < this.getData().getSentence().getWordArray().length; i++) {
      int labelIndex = this.getData().getSentence().getLabelArray()[i];
      // create local context for tagging t_i of size 2*windowSize+1 centered around t_i

      Window tokenWindow = new Window(this.getData().getSentence(), i, this.windowSize, this.data, this.alphabet);
      tokenWindow.setLabelIndex(labelIndex);

      this.getData().getInstances().add(tokenWindow);

      // Print how many windows are created so far, and pretty print every mod-th window
      if ((Window.getWindowCnt() % mod) == 0) {
        System.out.println("\n************");
        System.out.println("# Window instances: " + Window.getWindowCnt());
      }
    }
  }


  /**
   * Loops through a file where each line is conll encoded, collect tokens to a sentence
   * and call windows creator on sentence.
   * Steps involved:
   * <p>- collect conll tokens in list
   * <p>- create internal sentence object and label maps
   * <p>- create window frames and store in list (non-feature filled windows):
   *      I do this, because each window is then filled iteratively when calling the trainer; it actually saves space
   * <p>-  Finally, feature files for label set and word set lists are created and stored for taggerName
   * @param conllReader
   * @param max if -1 then infinite else max sentences are processed and then methods stops
   * @throws IOException
   */
  private void createTrainingInstancesFromConllReader(BufferedReader conllReader, int max) throws IOException {

    String line = "";
    List<String[]> tokens = new ArrayList<String[]>();

    while ((line = conllReader.readLine()) != null) {
      if (line.isEmpty()) {
        if ((max > 0) && (this.data.getSentenceCnt() > max)) {
          break;
        }

        // create internal sentence object and label maps
        this.data.generateSentenceObjectFromConllLabeledSentence(tokens);

        // System.out.println("In:  " + this.taggedSentenceToString());

        // create window frames and store in list
        createWindowFramesFromSentence();

        // reset tokens
        tokens = new ArrayList<String[]>();
      } else {
        String[] tokenizedLine = line.split("\t");
        tokens.add(tokenizedLine);
      }
    }
    conllReader.close();
    this.data.saveLabelSet();
    this.data.saveWordSet();
    System.out.println("... done");
  }


  /**
   * Initialize problem for liblinear using
   * Window.windowCnt for problem.l (training instance size)
   * OffSets.windowVectorSize for problem.n (OffSets.tokenVectorSize*windowSize+1)
   */
  private void initProblem() {

    this.problem = new Problem();
    this.problem.l = Window.getWindowCnt();
    //problem.n = OffSets.windowVectorSize;
    this.problem.x = new FeatureNode[this.problem.l][];
    this.problem.y = new double[this.problem.l];
    this.problem.bias = this.getBias();

    System.out.println("problem.l: " + this.problem.l);
    System.out.println("problem.n: " + this.problem.n);
    System.out.println("problem.y.size: " + this.problem.y.length);
    System.out.println("problem.x.size: " + this.problem.x.length);
  }


  /**
   * Loop through all window frames. Fill the window, adjust the feature indices
   * and create a feature vector for the filled window.
   * This is directly add to problem.x, where the corresponding label of the window
   * is added to problem.y
   * @param train
   * @param adjust
   * @throws IOException
   */
  private void constructProblem(boolean train, boolean adjust) {

    int mod = 10000;
    int problemCnt = 0;

    // Initialize problem with potential feature vector size and number of training instances
    // and size of x and y which uses training instance
    // current element has index i
    this.initProblem();

    for (int i = 0; i < this.data.getInstances().size(); i++) {
      Window nextWindow = this.data.getInstances().get(i);
      nextWindow.setOffSets(this.getOffSets());
      nextWindow.fillWindow(train, adjust);
      ProblemInstance problemInstance = new ProblemInstance();
      problemInstance.createProblemInstanceFromWindow(nextWindow);
      problemCnt++;

      this.getProblem().y[i] = nextWindow.getLabelIndex();
      this.getProblem().x[i] = problemInstance.getFeatureVector();

      if (this.getGlobalParams().isSaveModelInputFile()) {
        problemInstance.saveProblemInstance(
            this.getModelInfo().getModelInputFileWriter(),
            nextWindow.getLabelIndex());
      }

      nextWindow.clean();


      // Print how many problems are created so far
      if ((problemCnt % mod) == 0) {
        System.out.println("************");
        System.out.println("Problem instances created: " + problemCnt);
      }
    }

    // Number of feature can be set here, because we know the number of examples now.
    System.out.println("Window lenght: " + this.getProblem().x[0].length);
    this.getProblem().n = this.getOffSets().getWindowVectorSize();

  }


  /**
   * The wrapper to liblinear trainer.
   * @throws IOException
   */
  private void checkProblem() {

    for (Feature[] nodes : this.getProblem().x) {

      if (nodes == null) {
        System.out.println("shit!!");
        continue;
      }
      int indexBefore = 0;
      for (Feature n : nodes) {
        if (n.getIndex() <= indexBefore) {
          throw new IllegalArgumentException("feature nodes must be sorted by index in ascending order");
        }
      }
    }
  }


  private void runLiblinearTrainer() throws IOException {

    long time1;
    long time2;
    Linear.disableDebugOutput();
    time1 = System.currentTimeMillis();
    System.out.println("problem.n: " + this.getProblem().n);
    System.out.println("Test problem ");
    this.checkProblem();
    System.out.println("Do training:");
    Model model = Linear.train(this.getProblem(), this.getParameter());
    time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (time2 - time1));

    String modelFileName = GlobalConfig.getModelBuildFolder().resolve(
        this.modelInfo.getModelName() + ".txt").toString();
    System.out.println("Save  model file: " + modelFileName);
    time1 = System.currentTimeMillis();
    model.save(new File(modelFileName));
    time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (time2 - time1));
  }


  /**
   * Main pipeline for training a liblinear model from a training file with conll encoded labeled
   * examples.
   * @param sourceFileName
   * @param max
   * @throws IOException
   */
  //TODO: currently runs only a single training file
  public void trainFromConllTrainingFileInMemory(String sourceFileName, int max)
      throws IOException {

    long time1;
    long time2;

    BufferedReader conllReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(sourceFileName), "UTF-8"));
    boolean train = true;
    boolean adjust = true;

    System.out.println("Do training with TrainerInMem() from file: " + sourceFileName);
    System.out.println("Train?: " + train + " Adjust?: " + adjust);
    System.out.println(this.getModelInfo().toString());

    // Read training data
    time1 = System.currentTimeMillis();
    System.out.println("Create conll training instances ...");
    this.createTrainingInstancesFromConllReader(conllReader, max);
    time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (time2 - time1));

    this.getOffSets().initializeOffsets(
        this.getAlphabet(), this.getData(), this.getWindowSize());

    System.out.println("Offsets: " + this.getOffSets().toString());
    System.out.println("Sentences: " + this.getData().getSentenceCnt());
    System.out.println("Feature instances size: " + this.getOffSets().getWindowVectorSize());
    System.out.println("Training instances: " + Window.getWindowCnt());

    // Construct training problem
    System.out.println("Construct problem:");
    time1 = System.currentTimeMillis();
    this.constructProblem(train, adjust);
    time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (time2 - time1));

    System.out.println("Average window vector lenght: " + ProblemInstance.getCumLength() / Window.getWindowCnt());
    System.out.println("Approx. GB needed: "
        + ((ProblemInstance.getCumLength() / Window.getWindowCnt()) * Window.getWindowCnt() * 8 + Window.getWindowCnt())
            / 1000000000.0);

    // Do learning
    /*
     * If ModelInfo.saveModelInputFile=true, then close model input file stream
     * but do not do training; useful if liblinear should be run directly from shell, e.g., using the C-implementation
     */
    // NOTE this is the only place, where I make use of the model input file
    if (this.getGlobalParams().isSaveModelInputFile()) {
      time1 = System.currentTimeMillis();
      // Close the model input file writer buffer
      this.getModelInfo().getModelInputFileWriter().close();
      time2 = System.currentTimeMillis();
      System.out.println("Complete time for creating  and writing model input file (msec): " + (time2 - time1));
    } else {
      // ELSE DO training with java library
      time1 = System.currentTimeMillis();
      this.runLiblinearTrainer();
      time2 = System.currentTimeMillis();
      System.out.println("Complete time for training and writing model (msec): " + (time2 - time1));
    }

    // pack all files in model build folder
    Files.walkFileTree(GlobalConfig.getModelBuildFolder(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (!attrs.isDirectory()) {
          System.out.println(" add to archive: " + path);
          getArchivator().getFilesToPack().add(path.toString());
        }
        return FileVisitResult.CONTINUE;
      }
    });
    this.getArchivator().pack();
    System.out.println("Pack archive: " + this.getArchivator().getArchiveName());
    System.out.println("... Done!");
  }


  // Printing helpers
  public String taggedSentenceToString() {

    String output = "";
    int mod = 10;
    int cnt = 0;
    for (int i = 0; i < this.getData().getSentence().getWordArray().length; i++) {
      output += this.getData().getWordSet().getNum2label().get(this.getData().getSentence().getWordArray()[i]) + "/"
          + this.getData().getLabelSet().getNum2label().get(this.getData().getSentence().getLabelArray()[i]) + " ";
      cnt++;
      if ((cnt % mod) == 0) {
        output += "\n";
      }
    }
    return output;
  }
}
