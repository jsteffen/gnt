package gnt;

import java.io.IOException;

import caller.GNTaggerStandalone;

public class TestRunTaggerStandalone {
	
public static void main(String[] args) throws IOException{
	GNTaggerStandalone runner = new GNTaggerStandalone();
	runner.initRunner("resources/models/model_DEMORPH_2_0iw-1sent_FTTT_MCSVM_CS.zip");
		
	System.out.println("Tag text: ");
	runner.tagItRunner(
			"Breyer forderte einen detaillierten Plan zur Reparatur der rund 580.000 Dieselwagen , die in den Vereinigten Staaten von der Affäre um manipulierte "
			+ "Emissionswerte betroffen sind . Die Frist für die detaillierte Einigung setzte Breyer auf den 21. Juni . Ob ein Deal in den USA sich anschließend "
			+ "auf die Situation in Europa mit etwa 8,5 Millionen Fahrzeugen übertragen ließe , gilt jedoch als fraglich . "
			+ "Weltweit sind rund elf Millionen Fahrzeuge betroffen . "
			+ "Der für Hunderte Zivilklagen wegen Verstößen gegen US-Gesetze zuständige Breyer hatte bis zum Donnerstag eine Lösung verlangt . "
			+ "Er hatte Volkswagen und der US-Umweltbehörde EPA eine Frist dafür gesetzt, die schon ein Mal verlängert worden war . "
			+ "Angesichts der erwarteten Milliardenkosten für die Einigung dürfte Volkswagen für das vergangene Geschäftsjahr aller Voraussicht "
			+ "nach einen großen Verlust verbuchen . Die Eckdaten hierzu sollen nach einer Sitzung des VW-Aufsichtsrats am Freitag veröffentlicht werden . "
			+ "Bereits am Mittwoch hatte es Meldungen gegeben , dass sich Volkswagen mit den US-Behörden auf einen Vergleich geeinigt habe . "
			+ "Demnach wolle der Konzern den betroffenen Kunden in Nordamerika 5000 Dollar Entschädigung zahlen . "
			+ "Davon unabhängig müsse VW die Kosten für die Umrüstung der jeweiligen Autos tragen . "
			+ "Auch für VW-Kunden in Deutschland fordern Verbraucherschützer eine gerechte Entschädigung : "
			+ "Wenn Volkswagen geschädigten Kunden in den USA 5000 Dollar zahlt , steigt die Ungerechtigkeit gegenüber deutschen Kunden . "
			+ "Auch betroffene VW-Kunden in Deutschland erwarten eine unkomplizierte Lösung "
			+ "sagte Klaus Müller , Chef des Verbraucherzentrale Bundesverbands . ");		
	}

}
