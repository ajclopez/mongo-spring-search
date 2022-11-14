package io.github.ajclopez.mss;

import io.github.ajclopez.mss.criteria.CriteriaImpl;
import io.github.ajclopez.mss.criteria.CriteriaQueryVisitor;
import io.github.ajclopez.mss.exception.ArgumentNotValidException;
import io.github.ajclopez.mss.model.*;
import io.github.ajclopez.mss.parser.QueryParser;
import io.github.ajclopez.mss.pattern.SearchPatterns;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * 
 * <p><b>MongoSpringSearch</b>: Convert query parameters from API urls to MongoDB queries.
 *
 */
public class MongoSpringSearch {

	private MongoSpringSearch() {
		
	}
		
	/**
	 * 
	 * Converts query into a MongoDB query object.
	 * 
	 * @param query string part of the requested API URL
	 * @return MongoDB Query
	 * @throws ArgumentNotValidException Exception to be thrown when an argument fails.
	 */
	public static Query mss(String query) throws ArgumentNotValidException {
		return mss(query, Optional.empty());
	}
	
	/**
	 * 
	 * Converts query into a MongoDB query object.
	 * 
	 * @param query string part of the requested API URL.
	 * @param configuration object for advanced options.
	 * @return MongoDB Query
	 * @throws ArgumentNotValidException Exception to be thrown when an argument fails.
	 */
	public static Query mss(String query, Optional<Configuration> configuration) throws ArgumentNotValidException {
		
		if ( query == null || query.trim().isEmpty() ) {
			return new Query();
		}
		
		query = query.replace("+", "%2B");
		
		Query mongoQuery = new Query();
		
		List<SearchCriteria> filters = new ArrayList<SearchCriteria>();
		
		Map<String, CastType> casters = configuration.isPresent() ? configuration.get().getCasters() : null;
		Criteria filterCriteria = null;
		
		for ( SearchCriteria criteria : QueryParser.parse(query, casters) ) {
			switch (KeySearchOperation.getKeyOperation(criteria.getKey())) {
			case DEFAULT:
			default:
				filters.add(criteria);
				break;
			case FILTER:
				filterCriteria = parseFilterAdvanced(criteria.getValue(), casters);
				break;
			case SKIP:
				parseSkip(mongoQuery, criteria.getValue());
				break;
			case LIMIT:
				parseLimit(mongoQuery, criteria.getValue(), configuration);
				break;
			case FIELDS:
				parseProjection(mongoQuery, criteria.getValue());
				break;
			case SORT:
				parseSort(mongoQuery, criteria.getValue());
				break;
			}
		}
		
		parseDefaultFilter(mongoQuery, filters, filterCriteria);
		
		return mongoQuery;
	}
		
	private static void parseDefaultFilter(Query query, List<SearchCriteria> filters, Criteria filterCriteria) {
				
		Map<String, List<SearchCriteria>> groups = filters.stream().collect(Collectors.groupingBy(SearchCriteria::getKey));
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		
		if ( filterCriteria != null ) {
			criterias.add(filterCriteria);			
		}

		for ( String key : groups.keySet() ) {
			for ( SearchCriteria criteria : groups.get(key) ) {
				criterias.add(CriteriaImpl.buildCriteria(criteria));
			}
		}
				
        if ( !criterias.isEmpty() ) {
        	
        	Criteria unique = filterCriteria != null ? filterCriteria : new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));        	
        	query.addCriteria(criterias.size() == 1 ? unique : new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()])));
        }
	}
	
	private static void parseSkip(Query query, String value) {
		try {			
			query.skip(((Number)QueryParser.parseValue(value, null)).intValue());
		} catch (Exception e) {
			throw new ArgumentNotValidException(String.format("skip '%s' cannot be cast to number.", value));
		}
	}
	
	private static void parseLimit(Query query, String value, Optional<Configuration> configuration) {
		try {

			Optional<Integer> maxLimit = configuration.isPresent() 
					? configuration.get().getMaxLimit() != null ? Optional.of(configuration.get().getMaxLimit()) : Optional.empty()
							: Optional.empty();

			if ( maxLimit.isPresent() ) {

				int limit = ((Number) QueryParser.parseValue(value, null)).intValue();

				query.limit(limit <= maxLimit.get() ? limit : maxLimit.get());
			} else {
				query.limit(((Number)QueryParser.parseValue(value, null)).intValue());
			}

		} catch(Exception e) {
			if ( configuration.isPresent() && configuration.get().getDefaultLimit() != null ) {
				query.limit(configuration.get().getDefaultLimit());
				return;
			}

			throw new ArgumentNotValidException(String.format("limit '%s' cannot be cast to number.", value));
		}
	}
	
	private static void parseProjection(Query query, String value) {
		
		for( String key : value.split(",") )
			query.fields().include(key);

	}
	
	private static void parseSort(Query query, String value) {
		
		for ( String order : value.split(",") ) {
			Matcher matcher = SearchPatterns.getSortPattern().matcher(order);
			
			if ( matcher.matches() ) {
				
				if ( matcher.group(1) == null ) {
					query.with(Sort.by(Sort.Direction.ASC, matcher.group(2)));
					continue;
				}
				
				switch (SortSearchOperation.getSortOperation(matcher.group(1))) {
				case ASC:
				default:
					query.with(Sort.by(Sort.Direction.ASC, matcher.group(2)));
					break;
				case DESC:
					query.with(Sort.by(Sort.Direction.DESC, matcher.group(2)));
					break;
				}
			}
		}
	}
	
	private static Criteria parseFilterAdvanced(String input, Map<String, CastType> casters) {
		
		QueryLexer lexer = new QueryLexer(CharStreams.fromString(input));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		io.github.ajclopez.mss.QueryParser parser = new io.github.ajclopez.mss.QueryParser(tokens);
		
		CriteriaQueryVisitor visitor = new CriteriaQueryVisitor(casters);
		
		return visitor.visit(parser.input());
	}
	
}
