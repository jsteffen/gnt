package de.dfki.mlt.gnt.tagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.corpus.Corpus;
import de.dfki.mlt.gnt.corpus.GNTcorpusProperties;
import de.dfki.mlt.gnt.data.Alphabet;
import de.dfki.mlt.gnt.data.Data;
import de.dfki.mlt.gnt.data.GNTdataProperties;
import de.dfki.mlt.gnt.data.ModelInfo;
import de.dfki.mlt.gnt.data.OffSets;
import de.dfki.mlt.gnt.data.Sentence;
import de.dfki.mlt.gnt.data.Window;
import de.dfki.mlt.gnt.trainer.ProblemInstance;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class GNTagger {

  private static long tokenPersec = 0;

  private Data data = new Data();
  private Alphabet alphabet = new Alphabet();
  private ModelInfo modelInfo = new ModelInfo();
  private Corpus corpus = new Corpus();
  private OffSets offSets = new OffSets();
  private int windowSize = 2;
  private Model model;
  private Archivator archivator;
  private GNTdataProperties dataProps;

  private long time1;
  private long time2;


  public GNTagger() {
  }

  //  public GNTagger(ModelInfo modelInfo) throws IOException {
  //    this.setModelInfo(modelInfo);
  //    this.setData(new Data());
  //    modelInfo.createModelFileName(GlobalParams.windowSize, GlobalParams.dim, GlobalParams.numberOfSentences);
  //    this.setArchivator(new Archivator(modelInfo.getModelFileArchive()));
  //
  //    System.out.println("Extract archive ...");
  //    this.getArchivator().extract();
  //  }


  //  public GNTagger(ModelInfo modelInfo, GNTcorpusProperties props) throws IOException {
  //    this.setModelInfo(modelInfo);
  //    this.setData(new Data());
  //    this.corpus = new Corpus(props);
  //    System.out.println(this.getAlphabet().toActiveFeatureString());
  //
  //    modelInfo.createModelFileName(GlobalParams.windowSize, GlobalParams.dim, GlobalParams.numberOfSentences);
  //    System.out.println(modelInfo.toString());
  //
  //
  //    this.setArchivator(new Archivator(modelInfo.getModelFileArchive()));
  //    System.out.println("Extract archive ...");
  //    this.getArchivator().extract();
  //  }

  public GNTagger(String archiveName)
      throws IOException {

    this.setArchivator(new Archivator(archiveName));
    System.out.println("Extract archive ...");
    this.getArchivator().extract();
    System.out.println("Set dataProps ...");
    this.setDataProps(
        new GNTdataProperties(this.getArchivator().getArchiveMap().get(
            GlobalConfig.getModelBuildFolder().resolve(GlobalConfig.MODEL_CONFIG_FILE).toString())));
    this.setAlphabet(this.getDataProps().getAlphabet());

    this.setModelInfo(this.getDataProps().getModelInfo());
    this.setData(new Data());

    this.modelInfo.createModelFileName(this.dataProps.getGlobalParams().getWindowSize(),
        this.dataProps.getGlobalParams().getDim(),
        this.dataProps.getGlobalParams().getNumberOfSentences(),
        this.dataProps.getAlphabet(),
        this.dataProps.getGlobalParams());
  }


  public GNTagger(String archiveName, GNTcorpusProperties props)
      throws IOException {

    this.setArchivator(new Archivator(archiveName));
    System.out.println("Extract archive ...");
    this.getArchivator().extract();
    System.out.println("Set dataProps ...");
    System.out.println(GlobalConfig.getModelBuildFolder().resolve(GlobalConfig.MODEL_CONFIG_FILE));
    this.setDataProps(
        new GNTdataProperties(
            this.getArchivator().getArchiveMap().get(
                GlobalConfig.getModelBuildFolder().resolve(GlobalConfig.MODEL_CONFIG_FILE).toString())));
    this.setAlphabet(this.getDataProps().getAlphabet());

    this.setModelInfo(this.getDataProps().getModelInfo());
    this.setData(new Data());
    this.corpus = new Corpus(props, this.getDataProps().getGlobalParams());

    this.modelInfo.createModelFileName(this.dataProps.getGlobalParams().getWindowSize(),
        this.dataProps.getGlobalParams().getDim(),
        this.dataProps.getGlobalParams().getNumberOfSentences(),
        this.dataProps.getAlphabet(),
        this.dataProps.getGlobalParams());

  }


  public GNTdataProperties getDataProps() {

    return this.dataProps;
  }


  public void setDataProps(GNTdataProperties dataProps) {

    this.dataProps = dataProps;
  }


  public Archivator getArchivator() {

    return this.archivator;
  }


  public void setArchivator(Archivator archivator) {

    this.archivator = archivator;
  }


  public Data getData() {

    return this.data;
  }


  public void setData(Data data) {

    this.data = data;
  }


  public Alphabet getAlphabet() {

    return this.alphabet;
  }


  public void setAlphabet(Alphabet alphabet) {

    this.alphabet = alphabet;
  }


  public ModelInfo getModelInfo() {

    return this.modelInfo;
  }


  public void setModelInfo(ModelInfo modelInfo) {

    this.modelInfo = modelInfo;
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


  public Model getModel() {

    return this.model;
  }


  public void setModel(Model model) {

    this.model = model;
  }


  public Corpus getCorpus() {

    return this.corpus;
  }


  public void setCorpus(Corpus corpus) {

    this.corpus = corpus;
  }


  public void initGNTagger(int windowSizeParam, int dim) throws UnsupportedEncodingException, IOException {

    this.time1 = System.currentTimeMillis();

    System.out.println("Set window size: " + windowSizeParam);
    this.setWindowSize(windowSizeParam);
    System.out.println("Set window count: ");
    Window.setWindowCnt(0);
    GNTagger.tokenPersec = 0;

    System.out.println("Load feature files with dim: " + dim);
    this.getAlphabet().loadFeaturesFromFiles(this.getArchivator(), dim);

    System.out.println("Load label set from archive: " + this.getData().getLabelMapPath());
    this.getData().readLabelSet(this.getArchivator());

    System.out.println("Cleaning non-used variables in Alphabet and in Data:");
    this.getAlphabet().clean();
    this.getData().cleanWordSet();

    System.out.println("Initialize offsets:");
    this.getOffSets().initializeOffsets(
        this.getAlphabet(), this.getData(), this.getWindowSize());
    System.out.println("\t" + this.getOffSets().toString());

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1) + "\n");

    this.time1 = System.currentTimeMillis();

    System.out.println("Load model file from archive: " + this.getModelInfo().getModelName() + ".txt");

    //this.setModel(Model.load(new File(this.getModelInfo().getModelFile())));
    this.setModel(Linear.loadModel(
        new InputStreamReader(
            this.getArchivator().getArchiveMap().get(
                GlobalConfig.getModelBuildFolder().resolve(
                    this.getModelInfo().getModelName() + ".txt").toString()),
            "UTF-8")));
    System.out.println(".... DONE!");

    this.time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (this.time2 - this.time1));
    System.out.println(this.getModel().toString() + "\n");
  }


  // the same as trainer.TrainerInMem.createWindowFramesFromSentence()!
  private void createWindowFramesFromSentence() {

    // for each token t_i of current training sentence do
    // System.out.println("Sentence no: " + data.getSentenceCnt());
    int mod = 100000;
    for (int i = 0; i < this.getData().getSentence().getWordArray().length; i++) {
      // Assume that both arrays together define an ordered one-to-one correspondence
      // between token and label (POS)
      int labelIndex = this.getData().getSentence().getLabelArray()[i];

      // create local context for tagging t_i of size 2*windowSize+1 centered around t_i
      Window tokenWindow = new Window(this.getData().getSentence(), i, this.windowSize, this.data, this.alphabet);
      // This basically has no effect during tagging
      tokenWindow.setLabelIndex(labelIndex);

      this.getData().getInstances().add(tokenWindow);

      // Print how many windows are created so far, and pretty print every mod-th window
      if ((Window.getWindowCnt() % mod) == 0) {
        System.out.println("# Window instances: " + Window.getWindowCnt());
      }
    }
  }


  /**
   * Iterate through all window frames:
   * - create the feature vector: train=false means: handle unknown words; adjust=true: means adjust feature indices
   * - create a problem instance -> mainly the feature vector
   * - and call the learner with model and feature vector
   * - save the predicted label in the corresponding field of the word in the sentence.
   *
   * Mainly the same as trainer.TrainerInMem.constructProblem(train, adjust), but uses predictor
   */
  private void constructProblemAndTag(boolean train, boolean adjust) {

    int prediction = 0;

    for (int i = 0; i < this.data.getInstances().size(); i++) {
      // For each window frame of a sentence
      Window nextWindow = this.data.getInstances().get(i);
      // Fill the frame with all available features. First boolean sets
      // training mode to false which means that unknown words are handled.
      nextWindow.setOffSets(this.getOffSets());
      nextWindow.fillWindow(train, adjust);
      // Create the feature vector
      ProblemInstance problemInstance = new ProblemInstance();
      problemInstance.createProblemInstanceFromWindow(nextWindow);

      if (GlobalConfig.getBoolean(ConfigKeys.CREATE_LIBLINEAR_INPUT_FILE)) {
        problemInstance.saveProblemInstance(
            this.getModelInfo().getModelInputFileWriter(),
            nextWindow.getLabelIndex());
      } else {
        // Call the learner to predict the label
        prediction = (int)Linear.predict(this.getModel(), problemInstance.getFeatureVector());
        /*
        System.out.println(
            "Word: " + this.getData().getWordSet().getNum2label().get(this.getData().getSentence().getWordArray()[i])
                + "\tPrediction: " + this.getData().getLabelSet().getNum2label().get(prediction));
        */

        //  Here, I am assuming that sentence length equals # of windows
        // So store predicted label i to word i
        this.getData().getSentence().getLabelArray()[i] = prediction;
      }

      // Free space by resetting filled window to unfilled-window
      nextWindow.clean();
    }
  }


  public void tagSentenceObject() {

    // create window frames from sentence and store in list
    this.createWindowFramesFromSentence();

    // create feature vector instance for each window frame and tag
    this.constructProblemAndTag(false, true);

    // reset instances - need to do this here, because learner is called directly on windows
    this.getData().cleanInstances();
  }


  /**
   * A method for tagging a single sentence given as list of tokens.
   * @param tokens
   */
  public void tagUnlabeledTokens(List<String> tokens) {

    // create internal sentence object
    this.getData().generateSentenceObjectFromUnlabeledTokens(tokens);

    // tag sentence object
    this.tagSentenceObject();
  }


  /**
   * A simple print out of a sentence in form of list of word/tag
   * @return
   */
  public String taggedSentenceToString() {

    StringBuilder output = new StringBuilder();
    Sentence sentence = this.getData().getSentence();
    for (int i = 0; i < sentence.getWordArray().length; i++) {
      String word = this.getData().getWordSet().getNum2label().get(sentence.getWordArray()[i]);
      String label = this.getData().getLabelSet().getNum2label().get(sentence.getLabelArray()[i]);

      label = PostProcessor.determineTwitterLabel(word, label);

      output.append(word + "/" + label + " ");
    }
    return output.toString();
  }


  private Path tagAndWriteSentencesFromConllReader(String sourceFileName, int max)
      throws IOException {

    Path sourcePath = Paths.get(sourceFileName);
    String evalFileName = sourcePath.getFileName().toString();
    evalFileName = evalFileName.substring(0, evalFileName.lastIndexOf(".")) + ".txt";
    Path evalPath = GlobalConfig.getPath(ConfigKeys.EVAL_FOLDER).resolve(evalFileName);
    Files.createDirectories(evalPath.getParent());
    System.out.println("Create eval file: " + evalPath);

    try (BufferedReader conllReader = Files.newBufferedReader(sourcePath, StandardCharsets.UTF_8);
        PrintWriter conllWriter = new PrintWriter(Files.newBufferedWriter(evalPath, StandardCharsets.UTF_8))) {

      String line = "";
      List<String[]> tokens = new ArrayList<String[]>();

      while ((line = conllReader.readLine()) != null) {
        if (line.isEmpty()) {
          // For found sentence, do tagging:
          // Stop if max sentences have been processed
          if ((max > 0) && (this.data.getSentenceCnt() > max)) {
            break;
          }

          // create internal sentence object and label maps
          // Use specified label from conll file for evaluation purposes later
          this.data.generateSentenceObjectFromConllLabeledSentence(tokens);

          // tag sentence object
          this.tagSentenceObject();

          // Create conlleval consistent output using original conll tokens plus predicted labels
          this.writeTokensAndWithLabels(conllWriter, tokens, this.data.getSentence());

          // reset tokens
          tokens = new ArrayList<String[]>();
        } else {
          // Collect all the words of a conll sentence
          String[] tokenizedLine = line.split("\t");
          tokens.add(tokenizedLine);
        }
      }
    }

    return evalPath;
  }


  // NOTE:
  // I have adjusted the NER conll format to be consistent with the other conll formats, i.e.,
  // LONDON NNP I-NP I-LOC -> 1  LONDON  NNP  I-NP  I-LOC
  // This is why I have 5 elements instead of 4
  private void writeTokensAndWithLabels(PrintWriter conllWriter,
      List<String[]> tokens, Sentence sentence) {

    for (int i = 0; i < tokens.size(); i++) {
      String[] token = tokens.get(i);
      String label = this.getData().getLabelSet().getNum2label().get(sentence.getLabelArray()[i]);

      String word = token[Data.getWordFormIndex()];

      label = PostProcessor.determineTwitterLabel(word, label);


      String newConllToken = token[0] + " "
          + word + " "
          + token[Data.getPosTagIndex()] + " "
          + label;

      conllWriter.println(newConllToken);
    }
    conllWriter.println();
  }


  // This is the current main caller for the GNTagger
  public Path tagAndWriteFromConllDevelFile(String sourceFileName, int sentenceCnt)
      throws IOException {

    long localTime1;
    long localTime2;

    // Set the writer buffer for the model input file based on the given sourceFileName
    if (GlobalConfig.getBoolean(ConfigKeys.CREATE_LIBLINEAR_INPUT_FILE)) {
      String fileName = new File(sourceFileName).getName();
      Path libLinearInputPath =
          GlobalConfig.getPath(ConfigKeys.MODEL_OUTPUT_FOLDER)
          .resolve("liblinear_input_" + fileName + ".txt");
      if (libLinearInputPath.getParent() != null) {
        Files.createDirectories(libLinearInputPath.getParent());
      }
      this.getModelInfo().setModelInputFileWriter(
          Files.newBufferedWriter(libLinearInputPath, StandardCharsets.UTF_8));
    }
    System.out.println("\n++++\nDo testing from file: " + sourceFileName);
    // Reset some data to make sure each file has same change
    this.getData().setSentenceCnt(0);
    Window.setWindowCnt(0);

    localTime1 = System.currentTimeMillis();

    Path evalPath = this.tagAndWriteSentencesFromConllReader(sourceFileName, sentenceCnt);

    if (GlobalConfig.getBoolean(ConfigKeys.CREATE_LIBLINEAR_INPUT_FILE)) {
      this.getModelInfo().getModelInputFileWriter().close();
    }

    localTime2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (localTime2 - localTime1));

    GNTagger.tokenPersec = (Window.getWindowCnt() * 1000) / (localTime2 - localTime1);
    System.out.println("Sentences: " + this.getData().getSentenceCnt());
    System.out.println("Testing instances: " + Window.getWindowCnt());
    System.out.println("Sentences/sec: " + (this.getData().getSentenceCnt() * 1000) / (localTime2 - localTime1));
    System.out.println("Words/sec: " + GNTagger.tokenPersec);

    return evalPath;
  }
}
