package corpus;

/**
 * We use the Berkeley parser word signatures (Petrov and Klein, 2007). 
 * cf. 
 * https://github.com/slavpetrov/berkeleyparser/blob/master/src/edu/berkeley/nlp/discPCFG/LexiconFeatureExtractor.java
 * https://github.com/slavpetrov/berkeleyparser/blob/master/src/edu/berkeley/nlp/discPCFG/LexiconFeature.java
 * 
 * Each word is mapped to a bit string encompassing 16 binary indicators that correspond to different orthographic 
 * (e.g., does the word contain a digit, hyphen, uppercase character) and morphological (e.g., does the
 * word end in -ed or -ing) features. 
 * There are 50 unique signatures in WSJ. 
 * We set the dimension of f_shape(w) that corresponds to the signature of w to 1 and all other dimensions to 0. 
 * We note that the shape features we use were designed for English and probably would have to be adjusted for other languages.
 * @author gune00
 *
 */

public class WordShapeVector {

}
