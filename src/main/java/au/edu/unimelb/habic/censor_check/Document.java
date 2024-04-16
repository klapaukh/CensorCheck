package au.edu.unimelb.habic.censor_check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Document is a file that needs to have tokens identified in it.
 */
public class Document {

	private String name;
	private List<String> fullText;
	private Annotation[] trueMask;
	private Annotation[] testMask;
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
		fullText = new ArrayList<>();
		
		BreakIterator boundary = BreakIterator.getCharacterInstance();
		boundary.setText(contents);
		for(int start = boundary.first(), end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			fullText.add(contents.substring(start, end));
		}
		
		allCategories = new HashSet<>();
		allCategories.add(Category.NONE);
		
		// Create an arrays to track what should be masked and what has been			
		trueMask = new Annotation[fullText.size()];
		testMask = new Annotation[fullText.size()];
		
		for (int i=0; i < trueMask.length; i++) {
			trueMask[i] = Annotation.NONE;
			testMask[i] = Annotation.NONE;
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
			if (message == null && trueMask[i] != Annotation.NONE) {
				message = String.format("%s has overlapping annotations %s and %s", name, trueMask[i], a);
			}
			trueMask[i] = a;
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
			if (message == null && testMask[i] != Annotation.NONE) {
				message = String.format("%s has overlapping annotations %s and %s", name, trueMask[i], a);
			}
			testMask[i] = a;
		}
		if (message != null) {
			System.err.println(message);
		}
	}
	
	@Override
	public String toString() {
		// Don't print out the whole text if it's too long.
		if (fullText.size() < 120) {
			return fullText.stream().reduce("", String::join);
		}
		return String.format("%s...%s", fullText.subList(0, 80).stream().reduce("", String::join), fullText.subList(fullText.size() - 20, fullText.size()).stream().reduce("", String::join));
	}
	
	/**
	 * The number of glyphs that get drawn to render this text.
	 * This maybe less than the number of Java chars or Unicode codepoints.
	 * @return String length
	 */
	public int length() {
		return fullText.size();
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
		for (int i=0; i < trueMask.length; i ++) {
			trueMask[i].assigned(testMask[i], fullText.get(i), resultsByClass, categoryExceptions);
		}
		
		return resultsByClass;			
	}
}
