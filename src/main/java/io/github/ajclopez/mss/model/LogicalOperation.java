package io.github.ajclopez.mss.model;

public enum LogicalOperation {

	AND, OR;
		
    public static LogicalOperation getLogicalOperation(String input) {
        switch (input) {
        case "AND":
        case "and":
        	return AND;
        case "OR":
        case "or":
            return OR;
        default:
             return null;
        }
    }
	
}
