package io.github.ajclopez.mss.model;

public enum SearchOperation {

	EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL, EXISTS;
    
    public static SearchOperation getOperation(String input) {
        switch (input) {
        case "=":
            return EQUAL;
        case "!=":
            return NOT_EQUAL;
        case ">":
            return GREATER_THAN;
        case ">=":
        	return GREATER_THAN_EQUAL;
        case "<":
            return LESS_THAN;
        case "<=":
        	return LESS_THAN_EQUAL;
        case "!":
        default:
        	return EXISTS;
        }
    }
	
}
