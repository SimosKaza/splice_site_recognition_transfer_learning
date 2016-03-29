package Ngram;

import java.util.ArrayList;
import java.util.Vector;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import beans.BranchSite;
import beans.RichPyrimidine;
import beans.motif_acceptor_position;
import weka.core.Attribute;
import weka.core.ProtectedProperties;

public class attribute {
	
	public static ArrayList<Attribute> atts=new ArrayList<Attribute>();
	int k_mer = 1;
    static Vector <String> Kmers=new Vector <String>();
    
	public attribute() {}	
	
	public void Setvalues(){
	
		Vector <String> Basis =new Vector <String>();
		Basis.add("A");Basis.add("T");Basis.add("G");Basis.add("C");
		CreateKmer(k_mer, Kmers, Basis);
		
		ArrayList<String> classVal = new ArrayList<String>();
		classVal.add("1");
		classVal.add("-1");
		for(int i=0;i<1;i++){ 
			atts.add(new Attribute("negative"+i));
			atts.add(new Attribute("positive"+i));
		}

				
		atts.add(new Attribute("distrGuanineAdenine"));
		atts.add(new Attribute("distrCytosineThymine"));
		
		for (int i=0;i<Kmers.size();i++){
			atts.add(new Attribute("distr"+i+"_"+Kmers.get(i)));
		}
		
		atts.add(new Attribute("ScoreSimilarity0"));
		atts.add(new Attribute("ScoreSimilarity1"));

		atts.add(new Attribute("BranchSiteScore"));
		atts.add(new Attribute("Motif_Acceptor"));
//		atts.add(new Attribute("BranchSite_Position"));
		atts.add(new Attribute("class",classVal));
	}
	
	public String getAttributeName(){
		String str="";
		for (Attribute st:atts){
			str+=st.toString()+"_";
		}
		return str.replaceAll("@attribute ", "").replaceAll(" numeric", "").replaceAll("_@@class@@ \\{1,-1\\}_", "");
	}

	public void getValuesAtts(double[] vals, double gsCur0,	double gsCur1, 
			String sequence,String label,String meanStr0, String meanStr1) {
		
		int indexVals=0;
		vals[indexVals++]	=  gsCur0;
		vals[indexVals++]	=  gsCur1;
		
		
		BranchSite branchSiteInfo = null;
		if (atts.contains(new Attribute("distrGuanineAdenine")) | atts.contains(new Attribute("distrGuanine")) | atts.contains(new Attribute("BranchSiteScore")) ) {
			branchSiteInfo = new BranchSite();
			branchSiteInfo.BranchSite_position_and_score(sequence);
		}
		double[] DistributionResidue = null;
		if (atts.contains(new Attribute("distrGuanineAdenine")) | atts.contains(new Attribute("distrGuanine")) ) {
			RichPyrimidine rch =new RichPyrimidine();
			DistributionResidue = rch.motif_RichPyrimidine(branchSiteInfo.getSeq(), Kmers );
		}	
		if (atts.contains(new Attribute("distrGuanineAdenine"))){
			vals[indexVals++] = DistributionResidue[0]+DistributionResidue[1];
			vals[indexVals++] = DistributionResidue[2]+DistributionResidue[3];
			for (int i=0;i<DistributionResidue.length;i++){
				vals[indexVals++] = DistributionResidue[i];
			}
		}
		
		if (atts.contains(new Attribute("ScoreSimilarity0"))){
			vals[indexVals++]	= getPairwiseAligner_Score(meanStr0, sequence);
			vals[indexVals++]	= getPairwiseAligner_Score(meanStr1, sequence);
		}
		if (atts.contains(new Attribute("BranchSiteScore"))){
			vals[indexVals++] = branchSiteInfo.getScore();
		}
		if (atts.contains(new Attribute("Motif_Acceptor"))){
			motif_acceptor_position motifAcceptor = new motif_acceptor_position();
			vals[indexVals++] = motifAcceptor.Score(sequence);
		}
		if (atts.contains(new Attribute("BranchSite_Position"))){
			vals[indexVals++] = branchSiteInfo.getPosition();
		}
		
		if (label.equals("1"))
			vals[indexVals]	= 0; 
		else
			vals[indexVals] = 1;
	}
	
	private String ConvertString(String str) {
		//DNA
		if (str.length()<70)
			return str;
		else
			return(str.substring(str.length()/2 - 45, str.length()/2  + 2 ));

	}
	
	private double getPairwiseAligner_Score(String meanStr, String sequence) {
		DNASequence target = null;
		DNASequence query = null;

		try {
			target = new DNASequence(ConvertString(meanStr),AmbiguityDNACompoundSet.getDNACompoundSet());
			query = new DNASequence(ConvertString(sequence),AmbiguityDNACompoundSet.getDNACompoundSet());
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
 
		SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
		SimpleGapPenalty gapP = new SimpleGapPenalty();
		gapP.setOpenPenalty((short)5);
		gapP.setExtensionPenalty((short)2); 

		PairwiseSequenceAligner<DNASequence, NucleotideCompound> psa0 = Alignments.getPairwiseAligner(query, target, PairwiseSequenceAlignerType.GLOBAL, gapP, matrix);
		return psa0.getScore();//Distance();
	}
	
	private void CreateKmer(int k_mer, Vector <String> Kmers, Vector <String> Basis){
		
		if (k_mer==1){
			Kmers.add("A");
			Kmers.add("G");
			Kmers.add("T");
			Kmers.add("C");	
		}
		else{
			CreateKmer(k_mer-1, Kmers, Basis);
			int Size=Kmers.size();
			for (int i=0;i<Size;i++){
				for (int j=0;j<Basis.size();j++){
					if (!Kmers.contains(Kmers.get(i)+Basis.get(j)))
						Kmers.add(Kmers.get(i)+Basis.get(j));
				}
			}
			
		}
		
	}
	
}
