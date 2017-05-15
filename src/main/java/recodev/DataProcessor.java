package recodev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class DataProcessor {

  // The main path to the base directory which hosts the label directories
  private String basePath = "/Users/gune00/dfki/NLP4Software/RecoDevGN/";

  // The name of the base directory
  private String baseDirectory = "eclipse_jdt_new";

  private String liblinearInputFile = "resources/recodev/liblinearInputFile.txt";

  private BufferedWriter writer = null;

  private Data dataObj = new Data();

  private Tokenizer tokenizer = new Tokenizer();

  private TokenNormalizer normalizer = new TokenNormalizer();


  public DataProcessor() {

  }


  public DataProcessor(Tokenizer tokenizer, TokenNormalizer normalizer) {
    this.setTokenizer(tokenizer);
    this.setNormalizer(normalizer);

    try {
      this.setWriter(new BufferedWriter(
          new OutputStreamWriter(
              new FileOutputStream(this.getLiblinearInputFile()), "utf-8")));
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  // setters and getters

  public BufferedWriter getWriter() {

    return this.writer;
  }


  public void setWriter(BufferedWriter writer) {

    this.writer = writer;
  }


  public String getLiblinearInputFile() {

    return this.liblinearInputFile;
  }


  public void setLiblinearInputFile(String liblinearInputFile)
      throws UnsupportedEncodingException, FileNotFoundException {

    this.liblinearInputFile = liblinearInputFile;

    this.setWriter(new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(this.getLiblinearInputFile()), "utf-8")));
  }


  public Tokenizer getTokenizer() {

    return this.tokenizer;
  }


  public void setTokenizer(Tokenizer tokenizer) {

    this.tokenizer = tokenizer;
  }


  public TokenNormalizer getNormalizer() {

    return this.normalizer;
  }


  public void setNormalizer(TokenNormalizer normalizer) {

    this.normalizer = normalizer;
  }


  public String getBasePath() {

    return this.basePath;
  }


  public void setBasePath(String basePath) {

    this.basePath = basePath;
  }


  public String getBaseDirectory() {

    return this.baseDirectory;
  }


  public void setBaseDirectory(String baseDirectory) {

    this.baseDirectory = baseDirectory;
  }


  public Data getDataObj() {

    return this.dataObj;
  }


  public void setDataObj(Data dataObj) {

    this.dataObj = dataObj;
  }


  // Methods


  //*****************************************************************************************************

  /**
   * It receives a string, tokenizes it, normalizes the token and then inserts it into the dictionary set.
   * @param line
   */
  /*
   * TODO
   * check here: different tokenization, normalization, filtering, counting etc on token level
   */
  private void updateDictionaryFromString(String line) {

    // Tokenization
    String[] tokens = this.getTokenizer().tokenizeString(line);

    for (int i = 0; i < tokens.length; i++) {
      // Normalize token
      String normalizedToken = this.getNormalizer().normalize(tokens[i]);
      // Insert and/or update token
      this.getDataObj().updateWordMap(normalizedToken);
    }
  }


  /**
   * it receives a document and processes it line-wise
   * @param document
   * @throws IOException
   */
  private void updateDictionaryFromFile(File document) throws IOException {

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(document), "utf-8"));
    String line = "";
    int lineCnt = 0;
    int mod = 100;

    while ((line = reader.readLine()) != null) {
      this.updateDictionaryFromString(line);
      lineCnt++;
      if ((lineCnt % mod) == 0) {
        System.out.println(lineCnt);
      }
    }

    reader.close();
  }


  /**
   * Get all text files from each label directory; they are used as feature source
   * @param labelDir
   * @throws IOException
   */
  private void processLabelDocs(File labelDir) {

    File[] documentFilesOfLabel = labelDir.listFiles();
    int docId = 0;
    if (documentFilesOfLabel != null) {
      for (File doc : documentFilesOfLabel) {
        System.out.println("\t" + docId + ": " + doc.getName());
        try {
          this.updateDictionaryFromFile(doc);
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }
  }


  /**
   * Get all directory names;
   * Use dir name as label and extend label set;
   * then process all doc files. It is assumed that the file list of dir is flat
   */
  private void processCorpusToCreateDataSets() {

    File labelPath = new File(this.basePath + this.baseDirectory);

    File[] labelDirs = labelPath.listFiles();
    for (File labelDir : labelDirs) {
      if (!labelDir.getName().endsWith(".DS_Store")) {
        //System.out.println("Label name: " + labelDir.getName());
        this.getDataObj().updateLabelMap(labelDir.getName());
        this.processLabelDocs(labelDir);
      }
    }
  }

  //******************************************************************************************************************


  /*
   * Now, that I have the label and word sets, I can create a simple
   * liblinear input file;
   */

  private void updateFeatureMapFromString(String line, FeatureMap featureMap) {

    // Tokenization
    String[] tokens = this.getTokenizer().tokenizeString(line);

    for (int i = 0; i < tokens.length; i++) {
      // Normalize token
      String normalizedToken = this.getNormalizer().normalize(tokens[i]);
      // Get integer encoding of current token of current line of current doc
      int integerCode = this.getDataObj().getWordSet().getLabel2num().get(normalizedToken);
      // Add integerCode plus its value as new liblinear feature-value to featureMap
      // I assume that it will be automatically sorted according to natural order
      featureMap.getFeatureMap().put(integerCode, 1.0);
    }

  }


  private FeatureMap updateFeatureMapFromFile(File doc) throws IOException {

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(doc), "utf-8"));
    FeatureMap featureMap = new FeatureMap();
    String line = "";
    int lineCnt = 0;
    int mod = 100;

    while ((line = reader.readLine()) != null) {
      this.updateFeatureMapFromString(line, featureMap);
      lineCnt++;
      if ((lineCnt % mod) == 0) {
        System.out.println(lineCnt);
      }
    }

    reader.close();
    return featureMap;
  }


  // HIERIX
  // create feature map per document
  // create problem instance from feature map
  // add label and write out problem instance
  private void updateLibLinearInputFile(File doc, String labelName) throws IOException {

    FeatureMap featureMap = this.updateFeatureMapFromFile(doc);
    ProblemInstance problem = new ProblemInstance();

    problem.createProblemInstanceFromWindow(featureMap);

    System.out.println("\t\tmap size: " + featureMap.getFeatureMap().size());

    problem.saveProblemInstance(
        this.getWriter(),
        this.getDataObj().getLabelSet().getLabel2num().get(labelName));

  }


  private void processLabelDocsToCreateLiblinearInputFile(File labelDir) throws IOException {

    String labelName = labelDir.getName();

    File[] documentFilesOfLabel = labelDir.listFiles();
    int docId = 0;
    if (documentFilesOfLabel != null) {
      for (File doc : documentFilesOfLabel) {
        System.out.println("\t" + docId + ": " + doc.getName());
        this.updateLibLinearInputFile(doc, labelName);
        docId++;
      }
    }
  }


  private void processCorpusToCreateLiblinearInputFile() {

    File labelPath = new File(this.basePath + this.baseDirectory);

    File[] labelDirs = labelPath.listFiles();
    for (File labelDir : labelDirs) {
      if (!labelDir.getName().endsWith(".DS_Store")) {
        try {
          this.processLabelDocsToCreateLiblinearInputFile(labelDir);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    try {
      this.getWriter().close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  public static void main(String[] args) {

    DataProcessor dp = new DataProcessor(new Tokenizer(), new TokenNormalizer());
    System.out.println("Processing classified documents from: " + dp.getBasePath() + dp.getBaseDirectory());

    dp.processCorpusToCreateDataSets();

    dp.getDataObj().saveLabelSet();
    dp.getDataObj().saveWordSet();

    System.out.println(dp.getDataObj().toString());

    dp.processCorpusToCreateLiblinearInputFile();

    System.out.println(dp.getDataObj().toString());
  }

}
