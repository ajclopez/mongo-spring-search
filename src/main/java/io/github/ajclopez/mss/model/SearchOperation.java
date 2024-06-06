package io.github.ajclopez.mss.model;

public enum SearchOperation {

	EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL, EXISTS;
    
    public static SearchOperation getOperation(String input) {
        return switch (input) {
            case "=" -> EQUAL;
            case "!=" -> NOT_EQUAL;
            case ">" -> GREATER_THAN;
            case ">=" -> GREATER_THAN_EQUAL;
            case "<" -> LESS_THAN;
            case "<=" -> LESS_THAN_EQUAL;
            default -> EXISTS;
        };
    }
	
}
