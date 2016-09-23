package corpus;

/**
 * Call:
 * Map annotated data from NLPBA to conll format, i.e.,
 * 
 * /Users/gune00/data/BioNLPdata/NLPBA-NER-2004/Genia4ERtraining
 * 
 * The	O
peri-kappa	B-DNA
B	I-DNA
site	I-DNA
mediates	O
human	B-DNA

to

1	The	X	Y	O
2	peri-kappa	X	Y	B-DNA
3	B		X 	Y 	I-DNA
4 	site	X	Y	I-DNA
5	mediates	X	Y	O
6	human	X	Y	B-DNA
...
 * @author gune00
 *
 */

public class NLPBA2ConllMapper {

}
