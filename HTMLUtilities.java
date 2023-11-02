/**
 *	Utilities for handling HTML
 *
 *	@author	Rishi Salvi
 *	@since	October 31, 2023
 */
public class HTMLUtilities {

	/**
	 *	Break the HTML string into tokens. The array returned is
	 *	exactly the size of the number of tokens in the HTML string.
	 *	Example:	HTML string = "Goodnight moon goodnight stars"
	 *				returns { "Goodnight", "moon", "goodnight", "stars" }
	 *	@param str			the HTML string
	 *	@return				the String array of tokens
	 */
	public String[] tokenizeHTMLString(String str) {
		// make the size of the array large to start
		String[] result = new String[10000];
		
		int counter = 0; 
		int parser = 0; 
		while (parser < str.length()){
			if (str.charAt(parser) == '<'){
				int tagStart = parser;
				while (str.charAt(parser) != '>')
					parser++;
				result[counter] = str.substring(tagStart, parser + 1);
				counter++;
			}
			else if (Character.isLetter(str.charAt(parser))){
				int wordStart = parser;
				while (parser < str.length() && Character.isLetter(str.charAt(parser)))
					parser++; 
				result[counter] = str.substring(wordStart, parser);
				counter++; 
			}
			else if (Character.isDigit(str.charAt(parser))){
				int numStart = parser;
				while (parser < str.length() && str.charAt(parser) != ' ')
					parser++; 
				result[counter] = str.substring(numStart, parser);
				counter++; 
			}
			else if (!Character.isLetterOrDigit(str.charAt(parser))){
				result[counter] = "" + str.charAt(parser);
				counter++; 
			}
			parser++;
		}
		
		String[] correct = new String[counter];
		for (int i = 0; i < counter; i++)
			correct[i] = result[i];
		
		// return the correctly sized array
		return correct;
	}
	
	/**
	 *	Print the tokens in the array to the screen
	 *	Precondition: All elements in the array are valid String objects.
	 *				(no nulls)
	 *	@param tokens		an array of String tokens
	 */
	public void printTokens(String[] tokens) {
		if (tokens == null) return;
		for (int a = 0; a < tokens.length; a++) {
			if (a % 5 == 0) System.out.print("\n  ");
			System.out.print("[token " + a + "]: " + tokens[a] + " ");
		}
		System.out.println();
	}

}
