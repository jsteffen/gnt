package gnt;

import java.io.IOException;

import caller.GNTaggerStandalone;

public class TestRunTaggerStandalone {
	
public static void main(String[] args) throws IOException{
	GNTaggerStandalone runner = new GNTaggerStandalone();
	runner.initRunner("resources/models/model_DEPOS_2_0iw-1sent_FTTT_MCSVM_CS.zip");
		
	System.out.println("Tag text: ");
	runner.tagStringRunner(
			"Das Bremsregelsystem stellt mit seinen Funktionen ABS, ASR, ESP, ... "
			+ "fahrdynamische Funktionen bereit."
			+ "Ferner enthält es Mehrwertfunktionen (Seitenwindassistent, ...) "
			+ "die den Fahrer beim Führen des Fahrzeuges unterstützen.");	
	
	//runner.tagFileRunner("/Users/gune00/dfki/projects/Daimler/Daimler-Mach2-2016/data/Mach2/de/Text-Korpus_Neu.bsp", "iso-8859-1", "utf-8");
	}

}
