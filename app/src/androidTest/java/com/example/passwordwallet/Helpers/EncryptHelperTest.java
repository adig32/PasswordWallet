package com.example.passwordwallet.Helpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class EncryptHelperTest {
    private String password;
    private String secret;
    private String expected;
    private EncryptHelper encryptHelper;

    public EncryptHelperTest(String password, String secret, String expected) {
        super();
        this.password = password;
        this.secret = secret;
        this.expected = expected;
    }

    @Before
    public void initialize() {
        encryptHelper = new EncryptHelper();
    }

    @Parameterized.Parameters
    public static Collection input() {
        return Arrays.asList(new Object[][] {{"test1","haslo1","O9/bZRDQnHCDHSJWuhQ8tg=="}, {"test2","haslo2","FaVMG4b938AA0yWPZrqoeQ=="}, {"test3","haslo3","y+mUjA3G+QVCWW6IMoYoaw=="}});
    }

    @Test
    public void encrypt_EncryptedPassword_PasswordAndSecretKeyAreValid() {
        assertEquals(expected, encryptHelper.encrypt(password, secret));
    }
}