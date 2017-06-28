package de.dfki.mlt.gnt.tagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;

import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.dfki.mlt.gnt.archive.Archivator;
import de.dfki.mlt.gnt.config.ConfigKeys;
import de.dfki.mlt.gnt.config.CorpusConfig;
import de.dfki.mlt.gnt.config.GlobalConfig;
import de.dfki.mlt.gnt.config.ModelConfig;
import de.dfki.mlt.gnt.corpus.ConllEvaluator;
import de.dfki.mlt.gnt.data.Alphabet;
import de.dfki.mlt.gnt.data.Data;
import de.dfki.mlt.gnt.data.OffSets;
import de.dfki.mlt.gnt.data.Sentence;
import de.dfki.mlt.gnt.data.Window;
import de.dfki.mlt.gnt.tokenizer.GntSimpleTokenizer;
import de.dfki.mlt.gnt.trainer.ProblemInstance;

/**
 *
 *
 * @author Günter Neumann, DFKI
 */
public class GNTagger {

  private Data data;
  private Alphabet alphabet;
  private OffSets offSets;
  private int windowSize = 2;
  private Model model;
  private Archivator archivator;
  private ModelConfig modelConfig;


  public GNTagger(String modelArchiveName)
      throws IOException, ConfigurationException {

    this.archivator = new Archivator(modelArchiveName);
    System.out.println("Extract archive ...");
    this.archivator.extract();
    System.out.println("Set dataProps ...");
    this.modelConfig =
        ModelConfig.create(
            this.archivator.getArchiveMap().get(
                GlobalConfig.getModelBuildFolder().resolve(GlobalConfig.MODEL_CONFIG_FILE).toString()));
    this.alphabet = new Alphabet(this.modelConfig);

    this.data = new Data();

    initGNTagger(this.modelConfig.getInt(ConfigKeys.WINDOW_SIZE), this.modelConfig.getInt(ConfigKeys.DIM));
  }


  private void initGNTagger(int windowSizeParam, int dim)
      throws UnsupportedEncodingException, IOException {

    long time1 = System.currentTimeMillis();

    System.out.println("Set window size: " + windowSizeParam);
    this.windowSize = windowSizeParam;
    System.out.println("Set window count: ");
    Window.setWindowCnt(0);

    System.out.println("Load feature files with dim: " + dim);
    this.alphabet.loadFeaturesFromFiles(this.archivator, dim);

    System.out.println("Load label set from archive: " + this.data.getLabelMapPath());
    this.data.readLabelSet(this.archivator);

    System.out.println("Cleaning non-used variables in Alphabet and in Data:");
    this.alphabet.clean();
    this.data.cleanWordSet();

    System.out.println("Initialize offsets:");
    this.offSets = new OffSets(this.alphabet, this.data, this.windowSize);
    System.out.println("\t" + this.offSets.toString());

    long time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (time2 - time1) + "\n");

    time1 = System.currentTimeMillis();

    System.out.println("Load model file from archive: " + this.modelConfig.getModelName() + ".txt");

    //this.setModel(Model.load(new File(this.getModelInfo().getModelFile())));
    this.model = Linear.loadModel(
        new InputStreamReader(
            this.archivator.getArchiveMap().get(
                GlobalConfig.getModelBuildFolder().resolve(
                    this.modelConfig.getModelName() + ".txt").toString()),
            "UTF-8"));
    System.out.println(".... DONE!");

