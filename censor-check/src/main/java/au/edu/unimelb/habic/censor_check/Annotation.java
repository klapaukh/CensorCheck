package au.edu.unimelb.habic.censor_check;

import java.util.Map;

/**
 * An annotation represents a contiguous linear segment of text that
 * has been assigned a category.
 */
public class Annotation {
	
	/**
	 * The annotation representing that there is no annotation present.
	 */
	public static Annotation NONE = new Annotation("N/A", Category.NONE, -1, -1, "");

	public final String id;
	public final Category category;
	public final int start;
	public final int end;
	public final String rough_text;
	
	public Annotation(String annotation) {
		try {
			String[] parts = annotation.split("\\s+", 5);
			id = parts[0];
			category = new Category(parts[1]);
			start = Integer.parseInt(parts[2]);
			end = Integer.parseInt(parts[3]);
			rough_text = parts.length > 4 ? parts[4] : " ";
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException("Failed to parse annotation " + annotation, e);
		}
	}
	
	public Annotation(String id, Category category, int start, int end, String rough_text) {
		this.id = id;
		this.category = category;
		this.start = start;
		this.end = end;
		this.rough_text = rough_text;
	}
	
	@Override
	public String toString() {
		return String.format("<%s: %s @ %d -- %d: %s>", id, category, start, end, rough_text);
	}

	public void assigned(Annotation other, String text, Map<Category, ResultSet> resultsByClass, Map<String, String> categoryExceptions) {
		if (category.equals(Category.NONE)) {
			if (other.category.equals(Category.NONE) || this.category.allow(text, categoryExceptions)) {
				// True negative
				resultsByClass.get(this.category).addTrueNegative();				
			} else {
				// This is a false positive
				resultsByClass.get(other.category).addFalsePositive();					
			}
		} else if (category.category.equals(other.category.category)) {
			// Perfect match!
			resultsByClass.get(this.category).addTruePositive();			
		} else if (Category.NONE.category.equals(other.category.category)) {
			// Missed! False Negative
			resultsByClass.get(this.category).addFalseNegative();
		} else {
			// Class mismatch, but tokens caught
			resultsByClass.get(this.category).addTruePositive();
			resultsByClass.get(this.category).addClassMiss();
		}
	}
}
