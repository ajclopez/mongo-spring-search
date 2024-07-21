package io.github.ajclopez.mss.model;

import io.github.ajclopez.mss.parser.QueryParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SearchCriteriaTest {

	@Test
	void canUseSearchCriteriaObject() {
		
		SearchCriteria expected = new SearchCriteria();
		expected.setPrefix(false);
		expected.setKey("name");
		expected.setOperation(SearchOperation.getOperation("="));
		expected.setValue("john");
		expected.setCaster(null);
		
		String condition = "name=john";
		
		SearchCriteria criteria = QueryParser.criteriaParser(condition, null);

		Assertions.assertNotNull(criteria);
		Assertions.assertEquals(expected.toString(), criteria.toString());
	}
	
}
