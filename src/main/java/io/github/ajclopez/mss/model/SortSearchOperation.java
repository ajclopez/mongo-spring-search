package io.github.ajclopez.mss.model;

public enum SortSearchOperation {

	ASC, DESC;
 
	public static SortSearchOperation getSortOperation(String input) {
        return switch (input) {
            case "-" -> DESC;
            default -> ASC;
        };
    }
	
}
