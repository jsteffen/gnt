package corpus;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/*
 * <tweet id="298211097217994752" date="2013-02-04" time="00:27:59" corpus="PoTwiC" subcorpus="HAMBURG">
<text>Und wieder werbung #superbowl #sat1 @sat1
</text>
<w start="T1" end="T2" lemma="und" type="KON">Und</w>
<w start="T2" end="T3" lemma="wieder" type="ADV">wieder</w>
<w start="T3" end="T4" lemma="Werbung" type="NN">werbung</w>
<w start="T4" end="T5" lemma="hashtag" type="HASH">#superbowl</w>
<w start="T5" end="T6" lemma="hashtag" type="HASH">#sat1</w>
<w start="T6" end="T7" lemma="user" type="ADDRESS">@sat1</w>
</tweet>
 */

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class TweetToConllMapper {
  private String encoding = "UTF-8";

  // relevant tweet elements defined as constants
  private static final QName Text_ELE = new QName("text");
  private static final QName Word_ELE = new QName("w");

  XMLInputFactory factory = XMLInputFactory.newInstance(); 

  XMLEventReader parser = null;


  public void parseTweetXMLFile (String source, String target) 
      throws XMLStreamException, FileNotFoundException {

    // The parser which is called for the PubMedXMLFile
    this.parser = factory.createXMLEventReader(new FileInputStream(source), this.encoding);
    BufferedWriter outStream = null;
    try {
      // The output for the transformed pubmed file.
      outStream = new BufferedWriter
          (new OutputStreamWriter
              (new FileOutputStream(target), this.encoding));
      // Flags for indicating the corresponding tag is opened or closed
      boolean wordFlag = false;

      // Holds the character representation of a Tags value part.
      StringBuilder wordString = new StringBuilder();
      String lemmaString = "";
      String typeString = "";
      int wordCnt = 0;

      while (parser.hasNext()) {
        XMLEvent event = parser.nextEvent(); 
        switch ( event.getEventType() ) 
        { 
        case XMLStreamConstants.START_ELEMENT: 

          // Activate start tags based on individual actions
          StartElement start = event.asStartElement();
          if (start.getName().equals(Text_ELE)){        
          }

          if (start.getName().equals(Word_ELE)){
            wordFlag = true;
            lemmaString = start.getAttributeByName(new QName("lemma")).getValue();
            typeString = start.getAttributeByName(new QName("type")).getValue();
            wordCnt++;
          }

          break;

        case XMLStreamConstants.END_ELEMENT:   

          EndElement end = event.asEndElement();

          // For end tags: add information to pubmed object and if end of article arrived output depending on output style
          // define by TargetUnitType.
          if (end.getName().equals(Text_ELE)){
            wordCnt = 0;
            outStream.newLine();
          }
          if (end.getName().equals(Word_ELE)){
            wordFlag = false;
            outStream.write(wordCnt +"\t" + wordString.toString() +"\t"+lemmaString+"\t"+typeString+"\t"+typeString);  
            outStream.newLine();
            wordString = new StringBuilder();
          }
          break;

        case XMLStreamConstants.CHARACTERS: 
          
          if (wordFlag){
            Characters characters = event.asCharacters(); 
            if ( ! characters.isWhiteSpace() ){
              wordString.append(characters.getData());}
          }
          break;

        default : 
          break; 
        }
      }
    }
    catch (IOException e) { e.printStackTrace();}
    finally {
      if ( outStream != null )
        try { outStream.close(); } catch ( IOException e ) { e.printStackTrace(); }
    }
  }

  public static void main(String[] args) throws IOException, XMLStreamException{
    TweetToConllMapper mapper = new TweetToConllMapper();
    mapper.parseTweetXMLFile(
        "/Users/gune00/data/twitter_gold/twitter.gold.train.xml", 
        "/Users/gune00/data/twitter_gold/twitter.gold.train.conll");
    mapper.parseTweetXMLFile(
        "/Users/gune00/data/twitter_gold/twitter.gold.dev.xml", 
        "/Users/gune00/data/twitter_gold/twitter.gold.dev.conll");
    mapper.parseTweetXMLFile(
        "/Users/gune00/data/twitter_gold/twitter.gold.test.xml", 
        "/Users/gune00/data/twitter_gold/twitter.gold.test.conll");
  }

  }
