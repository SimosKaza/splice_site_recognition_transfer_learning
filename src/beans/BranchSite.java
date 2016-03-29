package beans;


import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.alignment.template.SequencePair;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.utils.regex.Matcher;
import org.biojava.utils.regex.PatternFactory;

public class BranchSite {
	
	private int position;
	private String seq;
	private double score=0.0;
	public String value;


	public static void BranchSite_position_regex(String str) {
		try {
			// ****************************************************************
			//example if there is the motif like regex
			// ****************************************************************
//			String str= "TGTGTCTAGTTCATTATCTCCATTTCCTGGTACCATGCCTAGCAGGTGGTAGACTTCACTCAATAAATATCTGGTGAATCAGTGTCTTTCTGGAAATTTATACATAACTCCTTGAATGAGGCAACATGCTTCTGCACTATTTAATAAGAAAAACTAAGTCAAGTTTCTAATCTGTGGTTTTGTTTTGCTTTGAGAACAGGGTCTTGGCTATGTTGCCCTGTCTGGTCTCGAACTTCTGATCTCGAGCGATCACTCCCACCTCTCCCTTCCTAAGTGTTGGGATTAACAGGAGTGAGCCACCGCGCCCGGCCAAGTCTCCAACTTGCTCGAGGTCACTGAACTTGCAATTGAAGAAGCTACGATTCGAACCCATGTCTGTAGGATCCCAGAGCCCTCAT";
	
			str = str.substring(str.length()/2 -55, str.length()/2-15);
			
			// Variables needed...
			Matcher occurences;
			FiniteAlphabet IUPAC = DNATools.getDNA();
			SymbolList WorkingSequence = DNATools.createDNA(str);

			// Create pattern using pattern factory.
			org.biojava.utils.regex.Pattern pattern;
			PatternFactory FACTORY = PatternFactory.makeFactory(IUPAC);
			try{
				pattern = FACTORY.compile("ynyyray");
			} catch(Exception e) {e.printStackTrace(); return;}
			System.out.println("Searching for: "+pattern.patternAsString());

			// Obtain iterator of matches.
			try {
				occurences = pattern.matcher( WorkingSequence );
			} catch(Exception e) {e.printStackTrace(); return;}

			
			while( occurences.find() ) {
				System.out.println("Match: " +"\t"+ WorkingSequence.seqString() 
						+"\n"+ occurences.start() +"\t"+ occurences.group().seqString());
				str=occurences.group().seqString();
			}		
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public void BranchSite_position_and_score(String str){
		
//		str=str.substring(str.length()/2 -55, str.length()/2-15);
//		System.out.println( str);
		String targetSeq =  str.substring(str.length()/2 -55, str.length()/2-15);
		DNASequence target = null;
		try {
			target = new DNASequence(targetSeq,AmbiguityDNACompoundSet.getDNACompoundSet());
		} catch (CompoundNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
 
		String querySeq = "ynyyray"; //ynyyray
		DNASequence query = null;
		try {
			query = new DNASequence(querySeq,AmbiguityDNACompoundSet.getDNACompoundSet());
		} catch (CompoundNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
 
		SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
		SimpleGapPenalty gapP = new SimpleGapPenalty();
		gapP.setOpenPenalty((short)5);
		gapP.setExtensionPenalty((short)2); 

		SequencePair<DNASequence, NucleotideCompound> psa =	Alignments.getPairwiseAlignment(query, target,PairwiseSequenceAlignerType.LOCAL, gapP, matrix);
//		System.out.println(psa);
//		System.out.println(psa.getIndexInTargetAt(1));
//		System.out.println(Alignments.getPairwiseAligner(query,	target,PairwiseSequenceAlignerType.LOCAL, gapP, matrix).getScore());
		
		setPosition(psa.getIndexInTargetAt(1));
		PairwiseSequenceAligner<DNASequence, NucleotideCompound> psa1 = Alignments.getPairwiseAligner(query, target, PairwiseSequenceAlignerType.LOCAL, gapP, matrix);
		
		value= (((psa1.getScore())))+"";
//		value= Math.log(psa1.getScore()/psa.getIndexInTargetAt(1))+"";	
//		value= Math.log((psa1.getSimilarity()/psa.getIndexInTargetAt(1)))+psa1.getScore()+"";
	
		setScore(Double.parseDouble(value));
		
//		setSeq(str.substring(str.length()/2 - 55 , str.length()/2));
		setSeq(str.substring(str.length()/2 - 55 + position, str.length()/2-2)); //3 is the motif around of the acceptor e.g. "YAGR" 

	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}


}
