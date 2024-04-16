package au.edu.unimelb.habic.censor_check;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CategoryTest {

	@Test
	public void testNormalise() {
		Category a = new Category("HellO");
		Category b = new Category("hElLo");
		
		assertEquals(a.category, b.category);
		assertEquals(a.category, "HELLO");
	}
}
