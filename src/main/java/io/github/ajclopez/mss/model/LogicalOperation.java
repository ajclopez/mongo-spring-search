package io.github.ajclopez.mss.model;

public enum LogicalOperation {

	AND, OR;
		
    public static LogicalOperation getLogicalOperation(String input) {
        return switch (input) {
            case "AND", "and" -> AND;
            case "OR", "or" -> OR;
            default -> null;
        };
    }
	
}
