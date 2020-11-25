package com.example.passwordwallet.Helpers;

import java.util.Random;

public class GetSaltStringHelper {

    protected static String getSaltString(String SALTCHARS) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 20) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

}
