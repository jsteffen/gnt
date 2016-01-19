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
 * Map the tiger2 format to conll POS format and create three conll files from it:
 * First 40,474 sentences -> tiger2-train-conll
 * Next 5000 -> tiger2-devel.conll
 * Last 5000 -> tiger2-test.conll
 * 
 * What encoding ? ISO-8859-1 
 * 
 * Currently I will only map information needed for POS tagging, later also for
 * Chunk parsing, and eventually dependencies and NER;
 * 
 * 
 * @author gune00
 *
 */

/**
 * Approach:
 * Tiger 2 relevant format:
#BOS 1 0 1098266456 1 %% @SB2AV@
``                      --                      $(      --              --      0
Ross                    Ross                    NE      Nom.Sg.Masc     PNC     500
Perot                   Perot                   NE      Nom.Sg.Masc     PNC     500
w<E4>re                 sein                    VAFIN   3.Sg.Past.Subj  HD      502
vielleicht              vielleicht              ADV     --              MO      502
ein                     ein                     ART     Nom.Sg.Masc     NK      501
pr<E4>chtiger           pr<E4>chtig             ADJA    Pos.Nom.Sg.Masc NK      501
Diktator                Diktator                NN      Nom.Sg.Masc     NK      501
''                      --                      $(      --              --      0
#500                    --                      PN      --              SB      502
#501                    --                      NP      --              PD      502
#502                    --                      S       --              --      0
#EOS 1

Map to:

1	``	_	$(	$(	_	4	PUNC	4	PUNC
2	Ross	_	NE	NE	_	4	SB	4	SB
3	Perot	_	NE	NE	_	2	PNC	2	PNC
4	wäre	_	VAFIN	VAFIN	_	0	ROOT	0	ROOT
5	vielleicht	_	ADV	ADV	_	4	MO	4	MO
6	ein	_	ART	ART	_	8	NK	8	NK
7	prächtiger	_	ADJA	ADJA	_	8	NK	8	NK
8	Diktator	_	NN	NN	_	4	PD	4	PD
9	''	_	$(	$(	_	4	PUNC	4	PUNC

Thus:
- if line begins with #BOS sentId 
	-> sent sentCnt to sentId
	-> set start = true (should be false!)
	-> set tokenCnt = 0
- next line
- if startSentence = true, tokenCnt++
- create conll format for next line and write to selected file
- if startSentence = true and line begins with #500
- write \newline to selected file, set start=false


 * @author gune00
 *
 */

public class TigerToConllMapper {

