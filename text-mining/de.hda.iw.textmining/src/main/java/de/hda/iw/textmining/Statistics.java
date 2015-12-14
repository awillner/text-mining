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
	private Long adjectiveCount;
	private Long adverbCount;
	private Long verbCount;
	private Long nounCount;
	private FrequencyDistribution<String> posFreq;
	
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

        // Ausgabe Zählungen
        Long nomen = getNounCount();
		Long verben = getVerbCount();
		Long adverben = getAdverbCount();
		Long adjektive = getAdjectiveCount();
//        System.out.println("POS (Anzahl): " + freq.toString() );
		System.out.println("Anzahl POS (gesamt): " + getPosFreq().getB() );
        System.out.println("POS (Nomen): " + nomen );
        System.out.println("POS (Verben): " + verben );
        System.out.println("POS (Adverben): " + adverben );
        System.out.println("POS (Adjektive): " + adjektive );
        
        
        // Ausgabe Verhältnisse
        System.out.println("Anzahl Lemma (gesamt): " + getLemmaMap().size());
        
        FrequencyDistribution<String> lemmas = getLemmaDistribution();
        double nomenAnteil = getNounRate();
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
    public FrequencyDistribution<String> getPosFreq()
    {
        if( this.posFreq == null )
        {
			this.posFreq = new FrequencyDistribution<String>();
        	for (Token token : select(this.jcas, Token.class)) {
				this.posFreq.inc( token.getPos().getPosValue() );
			}
        }
		return this.posFreq;
    }
    
    /**
     * Zählt die Nomen im Cas.
     * 
     * @return long
     */
    public Long getNounCount()
    {
        if( this.nounCount == null )
        {
    		this.nounCount = getPosFreq().getCount( "NN" ) + getPosFreq().getCount( "NNS" ) + getPosFreq().getCount( "NNP" ) 
        	+ getPosFreq().getCount( "NNPS" );
        }
    		
    	return this.nounCount;
    }
    
    /**
     * Zählt die Verben im Cas.
     * 
     * @return long
     */
    public Long getVerbCount()
    {
        if( this.verbCount == null )
    		this.verbCount = getPosFreq().getCount( "VB" ) + getPosFreq().getCount( "VBD" ) + getPosFreq().getCount( "VBG" ) 
        		+ getPosFreq().getCount( "VBN" ) + getPosFreq().getCount( "VBP" ) + getPosFreq().getCount( "VBZ" );
    	return this.verbCount;
    }
    
    /**
     * Zählt die Adverben im Cas.
     * 
     * @return long
     */
    public Long getAdverbCount()
    {
        if( this.adverbCount == null )
    		this.adverbCount = this.getPosFreq().getCount( "RB" ) + getPosFreq().getCount( "RBR" ) + getPosFreq().getCount( "RBS" );
    	return this.adverbCount;

    }
    
    /**
     * Zählt die Advjektive im Cas.
     * 
     * @return long
     */
    public Long getAdjectiveCount()
    {
        if( this.adjectiveCount == null )
    		this.adjectiveCount = getPosFreq().getCount( "JJ" ) + getPosFreq().getCount( "JJR" ) + getPosFreq().getCount( "JJS" );
    	return this.adjectiveCount;
    }
    
    /**
     * Berechnung des Verhältnisses von Nomen zu allen Lemmata
     * 
     * @param FrequencyDistribution<String> nomen Nomenanzahl
     * @return long
     */
    public double getNounRate()
    {
        return getNounCount() / getTokenCount() ;

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
