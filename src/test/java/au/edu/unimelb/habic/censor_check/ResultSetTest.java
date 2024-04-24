package au.edu.unimelb.habic.censor_check;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ResultSetTest {

	@Test
	public void testAdd() {
		Map<String, Integer> bMiss = new HashMap<>();
		bMiss.put("Dog" , 3);
		bMiss.put("Cat" , 2);
		
		ResultSet a = new ResultSet();
		ResultSet b = new ResultSet(1, 5, 10, 15, bMiss);
		
		ResultSet s = a.add(b);
		assertEquals(s.truePositives(), 1);
		assertEquals(s.falsePositive(), 5);
		assertEquals(s.trueNegative(), 10);
		assertEquals(s.falseNegative(), 15);
		assertEquals(s.classMiss(), 5);
		
		Map<String, Integer> cMiss = new HashMap<>();
		cMiss.put("Dog" , 5);
		cMiss.put("Fish" , 4);
		
		ResultSet c = new ResultSet(2, 6, 11, 16, cMiss);
		
		s = b.add(c);
		assertEquals(s.truePositives(), 3);
		assertEquals(s.falsePositive(), 11);
		assertEquals(s.trueNegative(), 21);
		assertEquals(s.falseNegative(), 31);
		assertEquals(s.classMiss(), 14);
		
		Map<String, Integer> totalMiss = s.classMissBreakDown();
		assertEquals(totalMiss.size(), 3);
		assertEquals(totalMiss.get("Dog"), 8);
		assertEquals(totalMiss.get("Cat"), 2);
		assertEquals(totalMiss.get("Fish"), 4);
	}
}
