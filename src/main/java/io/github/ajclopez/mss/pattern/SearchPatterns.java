package io.github.ajclopez.mss.pattern;

import java.util.regex.Pattern;

/**
 * 
 * Class used to load the query search patterns. 
 *
 */
public class SearchPatterns {

    private static final String OPERATOR_PATTERN = "(!?)([^><!=]+)([><]=?|!?=|)(.*)";
    private static final String REGEX_PATTERN = "^/(.*)/([igmsx]*)$";
    private static final String SORT_PATTERN = "^(\\+|-)?(.*)";
    private static final String NUMBER_PATTERN = "^(-?)(\\d+)([.0-9]*)$";
    
    private static Pattern operatorPattern;
    private static Pattern regExpPattern;
    private static Pattern sortPattern;
    private static Pattern numberPattern;

    static {
    	operatorPattern = Pattern.compile(OPERATOR_PATTERN);
    	regExpPattern = Pattern.compile(REGEX_PATTERN);
    	sortPattern = Pattern.compile(SORT_PATTERN);
    	numberPattern = Pattern.compile(NUMBER_PATTERN);
    }
	
    private SearchPatterns() {
    	
    }
    
    public static Pattern getOperatorPattern() {
		return operatorPattern;
	}
    
    public static Pattern getRegExpPattern() {
		return regExpPattern;
	}
    
    public static Pattern getSortPattern() {
		return sortPattern;
	} 
    
    public static Pattern getNumberPattern() {
    	return numberPattern;
    }
    
    public static int getFlags(String options) {
    	
    	int flags = 0x00;
    	
    	if ( options == null || options.isEmpty() ) {
    		return flags;
    	}
    	
    	for ( int i = 0; i < options.split("").length; i++ ) {
    		switch (options.charAt(i)) {
    		case 'i':
    			flags = flags | Pattern.CASE_INSENSITIVE;
    			break;
    		case 'g':
    			flags = flags | Pattern.LITERAL;
    			break;
    		case 'm':
    			flags = flags | Pattern.MULTILINE;
    			break;
    		case 's':
    			flags = flags | Pattern.DOTALL;
    			break;
    		case 'x':
    			flags = flags | Pattern.COMMENTS;
    			break;
    		}
    	}
    	
    	return flags;
    }
	
}
