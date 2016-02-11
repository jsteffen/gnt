package gnt;

import java.io.IOException;

import caller.RunTaggerStandalone;

public class TestRunTaggerStandalone {
	
public static void main(String[] args) throws IOException{
		
	RunTaggerStandalone.runner(
			"Ich bin ein Berliner.",
			"src/main/resources/props/DePosTagger.xml");		
	}

}
