package gnt;

import java.io.IOException;

import caller.RunTaggerStandalone;

public class TestRunTaggerStandalone {
	
public static void main(String[] args) throws IOException{
		
	RunTaggerStandalone.runner(
			"SPIEGEL ONLINE @SPIEGELONLINE 14 Std.vor 14 Stunden"
			+ " Flüchtlingskrise: Schauspieler George #Clooney hat für Freitag "
			+ "ein Treffen mit Angela Merkel angekündigt http://spon.de/aeGkB",
			"src/main/resources/props/DePosTagger.xml");		
	}

}
