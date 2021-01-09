package io.github.ajclopez.mss.model;

public enum SortSearchOperation {

	ASC, DESC;
 
	public static SortSearchOperation getSortOperation(String input) {
        switch (input) {
        case "+":
        case " ":
        default:
             return ASC;
        case "-":
            return DESC;
        }
    }
	
}
