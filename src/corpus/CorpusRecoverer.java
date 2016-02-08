package corpus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * A class for recovering corpus files which have been nomrlaized by class CorpusNormalizer.
 * That class saves the orig files to files with extension ".orig" before normalization is done.
 * The task of the CorpusRecoverer is to restore the original .conll and -sents.txt files and deleting the .orig files afterwards.
 * @author gune00
 *
 */
public class CorpusRecoverer {
	private Corpus1 corpus = null;

	public Corpus1 getCorpus() {
		return corpus;
	}
	public void setCorpus(Corpus1 corpus) {
		this.corpus = corpus;
	}

	// These methods are for recovering the orig files

	private static void recoverCopyConllFile(String fileNameOrig) throws IOException{
		File fileOrig= new File(fileNameOrig);

		String fileNameCopy = (String) fileNameOrig.subSequence(0, (fileNameOrig.length()-".orig".length()));
		File fileCopyConll = new File(fileNameCopy);

		if (!fileOrig.exists()) {
			System.out.println(fileOrig.toString()+" already recovered!");
		}
		else {
			Files.copy(fileOrig.toPath(), fileCopyConll.toPath(), StandardCopyOption.REPLACE_EXISTING);
			System.out.println(fileCopyConll.toString()+" recovered!");
		}
		if (!fileOrig.exists()) {
			System.out.println(fileOrig.toString()+" already recovered!");
		}
		else
		{
			Files.deleteIfExists(fileOrig.toPath());
			System.out.println(fileOrig.toString()+" deleted!");
		}
	}

	private void recoverCopiedFilesFromCorpus() throws IOException{
		// Labeled data
		for (String fileName : this.getCorpus().getTrainingLabeledData()){
			String fileNameComplete = fileName+".conll.orig";
			recoverCopyConllFile(fileNameComplete);
		}
		for (String fileName : this.getCorpus().getDevLabeledData()){
			String fileNameComplete = fileName+".conll.orig";
			recoverCopyConllFile(fileNameComplete);
		}
		for (String fileName : this.getCorpus().getTestLabeledData()){
			String fileNameComplete = fileName+".conll.orig";
			recoverCopyConllFile(fileNameComplete);
		}

		// Unlabeled data
		for (String fileName : this.getCorpus().getTrainingUnLabeledData()){
			String fileNameComplete = fileName+".orig";
			recoverCopyConllFile(fileNameComplete);
		}
		for (String fileName : this.getCorpus().getDevUnLabeledData()){
			String fileNameComplete = fileName+".orig";
			recoverCopyConllFile(fileNameComplete);
		}
		for (String fileName : this.getCorpus().getTestUnLabeledData()){
			String fileNameComplete = fileName+".orig";
			recoverCopyConllFile(fileNameComplete);
		}
	}

	public void recoverAlltaggerNameCorpora() throws IOException{
		for (String taggerName : Corpus1.knownTaggerNames){
			this.setCorpus(new Corpus1(taggerName));
			this.recoverCopiedFilesFromCorpus();
		}
	}
}
