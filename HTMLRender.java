import java.util.ArrayList;
import java.util.Scanner;
/**
 *	HTMLRender
 *	This program renders HTML code into a JFrame window.
 *	It requires your HTMLUtilities class and
 *	the SimpleHtmlRenderer and HtmlPrinter classes.
 *
 *	The tags supported:
 *		<html>, </html> - start/end of the HTML file
 *		<body>, </body> - start/end of the HTML code
 *		<p>, </p> - Start/end of a paragraph.
 *					Causes a newline before and a blank line after. Lines are restricted
 *					to 80 characters maximum.
 *		<hr>	- Creates a horizontal rule on the following line.
 *		<br>	- newline (break)
 *		<b>, </b> - Start/end of bold font print
 *		<i>, </i> - Start/end of italic font print
 *		<q>, </q> - Start/end of quotations
 *		<hX>, </hX> - Start/end of heading with size X = 1, 2, 3, 4, 5, 6
 *		<pre>, </pre> - Preformatted text
 *
 *	@author Rishi Salvi
 *	@since 11/20/23
 */
public class HTMLRender {
	
	// the array holding all the tokens of the HTML file
	private ArrayList<String> tokens; 

	// SimpleHtmlRenderer fields
	private SimpleHtmlRenderer render;
	private HtmlPrinter browser;
	
	// determines the current priniting state 
	private enum TextState { NONE, BOLD, ITALIC, HEADING };
	private TextState state; // holds current printing state
	private int lineSize; // how many characters the line is currently
		
	public HTMLRender() {
		// Initialize token array
		//tokens = new String[TOKENS_SIZE];
		state = TextState.NONE;
		tokens = new ArrayList<>();
		lineSize = 0;
		
		// Initialize Simple Browser
		render = new SimpleHtmlRenderer();
		browser = render.getHtmlPrinter();
	}
	
	/**
	 * passes on parameters to method if they are provided; otherwise calls the 
	 * overriden method that prints out the default, prewritten code
	 */
	public static void main(String[] args) {
		HTMLRender hf = new HTMLRender();
		if (args.length > 0)
			hf.run(args[0]);
		else // no arguments provided (default)
			hf.run(); 
	}
	
	/**
	 * responsible for controlling the program
	 * two parts - tokenizes the file and then deals with said tokens
	 * @param example	the name of the file to be tokenized (ex. example7.html)
	 */
	public void run(String example){
		readFile(example);
		handleArray();
	}

	/**
	 * responsible for reading the file, tokenizing it, and then adding tokens 
	 * to ArrayList
	 * makes a Scanner using FileUtils and has a while-loop that runs the duration
	 * of the file, tokenizing each individual line, and adding them to an ArrayList
	 * @param fileName	the name of the file to be tokenized (ex. example7.html)
	 */
	public void readFile(String fileName){
		Scanner scan = FileUtils.openToRead(fileName);
		HTMLUtilities hu = new HTMLUtilities(); 
		while (scan.hasNext()){
			String[] current = hu.tokenizeHTMLString(scan.nextLine());
			for (int i = 0; i < current.length; i++){
				tokens.add(current[i]);
			}
		}
	}

	/**
	 * decides what to do with each individual token
	 * has a while-loop that runs the duration of the ArrayList and makes multiple checks
	 * checks if token is a tag (using isTag() method))
	 * checks if token is supposed to be preformatted text (has a nested while-loop)
	 * if token is to be printed, checks line length to make sure text can fit on 
	 * line (otherwise goes onto next) and then prints it out to the appropriate
	 * text type (bold, italic)
	 * checks if it is a header to call printHeading() method, as there are 6 cases
	 * in the heading TextState
	 */
	public void handleArray(){
		int j = 0;
		while(j < tokens.size()){
			String current = tokens.get(j);
			if (!isTag(current)){ // not a tag (with 1 exception)
				if (current.equalsIgnoreCase("<pre>")){
					j++; 
					current = tokens.get(j);
					while (!current.equalsIgnoreCase("</pre>")){
						browser.printPreformattedText(current); 
						browser.println();
						j++;
						current = tokens.get(j);
					}
				}
				else{
					if (state == TextState.HEADING)
						j = printHeading(j - 1); // need to get heading type
					else{
						if (lineSize + current.length() < 80){ // text fits in line
							if (lineSize != 0 && // if not start of a new line or punctuation 
							   !(current.length() == 1 && !Character.isLetterOrDigit(current.charAt(0)))){
								current = " " + current; // +1 for space
							}
						}
						else{ // text doesn't fit in line
							browser.println();
							lineSize = 0; 
						}
						lineSize += current.length();
						if (state == TextState.NONE)
							browser.print(current);
						else if (state == TextState.ITALIC)
							browser.printItalic(current);
						else
							browser.printBold(current);
					}
				}
			}
			j++;
		}
	}

