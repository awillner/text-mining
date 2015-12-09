package de.hda.iw.textmining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ArffWriter {

	/**
	 * 
	 */
	private String data = "";
	
	public void addData( Statistics stats, String bool )
	{
		this.data += stats.getTokenCount() + "," + bool + "\r\n";
	}
	
	/**
	 * Schreibt die Daten im arff-Format
	 * 
	 * @throws IOException
	 * @return void
	 */
	public void write()
			throws IOException 
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("./output/scientific.arff"));
		writer.write("@RELATION scientific\r\n");
		writer.write("\r\n");
		writer.write("@ATTRIBUTE tokencount NUMERIC\r\n");
		writer.write("@ATTRIBUTE scientific {yes,no}\r\n");
		writer.write("\r\n");
		writer.write("@DATA\r\n");
		writer.write( this.data );
		writer.flush();
		writer.close();
		
	}
}
