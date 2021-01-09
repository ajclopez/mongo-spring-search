package io.github.ajclopez.mss.model;

public enum KeySearchOperation {

	SKIP, LIMIT, SORT, FIELDS, FILTER, DEFAULT;
	    
    public static KeySearchOperation getKeyOperation(String input) {
        switch (input) {
        case "skip":
            return SKIP;
        case "limit":
             return LIMIT;
        case "sort":
            return SORT;
        case "fields":
        	return FIELDS;
        case "filter":
            return FILTER;
        default:
            return DEFAULT;
        }
    }
	
}
