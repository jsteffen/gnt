# gnt

Developed by Günter Neumann, http://www.dfki.de/~neumann/, Feb. 2016

## GNT - A GeNeralized Tagger for POS, NER and Morphology tagging

Uses a semi-supervised approach by using a supervised training set in form of CONLL tables, 
and a set of unlabeled data for creating word vectors.

Uses liblinear as basic Machine Learning tool.

Currently it uses the following set of features for all tasks:

- suffix: 	compute a list of all possible suffixes from training set
- shape: 	compute a bit vector which characterizes the shape of a token
- cluster:	uses cluster id for tokens from training set
- vector:	create word vectors from unlabeled data set

Integration of new feature functions is possible.
Integration of pos-processing is easy.

Training and application phase is very fast.

## Current tasks:

- POS tagging with tests on EN and DE
- NER with tests on EN and DE
- Morphology with tests on DE
- Twitter-based POS tagging for DE

I defined a simple GntTokenier class to process text files.
Need to be improved soon.

## Evaluation 

Evaluation is based on file format of form

token-index token gold-label predicted-label 

for example:
4 eines DET PRON

means that the correct POS tag should be DET, but GNT predicted PRON

for each experiment and test file X, such a eval file is created in 
folder resources/eval/X.txt

all errors are also stored in file X.txt.debug using the same format, i.e.,
keeping the token and its order as given from the test file.

Using this file, we also create an error file, which stores the token-free wrong labels pairs 
and their counts in form of

gold-label:predicted-label	count

these pairs are sorted by frequency