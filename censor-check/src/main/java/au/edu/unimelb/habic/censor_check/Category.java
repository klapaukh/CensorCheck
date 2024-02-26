package au.edu.unimelb.habic.censor_check;

import java.util.Map;

/**
 * A Category is a case-insensitive class that a token can be assigned. 
 * There are two special categories NONE and ALL, which users should not use.
 * The ALL class describes summary statistics and characters which should be
 * ignored for any annotated fragment.
 * NONE describes characters with no assigned category, and the ignoring rules
 * for tokens which shouldn't count as false positives.
 */
public class Category {

	/**
	 * The special category NONE.
	 */
	public static final Category NONE = new Category("NONE");
	/**
	 * The special category all.
	 */
	public static final Category ALL = new Category("ALL");
	public final String category;
	
	/**
	 * Create a new category with the given name
	 * @param string
	 */
	public Category(String string) {
		this.category = string.toUpperCase();
	}
	
	@Override
	public String toString() {
		return category;
	}
	
	/**
	 * Check whether a particular string should be ignored under this categories rules.
	 * @param text The string to check.
	 * @param categoryExceptions All the user provided regexes. 
	 * @return True if this combination of category and character can be ignored.
	 */
	public boolean skip(String text,  Map<String, String> categoryExceptions) {
		if (categoryExceptions.containsKey(category) && text.matches(categoryExceptions.get(category))) {
			// This can be ignored.
			 return true;
		}
		
		if (category.equals(NONE.category) || category.equals(ALL.category) ) {
			// Special case, it's definitely false
			return false;
		}
		// Otherwise check if it matches against all.
		return ALL.skip(text, categoryExceptions);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Category && ((Category)other).category.equals(category);
	}
	
	@Override
	public int hashCode() {
		return category.hashCode();
	}
}
