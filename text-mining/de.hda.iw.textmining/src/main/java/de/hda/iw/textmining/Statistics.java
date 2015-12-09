package de.hda.iw.textmining;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * 
 */
public class Statistics
{
	private JCas jcas;
	private Integer sentenceCount;
	private Integer paragraphCount;
	private Integer tokenCount;
	private Map<String, Integer> tokenMap;
	private Integer lemmaCount;
	private Map<String, Integer> lemmaMap;
	private Integer posCount;
	private Map<String, Integer> posMap;
	private Integer namedEntityCount;
	private Map<String, Integer> namedEntityMap;
	
    public static final String PARAM_USE_STOPWORDS = "useStopwords";
    @ConfigurationParameter(name = PARAM_USE_STOPWORDS, mandatory = true, defaultValue = "true")
    private boolean useStopWords;

	public Statistics(JCas jcas) throws Exception
    {
    	this.jcas = jcas;
    }

	/**
	 * Writes to counts to the console.
	 */
	public void print()
	{
		// Zählt die Sätze Text
        System.out.println("Anzahl Sätze: " + getSentenceCount());

//        // Ausgabe Gesamtanzahl Tokens
//        System.out.println("Anzahl Tokens (gesamt): " + getTokenCount());

        // Ausgabe Gesamtanzahl Tokens
        FrequencyDistribution<String> pos = getPOSDistribution();
        System.out.println("Anzahl POS (gesamt): " + pos.getB() );
//        System.out.println("POS (Anzahl): " + freq.toString() );
        long nomen = pos.getCount( "NN" ) + pos.getCount( "NNS" ) + pos.getCount( "NNP" ) + pos.getCount( "NNPS" );
        System.out.println("POS (Nomen): " + nomen );
        
        
        // Ausgabe Gesamtanzahl Tokens
        System.out.println("Anzahl Lemma (gesamt): " + getLemmaMap().size());

        FrequencyDistribution<String> lemmas = getLemmaDistribution();
        Double p = ( (double)lemmas.getB() / (double)getTokenCount() ) * 100;
        DecimalFormat numberFormat = new DecimalFormat("#.0");
        System.out.println("Anteil Lemma an Tokens (gesamt): " + numberFormat.format(p) );
        System.out.println( lemmas.toString() );

//        // Ausgabe der Anzahl der einzelnen POS-Elemente 
//        for (Map.Entry<String, Integer> entry : getLemmaMap().entrySet()) {
//            Double percentage = ( (double)entry.getValue() / (double)tokenCount ) * 100;
//            //DecimalFormat numberFormat = new DecimalFormat("#.0");
//        	if( entry.getValue() > 5 )
//        	{
//                System.out.println("Anzahl Lemma " + entry.getKey() + ": " + entry.getValue());
//                System.out.println(
//                		"Prozentualer Anteil des Lemma " + entry.getKey() 
//                		+ ": " + ( numberFormat.format(percentage) ) 
//                		+ "%");
//        	}
//        }
	}
	
    /**
     * Returns the count of the Sentences in the CAS
     * 
     * @return Integer
     */
    public Integer getSentenceCount()
    {
    	if( this.sentenceCount == null )
    		this.sentenceCount = select(this.jcas, Sentence.class).size();
    	return this.sentenceCount;
    }
    
    /**
     * Returns the count of the Tokens in the CAS
     * 
     * @return Integer
     */
    public Integer getTokenCount()
    {
    	if( this.tokenCount == null )
    		this.tokenCount = select(this.jcas, Token.class).size();
    	return this.tokenCount;
    }

    /**
     * Returns the distinct POS-Count of the CAS
     * 
     * @return Map<String, Integer>
     */
    public Map<String, Integer> getPOSMap()
    {
    	if( this.posMap == null )
    	{
    		this.posMap = new HashMap<String, Integer>();
    		for (Token token : select(this.jcas, Token.class)) {
    			// TODO: Unterschied von getSimpleName() und getPosValue() ?
    			//String posName = token.getPos().getClass().getSimpleName();
    			String posName = token.getPos().getPosValue();
    			
    			if( this.posMap.get(posName) == null )
    			{
    				this.posMap.put(posName, 1 );
    			} else {
    				Integer count = this.posMap.get( posName );
    				count = count + 1;
    				this.posMap.put( posName, count );
    			}
    		}
    	}
        return this.posMap;
    }

    /**
     * Gibt die Frequency Distribution für POS zurück.
     * 
     * @return FrequencyDistribution<String>
     */
    public FrequencyDistribution<String> getPOSDistribution()
    {
		FrequencyDistribution<String> freq = new FrequencyDistribution<String>();
		for (Token token : select(this.jcas, Token.class)) {
			freq.inc( token.getPos().getPosValue() );
		}
		return freq;
    }
    
    /**
     * Returns the distinct POS-Count of the CAS
     * 
     * @return Map<String, Integer>
     */
    public Map<String, Integer> getLemmaMap()
    {
    	if( this.lemmaMap == null )
    	{
    		this.lemmaMap = new HashMap<String, Integer>();
    		for (Lemma lemma: select(this.jcas, Lemma.class)) {
    			String l = lemma.getCoveredText();
    			
    			if( this.lemmaMap.get(l) == null )
    			{
    				this.lemmaMap.put(l, 1 );
    			} else {
    				Integer count = this.lemmaMap.get( l );
    				count = count + 1;
    				this.lemmaMap.put( l, count );
    			}
    		}
    	}
        return this.lemmaMap;
    }

    /**
     * Gibt die Frequency Distribution für Lemma zurück.
     * 
     * @return FrequencyDistribution<String>
     */
    public FrequencyDistribution<String> getLemmaDistribution()
    {
		FrequencyDistribution<String> freq = new FrequencyDistribution<String>();
		for (Token token: select(this.jcas, Token.class)) {
			freq.inc( token.getLemma().getValue() );
		}
		return freq;
    }
}
