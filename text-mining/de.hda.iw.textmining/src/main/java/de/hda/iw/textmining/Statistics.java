package de.hda.iw.textmining;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * 
 */
public class Statistics {
	private JCas jcas;
	private Integer sentenceCount;
	private Integer paragraphCount;
	private Integer namedEntitiesCount;
	private Integer tokenCount;
	private Long adjectiveCount;
	private Long adverbCount;
	private Long verbCount;
	private Long nounCount;
	private FrequencyDistribution<String> posFreq;
	private Long numberCount;
	private Long foreignWordCount;
	private Long symbolCount;
	private Long interjectionCount;

	public Statistics(JCas jcas) throws Exception {
		this.jcas = jcas;
	}

	/**
	 * Writes to counts to the console.
	 */
	public void print() {
		// Zählt die Sätze Text
		System.out.println("Anzahl Sätze: " + getSentenceCount());

		// // Ausgabe Gesamtanzahl Tokens
		// System.out.println("Anzahl Tokens (gesamt): " + getTokenCount());

		// Ausgabe Zählungen
		// System.out.println("POS (Anzahl): " + freq.toString() );
		System.out.println("Anzahl POS (gesamt): " + getPosFreq().getB());
		System.out.println("POS (Nomen): " + getNounCount());
		System.out.println("POS (Verben): " + getVerbCount());
		System.out.println("POS (Adverben): " + getAdverbCount() );
		System.out.println("POS (Adjektive): " + getAdverbCount());

		// Ausgabe Verhältnisse
		System.out.println("Anteil Nomen: " + getNounRate() + "%");
		System.out.println("Anteil Verben: " + getVerbRate() + "%");
		System.out.println("Anteil Adverben: " + getAdverbRate() + "%");
		System.out.println("Anteil Adjektive: " + getAdjectiveRate() + "%");

		System.out.println("Anteil Lemma an Tokens (gesamt): " + getLemmaRate());
	}

	/**
	 * Returns the count of the Sentences in the CAS
	 * 
	 * @return Integer
	 */
	public Integer getSentenceCount() {
		if (this.sentenceCount == null)
			this.sentenceCount = select(this.jcas, Sentence.class).size();
		return this.sentenceCount;
	}

	/**
	 * Returns the count of the Tokens in the CAS
	 * 
	 * @return Integer
	 */
	public Integer getTokenCount() {
		if (this.tokenCount == null)
			this.tokenCount = select(this.jcas, Token.class).size();
		return this.tokenCount;
	}

	/**
	 * Returns the count of the Lemmata in the CAS
	 * 
	 * @return Integer
	 */
	public Integer getLemmaCount() {
		return (int) getLemmaDistribution().getB();
	}

	/**
	 * Gibt die Frequency Distribution für POS zurück.
	 * 
	 * @return FrequencyDistribution<String>
	 */
	public FrequencyDistribution<String> getPosFreq() {
		if (this.posFreq == null) {
			this.posFreq = new FrequencyDistribution<String>();
			for (Token token : select(this.jcas, Token.class)) {
				this.posFreq.inc(token.getPos().getPosValue());
			}
		}
		return this.posFreq;
	}

	/**
	 * Zählt die Nomen im Cas.
	 * 
	 * @return Long
	 */
	public Long getNounCount() {
		if (this.nounCount == null) {
			this.nounCount = getPosFreq().getCount("NN") 
					+ getPosFreq().getCount("NNS") 
					+ getPosFreq().getCount("NNP")
					+ getPosFreq().getCount("NNPS");
		}

		return this.nounCount;
	}

	/**
	 * Zählt die Verben im Cas.
	 * 
	 * @return Long
	 */
	public Long getVerbCount() {
		if (this.verbCount == null)
			this.verbCount = getPosFreq().getCount("VB") + getPosFreq().getCount("VBD") + getPosFreq().getCount("VBG")
					+ getPosFreq().getCount("VBN") + getPosFreq().getCount("VBP") + getPosFreq().getCount("VBZ");
		return this.verbCount;
	}

	/**
	 * Zählt die Adverben im Cas.
	 * 
	 * @return Long
	 */
	public Long getAdverbCount() {
		if (this.adverbCount == null)
			this.adverbCount = this.getPosFreq().getCount("RB") + getPosFreq().getCount("RBR")
					+ getPosFreq().getCount("RBS");
		return this.adverbCount;

	}

	/**
	 * Zählt die Advjektive im Cas.
	 * 
	 * @return Long
	 */
	public Long getAdjectiveCount() {
		if (this.adjectiveCount == null)
			this.adjectiveCount = getPosFreq().getCount("JJ") + getPosFreq().getCount("JJR")
					+ getPosFreq().getCount("JJS");
		return this.adjectiveCount;
	}

	/**
	 * Zählt die Kardinalzahlen im Cas.
	 * 
	 * @return Long
	 */
	public Long getNumberCount() {
		if (this.numberCount == null)
			this.numberCount = getPosFreq().getCount("JJ") + getPosFreq().getCount("JJR")
					+ getPosFreq().getCount("JJS");
		return this.numberCount;
	}

	/**
	 * Zählt die Nomen im Cas.
	 * 
	 * @return Long
	 */
	public Long getSymbolCount() {
		if (this.symbolCount == null) {
			this.symbolCount = getPosFreq().getCount("SYM");
		}

		return this.symbolCount;
	}

	/**
	 * Zählt die Fremdwörter im Cas.
	 * 
	 * @return Long
	 */
	public Long getForeignWordCount() {
		if (this.foreignWordCount == null) {
			this.foreignWordCount = getPosFreq().getCount("SYM");
		}

		return this.foreignWordCount;
	}

