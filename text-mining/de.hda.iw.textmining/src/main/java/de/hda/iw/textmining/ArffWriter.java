package de.hda.iw.textmining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ArffWriter {

	/**
	 * 
	 */
	private String data = "";

	public void addData(Statistics stats, String bool) {
		this.data += stats.getTokenCount() + "\t," 
//				+ stats.getLemmaCount() + "\t," 
				+ stats.getNounCount() + "\t,"
				+ stats.getNounRate() + "\t," + stats.getAdjectiveCount() + "\t," + stats.getAdjectiveRate() + "\t,"
				+ stats.getAdverbCount() + "\t," + stats.getAdverbRate() + "\t," + stats.getVerbCount() + "\t,"
				+ stats.getVerbRate() + "\t," + stats.getNumberCount() + "\t," + stats.getNumberRate() + "\t,"
				+ stats.getSymbolCount() + "\t," + stats.getSymbolRate() + "\t," + stats.getForeignWordCount() + "\t,"
				+ stats.getForeignWordRate() + "\t," + stats.getSentenceCount() + "\t," + stats.getParagraphCount() + "\t,"
				+ stats.getAvgTokensPerSentence() + "\t," 
//				+ stats.getAvgSentencesPerParagraph() + "\t," 
				+ bool + "\r\n";
	}

	/**
	 * Schreibt die Daten im arff-Format
	 * 
	 * @throws IOException
	 * @return void
	 */
	public void write() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("./output/scientific.arff"));
		writer.write("@RELATION scientific\r\n");
		writer.write("\r\n");
		writer.write("@ATTRIBUTE tokencount NUMERIC\r\n");
//		writer.write("@ATTRIBUTE lemmacount NUMERIC\r\n");
		writer.write("@ATTRIBUTE nouncount NUMERIC\r\n");
		writer.write("@ATTRIBUTE nounrate NUMERIC\r\n");
		writer.write("@ATTRIBUTE adjectivecount NUMERIC\r\n");
		writer.write("@ATTRIBUTE adjectiverate NUMERIC\r\n");
		writer.write("@ATTRIBUTE adverbcount NUMERIC\r\n");
		writer.write("@ATTRIBUTE adverbrate NUMERIC\r\n");
		writer.write("@ATTRIBUTE verbcount NUMERIC\r\n");
		writer.write("@ATTRIBUTE verbrate NUMERIC\r\n");
		writer.write("@ATTRIBUTE numbercount NUMERIC\r\n");
		writer.write("@ATTRIBUTE numberrate NUMERIC\r\n");
		writer.write("@ATTRIBUTE symbolcount NUMERIC\r\n");
		writer.write("@ATTRIBUTE symbolrate NUMERIC\r\n");
		writer.write("@ATTRIBUTE foreignwordcount NUMERIC\r\n");
		writer.write("@ATTRIBUTE foreignwordrate NUMERIC\r\n");
		writer.write("@ATTRIBUTE sentencecount NUMERIC\r\n");
		writer.write("@ATTRIBUTE paragraphcount NUMERIC\r\n");
		writer.write("@ATTRIBUTE tokenpersentence NUMERIC\r\n");
//		writer.write("@ATTRIBUTE sentenceperparagraph NUMERIC\r\n");
		writer.write("@ATTRIBUTE scientific {yes,no}\r\n");
		writer.write("\r\n");
		writer.write("@DATA\r\n");
		writer.write(this.data);
		writer.flush();
		writer.close();

	}
}
