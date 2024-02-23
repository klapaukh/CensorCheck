package au.edu.unimelb.habic.censor_check;

import java.io.File;

public class FileTuple {
	public final File groundTruthFile;
	public final File fullTextFile;
	public final File testAnnotationsFile;
	
	public FileTuple(File fullText, File groundTruth, File testAnnotations) {
		groundTruthFile = groundTruth;
		fullTextFile = fullText;
		testAnnotationsFile = testAnnotations;
	}
}