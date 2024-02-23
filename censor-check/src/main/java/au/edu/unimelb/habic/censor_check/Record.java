package au.edu.unimelb.habic.censor_check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Record is a document that needs to be anonymised in part.
 */
public class Record {

	private String name;
	private String full_text;
	private Annotation[] true_mask;
	private Annotation[] test_mask;
	private static final Category NONE = new Category("None");
	private final Set<Category> allCategories;
	
	/** Create a new Record from a file on disk **/
	public Record(File sourceFile) throws IOException {
		if (!sourceFile.isFile() && sourceFile.canRead()) {
			throw new FileNotFoundException(sourceFile.getAbsolutePath() + " is not a reable file");
		}
		// Read the contents of the file
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		try {
			while(true) {
				char[] buffer = new char[1024];
				int next_read = reader.read(buffer);
				if (next_read < 0) {
					break;
				}
				builder.append(buffer, 0, next_read);			
			}
		} finally {
			reader.close();
		}
		full_text = builder.toString();
		allCategories = new HashSet<>();
		allCategories.add(NONE);
		
		// Create an arrays to track what should be masked and what has been			
		true_mask = new Annotation[full_text.length()];
		test_mask = new Annotation[full_text.length()];
		name = sourceFile.getName();
	}
	
	/** Create a new Record based on the data that would be stored on disk */
	public Record(String filename, String contents) {
		full_text = contents;
		allCategories = new HashSet<>();
		allCategories.add(NONE);
		
		// Create an arrays to track what should be masked and what has been			
		true_mask = new Annotation[full_text.length()];
		test_mask = new Annotation[full_text.length()];
		name = filename;
	}
	
	public void addTrueMask(Annotation a) {
		allCategories.add(a.category);
		for (int i = a.start; i < a.end; i++) {
			if (true_mask[i] != null) {
				throw new MaskAlreadyExists(name, true_mask[i], a);
			}
			true_mask[i] = a;
		}
	}
	
	public void addTestMask(Annotation a) {
		allCategories.add(a.category);
		for (int i = a.start; i < a.end; i++) {
			if (test_mask[i] != null) {
				throw new MaskAlreadyExists(name, test_mask[i], a);
			}
			test_mask[i] = a;
		}
	}
	
	public String toString() {
		return full_text;
	}
	
	/**
	 * The UTF-16 character length of the text.
	 * @return String length
	 */
	public int length() {
		return true_mask.length;
	}
	
	/**
	 * The length of the record in glyphs. This is generally the same as the length for western languages.
	 * @return Number of glyphs in the document
	 */
	public int glyph_length() {
		BreakIterator iter = BreakIterator.getCharacterInstance();
		iter.setText(full_text);
		int num_glyphs = 0;
		while(iter.next() != BreakIterator.DONE) {
			num_glyphs ++;
		}
		return num_glyphs;
	}
	
	public Map<Category, ResultSet> computeStats() {
		Map<Category, ResultSet> resultsByClass = new HashMap<>();
		for (Category cat : allCategories) {
			resultsByClass.put(cat, new ResultSet());
		}
		for (int i=0; i < true_mask.length; i ++) {
			if (true_mask[i] == null && test_mask[i] == null) {
				resultsByClass.get(NONE).addTrueNegative();
			} else if (true_mask[i] == null && test_mask[i] != null) {
				resultsByClass.get(test_mask[i].category).addFalsePositive();
			} else if (true_mask[i] != null && test_mask[i] == null) {
				resultsByClass.get(true_mask[i].category).addFalseNegative();
			} else {
				resultsByClass.get(true_mask[i].category).addTruePositive();
				if (!true_mask[i].category.equals(test_mask[i].category)) {
					resultsByClass.get(true_mask[i].category).addClassMiss();
				}
			}
		}
		
		return resultsByClass;			
	}
}
