package smile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmileNameConverter {
	
	private static String pattern = "[^A-Za-z0-9]";
	private static String replaceChar = "_";
	
	public static String convertToLegalSmileName(String name) {
		
		if(name.matches("^[0-9]")){
			name = "x" + name;
		}
			
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(name); 
		
		return m.replaceAll(replaceChar);
	}
}
