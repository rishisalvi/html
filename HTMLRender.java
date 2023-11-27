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
 *	@version 
 */
public class HTMLRender {
	
	// the array holding all the tokens of the HTML file
	//private String [] tokens;
	//private final int TOKENS_SIZE = 100000;	// size of array
	private ArrayList<String> tokens; 

	// SimpleHtmlRenderer fields
	private SimpleHtmlRenderer render;
	private HtmlPrinter browser;
	
	private enum TextState { NONE, BOLD, ITALIC, HEADING };
	// the current printing state
	private TextState state;
	private int lineSize;
		
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
	
	
	public static void main(String[] args) {
		HTMLRender hf = new HTMLRender();
		if (args.length > 0)
			hf.run(args[0]);
		else
			hf.run(); 
	}
	
	public void run(String example){
		Scanner scan = FileUtils.openToRead(example);
		HTMLUtilities hu = new HTMLUtilities(); 
		while (scan.hasNext()){
			String[] current = hu.tokenizeHTMLString(scan.nextLine());
			for (int i = 0; i < current.length; i++){
				tokens.add(current[i]);
			}
		}

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
						if (lineSize + current.length() < 80){
							if (lineSize == 0 ||
							   (current.length() == 1 && !Character.isLetterOrDigit(current.charAt(0)))){
								lineSize += current.length();
							}
							else{
								current = " " + current;
								lineSize += current.length(); // +1 for space
							}
							if (state == TextState.NONE)
								browser.print(current);
							else if (state == TextState.ITALIC)
								browser.printItalic(current);
							else
								browser.printBold(current);
						}
						else{
							int overflowAmt = Math.max(1, 80 - lineSize);
							String overflow = current.substring(0, overflowAmt - 1); // -1 for space
							if (state == TextState.NONE){
								browser.print(" " + overflow);
								browser.println();
								browser.print(current.substring(overflowAmt - 1));
							}
							else if (state == TextState.ITALIC){
								browser.printItalic(" " + overflow);
								browser.println();
								browser.printItalic(current.substring(overflowAmt - 1));
							}
							else{
								browser.printBold(" " + overflow);
								browser.println();
								browser.printBold(current.substring(overflowAmt - 1));
							}
							lineSize = current.length() - (overflowAmt - 1);
						}
					}
				}
			}
			j++;
		}
	}


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
			browser.println(); 
			browser.println();
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

	public int lineLength(int headingSize){
		if (headingSize <= 3)
			return 30 + 10 * headingSize;
		else
			return 20 * headingSize; 
	}

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

	public int printHeading(int tokenNum){
		lineSize = 0; 
		String headingType = tokens.get(tokenNum);
		int size = Integer.parseInt("" + headingType.charAt(2));
		int maxLength = lineLength(size);
		tokenNum++; // don't print the tag
		String current = tokens.get(tokenNum);
		String end = headingType.charAt(0) + "/" + headingType.substring(1);

		while (current.indexOf("/") == -1){
			if (lineSize + current.length() < maxLength){
				if (lineSize == 0 ||
				   (current.length() == 1 && !Character.isLetterOrDigit(current.charAt(0)))){
					lineSize += current.length();
				}
				else{
					current = " " + current;
					lineSize += current.length(); // +1 for space
				}
				printHeadingLine(current, headingType);
			}
			else{
				int overflowAmt = Math.max(1, maxLength - lineSize);
				String overflow = current.substring(0, overflowAmt - 1); // -1 for space
				printHeadingLine(" " + overflow, headingType);
				browser.println();
				printHeadingLine(current.substring(overflowAmt - 1), headingType);
				lineSize = current.length() - (overflowAmt - 1);
			}
			tokenNum++; 
			current = tokens.get(tokenNum);
		}
		browser.printBreak();
		state = TextState.NONE;
		return tokenNum; 	
	}

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
