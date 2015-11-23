package de.hda.iw.textmining;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
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
                TextReader.PARAM_SOURCE_LOCATION, "input/*",
                TextReader.PARAM_LANGUAGE, "en");
        
        AnalysisEngineDescription pipeline = createEngineDescription(
                createEngineDescription(OpenNlpSegmenter.class),
                createEngineDescription(OpenNlpPosTagger.class)
//                createEngineDescription(OpenNlpNameFinder.class)
//                createEngineDescription(StanfordNamedEntityRecognizer.class)
        );

        for (JCas jcas : SimplePipeline.iteratePipeline(reader, pipeline)) {
        	// Zählt die Sätze Text
            System.out.println("Anzahl Sätze: " + countSentence(jcas));

            int tokenCount = countTokens(jcas);
            
        	
            // Ausgabe Gesamtanzahl Tokens
            System.out.println("Anzahl Tokens (gesamt): " + tokenCount);
            // Ausgabe der Anzahl der einzelnen POS-Elemente 
            Map<String,Integer> posmap = getPOSMap(jcas);
            for (Map.Entry<String, Integer> entry : posmap.entrySet()) {
                Double percentage = ( (double)entry.getValue() / (double)tokenCount ) * 100;
                DecimalFormat numberFormat = new DecimalFormat("#.0");
            	System.out.println("Anzahl POS " + entry.getKey() + ": " + entry.getValue());
                System.out.println(
                		"Prozentualer Anteil des POS " + entry.getKey() 
                		+ ": " + ( numberFormat.format(percentage) ) 
                		+ "%");
            }
        }
    }
    
    /**
     * Returns the count of the Sentences in the Document(s)
     * 
     * @param jcas
     * @return Integer
     */
    private static Integer countSentence( JCas jcas )
    {
    	return select(jcas, Sentence.class).size();
    }
    
    /**
     * Returns the count of the Tokens in the Document(s)
     * 
     * @param jcas
     * @return Integer
     */
    private static Integer countTokens( JCas jcas)
    {
    	return select(jcas, Token.class).size();
    }

    private static Map<String, Integer> getPOSMap( JCas jcas )
    {
    	Map<String, Integer> map = new HashMap<String, Integer>();
        for (Token token : select(jcas, Token.class)) {
        	// TODO: Unterschied von getSimpleName() und getPosValue() ?
        	String posName = token.getPos().getClass().getSimpleName();
//        	String posName = token.getPos().getPosValue();

//        	String tokenValue = token.getCoveredText().trim();
//        	Pattern p = Pattern.compile("\\W+\\w+|\\w+\\W+");
//        	Matcher m = p.matcher(tokenValue);
//        	if(m.find())
//        	{
//        		//System.out.println(tokenValue);
//        	}
        	if( map.get(posName) == null )
        	{
            	map.put(posName, 1 );
        	} else {
        		Integer count = map.get( posName );
        		count = count + 1;
        		map.put( posName, count );
        	}
        }
        return map;
    }
}
