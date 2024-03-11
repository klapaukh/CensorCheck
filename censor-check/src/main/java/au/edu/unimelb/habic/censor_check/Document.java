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
 * A Document is a file that needs to be anonymised in part.
 */
public class Document {

	private String name;
	private String full_text;
	private Annotation[] true_mask;
	private Annotation[] test_mask;
	private Set<Category> allCategories;
	
	/** Create a new Record from a file on disk **/
	public Document(File sourceFile) throws IOException {
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
		
		this.init(sourceFile.getName(), builder.toString());
	}
	
	/** Create a new Record based on the data that would be stored on disk */
	public Document(String filename, String contents) {
		this.init(filename, contents);
	}
	
	/**
	 * Initialise the internal structure of the Document no matter how it was created.
	 * @param filename The filename of the source document.
	 * @param contents The full text of the document.
	 */
	private void init(String filename, String contents) {
		full_text = contents;
		allCategories = new HashSet<>();
		allCategories.add(Category.NONE);
		
		// Create an arrays to track what should be masked and what has been			
		true_mask = new Annotation[full_text.length()];
		test_mask = new Annotation[full_text.length()];
		
		for (int i=0; i<full_text.length(); i++) {
			true_mask[i] = Annotation.NONE;
			test_mask[i] = Annotation.NONE;
		}
		
		name = filename;
	}
	
	/**
	 * Add a ground truth annotation to the document.
	 * @param a The annotation
	 */
	public void addTrueMask(Annotation a) {
		allCategories.add(a.category);
		String message = null;
		for (int i = a.start; i < a.end; i++) {
			if (message == null && true_mask[i] != Annotation.NONE) {
				message = String.format("%s has overlapping annotations %s and %s", name, true_mask[i], a);
			}
			true_mask[i] = a;
		}
		if (message != null) {
			System.err.println(message);
		}
	}
	
	/**
	 * Add a test annotation to the document.
	 * @param a The annotation
	 */
	public void addTestMask(Annotation a) {
		allCategories.add(a.category);
		String message = null;
		for (int i = a.start; i < a.end; i++) {
			if (message == null && test_mask[i] != Annotation.NONE) {
				message = String.format("%s has overlapping annotations %s and %s", name, true_mask[i], a);
			}
			test_mask[i] = a;
		}
		if (message != null) {
			System.err.println(message);
		}
	}
	
	@Override
	public String toString() {
		// Don't print out the whole text if it's too long.
		if (full_text.length() < 120) {
			return full_text;
		}
		return String.format("%s...%s", full_text.substring(0, 80), full_text.substring(full_text.length() - 20));
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
	
	/**
	 * Compute how well masked the hidden information is and return it as a table.
	 * Note that this will add a new class called "ALL" which contains the summary data.
	 * @param categoryExceptions Regexes which are allowed to be ignored for each class
	 * @return A map of category to stats.
	 */
	public Map<Category, ResultSet> computeStats(Map<String, String> categoryExceptions) {
		// This method shouldn't change the data. If you rerun it with different exceptions,
		// it should work
		Map<Category, ResultSet> resultsByClass = new HashMap<>();
		for (Category cat : allCategories) {
			resultsByClass.put(cat, new ResultSet());
		}
		for (int i=0; i < true_mask.length; i ++) {
			true_mask[i].assigned(test_mask[i], full_text.substring(i, i+1), resultsByClass, categoryExceptions);
		}
		
		return resultsByClass;			
	}
}
