package io.github.ajclopez.mss;

import io.github.ajclopez.mss.model.CastType;
import io.github.ajclopez.mss.model.SearchCriteria;
import io.github.ajclopez.mss.parser.QueryParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class QueryParserTest {


    @Test
    public void whenQueryNotMatchThenReturnNull() {

        SearchCriteria result = QueryParser.criteriaParser("", null);
        Assert.assertEquals(null, result);
    }

    @Test
    public void whenRegexNotMatchWithPatternThenReturnPatternWithValue() {

        String regex = "^58";
        Object result = QueryParser.parseValue(regex, CastType.PATTERN);
        Assert.assertEquals(Pattern.compile(regex).toString(), result.toString());
    }

    @Test
    public void whenFalseValueThenReturnBooleanWithFalseValue() {

        Object result = QueryParser.parseValue("false", null);
        Assert.assertEquals(Boolean.parseBoolean("false"), result);
    }

    @Test
    public void whenValueIsNullThenReturnNullValue() {

        Object result = QueryParser.parseValue(null, null);
        Assert.assertEquals(null, result);
    }

}
