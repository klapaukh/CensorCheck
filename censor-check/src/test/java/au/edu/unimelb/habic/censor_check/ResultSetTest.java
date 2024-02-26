package au.edu.unimelb.habic.censor_check;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ResultSetTest {

	@Test
	public void testAdd() {
		ResultSet a = new ResultSet();
		ResultSet b = new ResultSet(1, 5, 10, 15, 20);
		
		ResultSet s = a.add(b);
		assertEquals(s.truePositives(), 1);
		assertEquals(s.falsePositive(), 5);
		assertEquals(s.trueNegative(), 10);
		assertEquals(s.falseNegative(), 15);
		assertEquals(s.classMiss(), 20);
		
		ResultSet c = new ResultSet(2, 6, 11, 16, 21);
		s = b.add(c);
		assertEquals(s.truePositives(), 3);
		assertEquals(s.falsePositive(), 11);
		assertEquals(s.trueNegative(), 21);
		assertEquals(s.falseNegative(), 31);
		assertEquals(s.classMiss(), 41);
	}
}
