package beans;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

public class motif_donor_position {
	private static double score=0.0;

	public static double Score(String str){
//		String str="AATGCAATATGGATTTTCTAAAAAATATGGTTGAAATCTCGTTGAATATGGTTGAAATCTAATTAGACGACTAAGCGTTTATCTGAGAAACTGGAAAAACCTAAAAAAAATCTGAAAATTTTCAGTTTTGTGTGGAAAAAATCAATGAAAAACTCAATCCTACAGTAATTTAAAAATTCTTTTTCACTAAAAAAATCAGTTCCCATTGAAAAAACAAACTGAAAATCAATTATTTCAGCTCTCAAATGGCAGTTGGGGCAATTCGGAGCACTCGACCGTCTTCACAATCGCCGCCTTGCTCTTTCTGCTCGTCATTTTTCTGCTTTTCGTCGCGATTTTCTACCAAATCGGAAATCTTCTCATCCCGCACATCGTCATGCAGATTCTGCTCGTTTTGT";
		str=str.substring(str.length()/2-5, str.length()/2+1);
//		System.out.println( str);

		String targetSeq =  str;
		DNASequence target = null;
		try {
			target = new DNASequence(targetSeq,AmbiguityDNACompoundSet.getDNACompoundSet());
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
		
 
		String querySeq = "MRGGTRAG";
		DNASequence query = null;
		try {
			query = new DNASequence(querySeq,AmbiguityDNACompoundSet.getDNACompoundSet());
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
	
 
		SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
		SimpleGapPenalty gapP = new SimpleGapPenalty();
		gapP.setOpenPenalty((short)5);
		gapP.setExtensionPenalty((short)2); 

		score = ((Alignments.getPairwiseAligner(query, target, PairwiseSequenceAlignerType.LOCAL, gapP, matrix).getScore()));
//		System.out.println(getScore());
		return getScore();
	}

	public static double getScore() {
		return score;
	}

	public void setScore(double score) {
		motif_donor_position.score = score;
	}
}
