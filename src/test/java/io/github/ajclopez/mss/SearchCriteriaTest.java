package io.github.ajclopez.mss;

import org.junit.Assert;
import org.junit.Test;

import io.github.ajclopez.mss.model.SearchCriteria;
import io.github.ajclopez.mss.model.SearchOperation;
import io.github.ajclopez.mss.parser.QueryParser;

public class SearchCriteriaTest {

	@Test
	public void canUseSearchCriteriaObject() {
		
		SearchCriteria expected = new SearchCriteria();
		expected.setPrefix(false);
		expected.setKey("name");
		expected.setOperation(SearchOperation.getOperation("="));
		expected.setValue("jhon");
		expected.setCaster(null);
		
		String condition = "name=jhon";
		
		SearchCriteria criteria = QueryParser.criteriaParser(condition, null);
		
		Assert.assertEquals(expected.toString(), criteria.toString());
	}
	
}
