package au.edu.unimelb.habic.censor_check;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * The Config holds all the user options and locations of all the required
 * files.
 */
public class Config {
	@Parameter(names = { "-g",
			"--ground-truth" }, description = "The path to the folder with the ground truth annotations", required = true)
	private String groundTruthDir;

	@Parameter(names = { "-e",
			"--test-annotations" }, description = "The path to the folder with the annotations to test", required = true)
	private String evalAnnotationDir;

	@Parameter(names = { "-t",
			"--full-texts" }, description = "The path to the folder with original full texts", required = true)
	private String fullTextDir;

	public static final String ANNOTATION_EXTENSION = ".ann";
	public static final String TEXT_EXTENSION = ".txt";

	public List<FileTuple> listFiles() {
		// Find all the possible files
		File[] gt = new File(groundTruthDir)
				.listFiles((dir, name) -> name.toLowerCase().endsWith(ANNOTATION_EXTENSION));
		File[] ft = new File(fullTextDir).listFiles((dir, name) -> name.toLowerCase().endsWith(TEXT_EXTENSION));
		File[] ta = new File(evalAnnotationDir)
				.listFiles((dir, name) -> name.toLowerCase().endsWith(ANNOTATION_EXTENSION));

		FilenameComparator comp = new FilenameComparator();
		// Sort them by name so they are easier to match
		Arrays.sort(gt, comp);
		Arrays.sort(ft, comp);
		Arrays.sort(ta, comp);

		// Iterate through the lists creating tuples of files to read
		int idxGt = 0;
		int idxTa = 0;
		List<FileTuple> matchedFiles = new ArrayList<>();

		// Only documents with a full text present are evaluated
		// Is this strictly necessary? No. But having an idea of
		// how text there is in total is helpful for understanding.
		for (File text : ft) {
			int gt_compare = comp.compare(text, gt[idxGt]);
			while (gt_compare > 0) {
				idxGt++;
				gt_compare = comp.compare(text, gt[idxGt]);
			}

			int ta_compare = comp.compare(text, ta[idxTa]);
			while (ta_compare > 0) {
				idxTa++;
				ta_compare = comp.compare(text, ta[idxTa]);
			}

			matchedFiles.add(
					new FileTuple(text, gt_compare == 0 ? gt[idxGt++] : null, ta_compare == 0 ? ta[idxTa++] : null));
		}

		return matchedFiles;
	}
}
