package com.example.utils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SecretReaderTest {
//    @Test
    public void testGetSecretContents() throws IOException {
//        System.setProperty("isDev","true");
        SecretReader secretReader = new SecretReader();
        System.out.println(secretReader.getSecretContents("<ocid1.vaultsecret...>").toPrettyString());
    }

}