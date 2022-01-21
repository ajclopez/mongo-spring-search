package io.github.ajclopez.mss.parser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.ajclopez.mss.exception.ArgumentNotValidException;
import io.github.ajclopez.mss.model.CastType;
import io.github.ajclopez.mss.model.SearchCriteria;
import io.github.ajclopez.mss.model.SearchOperation;
import io.github.ajclopez.mss.pattern.SearchPatterns;

/**
 * 
 * Class used to parse a search query {@code String} and create a list of {@link SearchCriteria}.
 * 
 */
public class QueryParser {

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	private QueryParser() {
		
	}
	
	/**
	 * 
	 * This function expect a search string for build a list of criteria.
	 * 
	 * @param query The search string.
	 * @param casters object which map keys to casters ({@code BOOLEAN, NUMBER, PATTERN, DATE, STRING}).
	 * @return A list of {@link SearchCriteria} used to build MongoDB queries.
	 */
	public static List<SearchCriteria> parse(String query, Map<String, CastType> casters) {
	
		List<SearchCriteria> criterias = new ArrayList<SearchCriteria>();
		
		for ( String condition : query.split("&") ) {
			criterias.add(criteriaParser(condition, casters));
		}		
		
		return criterias;
	}
	
	/**
	 * 
	 * This function creates a {@link SearchCriteria} instance from a search string condition.
	 * 
	 * @param condition A condition in search string.
	 * @param casters object which map keys to casters ({@code BOOLEAN, NUMBER, PATTERN, DATE, STRING}).
	 * @return A {@link SearchCriteria} instance.
	 */
	public static SearchCriteria criteriaParser(String condition, Map<String, CastType> casters) {
				
		Matcher matcher = SearchPatterns.getOperatorPattern().matcher(URLDecoder.decode(condition, StandardCharsets.UTF_8));

		if ( matcher.matches() ) {

			Boolean prefix = matcher.group(1) != null && !matcher.group(1).isEmpty();
			String key = matcher.group(2);
			SearchOperation operation = SearchOperation.getOperation(matcher.group(3));
			String value = matcher.group(4);
			CastType caster = casters != null && casters.containsKey(key) ? casters.get(key) : null;
			
			return new SearchCriteria(prefix, key, operation, value, caster);
		}

		return null;
	}
	
	/**
	 * 
	 * The value is automatically casted: {@code Number, Date, Boolean, RegExp, String, List}.
	 * 
	 * @param value The value will be cast.
	 * @param caster Specify casting per value.
	 * @return Object Casted to {@code Number, Date, Boolean, RegExp, String, List}.
	 */
	public static Object parseValue(String value, CastType caster) {
		
		if ( caster == null ) {
			return parseValue(value);
		}
		
		switch (caster) {
		case BOOLEAN:
			return Boolean.parseBoolean(value);
		case DATE:
			return parseLocalDateTime(value, DATE_FORMAT);
		case NUMBER:
			try {
				return NumberFormat.getInstance().parse(value);
			} catch (ParseException ignored) {
				throw new ArgumentNotValidException(String.format("'%s' cannot be cast to number.", value));
			}
		case PATTERN:
			Matcher matcher = SearchPatterns.getRegExpPattern().matcher(value);
			
			if ( matcher.matches() ) {
				return Pattern.compile(matcher.group(1).replace("/", ""), SearchPatterns.getFlags(matcher.group(2)));
			}
			
			return Pattern.compile(value.replace("/", ""));
		case STRING:
			return value;
		default:
			return parseValue(value);
		}
	}

	private static Object parseValue(String value) {
		
		if ( value == null ) {
			return null;
		}
		
		String[] parts = value.split(",");
		if ( parts.length > 1 ) {
			
			List<Object> list = new ArrayList<Object>();
			for ( String part : parts ) {
				list.add(parseValue(part));
			}
			
			return list;
		}
		
		if ( value.equals("true") || value.equals("false") ) {
			return Boolean.parseBoolean(value);
		}
		
		if ( value.equals("null") ) {
			return null;
		}
				
		try {			
	        return parseLocalDateTime(value, DATE_FORMAT);
		} catch(DateTimeParseException | IllegalArgumentException ignored) {
		}
		
		try {
			if ( SearchPatterns.getNumberPattern().matcher(value).matches() ) {
				return NumberFormat.getInstance().parse(value);
			}
		} catch(ParseException ignored) {
		}

		Matcher matcher = SearchPatterns.getRegExpPattern().matcher(value);
		if ( matcher.matches() ) {
			return Pattern.compile(matcher.group(1).replace("/", ""), SearchPatterns.getFlags(matcher.group(2)));
		}
		
		return value;
	}
	
    private static Instant parseLocalDateTime(String value, String format) {

    	return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format)).atZone(ZoneId.of("UTC")).toInstant();
    }
    
}
