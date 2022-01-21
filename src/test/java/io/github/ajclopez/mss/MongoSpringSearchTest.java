package io.github.ajclopez.mss;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBList;

import io.github.ajclopez.mss.exception.ArgumentNotValidException;
import io.github.ajclopez.mss.model.CastType;
import io.github.ajclopez.mss.model.Configuration;

public class MongoSpringSearchTest {

	
	@Test
	public void queryEmptyAndNullCreateQueryMongoDBEmpty() {
		Assert.assertNotNull(MongoSpringSearch.mss(null));
		Assert.assertNotNull(MongoSpringSearch.mss(""));
	}
		
	@Test
	public void findQuery() {
		
		String query = "age>=10&mobile=/^+34.*/&firstname=jhon&date>2021-01-08T00:00:00.000Z";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Assert.assertEquals(Integer.valueOf(query.split("&").length), Integer.valueOf(mongoQuery.getQueryObject().get("$and", BasicDBList.class).size()));
	}
	
	@Test
	public void findAllOperatorsInQuery() {
		
		String query = "age>=18&age<=21"
				+ "&mobile=/^+34.*/"
				+ "&date>2021-01-08T00:00:00.000Z&date<2021-01-08T23:59:59.999Z"
				+ "&city!=USA&!email&code=123456,98765&zone!=zone1,zone2&mobile"
				+ "&email!=/.*@example.com/";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Assert.assertEquals(Integer.valueOf(query.split("&").length), Integer.valueOf(mongoQuery.getQueryObject().get("$and", BasicDBList.class).size()));
	}
	
	@Test
	public void canUseAllTypeValueInQuery() {
		
		String query = "age=21&name=Elisa&data>2021-01-08T00:00:00.000Z&female=true&email=/.*@example.com/&flag=null";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Assert.assertEquals(Integer.valueOf(query.split("&").length), Integer.valueOf(mongoQuery.getQueryObject().get("$and", BasicDBList.class).size()));
	}
	
	@Test
	public void canUseRegex() {
		
		String query = "mobile=/^58/";
		Query mongoQuery = MongoSpringSearch.mss(query);
				
		Pattern pattern = Pattern.compile("/^58/");
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = Pattern.compile(pattern.pattern().replace("/", "")).pattern();
		
		Assert.assertEquals(expected, document.get("mobile").toString());	
	}
	
	@Test
	public void canUseRegexWithSpecialCharacters() {
		
		String query = "mobile=/^\\+58/";
		Query mongoQuery = MongoSpringSearch.mss(query);
				
		Pattern pattern = Pattern.compile("/^\\+58/");
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = Pattern.compile(pattern.pattern().replace("/", "")).pattern();
		
		Assert.assertEquals(expected, document.get("mobile").toString());	
	}

	@Test
	public void canUseRegexWithOptions() {
		
		String query = "firstname=/JOHN/i";
		Query mongoQuery = MongoSpringSearch.mss(query);
				
		Pattern pattern = Pattern.compile("/JOHN/", Pattern.CASE_INSENSITIVE);
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = Pattern.compile(pattern.pattern().replace("/", ""), Pattern.CASE_INSENSITIVE).pattern();		

		Assert.assertEquals(expected, document.get("firstname").toString());
	}
	
	@Test
	public void canUseRegexWithAllOptions() {
		
		String query = "firstname=/JOHN/gmixs";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Pattern pattern = Pattern.compile("/JOHN/", Pattern.CASE_INSENSITIVE);
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = Pattern.compile(pattern.pattern().replace("/", ""), Pattern.CASE_INSENSITIVE).pattern();		

		Assert.assertEquals(expected, document.get("firstname").toString());
		
	}
	
