package de.hda.iw.textmining;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.util.Date;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
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
		CollectionReaderDescription scientificreader = createReaderDescription(TextReader.class,
				TextReader.PARAM_SOURCE_LOCATION, "input/training/scientific/**/*", TextReader.PARAM_LANGUAGE, "en");

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

		ArffWriter arff = new ArffWriter();
		int count = 0;
		Date start = new Date();
		System.out.println(start.toString() + " Start");
		for (JCas jcas : SimplePipeline.iteratePipeline(scientificreader, pipeline)) {
			Date time = new Date();
			Statistics stats = new Statistics(jcas);
			System.out.println(time.toString() + " scientific: " + count++ + " " + stats.getTokenCount());
			arff.addData(stats, "yes");
			if (count > 500)
				break;
		}

		CollectionReaderDescription nonscientificreader = createReaderDescription(TextReader.class,
				TextReader.PARAM_SOURCE_LOCATION, "input/training/non-scientific/**/*", TextReader.PARAM_LANGUAGE,
				"en");

		count = 0;
		for (JCas jcas : SimplePipeline.iteratePipeline(nonscientificreader, pipeline)) {
			Date time = new Date();
			Statistics stats = new Statistics(jcas);
			System.out.println(time.toString() + " non-scientific: " + count++ + " " + stats.getTokenCount());
			arff.addData(stats, "no");
			if (count > 500)
				break;

		}
		arff.write();
		Date end = new Date();
		System.out.println(end.toString() + " Ende");
	}
}
