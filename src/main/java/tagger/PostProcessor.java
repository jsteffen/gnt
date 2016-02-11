package tagger;

public class PostProcessor {
	
	public static String determineTwitterLabel(String word, String label){
		// missing PAUSE and COMMENTS
		if (word.startsWith("@")) 
			return "ADDRESS";
		else
			if (word.startsWith("http://")) 
				return "URL";
			else
				if (word.startsWith("#")) 
					// very ambiguous
					return "HASH";
				else
					if (word.startsWith(":)") ||
							word.startsWith("._.") ||
							word.startsWith(";)") ||
							word.startsWith("\\o/") ||
							word.startsWith("😁😁") ||
							word.startsWith(":/") ||
							word.startsWith("xD") ||
							word.startsWith("^^") ||
							word.startsWith(":')") ||
							word.startsWith("&lt;3") ||
							word.startsWith(":3") ||
							word.startsWith("*Q*") ||
							word.startsWith("(-;") ||
							word.startsWith(":o)") ||
							word.startsWith("v.v") ||
							word.startsWith("(:") ||
							word.startsWith(";D") ||
							word.startsWith("😂") ||
							word.startsWith("oO") ||
							word.startsWith("D:") ||
							word.startsWith(":D")
							) 
						return "EMO";
					else
						return label;

	}

}
