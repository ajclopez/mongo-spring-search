package io.github.ajclopez.mss.pattern;

import java.util.regex.Pattern;

/**
 * 
 * Class used to load the query search patterns. 
 *
 */
public class SearchPatterns {

    private static final String OPERATOR_PATTERN = "(!?)([^><!=]+)([><]=?|!?=|)(.*)";
    private static final String REGEX_PATTERN = "^/(.*)([igm]*)/$";
    private static final String SORT_PATTERN = "^(\\+|-)?(.*)";
    private static final String NUMBER_PATTERN = "^(-?)(\\d+)([,.0-9]*)$";
    
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
	
}
