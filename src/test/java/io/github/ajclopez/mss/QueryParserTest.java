package io.github.ajclopez.mss;

import io.github.ajclopez.mss.model.CastType;
import io.github.ajclopez.mss.model.SearchCriteria;
import io.github.ajclopez.mss.parser.QueryParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

class QueryParserTest {


    @Test
    void whenQueryNotMatchThenReturnNull() {

        SearchCriteria result = io.github.ajclopez.mss.parser.QueryParser.criteriaParser("", null);
        Assertions.assertNull(result);
    }

    @Test
    void whenRegexNotMatchWithPatternThenReturnPatternWithValue() {

        String regex = "^58";
        Object result = io.github.ajclopez.mss.parser.QueryParser.parseValue(regex, CastType.PATTERN);
        Assertions.assertEquals(Pattern.compile(regex).toString(), result.toString());
    }

    @Test
    void whenFalseValueThenReturnBooleanWithFalseValue() {

        Object result = io.github.ajclopez.mss.parser.QueryParser.parseValue("false", null);
        Assertions.assertEquals(false, result);
    }

    @Test
    void whenValueIsNullThenReturnNullValue() {

        Object result = QueryParser.parseValue(null, null);
        Assertions.assertNull(result);
    }

}
