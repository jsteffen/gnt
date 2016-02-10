package gnt;

import java.io.IOException;

import caller.RunPosTagger;

public class TestRunPosTagger {

	public static void main(String[] args) throws IOException{
		
		RunPosTagger.runner("src/main/resources/props/FrUniPosTagger.xml");		
	}
}
