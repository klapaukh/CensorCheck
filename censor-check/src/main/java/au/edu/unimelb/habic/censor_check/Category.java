package au.edu.unimelb.habic.censor_check;

public class Category {

	public final String category;
	
	public Category(String string) {
		this.category = string.toUpperCase();
	}
	
	public String toString() {
		return category;
	}
	
	public boolean equals(Object other) {
		return other instanceof Category && ((Category)other).category.equals(category);
	}
}
