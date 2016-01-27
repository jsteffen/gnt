package recodev;

import java.io.File;
import java.io.IOException;

public class DataProcessor {

	/**
	 * The main path to the base directory which hosts the label directories
	 */
	String basePath = "/Users/gune00/dfki/NLP4Software/RecoDevGN/";
	/**
	 * The name of the base directory 
	 */
	String baseDirectory = "eclipse_jdt_new";


	/**
	 * Get all directory names; they are used as label names later
	 */
	private void getLabelDirs(){
		File labelPath = new File(basePath+baseDirectory);

		File[] labelDirs = labelPath.listFiles();
		for(File labelDir: labelDirs){
			System.out.println("Label name: " + labelDir.getName());
			this.getLabelDocs(labelDir);
		}
	}

	/**
	 * Get all txt files from each label directory; they are used as feature source
	 * @param labelDir
	 */
	private void getLabelDocs(File labelDir){
		File[] documentFilesOfLabel = labelDir.listFiles();
		int docId = 0;
		if (documentFilesOfLabel != null)
			for(File doc: documentFilesOfLabel){
				System.out.println("\t" + docId + ": " + doc.getName());
				docId++;
			}	
	}

	/*
	 * Ok, the above works, so I can now create data files:
	 * - label name collection and liblinear integer mapping
	 * - vocabulary collection and liblinear integer mapping
	 */

	public static void main(String[] args) throws IOException {
		DataProcessor dp = new DataProcessor();
		dp.getLabelDirs();
	}

}
