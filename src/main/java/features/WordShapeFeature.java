package features;

/**
 * We use the Berkeley parser word signatures (Petrov and Klein, 2007). 
 * cf. 
 * https://github.com/slavpetrov/berkeleyparser/blob/master/src/edu/berkeley/nlp/discPCFG/LexiconFeatureExtractor.java
 * https://github.com/slavpetrov/berkeleyparser/blob/master/src/edu/berkeley/nlp/discPCFG/LexiconFeature.java
 * 
 * Each word is mapped to a bit string encompassing 16 binary indicators that correspond to different orthographic 
 * (e.g., does the word contain a digit, hyphen, upper case character) and morphological (e.g., does the
 * word end in -ed or -ing) features. 
 * There are 50 unique signatures in WSJ. 
 * We set the dimension of f_shape(w) that corresponds to the signature of w to 1 and all other dimensions to 0. 
 * We note that the shape features we use were designed for English and probably would have to be adjusted for other languages.
 * @author gune00
 *
 */

public class WordShapeFeature {
	/** I guess: ALL_CAPS means: always initial capital (as for NE in English); INIT_CAP means:
	 * only capital in initial sentence position.
	 * @author gune00
	 *
	 */
	public static enum MorphFeature {
		ALL_CAPS, HAS_DASH, HAS_DIGIT, INIT_CAP, KNOWNLC, LOWER_CASE, SUFF_AL, SUFF_ED, 
		SUFF_ER, SUFF_EST, SUFF_ING, SUFF_ION,  SUFF_ITY, SUFF_LY, SUFF_S, SUFF_Y, 
	}

	private MorphFeature[] num2MorphFeature = {
			MorphFeature.ALL_CAPS, MorphFeature.HAS_DASH, MorphFeature.HAS_DIGIT, MorphFeature.INIT_CAP, 
			MorphFeature.KNOWNLC, MorphFeature.LOWER_CASE, MorphFeature.SUFF_AL, MorphFeature.SUFF_ED, 
			MorphFeature.SUFF_ER, MorphFeature.SUFF_EST, MorphFeature.SUFF_ING, MorphFeature.SUFF_ION,  
			MorphFeature.SUFF_ITY, MorphFeature.SUFF_LY, MorphFeature.SUFF_S, MorphFeature.SUFF_Y, 
	};

	// define bit vector - order follows order in enum; default value is false for all elements
	private boolean[] bitVector = 
		{
			false, false, false, false,
			false,false, false,false, 
			false,false, false,false, 
			false,false, false, false};

	private String bitVectorString = "0000000000000000";

	// getters and setters
	public MorphFeature[] getNum2MorphFeature() {
		return num2MorphFeature;
	}

	public void setNum2MorphFeature(MorphFeature[] num2MorphFeature) {
		this.num2MorphFeature = num2MorphFeature;
	}

	public boolean[] getBitVector() {
		return bitVector;
	}

	public void setBitVector(boolean[] bitVector) {
		this.bitVector = bitVector;
	}

	public String getBitVectorString() {
		return bitVectorString;
	}

	public void setBitVectorString(String bitVectorString) {
		this.bitVectorString = bitVectorString;
	}

	public WordShapeFeature(String word, int wordIndex) {
		createShapeVectorFromWord(word, wordIndex);
	}

	private void bitVectorflip(int i) {
		if (this.bitVector[i])
			this.bitVector[i]=false;
		else
			this.bitVector[i]=true;
	}

	// Default truth value is false, so flipping means, change it to true
	public void setBit(MorphFeature morphFeature){
		switch (morphFeature){
		case ALL_CAPS: 
			this.bitVectorflip(0); break;
		case HAS_DASH: 
			this.bitVectorflip(1); break;
		case HAS_DIGIT: 
			this.bitVectorflip(2); break;
		case INIT_CAP:
			this.bitVectorflip(3); break;
		case KNOWNLC:
			this.bitVectorflip(4); break;
		case LOWER_CASE:
			this.bitVectorflip(5); break;
		case SUFF_AL:
			this.bitVectorflip(6); break;
		case SUFF_ED:
			this.bitVectorflip(7); break;
		case SUFF_ER:
			this.bitVectorflip(8); break;
		case SUFF_EST:
			this.bitVectorflip(9); break;
		case SUFF_ING:
			this.bitVectorflip(10); break;
		case SUFF_ION:
			this.bitVectorflip(11); break;
		case SUFF_ITY:
			this.bitVectorflip(12); break;
		case SUFF_LY:
			this.bitVectorflip(13); break;
		case SUFF_S:
			this.bitVectorflip(14); break;
		case SUFF_Y:
			this.bitVectorflip(15); break;
		default:
			break;
		}
	}

