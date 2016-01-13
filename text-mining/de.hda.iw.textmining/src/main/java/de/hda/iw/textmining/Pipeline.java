package de.hda.iw.textmining;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNameFinder;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stopwordremover.StopWordRemover;

/**
 * Simple pipeline with a reader but no writer, as it could be used when
 * embedding it into an application. The processed text is tokenizer and tagger
 * with part-of-speech information.
 */
public class Pipeline {
	public static final String PARAM_USE_STOPWORDS = "useStopwords";
	@ConfigurationParameter(name = PARAM_USE_STOPWORDS, mandatory = true, defaultValue = "true")
	private boolean useStopWords;

	public static void main(String[] args) throws Exception {
		//Trainings-Daten
		CollectionReaderDescription scientificTrainingReader = createReaderDescription(
				TextReader.class,
				TextReader.PARAM_SOURCE_LOCATION, "input/training/scientific/**/*", 
				TextReader.PARAM_LANGUAGE, "en");
		CollectionReaderDescription nonscientificTrainingReader = createReaderDescription(
				TextReader.class,
				TextReader.PARAM_SOURCE_LOCATION, "input/training/non-scientific/**/*", 
				TextReader.PARAM_LANGUAGE, "en");

		//Test-Daten
	    CollectionReaderDescription scientificTestReader = createReaderDescription(
	    		TextReader.class,
				TextReader.PARAM_SOURCE_LOCATION, "input/test/scientific/**/*", 
				TextReader.PARAM_LANGUAGE, "en");
		CollectionReaderDescription nonscientificTestReader = createReaderDescription(
				TextReader.class,
				TextReader.PARAM_SOURCE_LOCATION, "input/test/non-scientific/**/*", 
				TextReader.PARAM_LANGUAGE, "en");

		//Analyse
		AnalysisEngineDescription pipeline = createEngineDescription(
				createEngineDescription(OpenNlpSegmenter.class), 
				createEngineDescription(StopWordRemover.class,
						StopWordRemover.PARAM_MODEL_LOCATION, 
						"[en]input/stop-word-list.txt"),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(LanguageToolLemmatizer.class),
				createEngineDescription(OpenNlpNameFinder.class,
						OpenNlpNameFinder.PARAM_VARIANT, "person"),
				createEngineDescription(OpenNlpNameFinder.class,
						OpenNlpNameFinder.PARAM_VARIANT, "location"),
				createEngineDescription(OpenNlpNameFinder.class,
						OpenNlpNameFinder.PARAM_VARIANT, "organization")
		);

		// Datensatz-Beschränkung (0=alle)
		int max = 1000;
		
		//Ausführung beginnen
		Date start = new Date();

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd.HHmmss");
	    String startDate = sdfDate.format(start);
		System.out.println(start.toString() + " Start");
		
		//Arff-Datei für Trainings-Daten erstellen
		int count = 0;
		ArffWriter trainingArff = new ArffWriter();
		for (JCas jcas : SimplePipeline.iteratePipeline(scientificTrainingReader, pipeline)) {
			Date time = new Date();
			Statistics stats = new Statistics(jcas);
			System.out.println(time.toString() + " scientific (train):" + count++ + " " + stats.getTokenCount());
			trainingArff.addData(stats, "yes");
			if (max > 0 && count > max)
				break;
		}
		count = 0;
		for (JCas jcas : SimplePipeline.iteratePipeline(nonscientificTrainingReader, pipeline)) {
			Date time = new Date();
			Statistics stats = new Statistics(jcas);
			System.out.println(time.toString() + " non-scientific (train): " + count++ + " " + stats.getTokenCount());
			trainingArff.addData(stats, "no");
			if (max > 0 && count > max)
				break;
		}
		// Trainings-Datei schreiben
	    trainingArff.write( "training." + startDate );

		//Arff-Datei für Test-Daten erstellen
		count = 0;
		ArffWriter testArff = new ArffWriter();
		for (JCas jcas : SimplePipeline.iteratePipeline(scientificTestReader, pipeline)) {
			Date time = new Date();
			Statistics stats = new Statistics(jcas);
			System.out.println(time.toString() + " scientific (test):" + count++ + " " + stats.getTokenCount());
			testArff.addData(stats, "yes");
			if (max > 0 && count > max)
				break;
		}
		count = 0;
		for (JCas jcas : SimplePipeline.iteratePipeline(nonscientificTestReader, pipeline)) {
			Date time = new Date();
			Statistics stats = new Statistics(jcas);
			System.out.println(time.toString() + " non-scientific (test): " + count++ + " " + stats.getTokenCount());
			testArff.addData(stats, "no");
			if (max > 0 && count > max)
				break;
		}
		// Test-Datei schreiben
	    testArff.write( "test." + startDate );


		// Ausführung beenden
		Date end = new Date();
		System.out.println(end.toString() + " Ende");
	}
}
