/**
 * 
 */
/**
 * @author simos
 *
 */
package beans;


import org.biojava.bio.seq.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.dist.*;

import java.util.*;

public class RichPyrimidine {

	public double[] motif_RichPyrimidine(String str, Vector <String> Kmers) { //void main(String[] args){ //
		double[] DistributionResidue = new double[Kmers.size()];
		
//		try {
////			System.out.println( str );
//			//make a DNA SymbolList
//			SymbolList dna = DNATools.createDNA(str);
//			SymbolList[] sla = {dna};
//
//			//get a DistributionTrainerContext
//			DistributionTrainerContext dtc = new SimpleDistributionTrainerContext();
//
//			//make three Distributions
//			Distribution dnaDist =
//					DistributionFactory.DEFAULT.createDistribution(dna.getAlphabet());
//
//
//
//			//register the Distributions with the trainer
//			dtc.registerDistribution(dnaDist);
//
//			//for each Sequence
//			for (int i = 0; i < sla.length; i++) {
//				//count each Symbol to the appropriate Distribution
//				for(int j = 1; j <= sla[i].length(); j++){
//					dtc.addCount(dnaDist, sla[i].symbolAt(j), 1.0);
//				}
//			}
//
//			//train the Distributions
//			dtc.train();
//
//			//print the weights of each Distribution
//				int k=0;
//				for (Iterator iter = ((FiniteAlphabet)dnaDist.getAlphabet()).iterator();
//						iter.hasNext(); ) {
//					Symbol sym = (Symbol)iter.next();
////					System.out.println(sym.getName()+" : "+dnaDist.getWeight(sym)); 
//					
//					// put the nucleotide in the right position
//					if (sym.getName().equals("adenine"))
//						DistributionResidue[0]= dnaDist.getWeight(sym);
//					else if (sym.getName().equals("guanine"))
//						DistributionResidue[1]= dnaDist.getWeight(sym);
//					else if (sym.getName().equals("thymine"))
//						DistributionResidue[2]= dnaDist.getWeight(sym);
//					else if (sym.getName().equals("cytosine"))
//						DistributionResidue[3]= dnaDist.getWeight(sym);
//					
//					
//				}
////				String DistrReturn= Double.toString(DistributionResidue[0])+", "+  //adenine
////									Double.toString(DistributionResidue[1])+", "+  //guanine
////									Double.toString(DistributionResidue[2])+", "+  //thymine
////									Double.toString(DistributionResidue[3])+", ";  //cytosine
////			
////			System.out.println(DistrReturn);
//		}
//		catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		
//		


		int Size=Kmers.size();
		for (int i=0;i<Size;i++){
			double distr=1.0;
			int len=Kmers.get(i).length();
			int plhthos = (str.length()-len+1);
			for (int j=0;j<plhthos;j++){
				if (str.substring(j, j+len).equals(Kmers.get(i))){
					distr+=1.0;
				}	
			}
			DistributionResidue[i]=distr/plhthos;
		}
		return DistributionResidue;
	}
	
	public static void main(String[] args){ //
		String str= "CTGCCCTCTTCCCAACCTCCCGAGGGCCTGTGTGGCAGCGCCGCCCCCTGCCCCAACCTTCCCCACACCTCAGCTCACCTGGGTCCCACCCCTCACACCCTCCCCACCCCCTCCAGCCCCAGCCTCTGCCCCCCACTCACCCTCTCTCCCCCACCCGCACCAGACCTGGTGCTCGAGACCTTCCGGAAGAGCCTGCGCGGCCAGAAGATGTTGCCTCTGCTGTCCCAGCGCCGCTTCGTGCTCCTGCACAACGGTGAGGCCGACCCGCGGCCGCACCTGGGGGGCTCGTGCAGCCTCCGCCGCTGGCCGCCCCTGCCCACCCGCCAGGCCAAGTCCTCCGGGCCACCCATGCCGCATGCCCC";
		str = str.substring(str.length()/2 -55, str.length()/2-3);
		System.out.println( str );
//		RichPyrimidine rch =new RichPyrimidine();
//
//		System.out.println (rch.motif_RichPyrimidine(str));
		Vector <String> Basis =new Vector <String>();
		Basis.add("A");Basis.add("T");Basis.add("G");Basis.add("C");
		Vector <String> Kmers = new Vector <String>();
		
		int k_mer=3;
		CreateKmer(k_mer, Kmers, Basis);

		int Size=Kmers.size();
		for (int i=0;i<Size;i++){
			double distr=0.0;
			int len=Kmers.get(i).length();
			for (int j=0;j<str.length()-len+1;j++){
				if (str.substring(j, j+len).equals(Kmers.get(i))){
					distr+=1.0;
				}
					
			}
			
			System.out.println(i+"---"+Kmers.get(i)+"-->"+ distr/(str.length()-len+1));
		}
		
		
	}
	
	public static void CreateKmer(int k_mer, Vector <String> Kmers, Vector <String> Basis){
		
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