/**
 *	Utilities for handling HTML
 *	There are four types: tags, words, numbers, and random punctuation. It tokenizes the
 *	input based on what type of it is. For tags, there are special kinds that result in the
 *	tokenized process being different. For numbers, there are checks for all types of numbers
 * 	(4e2, 232, .32, -32, etc.). Words are determined by a constant string of letters, with or without
 *	a hyphen. 
 *
 *	@author	Rishi Salvi
 *	@since	11/9/23
 */
public class HTMLUtilities {
	// NONE = not nested in a block, COMMENT = inside a comment block
	// PREFORMAT = inside a pre-format block
	private enum TokenState { NONE, COMMENT, PREFORMAT };
	// the current tokenizer state
	private TokenState state;
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
		boolean isDecimal = false; // only one decimal place in number
		boolean hasHyphen = false; // only one hyphen in word
		while (parser < str.length()){
			String checkWhitespace = "" + str.charAt(parser);
			if (state == TokenState.COMMENT){
				int index = str.indexOf("-->");
				if (index > parser){
					parser = index + 3; // skips line until end of comment
					state = TokenState.NONE;
				}
				else
					parser = str.length(); // skips entire line
			}
			if (state == TokenState.PREFORMAT){
				if (str.equals("</pre>")){
					state = TokenState.NONE;
					result[numTags] = str;
					numTags++; 
				}
				else{
					result[numTags] = str;
					numTags++;
					parser = str.length();  
				}
			}
			else if (checkWhitespace.trim().equals(""))
				parser++;
			else if (parser < str.length() && str.charAt(parser) == '<'){
				isTag = true;
				if (parser < str.length() - 3 && str.substring(parser + 1, parser + 4).equals("!--")){
					isTag = false; 
					state = TokenState.COMMENT;
				}
				else if (parser < str.length() - 4 && str.substring(parser + 1, parser + 5).equals("pre>")){
					isTag = false; 
					state = TokenState.PREFORMAT;
					result[numTags] = str; // whole line is just 1 tag always
					numTags++; 
					parser = str.length(); // leaves the loop
				}
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
			else if (parser < str.length() && Character.isLetter(str.charAt(parser))){ // is a word
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
			else if (parser < str.length() && Character.isDigit(str.charAt(parser))){ // is a digit
				isNumber = true;
				while (isNumber){
					if (parser > 0){ // checks to see if number is negative
						if (str.charAt(parser - 1) == '-'){
							if (current.equals(""))
								current += "-";
						}
						else if (str.charAt(parser - 1) == '.'){ // checks if number is like .23
							isDecimal = true;
							if (current.equals("")){
								if (parser > 1){ // if number is like -.23
									if (str.charAt(parser - 2) == '-')
										current += "-";
								}
								current += ".";
							}
						}
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
					// if you find a decimal place/negative symbol before a number
				else if (parser < str.length() - 2 && str.charAt(parser + 1) == '.'
					&& Character.isDigit(str.charAt(parser + 2))) {}
					// if you find a decimal place & negative symbol
				else if (parser < str.length()){
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
