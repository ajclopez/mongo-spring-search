package io.github.ajclopez.mss.pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SearchPatternsTest {

    @Test
    void canUseDefaultFlagWithNullOptions() {
        int flags = SearchPatterns.getFlags(null);
        Assertions.assertEquals(0x00, flags);
    }

    @Test
    void canUseDefaultFlagWithUnknownRegexFlag() {
        int flags = SearchPatterns.getFlags("t");
        Assertions.assertEquals(0x00, flags);
    }

}
