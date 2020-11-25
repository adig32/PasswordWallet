package com.example.passwordwallet.Helpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DecryptHelperTest {
    private String password;
    private String secret;
    private String expected;
    private DecryptHelper decryptHelper;

    public DecryptHelperTest(String password, String secret, String expected) {
        super();
        this.password = password;
        this.secret = secret;
        this.expected = expected;
    }

    @Before
    public void initialize() {
        decryptHelper = new DecryptHelper();
    }

    @Parameterized.Parameters
    public static Collection input() {
        return Arrays.asList(new Object[][] {{"O9/bZRDQnHCDHSJWuhQ8tg==","haslo1","test1"}, {"FaVMG4b938AA0yWPZrqoeQ==","haslo2","test2"}, {"y+mUjA3G+QVCWW6IMoYoaw==","haslo3","test3"}});
    }

    @Test
    public void decrypt_DecryptedPassword_HashedPasswordAndSecretKeyAreValid() {
        assertEquals(expected, decryptHelper.decrypt(password, secret));
    }
}