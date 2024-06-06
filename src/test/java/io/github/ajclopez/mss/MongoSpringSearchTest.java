package io.github.ajclopez.mss;

import com.mongodb.BasicDBList;
import io.github.ajclopez.mss.exception.ArgumentNotValidException;
import io.github.ajclopez.mss.model.CastType;
import io.github.ajclopez.mss.model.Configuration;
import io.github.ajclopez.mss.model.LogicalOperation;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class MongoSpringSearchTest {

	
	@Test
	void queryEmptyAndNullCreateQueryMongoDBEmpty() {
		Assertions.assertNotNull(MongoSpringSearch.mss(null));
		Assertions.assertNotNull(MongoSpringSearch.mss(""));
	}
		
	@Test
	void findQuery() {
		
		String query = "age>=10&mobile=/^+34.*/&firstname=john&date>2021-01-08T00:00:00.000Z";
		Query mongoQuery = MongoSpringSearch.mss(query);

		Assertions.assertEquals(Integer.valueOf(query.split("&").length), Integer.valueOf(mongoQuery.getQueryObject().get("$and", BasicDBList.class).size()));
	}
	
	@Test
	void findAllOperatorsInQuery() {
		
		String query = "age>=18&age<=21"
				+ "&mobile=/^+34.*/"
				+ "&date>2021-01-08T00:00:00.000Z&date<2021-01-08T23:59:59.999Z"
				+ "&city!=USA&!email&code=123456,98765&zone!=zone1,zone2&mobile"
				+ "&email!=/.*@example.com/";
		Query mongoQuery = MongoSpringSearch.mss(query);

		Assertions.assertEquals(Integer.valueOf(query.split("&").length), Integer.valueOf(mongoQuery.getQueryObject().get("$and", BasicDBList.class).size()));
	}
	
	@Test
	void canUseAllTypeValueInQuery() {
		
		String query = "age=21&name=Elisa&data>2021-01-08T00:00:00.000Z&female=true&email=/.*@example.com/&flag=null";
		Query mongoQuery = MongoSpringSearch.mss(query);

		Assertions.assertEquals(Integer.valueOf(query.split("&").length), Integer.valueOf(mongoQuery.getQueryObject().get("$and", BasicDBList.class).size()));
	}
	
	@Test
	void canUseRegex() {
		
		String query = "mobile=/^58/";
		Query mongoQuery = MongoSpringSearch.mss(query);
				
		Pattern pattern = compile("/^58/");
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = compile(pattern.pattern().replace("/", "")).pattern();

		Assertions.assertEquals(expected, document.get("mobile").toString());
	}
	
	@Test
	void canUseRegexWithSpecialCharacters() {
		
		String query = "mobile=/^\\+58/";
		Query mongoQuery = MongoSpringSearch.mss(query);
				
		Pattern pattern = compile("/^\\+58/");
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = compile(pattern.pattern().replace("/", "")).pattern();

		Assertions.assertEquals(expected, document.get("mobile").toString());
	}

	@Test
	void canUseRegexWithOptions() {
		
		String query = "firstname=/JOHN/i";
		Query mongoQuery = MongoSpringSearch.mss(query);
				
		Pattern pattern = compile("/JOHN/", Pattern.CASE_INSENSITIVE);
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = compile(pattern.pattern().replace("/", ""), Pattern.CASE_INSENSITIVE).pattern();

		Assertions.assertEquals(expected, document.get("firstname").toString());
	}
	
	@Test
	void canUseRegexWithAllOptions() {
		
		String query = "firstname=/JOHN/gmixs";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Pattern pattern = compile("/JOHN/", Pattern.CASE_INSENSITIVE);
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = compile(pattern.pattern().replace("/", ""), Pattern.CASE_INSENSITIVE).pattern();

		Assertions.assertEquals(expected, document.get("firstname").toString());
		
	}
	
	@Test
	void limitQuery() {
		
		String query = "limit=10";
		Query mongoQuery = MongoSpringSearch.mss(query);

		Assertions.assertEquals(Integer.valueOf(10), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test
	void limitIsAnArgumentNotValid() {
		
		String query = "limit=a";
		Assertions.assertThrows(ArgumentNotValidException.class,  () -> MongoSpringSearch.mss(query));
	}
	
	@Test
	void canUseDefaultLimitValue() {
		
		Configuration options = new Configuration(null, 10, null);
		
		String query = "city=Madrid&limit=a";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));

		Assertions.assertEquals(Integer.valueOf(10), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test
	void canUseMaxLimitValue() {
		
		Configuration options = new Configuration(null, 10, 500);
		
		String query = "city=Madrid&limit=100";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));

		Assertions.assertEquals(Integer.valueOf(100), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test
	void canUseMaxLimitValueIfLimitParamExceedsRule() {
		
		Configuration options = new Configuration(null, 10, 500);
		
		String query = "city=Madrid&limit=10000";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));

		Assertions.assertEquals(Integer.valueOf(500), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test
	void skipQuery() {
		
		String query = "skip=50";
		Query mongoQuery = MongoSpringSearch.mss(query);

		Assertions.assertEquals(Long.valueOf(50), Long.valueOf(mongoQuery.getSkip()));
	}
	
	@Test
	void skipIsAnArgumentNotValid() {
		
		String query = "skip=a";
		Assertions.assertThrows(ArgumentNotValidException.class,  () -> MongoSpringSearch.mss(query));
	}

	@Test
	void projectionQuery() {
		
		String query = "fields=firstname,lastname,age";
		Query mongoQuery = MongoSpringSearch.mss(query);

		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("firstname"));
		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("lastname"));
		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("age"));
	}
	
	@Test
	void sortQuery() {
		
		String query = "sort=+date,id,-city";
		Query mongoQuery = MongoSpringSearch.mss(query);

		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getSortObject().getInteger("date"));
		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getSortObject().getInteger("id"));
		Assertions.assertEquals(Integer.valueOf(-1), mongoQuery.getSortObject().getInteger("city"));
	}
	
	@Test
	void canUseLogicalOperators() {
		
		String query = "filter=(country=Mexico OR country=Spain) and gender=female";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		BasicDBList list = mongoQuery.getQueryObject().get("$and", BasicDBList.class);

		Assertions.assertEquals(Integer.valueOf(2), Integer.valueOf(list.size()));
		Assertions.assertEquals(Integer.valueOf(2), Integer.valueOf(((Document)list.get(0)).get("$or", BasicDBList.class).size()));
		
	}
	
	@Test
	void canUseCastingString() {
		
		Map<String, CastType> caster = new HashMap<>();
		caster.put("mobile", CastType.STRING);
		
		Configuration options = new Configuration(caster, null, null);
		
		String query = "mobile=134000000000";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));

		Assertions.assertEquals("134000000000", document.getString("mobile"));
	}
	
	@Test
	void canUseCastingNumber() {
		
		Map<String, CastType> caster = new HashMap<>();
		caster.put("mobile", CastType.NUMBER);
		
		Configuration options = new Configuration(caster, null, null);
		
		String query = "mobile=134000000000";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));

		Assertions.assertEquals(Long.valueOf(134000000000L), document.getLong("mobile"));
	}
	
	@Test
	void canThrowArgumentNotValidCastingNumber() {
		
		Map<String, CastType> caster = new HashMap<>();
		caster.put("mobile", CastType.NUMBER);
		
		Configuration configuration = new Configuration(caster, null, null);
		var query = "mobile=a";
		var options = Optional.of(configuration);

		Assertions.assertThrows(ArgumentNotValidException.class,  () -> MongoSpringSearch.mss(query, options));
	}
	
	@Test
	void canUseCastingBoolean() {
		
		Map<String, CastType> caster = new HashMap<>();
		caster.put("flag", CastType.BOOLEAN);
		
		Configuration options = new Configuration(caster, null, null);
		
		String query = "flag=false";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));

		Assertions.assertEquals(Boolean.FALSE, document.getBoolean("flag"));
	}
	
	@Test
	void canUseCastingDate() {
		
		Map<String, CastType> caster = new HashMap<>();
		caster.put("date", CastType.DATE);
		
		Configuration options = new Configuration(caster, null, null);
		
		Instant now = Instant.now(Clock.systemUTC());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
				
		String query = String.format("date=%s", formatter.format(now));
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));

		Assertions.assertEquals(formatter.format(now), document.get("date").toString());
	}
	
	@Test
	void canUseCastingPattern() {
		
		Map<String, CastType> caster = new HashMap<>();
		caster.put("email", CastType.PATTERN);
		
		Configuration options = new Configuration(caster, null, null);
		
		Pattern pattern = compile("/.*@example.com/");
		
		String query = String.format("email=%s", pattern.pattern());
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = compile(pattern.pattern().replace("/", "")).pattern();

		Assertions.assertEquals(expected, document.get("email").toString());
	}
	
	@Test
	void complexQuery() {
		
		String query = "filter=(city=Madrid or city=Barcelona) and gender=female"
				+ "&skip=50"
				+ "&limit=10"
				+ "&sort=-birthday"
				+ "&fields=firstname,lastname,age"
				+ "&name=/^an/&age>=18&age<=21";
		
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		BasicDBList list = mongoQuery.getQueryObject().get("$and", BasicDBList.class);

		Assertions.assertEquals(Integer.valueOf(4), Integer.valueOf(list.size()));
		Assertions.assertEquals(Long.valueOf(50), Long.valueOf(mongoQuery.getSkip()));
		Assertions.assertEquals(Integer.valueOf(10), Integer.valueOf(mongoQuery.getLimit()));
		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("firstname"));
		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("lastname"));
		Assertions.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("age"));
		Assertions.assertEquals(Integer.valueOf(-1), mongoQuery.getSortObject().getInteger("birthday"));
	}

	@Test
	void whenUseInvalidLogicalOperator() {

		LogicalOperation result = LogicalOperation.getLogicalOperation("y");
        Assertions.assertNull(result);

	}

	@Test
	void whenUseKeyWithSpecialCharacterThenReturnQuery() {

		String query = "filter=(core:city=Madrid and core:city=Barcelona)";
		Query mongoQuery = MongoSpringSearch.mss(query);

		BasicDBList list = mongoQuery.getQueryObject().get("$and", BasicDBList.class);
		Document document = (Document) list.get(0);

		Assertions.assertEquals(Integer.valueOf(2), Integer.valueOf(list.size()));
		Assertions.assertTrue(document.containsKey("core:city"));
	}
	
}