	/**
	 * Parse the tiger export file and create three conll format files for train/devel/test according to 
	 * Müller et al. 2013.
	 * Parsing is then quite straight.
	 * @param sourceFileName
	 * @param trainFile
	 * @param develFile
	 * @param testFile
	 * @throws IOException
	 */
	public static String whatTags = "pos"; // "posmorph", "morph"
	private void transcodeSourcefile(String sourceFileName, String trainFile, String develFile, String testFile)
			throws IOException{
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(sourceFileName),
						"ISO-8859-1"));

		BufferedWriter trainWriter = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(trainFile),
						"utf-8"));

		BufferedWriter develWriter = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(develFile),
						"utf-8"));

		BufferedWriter testWriter = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(testFile),
						"utf-8"));

		String line = "";
		int develSentIndex = 40474;
		int testSentIndex = 45474;
		int sentenceIndex = 0;
		int tokenCnt = 0;
		boolean startSentence = false;

		while ((line = reader.readLine()) != null) {
			if (startSentence && line.startsWith("#EOS")){
				startSentence = false;
				tokenCnt = 0;
				if (sentenceIndex > testSentIndex)
					testWriter.newLine();
				else
					if (sentenceIndex > develSentIndex)
						develWriter.newLine();
					else
						trainWriter.newLine();
				if (sentenceIndex > testSentIndex)
					System.out.println("TestFile: " + sentenceIndex+" "+line+"\n");
				else
					if (sentenceIndex > develSentIndex)
						System.out.println("DevelFile: " + sentenceIndex+" "+line+"\n");
					else
						System.out.println("TrainFile: " + sentenceIndex+" "+line+"\n");
			}
			else
				if ((!startSentence) && line.startsWith("#BOS")){
					startSentence = true;
					tokenCnt = 0;
					sentenceIndex++;
				}
				else
					if (startSentence && (!line.startsWith("#5"))){
						tokenCnt++;
						String conllString = tiger2conllString(line, tokenCnt);
						if (sentenceIndex > testSentIndex)
							testWriter.write(conllString+"\n");
						else
							if (sentenceIndex > develSentIndex)
								develWriter.write(conllString+"\n");
							else
								trainWriter.write(conllString+"\n");
					}

		}

		reader.close();
		trainWriter.close();
		develWriter.close();
		testWriter.close();
	}

	/**
	 * gets a line which represents the token information in tiger export format
	 * Splitting the string is a bit complex, because the token elements are not always
	 * separated by a \t as I expected. Sometimes a tab can be empty, so I need to check this carefully.
	 * Currently, I do not take into account other information than POS.
	 * @param line
	 * @param index
	 * @return
	 */
	private String tiger2conllString(String line, int index) {

		String[] tokenizedLine = line.split("\t");
		String word = tokenizedLine[0];
		int ptr = 1;

		for (int i = ptr; i < tokenizedLine.length;i++){
			if (!tokenizedLine[i].isEmpty()) {ptr=i; break;}
		}
		String lemma = tokenizedLine[ptr];
		ptr++;
		for (int i = ptr; i < tokenizedLine.length;i++){
			if (!tokenizedLine[i].isEmpty()) {ptr=i; break;}
		}
		String morphTag = tokenizedLine[ptr+1];

		String posTag = createPosTag(tokenizedLine[ptr], morphTag);

		String output = index+"\t"; 	// token id
		output +=word+"\t"; // word 0
		output +=lemma+"\t";	// lemma 3
		output +=posTag+"\t";	// POS 6
		output +=posTag+"\t";	// POS 6
		output +="_";		// ptr+1morphology 7
		return output;
	}

	/**
	 * Create tags:
	 * for POS just POSTag
	 * for Morph: if MorphTag = -- then POSTag else MorphTag
	 * for POS and Morph make POS+MORPH
	 * @param string
	 * @param morphTagIn
	 * @return
	 */
	
	private String createPosTag(String string, String morphTagIn) {
		String posTag = string;
		String morphTag = morphTagIn.equals("--")?posTag:morphTagIn;
		
		switch (TigerToConllMapper.whatTags.toLowerCase()) {
		case "pos" : break;
		case "morph" : posTag = morphTag; break;
		case "posmorph" : posTag += morphTag; break;
		default: break;
		}
		return posTag;
	}

	private void makePOSconll() throws IOException{
		TigerToConllMapper.whatTags = "pos";
		this.transcodeSourcefile(
				"/Users/gune00/data/tigercorpus2/corpus/tiger_release_dec05.export", 
				"resources/data/german/tiger2_train.conll", 
				"resources/data/german/tiger2_devel.conll",
				"resources/data/german/tiger2_test.conll");
	}

	private void makeMorphconll() throws IOException{
		TigerToConllMapper.whatTags = "morph";
		this.transcodeSourcefile(
				"/Users/gune00/data/tigercorpus2/corpus/tiger_release_dec05.export", 
				"resources/data/german/tiger2_morph_train.conll", 
				"resources/data/german/tiger2_morph_devel.conll",
				"resources/data/german/tiger2_morph_test.conll");
	}

	private void makePOSandMorphconll() throws IOException{
		TigerToConllMapper.whatTags = "posmorph";
		this.transcodeSourcefile(
				"/Users/gune00/data/tigercorpus2/corpus/tiger_release_dec05.export", 
				"resources/data/german/tiger2_posmorph_train.conll", 
				"resources/data/german/tiger2_posmorph_devel.conll",
				"resources/data/german/tiger2_posmorph_test.conll");
	}

	public static void main(String[] args) throws IOException {
		TigerToConllMapper mapper = new TigerToConllMapper();
		mapper.makePOSconll(); 
		mapper.makeMorphconll(); 
		mapper.makePOSandMorphconll();
	}
}
