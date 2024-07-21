package io.github.ajclopez.mss.parser;

import io.github.ajclopez.mss.model.CastType;
import io.github.ajclopez.mss.model.SearchCriteria;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

class QueryParserTest {


    @Test
    void whenQueryNotMatchThenReturnNullWhenCasterIsNull() {

        SearchCriteria result = QueryParser.criteriaParser("", null);
        Assertions.assertNull(result);
    }

    @Test
    void whenValueIsNullThenReturnNullValueWhenCasterIsNull() {

        Object result = QueryParser.parseValue(null, null);
        Assertions.assertNull(result);
    }

    @Test
    void whenValueIsStringWithCommaThenReturnListWhenCasterIsNull() {

       Object result = QueryParser.parseValue("value1,value2,value3", null);

       Assertions.assertNotNull(result);
       Assertions.assertInstanceOf(List.class, result);

       var list = (List<?>) result;
       Assertions.assertEquals(3, list.size());
    }

    @Test
    void whenTrueValueThenReturnBooleanWithTrueValueWhenCasterIsNull() {

        Object result = QueryParser.parseValue("true", null);
        Assertions.assertEquals(true, result);
    }

    @Test
    void whenFalseValueThenReturnBooleanWithFalseValueWhenCasterIsNull() {

        Object result = QueryParser.parseValue("false", null);
        Assertions.assertEquals(false, result);
    }

    @Test
    void whenNullStringThenReturnNullValueWhenCasterIsNull() {

        Object result = QueryParser.parseValue("null", null);
        Assertions.assertNull(result);
    }

    @Test
    void whenDateTimeStringThenReturnInstantValueWhenCasterIsNull() {

        Object result = QueryParser.parseValue("2024-07-21T12:00:00.000Z", null);
        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Instant.class, result);
    }

    @Test
    void whenRegexStringThenReturnPatternWhenCasterIsNull() {

        String regex = "^58";
        Object result = QueryParser.parseValue(regex, null);
        Assertions.assertEquals(Pattern.compile(regex).toString(), result.toString());
    }

    @Test
    void whenObjectIdStringThenReturnObjectIdWhenCasterIsNull() {

        Object result = QueryParser.parseValue("6674249e4d854906d60314ce", null);
        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(ObjectId.class, result);
        Assertions.assertEquals("6674249e4d854906d60314ce", result.toString());
    }

    @Test
    void whenStringValueThenReturnStringWhenCasterIsNull() {

        Object result = QueryParser.parseValue("firstname", null);
        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(String.class, result);
        Assertions.assertEquals("firstname", result);
    }

    @Test
    void whenTrueValueThenReturnBooleanWithTrueWhenCasterIsBoolean() {

        Object result = QueryParser.parseValue("true", CastType.BOOLEAN);
        Assertions.assertEquals(true, result);
    }

    @Test
    void whenFalseValueThenReturnBooleanWithFalseWhenCasterIsBoolean() {

        Object result = QueryParser.parseValue("false", CastType.BOOLEAN);
        Assertions.assertEquals(false, result);
    }

    @Test
    void whenDateTimeStringThenReturnInstantValueWhenCasterIsDate() {

        Object result = QueryParser.parseValue("2024-07-21T12:00:00.000Z", CastType.DATE);
        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Instant.class, result);
    }

    @Test
    void whenNumberValueThenReturnNumberWhenCasterIsNumber() {

        Object result = QueryParser.parseValue("10", CastType.NUMBER);
        Assertions.assertInstanceOf(Number.class, result);
        Assertions.assertEquals(10L, result);
    }

    @Test
    void whenRegexValueThenReturnPatternWhenCasterIsPattern() {

        String regex = "^58";
        Object result = QueryParser.parseValue(regex, CastType.PATTERN);
        Assertions.assertEquals(Pattern.compile(regex).toString(), result.toString());
    }

    @Test
    void whenObjectIdValueThenReturnObjectIdWhenCasterIsObjectId() {

        Object result = QueryParser.parseValue("6674249e4d854906d60314ce", CastType.OBJECT_ID);
        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(ObjectId.class, result);
        Assertions.assertEquals("6674249e4d854906d60314ce", result.toString());
    }

    @Test
    void whenStringValueThenReturnStringWhenCasterIsString() {

        Object result = QueryParser.parseValue("firstname", CastType.STRING);
        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(String.class, result);
        Assertions.assertEquals("firstname", result);
    }
}
