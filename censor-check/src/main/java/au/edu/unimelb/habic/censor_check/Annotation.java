package au.edu.unimelb.habic.censor_check;

public class Annotation {

	public final String id;
	public final Category category;
	public final int start;
	public final int end;
	public final String rough_text;
	
	public Annotation(String annotation) {
		String[] parts = annotation.split("\\s+");
		id = parts[0];
		category = new Category(parts[1]);
		start = Integer.parseInt(parts[2]);
		end = Integer.parseInt(parts[3]);
		rough_text = parts[4];
	}
	
	public String toString() {
		return String.format("<%s: %s @ %d -- %d: %s>", id, category, start, end, rough_text);
	}
}
