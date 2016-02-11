package gnt;

import java.io.IOException;

import caller.RunTagger;

public class TestRunPosTagger {

	public static void main(String[] args) throws IOException{
		
		RunTagger.runner("src/main/resources/props/DePosTagger.xml");		
	}
}
