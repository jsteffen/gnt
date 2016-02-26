# gnt
GNT - A GeNeralized Tagger for POS, NER and Morphology tagging

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

Current tasks:

- POS tagging with tests on EN and DE
- NER with tests on EN and DE
- Morphology with tests on DE
- Twitter-based POS tagging for DE
