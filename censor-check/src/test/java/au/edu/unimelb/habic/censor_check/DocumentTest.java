package au.edu.unimelb.habic.censor_check;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Record
 */
public class DocumentTest 
{
    /**
     * Test that overlaps in Documents don't throw exceptions
     */
	@Test
    public void testOverlap()
    {
		Document rec = new Document("file.txt", "Hello world");
		rec.addTrueMask(new Annotation("T1\tPHONE 4 7\to w"));
        rec.addTrueMask(new Annotation("T1\tHOUSE 6 9\twor"));
    }
}
