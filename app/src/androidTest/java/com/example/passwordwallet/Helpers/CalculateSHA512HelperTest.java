package com.example.passwordwallet.Helpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CalculateSHA512HelperTest {
    private String text;
    private String expected;
    private CalculateSHA512Helper calculateSHA512Helper;

    public CalculateSHA512HelperTest(String text, String expected) {
        super();
        this.text = text;
        this.expected = expected;
    }

    @Before
    public void initialize() {
        calculateSHA512Helper = new CalculateSHA512Helper();
    }

    @Parameterized.Parameters
    public static Collection input() {
        return Arrays.asList(new Object[][] {{"test1","b16ed7d24b3ecbd4164dcdad374e08c0ab7518aa07f9d3683f34c2b3c67a15830268cb4a56c1ff6f54c8e54a795f5b87c08668b51f82d0093f7baee7d2981181"},
                {"test2","6d201beeefb589b08ef0672dac82353d0cbd9ad99e1642c83a1601f3d647bcca003257b5e8f31bdc1d73fbec84fb085c79d6e2677b7ff927e823a54e789140d9"},
                {"test3","cb872de2b8d2509c54344435ce9cb43b4faa27f97d486ff4de35af03e4919fb4ec53267caf8def06ef177d69fe0abab3c12fbdc2f267d895fd07c36a62bff4bf"}});
    }

    @Test
    public void calculateSHA512_GeneratedSHA512_PasswordIsNotEmpty() {
        assertEquals(expected, calculateSHA512Helper.calculateSHA512(text));
    }
}