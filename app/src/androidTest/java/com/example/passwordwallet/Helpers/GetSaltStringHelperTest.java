package com.example.passwordwallet.Helpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class GetSaltStringHelperTest {
    private String saltChars;
    private String expected;
    private GetSaltStringHelper getSaltStringHelper;

    public GetSaltStringHelperTest(String saltChars, String expected) {
        super();
        this.saltChars = saltChars;
        this.expected = expected;
    }

    @Before
    public void initialize() {
        getSaltStringHelper = new GetSaltStringHelper();
    }

    @Parameterized.Parameters
    public static Collection input() {
        return Arrays.asList(new Object[][] {{"A","AAAAAAAAAAAAAAAAAAAA"}, {"B","BBBBBBBBBBBBBBBBBBBB"}, {"C","CCCCCCCCCCCCCCCCCCCC"}});
    }

    @Test
    public void getSaltString_GeneratedSalt_SaltCharsAreNotEmpty() {
        assertEquals(expected, getSaltStringHelper.getSaltString(saltChars));
    }
}