package au.edu.unimelb.habic.censor_check;

import java.util.Map;

public class Category {

	public static final Category NONE = new Category("NONE");
	public static final Category ALL = new Category("ALL");
	public final String category;
	
	public Category(String string) {
		this.category = string.toUpperCase();
	}
	
	@Override
	public String toString() {
		return category;
	}
	
	public boolean allow(String text,  Map<String, String> categoryExceptions) {
		if (categoryExceptions.containsKey(category) && text.matches(categoryExceptions.get(category))) {
			// This can be ignored.
			 return true;
		}
		
		if (category.equals(NONE.category) || category.equals(ALL.category) ) {
			// Special case, it's definitely false
			return false;
		}
		// Otherwise check if it matches against all.
		return ALL.allow(text, categoryExceptions);
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
