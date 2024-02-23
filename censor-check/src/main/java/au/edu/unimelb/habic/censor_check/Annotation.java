package au.edu.unimelb.habic.censor_check;

public class Annotation {

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
	
	@Override
	public String toString() {
		return String.format("<%s: %s @ %d -- %d: %s>", id, category, start, end, rough_text);
	}
}