	/**
	 * Zählt die Fremdwörter im Cas.
	 * 
	 * @return Long
	 */
	public Long getInterjectionCount() {
		if (this.interjectionCount == null) {
			this.interjectionCount = getPosFreq().getCount("SYM");
		}

		return this.interjectionCount;
	}

	/**
	 * Berechnung des Verhältnisses von Nomen zu allen POS
	 * 
	 * @return String
	 */
	public String getNounRate() {
		Double nounRate = (double) getNounCount() / (double) getTokenCount() * 100;
		return getRate(nounRate);
	}

	/**
	 * Berechnung des Verhältnisses von Verben zu allen POS
	 * 
	 * @return String
	 */
	public String getVerbRate() {
		Double verbRate = (double) getVerbCount() / (double) getTokenCount() * 100;
		return getRate(verbRate);
	}

	/**
	 * Berechnung des Verhältnisses von Adjectiven zu allen POS
	 * 
	 * @return String
	 */
	public String getAdjectiveRate() {
		Double adjectiveRate = (double) getAdjectiveCount() / (double) getTokenCount() * 100;
		return getRate(adjectiveRate);
	}

	/**
	 * Berechnung des Verhältnisses von Adverbien zu allen POS
	 * 
	 * @return String
	 */
	public String getAdverbRate() {
		Double adverbRate = (double) getAdverbCount() / (double) getTokenCount() * 100;
		return getRate(adverbRate);
	}

	/**
	 * Berechnung des Verhältnisses von Adverbien zu allen POS
	 * 
	 * @return String
	 */
	public String getNumberRate() {
		Double numberRate = (double) getNumberCount() / (double) getTokenCount() * 100;
		return getRate(numberRate);
	}

	/**
	 * Berechnung des Verhältnisses von Adverbien zu allen POS
	 * 
	 * @return String
	 */
	public String getSymbolRate() {
		Double symbolRate = (double) getSymbolCount() / (double) getTokenCount() * 100;
		return getRate(symbolRate);
	}

	/**
	 * Berechnung des Verhältnisses von Adverbien zu allen POS
	 * 
	 * @return String
	 */
	public String getForeignWordRate() {
		Double foreignWordRate = (double) getForeignWordCount() / (double) getTokenCount() * 100;
		return getRate(foreignWordRate);
	}

	/**
	 * Berechnung des Verhältnisses von Adverbien zu allen POS
	 * 
	 * @return String
	 */
	public String getInterjectionRate() {
		Double interjectionRate = (double) getInterjectionCount() / (double) getTokenCount() * 100;
		return getRate(interjectionRate);
	}

	/**
	 * Berechnung des Verhältnisses von Lemma zu allen POS
	 * 
	 * @return String
	 */
	public String getLemmaRate() {
		Double lemmaRate = (double) getLemmaCount() / (double) getTokenCount() * 100;
		return getRate(lemmaRate);
	}

	/**
	 * Gibt das Verhältnis auf zwei Nachkommastellen gerundet zurück.
	 * 
	 * @param number
	 * @return
	 */
	private String getRate(Double number) {
		Locale locale  = new Locale("en", "UK");
		String pattern = "###.##";

		DecimalFormat decimalFormat = (DecimalFormat)
		        NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);

		return decimalFormat.format(number);
	}

	/**
	 * Gibt die Frequency Distribution für Lemma zurück.
	 * 
	 * @return FrequencyDistribution<String>
	 */
	public FrequencyDistribution<String> getLemmaDistribution() {
		FrequencyDistribution<String> freq = new FrequencyDistribution<String>();
		for (Token token : select(this.jcas, Token.class)) {
			freq.inc(token.getLemma().getValue());
		}
		return freq;
	}

	/**
	 * Gibt an, wieviel Tokens durchschnittlich pro Satz verwendet werden.
	 * 
	 * @return
	 */
	public String getAvgTokensPerSentence() {
		Double tokenpersentence = (double) getSentenceCount() / (double) getTokenCount() * 100;
		return getRate(tokenpersentence);
	}

	/**
	 * Gibt an, wieviel Tokens durchschnittlich pro Satz verwendet werden.
	 * 
	 * @return
	 */
	public String getAvgSentencesPerParagraph() {
		Double sentenceperparagraph = (double) getSentenceCount() / (double) getParagraphCount() * 100;
		return getRate(sentenceperparagraph);
	}

	/**
	 * Gibt die Anzahl an Absätzen zurück.
	 * 
	 * @return
	 */
	public Integer getParagraphCount() {
		if (this.paragraphCount == null)
			this.paragraphCount = select(this.jcas, Paragraph.class).size();
		return this.paragraphCount;
	}

	/**
	 * Gibt die Anzahl an NamedEntities zurück.
	 * 
	 * @return
	 */
	public Integer getNamedEntityCount() {
		return (int) getNamedEntitiyDistribution().getB();
	}

	/**
	 * Gibt die Frequency Distribution für NamedEntities zurück.
	 * 
	 * @return FrequencyDistribution<String>
	 */
	public FrequencyDistribution<String> getNamedEntitiyDistribution() {
		FrequencyDistribution<String> freq = new FrequencyDistribution<String>();
		for (NamedEntity ne : select(this.jcas, NamedEntity.class)) {
			freq.inc(ne.getValue());
		}
		return freq;
	}

	/**
	 * Berechnung des Verhältnisses von NamedEntites zu Lemmata
	 * 
	 * @return String
	 */
	public String getNamedEntityRate() {
		Double verbRate = (double) getNamedEntityCount() / (double) getLemmaCount() * 100;
		return getRate(verbRate);
	}
}
