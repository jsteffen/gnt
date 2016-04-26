/**
 * @author gune00
 *
 */
package archive;

/**
 * 
 * April, 2016

- generate compressed folder of all data that is necessary for tagging
	- store model file also under taggerName feature dir
	- then compress model file and feature files -> very useful !
	- see also corpus.WikiPediaConllReader.getBufferedReaderForCompressedFile(String)

I could do it similar to:

de.dfki.lt.mdparser.archive.Archivator

using *.zip seems to be ok

The idea is to create an archive.zip which also functions as name for an archive class.
Then store all necessary files there using a hashtable.
Then define two classes:
- pack and unpack which store and keeps files that are compressed in that archive.zip

I need to make sure to be flexible with new feature files not yet implemented.

*/

/**

How to pack an archive ?

Current files created and mainly loaded from 
	- Alphabet
		feature files
	- Data 
		label set and word set
	- ModelInfo
		data.ModelInfo.taggerName

Files which are created:
	distributed word features
		String iwFile = "resources/features/"+taggerName+"/iw"+maxIndicatorWords+".txt";
		String vocFile = "resources/features/"+taggerName+"/vocFile.txt";
		String dwvFile = "resources/features/"+taggerName+"/vocContext"+maxIndicatorWords+".txt";
	suffix list
		String suffixFileName = "resources/features/"+taggerName+"/suffixList.txt";
	shape feats
		String shapeFileName = "resources/features/"+taggerName+"/shapeList.txt";
	clusterId
		String fileName = "resources/features/"+taggerName+"/clusterId.txt";
	
	Data
		labelMapFileName = "resources/features/"+taggerName+"/labelSet.txt";
		wordMapFileName = "resources/features/"+taggerName+"/wordSet.txt";

ModelFile name can be used as archive name
	or inside archive and then tagger name as archive name
	
If ModelInfo.saveModelInputFile = true
	then save training input file
		needed for training with liblinearC version
	in this case, do training up  to that point in 
			trainer.TrainerInMem.trainFromConllTrainingFileInMemory(String, int)
			create archive add all feature files etc. also label/word files
			call liblinearC externally and add model file to archive
			
So, in any case, I can define and call a method unpack
	define slot archivator as part of trainer.TrainerInMem
	Archivator.pack() 
		as last statement in trainer.TrainerInMem.trainFromConllTrainingFileInMemory
		
NOTE:
 if this works I would not need the resources/features/ any more
 so could delete them
 but then, why not directly create the archive when creating the alphabet/data files ?
*/

/**

How to unpack an archive ?
	define slot archivator as part of tagger.GNTagger
		unpack archive as first statement in tagger.GNTagger.initGNTagger(int, int)
	make sure to load all files properly
		I need to define a method like data.Alphabet.loadFeaturesFromFiles(String, int) for archives
		similar for data()
		and modelfile

In order to make it more systematic:

- add feature dir to modelInfo
- make sure data is using taggerName from modelInfo
	
 */

