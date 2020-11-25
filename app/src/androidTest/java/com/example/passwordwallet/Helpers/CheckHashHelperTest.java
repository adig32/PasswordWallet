package com.example.passwordwallet.Helpers;

import org.junit.Test;

import static org.junit.Assert.*;

public class CheckHashHelperTest {

    @Test
    public void checkHash() {
        CheckHashHelper checkHashHelper = new CheckHashHelper(new CheckHashHelper() {
            public int GetArrayLength(String login) {
                return 4;
            }
        });
        int expected = 4;
        assertEquals(expected,checkHashHelper.checkHash("user","password",1));
    }
}