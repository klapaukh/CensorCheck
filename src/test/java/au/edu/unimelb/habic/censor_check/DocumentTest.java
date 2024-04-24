package au.edu.unimelb.habic.censor_check;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Record
 */
public class DocumentTest 
{
	@Test
    public void testBounds()
    {
		Document rec = new Document("file.txt", "Hello world", new Config());
		rec.addTrueMask(new Annotation("T1\tPHONE 0 2\tHe"));
        rec.addTrueMask(new Annotation("T1\tPHONE 9 11\tld"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rec.addTrueMask(new Annotation("T1\tPHONE 10 12\tld")));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> rec.addTrueMask(new Annotation("T1\tPHONE -1 2\tld")));
    }
	
	@Test
	public void testLength()
	{
		Config c = new Config();
		c.setUnicode(true);
		assertEquals(new Document("file.txt", "H̷̛̙̫̹̹̻͕͇͓̼̙͔̪͓͖̟̦̯̘̫͖̻͔̞̞̩̑̑͐̒͒̅ͅͅe̶̯͇̥̳͓̼͉̩͙̾̀̇̉͊̓̊͆̌͗͊̂̄̌̀̒͆̋͋̑̓̿͋̐͘͠͝l̷̦̄͛̓͌̈̀̅̆͑́̋̉̎̚͠͝l̴̢͕̗̘͚͚͇̮̙̪̘̖͙̠̯̭̦̂̐̀̈́͂̽̎̄͑́̈́̕̕̚ͅͅo̸̢͉̤͓̝̱̝͖̰̰̘̜͊ ̵̛̘̥̝̙̫͈̪͊̈́͒̏̓̊͑͌̃̒̈̿̀̍͂̆͋̈͗͒͒͑̆̚͝͝W̷̧̢͙̻̺̤͕̝̝̠͇̫̺͖̳̹͙̞̬̟̓̀͑̌͊̿͑̅̎̀͗̍̈́̂̑̈́̽̅̈́̈́̈́̾̕͠͝o̸͖̒̋̀̊̅̓̌͆̆r̵̰͍̥̼͎͙̖̀͒̀̈́̀̊́̎͑̈́͝͝l̴͙̝̠̖̳͑͒̔̄̇̉̆̽́̃̌̉͋͂̓̂̓̇̾͋͛͘̕͝͠ḑ̵̡̨̭̠͓͖͎̲̹̦̙̱̗͓͕̮̺̲̼͚̜͙̖͍̭͍͗̓̊͗̈́̏͐̓̚͝", c).length(), 11);
		assertEquals(new Document("file.txt", "a1👩‍🚀あ👨‍👩‍👧‍👦✨", c).length(), 6);
	}
}
