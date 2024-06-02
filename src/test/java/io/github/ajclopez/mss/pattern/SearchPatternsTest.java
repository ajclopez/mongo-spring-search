package io.github.ajclopez.mss.pattern;

import org.junit.Assert;
import org.junit.Test;

public class SearchPatternsTest {

    @Test
    public void canUseDefaultFlagWithNullOptions() {
        int flags = SearchPatterns.getFlags(null);
        Assert.assertEquals(0x00, flags);
    }

    @Test
    public void canUseDefaultFlagWithUnknownRegexFlag() {
        int flags = SearchPatterns.getFlags("t");
        Assert.assertEquals(0x00, flags);
    }

}
