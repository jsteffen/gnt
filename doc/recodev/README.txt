FEB, 2016

Text classification using LibLinear

Corpus:

RecoDevGN/eclipse_jdt_new

each class is a folder name, each file name inside folder is a document and source of feature;

For using liblinear, I need to create a data file of form:

classID 1:value 2:value ... n:value

value can be integer or real, but should be in any case > 0

natural numbers are the feature names;

What features:

Dictionary:

create dictionary;
compute tf/idf

subsampling

then
baseline1 training:
- loop across all labels: map to integer
- load dictionary map to natural order
- for each document
	- create binary feature vector for all words in doc
- add label and make problem and save in output file

baseline2:
- create count feature vector

STATUS:
- created label set
- created word set
- create liblinear input file

How to use it:
- call recodev.DataProcessor.main(String[])
	- reads all folders with cats
	- creates label set and word set
	- creates liblinear training file 

HIERIX
- for baseline1:
- training and cross-validation with liblinearC
	MDP Linlinear parametes:
	~/dfki/src/liblinear-2.1/train -v 10 -s 4 -c 0.1 -e 0.3 liblinearInputFile.txt 
	Cross Validation Accuracy = 66.9875%
	FLORS Linlinear parametes:
	~/dfki/src/liblinear-2.1/train -v 10 -s 1 -q liblinearInputFile.txt 
	Cross Validation Accuracy = 66.6795%

	
