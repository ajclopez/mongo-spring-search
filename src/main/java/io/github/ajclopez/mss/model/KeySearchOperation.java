package io.github.ajclopez.mss.model;

public enum KeySearchOperation {

	SKIP, LIMIT, SORT, FIELDS, FILTER, DEFAULT;
	    
    public static KeySearchOperation getKeyOperation(String input) {
        return switch (input) {
            case "skip" -> SKIP;
            case "limit" -> LIMIT;
            case "sort" -> SORT;
            case "fields" -> FIELDS;
            case "filter" -> FILTER;
            default -> DEFAULT;
        };
    }
	
}