	/**
	 * checks if token is a tag and if so, does appropriate action
	 * multiple if-statements that check for each tag
	 * notable ones:
	 * 		<b>, <i>, <h#> - change the state field to BOLD, ITALIC, HEADING
	 * 		<p>, </p> - print a line break
	 * 		</ - change state field back to NONE
	 * @param check		the token to be checked
	 * @return 			returns true if any of the if-conditions are net, false if none
	 */
	public boolean isTag(String check){
		if (check.indexOf("html") > -1 || 
		    check.indexOf("body") > -1){}
		else if (check.equalsIgnoreCase("<q>"))
			browser.print(" " + "\"");
		else if (check.equalsIgnoreCase("</q>"))
			browser.print("\"");
		else if(check.equalsIgnoreCase("<hr>")){
			browser.printHorizontalRule();
			lineSize = 0; 
		}
		else if (check.equalsIgnoreCase("<br>")){
			browser.printBreak();
			lineSize = 0;
		}
		else if (check.equalsIgnoreCase("<p>") ||
				 check.equalsIgnoreCase("</p>")){
			browser.printBreak(); 
			lineSize = 0;
		}
		else if (check.equalsIgnoreCase("<b>"))
			state = TextState.BOLD;
		else if (check.equalsIgnoreCase("<i>"))
			state = TextState.ITALIC;
		else if (check.length() > 2 && check.substring(0, 2).equalsIgnoreCase("<h"))
			state = TextState.HEADING;
		else if (check.length() > 2 && check.substring(0, 2).equals("</"))
			state = TextState.NONE; // normal
		else
			return false;
		return true; // something above is met
	}

	/**
	 * once heading start tag is located, deals with printing out the rest of heading
	 * determines max line length depending on heading type
	 * until end tag is found, it moves down the ArrayList and prints out each token
	 * @param tokenNum		the tag that indicates the start of a header (<h#>)
	 * @return				the index at which the end tag of the header is located
	 */
	public int printHeading(int tokenNum){
		lineSize = 0; // resets line length
		String headingType = tokens.get(tokenNum);
		int size = Integer.parseInt("" + headingType.charAt(2)); // get the number in the tag
		int maxLength = lineLength(size);
		tokenNum++; // don't print the tag
		String current = tokens.get(tokenNum);

		while (current.indexOf("/") == -1){ // until </h#> is found
			if (lineSize + current.length() < maxLength){
				if (lineSize != 0 && // if not start of line or punctuation
				   !(current.length() == 1 && !Character.isLetterOrDigit(current.charAt(0))))
					current = " " + current; // +1 for space
			}
			else{
				browser.println(); // new line
				lineSize = 0; 
			}
			lineSize += current.length();
			printHeadingLine(current, headingType);
			tokenNum++; 
			current = tokens.get(tokenNum);
		}
		browser.printBreak(); // starts text on next line
		state = TextState.NONE; // no longer a heading
		return tokenNum; 	
	}

	/**
	 * gets the max line length depending on the headingSize
	 * @param headingSize		the int following the heading tag that 
	 * 							determines the heading type
	 * @return					the maximum line length
	 */
	public int lineLength(int headingSize){
		if (headingSize <= 3) // h1, h2, h3
			return 30 + 10 * headingSize;
		else // h4, h5, h6
			return 20 * headingSize; 
	}

	/**
	 * prints the token depending on the appropriate token size
	 * @param output		the token to be printed
	 * @param tokenType		the int value of the heading type (ex. 3)
	 */
	public void printHeadingLine(String output, String tokenType){
		if (tokenType.indexOf("1") > -1)
			browser.printHeading1(output);
		else if (tokenType.indexOf("2") > -1)
			browser.printHeading2(output);
		else if (tokenType.indexOf("3") > -1)
			browser.printHeading3(output);
		else if (tokenType.indexOf("4") > -1)
			browser.printHeading4(output);
		else if (tokenType.indexOf("5") > -1)
			browser.printHeading5(output);
		else
			browser.printHeading6(output);
	}

	/**
	 * to print errors in case the user does not provide an argument at runtime
	 */
	public void run() {
		// Sample renderings from HtmlPrinter class
		
		// Print plain text without line feed at end
		browser.print("First line");
		
		// Print line feed
		browser.println();
		
		// Print bold words and plain space without line feed at end
		browser.printBold("bold words");
		browser.print(" ");
		
		// Print italic words without line feed at end
		browser.printItalic("italic words");
		
		// Print horizontal rule across window (includes line feed before and after)
		browser.printHorizontalRule();
		
		// Print words, then line feed (printBreak)
		browser.print("A couple of words");
		browser.printBreak();
		browser.printBreak();
		
		// Print a double quote
		browser.print("\"");
		
		// Print Headings 1 through 6 (Largest to smallest)
		browser.printHeading1("Heading1");
		browser.printHeading2("Heading2");
		browser.printHeading3("Heading3");
		browser.printHeading4("Heading4");
		browser.printHeading5("Heading5");
		browser.printHeading6("Heading6");
		
		// Print pre-formatted text (optional)
		browser.printPreformattedText("Preformat Monospace\tfont");
		browser.printBreak();
		browser.print("The end");
		
	}
	
	
}