	@Test
	public void limitQuery() {
		
		String query = "limit=10";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Assert.assertEquals(Integer.valueOf(10), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test(expected = ArgumentNotValidException.class)
	public void limitIsAnArgumentNotValid() {
		
		String query = "limit=a";
		MongoSpringSearch.mss(query);
	}
	
	@Test
	public void canUseDefaultLimitValue() {
		
		Configuration options = new Configuration(null, 10, null);
		
		String query = "city=Madrid&limit=a";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Assert.assertEquals(Integer.valueOf(10), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test
	public void canUseMaxLimitValue() {
		
		Configuration options = new Configuration(null, 10, 500);
		
		String query = "city=Madrid&limit=100";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Assert.assertEquals(Integer.valueOf(100), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test
	public void canUseMaxLimitValueIfLimitParamExceedsRule() {
		
		Configuration options = new Configuration(null, 10, 500);
		
		String query = "city=Madrid&limit=10000";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Assert.assertEquals(Integer.valueOf(500), Integer.valueOf(mongoQuery.getLimit()));
	}
	
	@Test
	public void skipQuery() {
		
		String query = "skip=50";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Assert.assertEquals(Long.valueOf(50), Long.valueOf(mongoQuery.getSkip()));
	}
	
	@Test(expected = ArgumentNotValidException.class)
	public void skipIsAnArgumentNotValid() {
		
		String query = "skip=a";
		MongoSpringSearch.mss(query);
	}

	@Test
	public void projectionQuery() {
		
		String query = "fields=firstname,lastname,age";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("firstname"));
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("lastname"));
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("age"));
	}
	
	@Test
	public void sortQuery() {
		
		String query = "sort=+date,id,-city";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getSortObject().getInteger("date"));
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getSortObject().getInteger("id"));
		Assert.assertEquals(Integer.valueOf(-1), mongoQuery.getSortObject().getInteger("city"));
	}
	
	@Test
	public void canUseLogicalOperators() {
		
		String query = "filter=(country=Mexico OR country=Spain) and gender=female";
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		BasicDBList list = mongoQuery.getQueryObject().get("$and", BasicDBList.class);
		
		Assert.assertEquals(Integer.valueOf(2), Integer.valueOf(list.size()));
		Assert.assertEquals(Integer.valueOf(2), Integer.valueOf(((Document)list.get(0)).get("$or", BasicDBList.class).size()));
		
	}
	
	@Test
	public void canUseCastingString() {
		
		Map<String, CastType> caster = new HashMap<String, CastType>();
		caster.put("mobile", CastType.STRING);
		
		Configuration options = new Configuration(caster, null, null);
		
		String query = "mobile=134000000000";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		Assert.assertEquals("134000000000", document.getString("mobile"));
	}
	
	@Test
	public void canUseCastingNumber() {
		
		Map<String, CastType> caster = new HashMap<String, CastType>();
		caster.put("mobile", CastType.NUMBER);
		
		Configuration options = new Configuration(caster, null, null);
		
		String query = "mobile=134000000000";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		Assert.assertEquals(Long.valueOf(134000000000l), document.getLong("mobile"));
	}
	
	@Test(expected = ArgumentNotValidException.class)
	public void canThrowArgumentNotValidCastingNumber() {
		
		Map<String, CastType> caster = new HashMap<String, CastType>();
		caster.put("mobile", CastType.NUMBER);
		
		Configuration options = new Configuration(caster, null, null);
		
		MongoSpringSearch.mss("mobile=a", Optional.of(options));
	}
	
	@Test
	public void canUseCastingBoolean() {
		
		Map<String, CastType> caster = new HashMap<String, CastType>();
		caster.put("flag", CastType.BOOLEAN);
		
		Configuration options = new Configuration(caster, null, null);
		
		String query = "flag=false";
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		Assert.assertEquals(Boolean.valueOf(false), document.getBoolean("flag"));
	}
	
	@Test
	public void canUseCastingDate() {
		
		Map<String, CastType> caster = new HashMap<String, CastType>();
		caster.put("date", CastType.DATE);
		
		Configuration options = new Configuration(caster, null, null);
		
		Instant now = Instant.now(Clock.systemUTC());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
				
		String query = String.format("date=%s", formatter.format(now));
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		Assert.assertEquals(formatter.format(now), document.get("date").toString());
	}
	
	@Test
	public void canUseCastingPattern() {
		
		Map<String, CastType> caster = new HashMap<String, CastType>();
		caster.put("email", CastType.PATTERN);
		
		Configuration options = new Configuration(caster, null, null);
		
		Pattern pattern = Pattern.compile("/.*@example.com/");
		
		String query = String.format("email=%s", pattern.pattern());
		Query mongoQuery = MongoSpringSearch.mss(query, Optional.of(options));
		
		Document document = ((Document)mongoQuery.getQueryObject().get("$and", BasicDBList.class).get(0));
		
		String expected = Pattern.compile(pattern.pattern().replace("/", "")).pattern();
		
		Assert.assertEquals(expected, document.get("email").toString());
	}
	
	@Test
	public void complexQuery() {
		
		String query = "filter=(city=Madrid or city=Spanin) and gender=female"
				+ "&skip=50"
				+ "&limit=10"
				+ "&sort=-birthday"
				+ "&fields=firstname,lastname,age"
				+ "&name=/^an/&age>=18&age<=21";
		
		Query mongoQuery = MongoSpringSearch.mss(query);
		
		BasicDBList list = mongoQuery.getQueryObject().get("$and", BasicDBList.class);
				
		Assert.assertEquals(Integer.valueOf(4), Integer.valueOf(list.size()));
		Assert.assertEquals(Long.valueOf(50), Long.valueOf(mongoQuery.getSkip()));
		Assert.assertEquals(Integer.valueOf(10), Integer.valueOf(mongoQuery.getLimit()));
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("firstname"));
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("lastname"));
		Assert.assertEquals(Integer.valueOf(1), mongoQuery.getFieldsObject().getInteger("age"));
		Assert.assertEquals(Integer.valueOf(-1), mongoQuery.getSortObject().getInteger("birthday"));
	}
	
}
