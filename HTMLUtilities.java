/**
 *	Utilities for handling HTML
 *
 *	@author	
 *	@since	
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

		int parser = 0;
		String current = "";
		int numTags = 0;
		boolean isTag = false;
		boolean isWord = false; 
		boolean isNumber = false; 
		boolean isDecimal = false; 
		boolean hasHyphen = false;
		while (parser < str.length()){
			String checkWhitespace = "" + str.charAt(parser);
			if (checkWhitespace.trim().equals(""))
				parser++;
			else if (str.charAt(parser) == '<'){
				isTag = true;
				while (isTag){
					current += str.charAt(parser);
					if (str.charAt(parser) == '>'){
						isTag = false;
						result[numTags] = current;
						current = "";
						numTags++; 
					}
					parser++; 
				}
			}
			else if (Character.isLetter(str.charAt(parser))){
				isWord = true;
				while (isWord){
					if (parser == str.length() || !Character.isLetter(str.charAt(parser))){
						if (!hasHyphen && (parser < str.length() && str.charAt(parser) == '-')){
							hasHyphen = true;
							current += str.charAt(parser);
							parser++;
						}
						else{
							isWord = false;
							hasHyphen = false;
							result[numTags] = current;
							current = "";
							numTags++; 
						}
					}
					else{
						current += str.charAt(parser);
						parser++;
					}
				}
			}
			else if (Character.isDigit(str.charAt(parser))){
				isNumber = true;
				while (isNumber){
					if (parser > 0 && str.charAt(parser - 1) == '-'){
						if (current.equals(""))
							current += "-";
					}
					if (Character.isDigit(str.charAt(parser)))
						current += str.charAt(parser);
					else if (str.charAt(parser) == '.'){
						if (!isDecimal){
							current += str.charAt(parser);
							isDecimal = true;
						}
						else{
							isNumber = false; 
							isDecimal = false; 
							result[numTags] = current;
							current = "";
							numTags++;
						}
					}
					else if (str.charAt(parser) == 'e' && 
						(str.charAt(parser + 1) == '-' || Character.isDigit(str.charAt(parser + 1)))){
						current += str.charAt(parser);
						parser++; 
						current += str.charAt(parser);
					}
					else{
						isNumber = false; 
						isDecimal = false; 
						result[numTags] = current;
						current = "";
						numTags++;
					}
					if (isNumber)
						parser++; 
					if (parser == str.length()){
						isNumber = false; 
						isDecimal = false; 
						result[numTags] = current;
						current = "";
						numTags++;
					}
				}
			}
			else{
				if (parser < str.length() - 1 && 
					(Character.isDigit(str.charAt(parser + 1)) || (str.charAt(parser) == '<'))){}
				else{
					result[numTags] = "" + str.charAt(parser);
					current = "";
					numTags++;
				}
				parser++;
			}
		}
		
		String[] correct = new String[numTags];
		for (int i = 0; i < numTags; i++)
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