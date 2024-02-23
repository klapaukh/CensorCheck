package au.edu.unimelb.habic.censor_check;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Record
 */
public class RecordTest 
{
    /**
     * Test that overlaps in records throw exceptions
     */
	@Test
    public void testOverlap()
    {
		Record rec = new Record("Hello world", "file.txt");
		rec.addTrueMask(new Annotation("T1\tPHONE 4 7\to w"));
        assertThrows(MaskAlreadyExists.class, () -> rec.addTrueMask(new Annotation("T1\tHOUSE 6 9\twor")));
    }
}
