package corpus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * <pre>
 * {@code
 * From (Müller et al., ACL, 2015):
 *
 * We preprocessed the Wikipedia dumps with
 * WIKIPEDIAEXTRACTOR (Attardi and Fuschetto,
 * 2013) and NLTK’S (Bird et al., 2009) implementation
 * of PUNKT (Kiss and Strunk, 2006) to detect
 * sentence boundaries. Tokenization was performed
 * using MAGYARLANC (Hungarian, Zsibrita et
 * al. (2013)), STANFORD TOKENIZER (English, Manning
 * et al. (2014)), FREELING (Spanish, Padr´o and
 * Stanilovsky (2012)) and CZECHTOK5 (Czech). For
 * Latin, we removed punctuation because PROIEL
 * does not contain punctuation. We also split off the
 * clitics ne, que and ve if the resulting token was accepted
 * by LATMOR (Springmann et al. (2014)). Following
 * common practice, we normalized the text by
 * replacing digits with 0s.
 *
 * Given the CONLL files encoded Wikipedia 2014 dumps
 * from Wikipedia German and English as computed by Marmot (Müller et al., ACL, 2015);
 *
 * The format is:
 *
 * 1       Wirtschaft      _       case=nom|number=sg|gender=fem
 * 2       Südkoreas       _       case=gen|number=sg|gender=neut
 *
 * 1       Die     _       case=nom|number=sg|gender=fem
 * 2       südkoreanische  _       case=nom|number=sg|gender=fem|degree=pos
 * 3       Volkswirtschaft _       case=nom|number=sg|gender=fem
 * 4       wird    _       number=sg|person=3|tense=pres|mood=ind
 * 5       grundsätzlich   _       degree=pos
 * 6       als     _       _
 * 7       freie   _       case=nom|number=sg|gender=fem|degree=pos
 * 8       Marktwirtschaft _       case=nom|number=sg|gender=fem
 * 9       eingestuft      _       _
 * 10      und     _       _
 * 11      hat     _       number=sg|person=3|tense=pres|mood=ind
 * 12      sich    _       case=acc|number=sg|person=3
 * 13      in      _       _
 * 14      den     _       case=dat|number=pl|gender=neut
 * 15      letzten _       case=dat|number=pl|gender=neut|degree=pos
 * 16      Jahrzehnten     _       case=dat|number=pl|gender=neut
 * 17      stetig  _       degree=pos
 * 18      weiterentwickelt        _       _
 * 19      .       _       _
 *
 * OBACHT: NUmbers are in generalized form, e.g., 1989,99 -> 0000,00
 *
 * STATUS:
 *
 * Currently (November, 2015) I am just extracting maxSent sentences.
 * All sentences for de-wikidump:
 * 35943938 610430095 3922225565 de-wikidump-sentsAll.txt
 *
 * Future:
 *
 * - attach file with POS, NER, Dependency analysis.
 * }
 * </pre>
 *
 * @author Günter Neumann, DFKI
 */
public class WikiPediaConllReader {

  /**
   * From http://stackoverflow.com/questions/4834721/java-read-bz2-file-and-uncompress-parse-on-the-fly
   * <p>
   * Reads in a compressed file
   * Note: the accepted formats are: gzip, bzip2, xz, lzma, Pack200, DEFLATE and Z.
   * As seen in the link, the correct one is automatically assigned – Danielson Aug 15 at 10:01
   * @param fileIn
   * @return
   * @throws FileNotFoundException
   * @throws CompressorException
   */
  public static BufferedReader getBufferedReaderForCompressedFile(String fileIn)
      throws FileNotFoundException, CompressorException {

    FileInputStream fin = new FileInputStream(fileIn);
    BufferedInputStream bis = new BufferedInputStream(fin);
    CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
    BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
    return br2;
  }


  public static BufferedWriter getBufferedWriterForTextFile(String fileOut)
      throws FileNotFoundException, CompressorException, UnsupportedEncodingException {

    FileOutputStream fout = new FileOutputStream(fileOut);
    BufferedOutputStream bout = new BufferedOutputStream(fout);
    CompressorOutputStream out = new CompressorStreamFactory().createCompressorOutputStream("bzip2", bout);
    BufferedWriter br2 = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
    return br2;
  }


  /**
   * Receives a file in CONLL format and maps each CONLL sentence to a sentence, where each sentence
   * is a line of words extracted from the CONLL sentence.
   * @param sourceFileName
   * @param sourceEncoding
   * @param targetFileName
   * @param targetEncoding
   * @param maxSent
   * @throws IOException, CompressorException
   */
  private void transcodeConllToSentenceFile(String sourceFileName, String sourceEncoding,
      String targetFileName, String targetEncoding, int maxSent)
      throws IOException, CompressorException {

    // init reader for CONLL style file
    //    BufferedReader reader = new BufferedReader(
    //        new InputStreamReader(
    //            new FileInputStream(sourceFileName),
    //            sourceEncoding));

    BufferedReader reader = WikiPediaConllReader.getBufferedReaderForCompressedFile(sourceFileName);

    // init writer for line-wise file
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(targetFileName),
            targetEncoding));

    String line = "";
    int sentCnt = 0;
    int lineCnt = 0;
    int mod = 1000000;
    List<String> tokens = new ArrayList<String>();
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        // If ew read a newline it means we know we have just extracted the words
        // of a sentence, so write them to file
        writer.write(sentenceToString(tokens) + "\n");
        tokens = new ArrayList<String>();
        // Increase sentence counter
        sentCnt++;
        // Stop if maxSent has been processed
        // if maxSent is < 0 this means: read until end of file.
        if ((maxSent > 0) && (sentCnt >= maxSent)) {
          break;
        }
        if ((lineCnt % mod) == 0) {
          System.out.println(lineCnt);
        }
        lineCnt++;
      } else {
        // Extract the word from each CONLL token line
        String[] tokenizedLine = line.split("\t");
        tokens.add(tokenizedLine[1]);
      }

    }
    reader.close();
    writer.close();
  }


  private String sentenceToString(List<String> tokens) {

    String sentenceString = "";
    for (int i = 0; i < tokens.size() - 1; i++) {
      sentenceString = sentenceString + tokens.get(i) + " ";
    }
    return sentenceString + tokens.get(tokens.size() - 1);
  }


  public static void main(String[] args) {

    try {
      WikiPediaConllReader mapper = new WikiPediaConllReader();

      mapper.transcodeConllToSentenceFile(
          "/Users/gune00/data/Marmot/en.wikidump.bz2", "dummy",
          "/Users/gune00/data/Marmot/en-wikidump-sents5millions.txt", "utf-8",
          5000000);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (CompressorException e) {
      e.printStackTrace();
    }
  }
}
