package au.edu.unimelb.habic.censor_check;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class FilenameComparatorTest {

	@Test
	public void filenameOrder() {
		File f1 = new File("data/fox.txt");
		File f2 = new File("fox.ann");
		File f3 = new File("data/aaa/goat.txt");
		File f4 = new File("aaa/zoo.zoo");
		File f5 = new File("cart");
		
		FilenameComparator c = new FilenameComparator();
		assertEquals(c.compare(f1, f2), 0);
		assertTrue(c.compare(f4, f5) > 0);
		
		File[] data = new File[] {f3, f1, f2, f4, f5};
		File[] ordered = new File[] {f5, f1, f2, f3, f4};
		Arrays.sort(data, c);
		
		for (int i= 0; i< data.length; i++) {
			assertEquals(data[i].getAbsolutePath(), ordered[i].getAbsolutePath());			
		}
	}
}
