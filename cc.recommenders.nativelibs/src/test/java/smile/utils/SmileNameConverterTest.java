package smile.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SmileNameConverterTest {
	
	@Test
	public void charactersAndNumbersAreNotConverted() {
		String expected = "aA1_";
		String actual = SmileNameConverter.convertToLegalSmileName(expected);
		
		assertEquals(expected, actual);
	}

	@Test
	public void preceedingNumbersAreHandled() {
		String actual = SmileNameConverter.convertToLegalSmileName("1");
		String expected = "x1";
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void nonAlphanumericCharactersAreConvertedToUnderscore() {
		String in = "aA1!\"§$%&/()=?{[]}-+*#;,:.><|öäüÖÄÜ^€@";
		String actual = SmileNameConverter.convertToLegalSmileName(in);
		String expected = "aA1___________________________________";
		
		assertEquals(expected, actual);
	}
}