	/**
	 * Given a word and its index position in a string (0 means: start position, others means: inside sentence)
	 * create a string which represent the signature of that word
	 * @param word
	 * @param wordIndex
	 */
	public void createShapeVectorFromWord(String word, int wordIndex) {
		// Main parts of this code are 
		// from https://github.com/slavpetrov/berkeleyparser/blob/master/src/edu/berkeley/nlp/discPCFG/LexiconFeatureExtractor.java
		int wlen = word.length();
		int numCaps = 0;
		boolean hasDigit = false;
		boolean hasDash = false;
		boolean hasLower = false;

		// scan the word
		for (int i = 0; i < wlen; i++) {
			char ch = word.charAt(i);
			if (Character.isDigit(ch)) {
				hasDigit = true;
			} else 
				if ( (ch == '-') )
				{
					hasDash = true;
				} else if (Character.isLetter(ch)) {
					if (Character.isLowerCase(ch)) {
						hasLower = true;
					} else if (Character.isTitleCase(ch)) {
						hasLower = true;
						numCaps++;
					} else {
						numCaps++;
					}
				}
		}
		// Remember first char
		char ch0 = word.charAt(0);
		// and then lowercase it
		String lowered = word.toLowerCase();

		// Now, analyse the different cases, and set the relevant bits in the bitVector
		if (Character.isUpperCase(ch0) || Character.isTitleCase(ch0)) {
			if (wordIndex == 0 && numCaps == 1) {
				this.setBit(MorphFeature.INIT_CAP);

			} else {
				this.setBit(MorphFeature.ALL_CAPS);
			}
		} else if (!Character.isLetter(ch0) && numCaps > 0) {
			this.setBit(MorphFeature.ALL_CAPS);
		} else if (hasLower) {
			this.setBit(MorphFeature.LOWER_CASE);
		}
		if (hasDigit) {
			this.setBit(MorphFeature.HAS_DIGIT);
		}
		if (hasDash) {
			this.setBit(MorphFeature.HAS_DASH);
		}
		//		if (lowered.endsWith("s") && wlen >= 3) {
		//			// here length 3, so you don't miss out on ones like 80s
		//			char ch2 = lowered.charAt(wlen - 2);
		//			// not -ess suffixes or greek/latin -us, -is
		//			if (ch2 != 's' && ch2 != 'i' && ch2 != 'u') {
		//				this.setBit(MorphFeature.SUFF_S);
		//			}
		//		} else if (word.length() >= 5 && !hasDash && !(hasDigit && numCaps > 0)) {
		//			// don't do for very short words;
		//			// Implement common discriminating suffixes
		//			/*
		//			 * if (Corpus.myLanguage==Corpus.GERMAN){
		//			 * sb.append(lowered.substring(lowered.length()-1)); }else{
		//			 */
		//			if (lowered.endsWith("ed")) {
		//				this.setBit(MorphFeature.SUFF_ED);
		//			} else if (lowered.endsWith("ing")) {
		//				this.setBit(MorphFeature.SUFF_ING);
		//			} else if (lowered.endsWith("ion")) {
		//				this.setBit(MorphFeature.SUFF_ION);
		//			} else if (lowered.endsWith("er")) {
		//				this.setBit(MorphFeature.SUFF_ER);
		//			} else if (lowered.endsWith("est")) {
		//				this.setBit(MorphFeature.SUFF_EST);
		//			} else if (lowered.endsWith("ly")) {
		//				this.setBit(MorphFeature.SUFF_LY);
		//			} else if (lowered.endsWith("ity")) {
		//				this.setBit(MorphFeature.SUFF_ITY);
		//			} else if (lowered.endsWith("y")) {
		//				this.setBit(MorphFeature.SUFF_Y);
		//			} else if (lowered.endsWith("al")) {
		//				this.setBit(MorphFeature.SUFF_AL);
		//			}
		//		}	

		//Finally
		this.bitVectorString = this.toBinaryString();
	}

	// Define: make binary string and use this for signature cache -> eventually is faster -> YES

	public String toBinaryString(){
		String bitsetString = "";
		for (int i=0; i < bitVector.length; i++){
			int bin = (bitVector[i])?1:0;
			bitsetString = bitsetString + bin;
		}
		return bitsetString;
	}

	public String toString(){
		return this.bitVectorString;
	}

	public static void main(String[] args){
		WordShapeFeature test = new WordShapeFeature("PETER", 0);
		System.out.println(test.toString());
	}
}
