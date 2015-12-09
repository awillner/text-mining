package de.hda.iw.textmining;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stopwordremover.StopWordRemover;

/**
 * Simple pipeline with a reader but no writer, as it could be used when embedding it into an
 * application. The processed text is tokenizer and tagger with part-of-speech information.
 */
public class Pipeline
{
    public static final String PARAM_USE_STOPWORDS = "useStopwords";
    @ConfigurationParameter(name = PARAM_USE_STOPWORDS, mandatory = true, defaultValue = "true")
    private boolean useStopWords;

    private List<String> attributes = new ArrayList<String>();
    
	public static void main(String[] args) throws Exception
    {
        CollectionReaderDescription reader = createReaderDescription(TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "input/aan/papers_text/A00*",
                TextReader.PARAM_LANGUAGE, "en");
        
        AnalysisEngineDescription pipeline = createEngineDescription(
                createEngineDescription(OpenNlpSegmenter.class),
                createEngineDescription(
                		StopWordRemover.class,
        				StopWordRemover.PARAM_MODEL_LOCATION, 
        				"[en]input/stop-word-list.txt" ),
                createEngineDescription(OpenNlpPosTagger.class),
                createEngineDescription(LanguageToolLemmatizer.class)
//                createEngineDescription(StanfordNamedEntityRecognizer.class)
        );

        ArffWriter arff = new ArffWriter();
        for (JCas jcas : SimplePipeline.iteratePipeline(reader, pipeline)) {
        	Statistics stats = new Statistics(jcas);
        	arff.addData(stats, "yes");
        }

        CollectionReaderDescription nonscientificreader = createReaderDescription(TextReader.class,
              TextReader.PARAM_SOURCE_LOCATION, "input/masc/spoken/face-to-face/*",
              TextReader.PARAM_LANGUAGE, "en");
      
        for (JCas jcas : SimplePipeline.iteratePipeline(nonscientificreader, pipeline)) {
      	Statistics stats = new Statistics(jcas);
      	arff.addData(stats, "no");
      }
      arff.write();
    }
}
