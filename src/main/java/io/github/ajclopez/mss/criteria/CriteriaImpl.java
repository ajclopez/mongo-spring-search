package io.github.ajclopez.mss.criteria;

import io.github.ajclopez.mss.model.SearchCriteria;
import io.github.ajclopez.mss.parser.QueryParser;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * Class used to creating queries.
 * 
 */
public class CriteriaImpl {

	private CriteriaImpl() {
		
	}
	
	/**
	 * 
	 * Build MongoDB queries.
	 * 
	 * @param searchCriteria A {@link SearchCriteria} instance.
	 * @return a {@link Criteria} instance.
	 */
	public static Criteria buildCriteria(SearchCriteria searchCriteria) {
		
		String key = searchCriteria.getKey();
		Object value = QueryParser.parseValue(searchCriteria.getValue(), searchCriteria.getCaster());
		
		Criteria criteria = Criteria.where(key);
		
		switch (searchCriteria.getOperation()) {
		case NOT_EQUAL:
			if ( value instanceof Pattern pattern) {
				criteria.not().regex(Pattern.quote(pattern.pattern()));
			} else if ( value instanceof List ) {				
				criteria.nin(((List<?>) value).toArray());
			} else {
				criteria.ne(value);
			}
			break;
		case GREATER_THAN:
			criteria.gt(value);
			break;
		case GREATER_THAN_EQUAL:
			criteria.gte(value);
			break;
		case LESS_THAN:
			criteria.lt(value);
			break;
		case LESS_THAN_EQUAL:
			criteria.lte(value);
			break;
		case EXISTS:
			criteria.exists(!searchCriteria.getPrefix());
			break;
		case EQUAL:
		default:
			if ( value instanceof Pattern pattern ) {
				criteria.regex(pattern);
			} else if ( value instanceof List ) {
				criteria.in(((List<?>) value).toArray());
			} else {
				criteria.is(value);
			}
			break;
		}
		
		return criteria;
	}
	
}
