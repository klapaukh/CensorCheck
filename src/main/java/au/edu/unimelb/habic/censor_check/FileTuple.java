package au.edu.unimelb.habic.censor_check;

import java.io.File;

/**
 * A Triple of three files.
 */
public class FileTuple {
	public final File groundTruthFile;
	public final File fullTextFile;
	public final File testAnnotationsFile;
	
	/**
	 * Create a new file triple
	 * @param fullText The full text file name.
	 * @param groundTruth The matching ground truth annotations
	 * @param testAnnotations The matching test annotations.
	 */
	public FileTuple(File fullText, File groundTruth, File testAnnotations) {
		groundTruthFile = groundTruth;
		fullTextFile = fullText;
		testAnnotationsFile = testAnnotations;
	}
}