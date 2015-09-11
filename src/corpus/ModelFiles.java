package corpus;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class ModelFiles {

	private String trainingInstances = "";
	private BufferedWriter writerTrainingInstances;
	private boolean isActive = false;

	public String getTrainingInstances() {
		return trainingInstances;
	}
	public void setTrainingInstances(String trainingInstances) {
		this.trainingInstances = trainingInstances;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public BufferedWriter getWriterTrainingInstances() {
		return writerTrainingInstances;
	}

	public void setWriterTrainingInstances(BufferedWriter writerTrainingInstances) {
		this.writerTrainingInstances = writerTrainingInstances;
	}

	public void openWriterTrainingInstances(String fileName) throws UnsupportedEncodingException, FileNotFoundException{
		this.trainingInstances = fileName;
		writerTrainingInstances = new BufferedWriter
				(new OutputStreamWriter (new FileOutputStream(fileName), "utf-8"));
		this.isActive = true;

	}

	public void closeWriterTrainingInstances(){
		try {
			this.getWriterTrainingInstances().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
