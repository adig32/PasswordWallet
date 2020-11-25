package com.example.passwordwallet.Helpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CalculateHMACHelperTest {
    private String text;
    private String key;
    private String expected;
    private CalculateHMACHelper calculateHMACHelper;

    public CalculateHMACHelperTest(String text, String key, String expected) {
        super();
        this.text = text;
        this.key = key;
        this.expected = expected;
    }

    @Before
    public void initialize() {
        calculateHMACHelper = new CalculateHMACHelper();
    }

    @Parameterized.Parameters
    public static Collection input() {
        return Arrays.asList(new Object[][] {{"test1","secret1","IZN0FQwZp41NYwNlFcn8y7/TOVpTLSx89pzjz8oc10AtTfUiV4VIstornTNRkh0SrWl15R8mAga5U7imtVEmdw=="},
                {"test2","secret2","XtzNVQqrvKkuuhQJcduNSiutwAgBTDtYkYuKWsw8p7y/3J9RyW8sCqHmaIqmAZSxm05BdhhfW2jCbk8AzbAVWQ=="},
                {"test3","secret3","DksRKKczSF3agyCYSoXzQanYWFquaayA6FWOxYOjkeln4XZ8viEY+1R6SJDkEABvi57dlTTny4WUN1M2Wfh/Aw=="}});
    }

    @Test
    public void calculateHMAC_GeneratedHMAC_PasswordAndKeyAreNotEmpty() {
        assertEquals(expected, calculateHMACHelper.calculateHMAC(text, key));
    }
}