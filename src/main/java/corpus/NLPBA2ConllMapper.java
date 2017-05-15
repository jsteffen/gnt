package corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Call:
 * Map annotated data from NLPBA to conll format, i.e.,
 *
 * /Users/gune00/data/BioNLPdata/NLPBA-NER-2004/Genia4ERtraining
 *
 * The  O
peri-kappa  B-DNA
B  I-DNA
site  I-DNA
mediates  O
human  B-DNA

to

1  The  X  Y  O
2  peri-kappa  X  Y  B-DNA
3  B    X   Y   I-DNA
4   site  X  Y  I-DNA
5  mediates  X  Y  O
6  human  X  Y  B-DNA
...
 * @author gune00
 *
 */

public class NLPBA2ConllMapper {

  private String directory = "resources/data/ner/nlpba/";


  private String makeFileName(String fileName) {

    return this.directory + fileName;
  }


  private static void transformNLPBAToConllFile(String sourceFileName, String targetFileName)
      throws IOException {

    String sourceEncoding = "utf-8";
    String targetEncoding = "utf-8";
    // init reader for CONLL style file
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(sourceFileName),
            sourceEncoding));

    // init writer for line-wise file
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(targetFileName),
            targetEncoding));

    String line = "";
    int lineCnt = 0;
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        writer.newLine();
        lineCnt = 0;
      } else {
        // Normalize line which is assumed to correspond to a sentence.
        if (!line.startsWith("#")) {
          String[] stringVector = line.split("\t");
          lineCnt++;
          String newLine = lineCnt + "\t" + stringVector[0] + "\t" + "X" + "\t" + "Y" + "\t" + stringVector[1];
          writer.write(newLine);
          writer.newLine();
        }
      }
    }
    reader.close();
    writer.close();
  }


  private static void transcodeConllToSentenceFile(String sourceFileName, String targetFileName)
      throws IOException {

    String sourceEncoding = "utf-8";
    String targetEncoding = "utf-8";
    // init reader for CONLL style file

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(sourceFileName),
            sourceEncoding));

    // init writer for line-wise file
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(targetFileName),
            targetEncoding));

    String line = "";
    List<String> tokens = new ArrayList<String>();
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        // If we read a newline it means we know we have just extracted the words
        // of a sentence, so write them to file
        if (!tokens.isEmpty()) {
          writer.write(sentenceToString(tokens) + "\n");
          tokens = new ArrayList<String>();
        }
      } else {
        // Extract the word from each CONLL token line
        String[] tokenizedLine = line.split("\t");
        tokens.add(tokenizedLine[1]);
      }

    }
    reader.close();
    writer.close();
  }


  private static String sentenceToString(List<String> tokens) {

    String sentenceString = "";
    for (int i = 0; i < tokens.size() - 1; i++) {
      sentenceString = sentenceString + tokens.get(i) + " ";
    }
    return sentenceString + tokens.get(tokens.size() - 1);
  }


  private static void normalizeUnlabeledFile4Marlin(String dir, String inFile, String outFile) throws IOException {

    CorpusNormalizer.normalizeUnLabeledFile(dir + inFile, dir + outFile);
  }


  public static void main(String[] args) throws IOException {

    /*
    NLPBA2ConllMapper mapper = new NLPBA2ConllMapper();

    NLPBA2ConllMapper.transformNLPBAToConllFile(mapper.makeFileName("nlpba-train.txt"),
        mapper.makeFileName("nlpba-train.conll"));
    NLPBA2ConllMapper.transformNLPBAToConllFile(mapper.makeFileName("nlpba-test.txt"),
        mapper.makeFileName("nlpba-test.conll"));

    NLPBA2ConllMapper.transcodeConllToSentenceFile(mapper.makeFileName("nlpba-train.conll"),
        mapper.makeFileName("nlpba-train-sents.txt"));
    NLPBA2ConllMapper.transcodeConllToSentenceFile(mapper.makeFileName("nlpba-test.conll"),
        mapper.makeFileName("nlpba-test-sents.txt"));
    */

    NLPBA2ConllMapper.normalizeUnlabeledFile4Marlin(
        "/Volumes/data1/BioNLPdata/PubMedXML/",
        "HumanGene-072013-sents.txt",
        "HumanGene-072013-sents-normalized.txt");
  }

}
