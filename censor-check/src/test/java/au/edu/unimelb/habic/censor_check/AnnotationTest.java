package au.edu.unimelb.habic.censor_check;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class AnnotationTest {

	@Test
	public void testParsing() {
		Annotation a = new Annotation("T0    Fish 12 30 Hello");
		Annotation b = new Annotation("T0 Fish 12 30");
		Annotation c = new Annotation("T0\tFish 12  30			");
		
		assertEquals(a.rough_text, "Hello");
		assertEquals(a.start, 12);
		assertEquals(a.end, 30);
		assertEquals(a.id, "T0");
		assertEquals(a.category.category, "FISH");
		
		assertEquals(b.id, c.id);
		assertEquals(b.id, a.id);
		
		assertEquals(b.start, c.start);
		assertEquals(b.start, a.start);
		
		assertEquals(b.end, c.end);
		assertEquals(b.end, a.end);
		
		assertEquals(b.category, c.category);
		assertEquals(b.category, a.category);
		
		assertTrue(b.rough_text.trim().isEmpty());
		assertTrue(c.rough_text.trim().isEmpty());
	}
	
	@Test
	public void testCaseAssignment() {
		Annotation a = Annotation.NONE;
		Map<Category, ResultSet> results = new HashMap<>();
		results.put(Category.NONE, new ResultSet());
		
		// None and none make a true negative
		Map<String, String> exceptions = new HashMap<>();		
		a.assigned(a, " ", results, exceptions);
		
		assertEquals(results.get(Category.NONE).trueNegative(), 1);
		assertEquals(results.get(Category.NONE).truePositives(), 0);
		assertEquals(results.get(Category.NONE).falseNegative(), 0);
		assertEquals(results.get(Category.NONE).falsePositive(), 0);
		assertEquals(results.get(Category.NONE).classMiss(), 0);
		
		Annotation b = new Annotation("", new Category("NAME"), 10, 20, "Sam");
		
		// Trying to assign a result to an unknown class throws an error
		assertThrows(NullPointerException.class, () -> a.assigned(b, "\t", results, exceptions));
		
		
		// False positive is assigned to the right place
		results.put(b.category, new ResultSet());
		a.assigned(b, "\t", results, exceptions);
		
		assertEquals(results.get(Category.NONE).trueNegative(), 1);
		assertEquals(results.get(Category.NONE).truePositives(), 0);
		assertEquals(results.get(Category.NONE).falseNegative(), 0);
		assertEquals(results.get(Category.NONE).falsePositive(), 0);
		assertEquals(results.get(Category.NONE).classMiss(), 0);
		
		assertEquals(results.get(b.category).trueNegative(), 0);
		assertEquals(results.get(b.category).truePositives(), 0);
		assertEquals(results.get(b.category).falseNegative(), 0);
		assertEquals(results.get(b.category).falsePositive(), 1);
		assertEquals(results.get(b.category).classMiss(), 0);
		
		// Exception characters get marked as true negatives always
		exceptions.put(Category.NONE.category, "\\s");
		a.assigned(b, "\t", results, exceptions);
		
		assertEquals(results.get(Category.NONE).trueNegative(), 2);
		assertEquals(results.get(Category.NONE).truePositives(), 0);
		assertEquals(results.get(Category.NONE).falseNegative(), 0);
		assertEquals(results.get(Category.NONE).falsePositive(), 0);
		assertEquals(results.get(Category.NONE).classMiss(), 0);
		
		assertEquals(results.get(b.category).trueNegative(), 0);
		assertEquals(results.get(b.category).truePositives(), 0);
		assertEquals(results.get(b.category).falseNegative(), 0);
		assertEquals(results.get(b.category).falsePositive(), 1);
		assertEquals(results.get(b.category).classMiss(), 0);
		
		// True positive
		b.assigned(b, "\t", results, exceptions);
		assertEquals(results.get(Category.NONE).trueNegative(), 2);
		assertEquals(results.get(Category.NONE).truePositives(), 0);
		assertEquals(results.get(Category.NONE).falseNegative(), 0);
		assertEquals(results.get(Category.NONE).falsePositive(), 0);
		assertEquals(results.get(Category.NONE).classMiss(), 0);
		
		assertEquals(results.get(b.category).trueNegative(), 0);
		assertEquals(results.get(b.category).truePositives(), 1);
		assertEquals(results.get(b.category).falseNegative(), 0);
		assertEquals(results.get(b.category).falsePositive(), 1);
		assertEquals(results.get(b.category).classMiss(), 0);
		
		// False negative
		b.assigned(a, "a", results, exceptions);
		assertEquals(results.get(Category.NONE).trueNegative(), 2);
		assertEquals(results.get(Category.NONE).truePositives(), 0);
		assertEquals(results.get(Category.NONE).falseNegative(), 0);
		assertEquals(results.get(Category.NONE).falsePositive(), 0);
		assertEquals(results.get(Category.NONE).classMiss(), 0);
		
		assertEquals(results.get(b.category).trueNegative(), 0);
		assertEquals(results.get(b.category).truePositives(), 1);
		assertEquals(results.get(b.category).falseNegative(), 1);
		assertEquals(results.get(b.category).falsePositive(), 1);
		assertEquals(results.get(b.category).classMiss(), 0);
		
		// Class miss is a true positive, and a class miss
		Annotation c = new Annotation("", new Category("Fish"), 4, 9, "XX");
		b.assigned(c, "C", results, exceptions);
		
		assertEquals(results.get(Category.NONE).trueNegative(), 2);
		assertEquals(results.get(Category.NONE).truePositives(), 0);
		assertEquals(results.get(Category.NONE).falseNegative(), 0);
		assertEquals(results.get(Category.NONE).falsePositive(), 0);
		assertEquals(results.get(Category.NONE).classMiss(), 0);
		
		assertEquals(results.get(b.category).trueNegative(), 0);
		assertEquals(results.get(b.category).truePositives(), 2);
		assertEquals(results.get(b.category).falseNegative(), 1);
		assertEquals(results.get(b.category).falsePositive(), 1);
		assertEquals(results.get(b.category).classMiss(), 1);
		
	}
}