    time2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (time2 - time1));
    System.out.println(this.model.toString() + "\n");
  }


  public Data getData() {

    return this.data;
  }


  /**
   * Tags all files in the given directory.
   *
   * @param inputDir
   * @param inEncode
   * @param outEncode
   * @throws IOException
   */
  public void tagFolder(String inputDirName, String inEncode, String outEncode)
      throws IOException {

    Path inputDirPath = Paths.get(inputDirName);
    try (DirectoryStream<Path> stream =
        Files.newDirectoryStream(inputDirPath, "*.{txt}")) {
      for (Path entry : stream) {
        long time1 = System.currentTimeMillis();

        System.out.println("Tagging file ... " + entry.toString());

        tagFile(entry, inEncode, outEncode);

        long time2 = System.currentTimeMillis();
        System.out.println("System time (msec): " + (time2 - time1));
      }
    }
  }


  /**
   * Tags each line of the given file and saves resulting tagged string in output file.
   * Output file is build from sourceFilename by adding suffix .GNT
   *
   * @param sourceFileName
   * @param inEncode
   * @param outEncode
   * @throws IOException
   */
  public void tagFile(Path sourcePath, String inEncode, String outEncode)
      throws IOException {

    Path resultPath = Paths.get(sourcePath.toString() + ".GNT");
    try (BufferedReader in = Files.newBufferedReader(
        sourcePath, Charset.forName(inEncode));
        PrintWriter out = new PrintWriter(Files.newBufferedWriter(
            resultPath, Charset.forName(outEncode)))) {

      String line;
      while ((line = in.readLine()) != null) {
        if (!line.isEmpty()) {
          List<String> tokens = GntSimpleTokenizer.tokenize(line);
          Sentence sentence = tagUnlabeledTokens(tokens);
          String taggedString = taggedSentenceToString(sentence);
          for (String token : taggedString.split(" ")) {
            out.println(token);
          }
        }
      }
    }
  }


  /**
   * Tags the given string and outputs it in a line-oriented format.
   *
   * @param inputString
   * @return the tagged string
   */
  public String tagString(String inputString) {

    List<String> tokens = GntSimpleTokenizer.tokenize(inputString);

    Sentence sentence = tagUnlabeledTokens(tokens);

    String taggedString = taggedSentenceToString(sentence);

    for (String token : taggedString.split(" ")) {
      System.out.println(token);
    }

    return taggedString;
  }


  /**
   * A method for tagging a single sentence given as list of tokens.
   * @param tokens
   */
  public Sentence tagUnlabeledTokens(List<String> tokens) {

    // create internal sentence object
    Sentence sentence = this.data.generateSentenceObjectFromUnlabeledTokens(tokens);

    // tag sentence object
    this.tagSentenceObject(sentence);

    return sentence;
  }


  private void tagSentenceObject(Sentence sentence) {

    // create window frames from sentence and store in list
    this.createWindowFramesFromSentence(sentence);

    // create feature vector instance for each window frame and tag
    this.constructProblemAndTag(false, true, sentence);

    // reset instances - need to do this here, because learner is called directly on windows
    this.data.cleanInstances();
  }


  // the same as trainer.TrainerInMem.createWindowFramesFromSentence()!
  private void createWindowFramesFromSentence(Sentence sentence) {

    // for each token t_i of current training sentence do
    // System.out.println("Sentence no: " + data.getSentenceCnt());
    int mod = 100000;
    for (int i = 0; i < sentence.getWordArray().length; i++) {
      // Assume that both arrays together define an ordered one-to-one correspondence
      // between token and label (POS)
      int labelIndex = sentence.getLabelArray()[i];

      // create local context for tagging t_i of size 2*windowSize+1 centered around t_i
      Window tokenWindow = new Window(sentence, i, this.windowSize, this.data, this.alphabet);
      // This basically has no effect during tagging
      tokenWindow.setLabelIndex(labelIndex);

      this.data.getInstances().add(tokenWindow);

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
  private void constructProblemAndTag(boolean train, boolean adjust, Sentence sentence) {

    int prediction = 0;

    for (int i = 0; i < this.data.getInstances().size(); i++) {
      // For each window frame of a sentence
      Window nextWindow = this.data.getInstances().get(i);
      // Fill the frame with all available features. First boolean sets
      // training mode to false which means that unknown words are handled.
      nextWindow.setOffSets(this.offSets);
      nextWindow.fillWindow(train, adjust);
      // Create the feature vector
      ProblemInstance problemInstance = new ProblemInstance();
      problemInstance.createProblemInstanceFromWindow(nextWindow);

      // Call the learner to predict the label
      prediction = (int)Linear.predict(this.model, problemInstance.getFeatureVector());
      /*
        System.out.println(
            "Word: " + this.data.getWordSet().getNum2label().get(this.data.getSentence().getWordArray()[i])
                + "\tPrediction: " + this.data.getLabelSet().getNum2label().get(prediction));
       */

      //  Here, I am assuming that sentence length equals # of windows
      // So store predicted label i to word i
      sentence.getLabelArray()[i] = prediction;

      // Free space by resetting filled window to unfilled-window
      nextWindow.clean();
    }
  }


  /**
   * A simple print out of a sentence in form of list of word/tag
   * @return
   */
  private String taggedSentenceToString(Sentence sentence) {

    StringBuilder output = new StringBuilder();
    for (int i = 0; i < sentence.getWordArray().length; i++) {
      String word = this.data.getWordSet().getNum2label().get(sentence.getWordArray()[i]);
      String label = this.data.getLabelSet().getNum2label().get(sentence.getLabelArray()[i]);

      label = PostProcessor.determineTwitterLabel(word, label);

      output.append(word + "/" + label + " ");
    }
    return output.toString();
  }


  /**
   * Evaluates the performance of the tagger using the given annotated corpus.
   *
   * @param corpusConfigFileName
   *          corpus configuration file name
   */
  public ConllEvaluator eval(String corpusConfigFileName) throws IOException, ConfigurationException {

    CorpusConfig corpusConfig = CorpusConfig.create(corpusConfigFileName);

    int wordFormIndex = corpusConfig.getInt(ConfigKeys.WORD_FORM_INDEX);
    int tagIndex = corpusConfig.getInt(ConfigKeys.TAG_INDEX);

    Data wordSetData = new Data();
    wordSetData.readWordSet(this.archivator);

    System.out.println("\n++++\nLoad known vocabulary from archive training for evaluating OOV: "
        + wordSetData.getWordMapPath());
    System.out.println(wordSetData.toString());
    ConllEvaluator evaluator = new ConllEvaluator(wordSetData);

    for (String fileName : corpusConfig.getList(String.class, ConfigKeys.DEV_LABELED_DATA, Collections.emptyList())) {
      Path evalPath = tagAndWriteFromConllDevelFile(fileName, -1, wordFormIndex, tagIndex);
      evaluator.computeAccuracy(evalPath, GlobalConfig.getBoolean(ConfigKeys.DEBUG));
    }
    for (String fileName : corpusConfig.getList(String.class, ConfigKeys.TEST_LABELED_DATA, Collections.emptyList())) {
      Path evalPath = tagAndWriteFromConllDevelFile(fileName, -1, wordFormIndex, tagIndex);
      evaluator.computeAccuracy(evalPath, GlobalConfig.getBoolean(ConfigKeys.DEBUG));
    }

    return evaluator;
  }


  // This is the current main caller for the GNTagger
  private Path tagAndWriteFromConllDevelFile(String sourceFileName, int sentenceCnt, int wordFormIndex, int tagIndex)
      throws IOException {

    long localTime1;
    long localTime2;

    System.out.println("\n++++\nDo testing from file: " + sourceFileName);
    // Reset some data to make sure each file has same change
    this.data.setSentenceCnt(0);
    Window.setWindowCnt(0);

    localTime1 = System.currentTimeMillis();

    Path evalPath = this.tagAndWriteSentencesFromConllReader(sourceFileName, sentenceCnt, wordFormIndex, tagIndex);

    localTime2 = System.currentTimeMillis();
    System.out.println("System time (msec): " + (localTime2 - localTime1));

    long tokenPerSec = (Window.getWindowCnt() * 1000) / (localTime2 - localTime1);
    System.out.println("Sentences: " + this.data.getSentenceCnt());
    System.out.println("Testing instances: " + Window.getWindowCnt());
    System.out.println("Sentences/sec: " + (this.data.getSentenceCnt() * 1000) / (localTime2 - localTime1));
    System.out.println("Words/sec: " + tokenPerSec);

    return evalPath;
  }


  private Path tagAndWriteSentencesFromConllReader(String sourceFileName, int max, int wordFormIndex, int tagIndex)
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
          Sentence sentence =
              this.data.generateSentenceObjectFromConllLabeledSentence(tokens, wordFormIndex, tagIndex);

          // tag sentence object
          this.tagSentenceObject(sentence);

          // Create conlleval consistent output using original conll tokens plus predicted labels
          this.writeTokensAndWithLabels(conllWriter, tokens, sentence, wordFormIndex, tagIndex);

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
  private void writeTokensAndWithLabels(
      PrintWriter conllWriter, List<String[]> tokens, Sentence sentence, int wordFormIndex, int tagIndex) {

    for (int i = 0; i < tokens.size(); i++) {
      String[] token = tokens.get(i);
      String label = this.data.getLabelSet().getNum2label().get(sentence.getLabelArray()[i]);

      String word = token[wordFormIndex];

      label = PostProcessor.determineTwitterLabel(word, label);


      String newConllToken = token[0] + " "
          + word + " "
          + token[tagIndex] + " "
          + label;

      conllWriter.println(newConllToken);
    }
    conllWriter.println();
  }


  /**
   * Closes all streams of embedded archivator.
   */
  public void close() {

    this.archivator.close();
  }
}
