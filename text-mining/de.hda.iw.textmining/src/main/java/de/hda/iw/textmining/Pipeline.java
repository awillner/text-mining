package de.hda.iw.textmining;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNameFinder;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;

/**
 * Simple pipeline with a reader but no writer, as it could be used when embedding it into an
 * application. The processed text is tokenizer and tagger with part-of-speech information.
 */
public class Pipeline
{
    public static void main(String[] args) throws Exception
    {
        CollectionReaderDescription reader = createReaderDescription(TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "input/document.txt",
                TextReader.PARAM_LANGUAGE, "de");
        
        AnalysisEngineDescription pipeline = createEngineDescription(
                createEngineDescription(OpenNlpSegmenter.class),
                createEngineDescription(OpenNlpPosTagger.class)
//                createEngineDescription(OpenNlpNameFinder.class)
//                createEngineDescription(StanfordNamedEntityRecognizer.class)
        );

        for (JCas jcas : SimplePipeline.iteratePipeline(reader, pipeline)) {
        	// Zählt die Sätze Text
        	int sentenceCount = select(jcas, Sentence.class).size();
            System.out.println(sentenceCount);

            int tokenCount = 0;
        	Map<String, Integer> map = new HashMap<String, Integer>();
            for (Token token : select(jcas, Token.class)) {
            	// TODO: Unterschied von getSimpleName() und getPosValue() ?
            	String posName = token.getPos().getClass().getSimpleName();
//            	String posName = token.getPos().getPosValue();
            	if( map.get(posName) == null )
            	{
                	map.put(posName, 1 );
            	} else {
            		Integer count = map.get( posName );
            		count = count + 1;
            		map.put( posName, count );
            	}

                tokenCount = tokenCount + 1;
            }
            // Ausgabe Gesamtanzahl Tokens
            System.out.println("Anzahl Tokens (gesamt): " + tokenCount);
            // Ausgabe der Anzahl der einzelnen POS-Elemente 
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                System.out.println("Anzahl POS " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}