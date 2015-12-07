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
//                TextReader.PARAM_SOURCE_LOCATION, "input/masc/spoken/face-to-face/*",
                TextReader.PARAM_LANGUAGE, "en");
        
        AnalysisEngineDescription pipeline = createEngineDescription(
                createEngineDescription(OpenNlpSegmenter.class),
//                createEngineDescription(
//                		StopWordRemover.class,
//        				StopWordRemover.PARAM_MODEL_LOCATION, 
//        				"[en]input/stop-word-list.txt" ),
                createEngineDescription(OpenNlpPosTagger.class),
                createEngineDescription(LanguageToolLemmatizer.class)
//                createEngineDescription(StanfordNamedEntityRecognizer.class)
        );

        for (JCas jcas : SimplePipeline.iteratePipeline(reader, pipeline)) {
        	Arff a = new Arff(jcas);
        	a.print();
        }
    }
    
//	public static String writeHeader() throws IOException
//    {
//		// Create the Arff header
//		StringBuilder arff = new StringBuilder();
//		arff.append("@relation temp-relation" + LF);
//		arff.append(LF);    	
//
//		for (File file : csvFiles)
//		{			
//			String feature = file.getParentFile().getName() + "/" + file.getName().substring(0, file.getName().length() - 4);
//			// feature = feature.replaceAll(",", "");
//			
//			// Add the attribute to the Arff header
//			arff.append("@attribute " + feature + " numeric" + LF);
//			
//			// Read data
//			List<String> lines = FileUtils.readLines(file);
//			for (int doc = 1; doc <= lines.size(); doc++)
//			{
//				String line = lines.get(doc - 1);
//				
//				if (line.length() > 0)	// Ignore empty lines
//				{
//					double value = Double.parseDouble(line);	// There's just the score on the line, nothing else.
//					
//					// Get doc object in data list
//					List<Double> docObj;
//					if (data.containsKey(doc))
//						docObj = data.get(doc);
//					else
//						docObj = new ArrayList<Double>();
//					
//					// Put data
//					docObj.add(value);
//					data.put(doc, docObj);
//				}
//			}
//		}
//		
//		// Add gold attribute to attribute list in header
//		// We also need to do this for unlabeled data
//		arff.append("@attribute gold real" + LF);
//		
//		// Add gold similarity score 
//		List<String> lines;
//		if (goldFile != null)
//		{
//			lines = FileUtils.readLines(goldFile);
//		}
//		else
//		{
//			lines = new ArrayList<String>();
//			for (int i = 0; i < FileUtils.readLines(csvFiles.iterator().next()).size(); i++)
//				lines.add("0.0");
//		}
//			
//		for (int doc = 1; doc <= lines.size(); doc++)
//		{					
//			double value = Double.parseDouble(lines.get(doc - 1));				
//			
//			List<Double> docObj = data.get(doc);
//			docObj.add(value);
//			data.put(doc, docObj);									
//		}
//		
//		// Finalize header
//		arff.append(LF);
//		arff.append("@data" + LF);
//		
//		// Write data
//		for (int i = 1; i <= data.keySet().size(); i++)
//		{			
//			String dataItem = StringUtils.join(data.get(i), ",");
//			
//			arff.append(dataItem + LF);
//		}
//		
//		
//		return arff.toString();
//    }
 }
