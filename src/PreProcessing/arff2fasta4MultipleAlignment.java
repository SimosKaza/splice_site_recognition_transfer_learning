package PreProcessing;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.alignment.template.Profile;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.util.ConcurrencyTools;

public class arff2fasta4MultipleAlignment {

	public static void arff2fasta() throws IOException {
		String path="/home/simos/workspace/Master_thesis/Data/common/data/";
		final File DataFolder = new File(path);
		String pathChanceFile = "/home/simos/workspace/Master_thesis/Data/common/data/fasta/";

		for (final File fileEntry : DataFolder.listFiles()) {
			if (!fileEntry.isDirectory() & fileEntry.getName().contains("acc")) {

				// Read file
				BufferedReader br = new BufferedReader(new FileReader(path+fileEntry.getName()), 500);
				// write file
				OutputStreamWriter outFewlines = new OutputStreamWriter(new FileOutputStream(new File(pathChanceFile+fileEntry.getName().replace(".arff", ".fasta"))));

				String sCurrentLine;
				int i= 0;
				sCurrentLine = br.readLine();
				sCurrentLine = br.readLine();
				sCurrentLine = br.readLine();
				sCurrentLine = br.readLine();
				while ( ((sCurrentLine = br.readLine()) != null)  ) {
					outFewlines.write("> "+ i++ +" label="+sCurrentLine.split(",")[1]+"\n"+sCurrentLine.split(",")[0].substring(145,199)+"\n");
				}
				br.close();
				outFewlines.close();


			}
		}

	}
	
	public static void fastat2arff() throws IOException {
		String pathChanceFile = "/home/simos/workspace/Master_thesis/Data/common/data/fasta/";
		final File DataFolder = new File(pathChanceFile);

		for (final File fileEntry : DataFolder.listFiles()) {
			if (!fileEntry.isDirectory() & fileEntry.getName().contains("acc_alignment.fasta")) {
				
				// Read file
				BufferedReader br = new BufferedReader(new FileReader(pathChanceFile+fileEntry.getName()), 500);
				// write file
				OutputStreamWriter outFewlines = new OutputStreamWriter(new FileOutputStream(new File(pathChanceFile+fileEntry.getName().replace(".fasta", ".arff"))));

				outFewlines.append("@relation "+fileEntry.getName()+"\n" +
						"@attribute sequence string\n" +
						"@attribute play {-1,1}\n" +
						"@data");
				String label = null;
				String SeqDna ="";
				String sCurrentLine;
				int firstTime=0;
				while ( ((sCurrentLine = br.readLine()) != null)  ) {	
					if ( sCurrentLine.contains(">")){
						if (firstTime==1)
							outFewlines.write("\n"+SeqDna+","+label.split("label=")[1]);
						label =sCurrentLine;
						SeqDna="";
						firstTime=1;
					}
					else{
						SeqDna += sCurrentLine;
					}
				}
				br.close();
				outFewlines.close();
			}
		}

	}
	
	
	public static void MultipleAlignment() throws IOException, CompoundNotFoundException {
		String pathChanceFile = "/home/simos/workspace/Master_thesis/Data/common/data/fasta/";
		final File DataFolder = new File(pathChanceFile);
		ArrayList<DNASequence> lst = new ArrayList<DNASequence>();

		for (final File fileEntry : DataFolder.listFiles()) {
			if (!fileEntry.isDirectory() & fileEntry.getName().contains("acc.fasta")) {
				lst.clear();
				// Read file
				BufferedReader br = new BufferedReader(new FileReader(pathChanceFile+fileEntry.getName()), 500);
				String sCurrentLine;
				while ( ((sCurrentLine = br.readLine()) != null)  ) {	
					if ( sCurrentLine.contains(">")){
						sCurrentLine = br.readLine();
						lst.add(new DNASequence(sCurrentLine,AmbiguityDNACompoundSet.getDNACompoundSet()));
					}
				}
				br.close();				
			}
		}
        Profile<DNASequence, NucleotideCompound> profile = Alignments.getMultipleSequenceAlignment(lst);
        System.out.printf("Clustalw:%s", profile.getAlignedSequences().get(1).toString());
        ConcurrencyTools.shutdown();
	
	}	
	public static void main(String[] args)  {
//		try {
//			arff2fasta();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		try {
//			MultipleAlignment();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (CompoundNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try {
		fastat2arff();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
