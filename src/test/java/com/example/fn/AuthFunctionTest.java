/*
# oci-apigw-authorizer-idcs-java version 1.0.
#
# Copyright (c) 2020 Oracle, Inc.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
*/

package com.example.fn;

import com.example.utils.JWKUtil;
import com.example.utils.ResourceServerConfig;
import com.example.utils.SecretReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnproject.fn.testing.*;
import org.junit.*;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class AuthFunctionTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static class Result {
        // required
        public boolean active;
        public String principal;
        public String[] scope;
        public String expiresAt;

        // optional
        public String wwwAuthenticate;

        // optional
        public String clientId;

        // optional context
        public Map<String,String> context;
    }

    private static String INVALID_TOKEN = "Bearer invalid";
    private static String INPUT_FORMAT = "{\"type\":\"TOKEN\",\"token\": \"Bearer %s\",\"scope\":\"%s\",\"aud\":\"%s\",\"secretOcid\":\"%s\"}";

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

//    @Test
//    public void shouldReturnInactive() throws IOException {
//        final String input = "{\n" +
//            "  \"type\":\"TOKEN\",\n" +
//            "  \"token\": \"" + INVALID_TOKEN + "\"\n" +
//            "}";
//
//        testing.givenEvent().withBody(input).enqueue();
//        testing.thenRun(AuthFunction.class, "handleRequest");
//
//        FnResult fnResult = testing.getOnlyResult();
//
//        Result result = mapper.readValue(fnResult.getBodyAsString(), Result.class);
//        assertFalse(result.active);
//        assertEquals("Bearer error=\"invalid_token\", error_description=\"Invalid JWT serialization: Missing dot delimiter(s)\"", result.wwwAuthenticate);
//    }

    @Test
    public void shouldReturnActive() throws Exception {
        SecretReader secretReader = new SecretReader();
        JsonNode contents = secretReader.getSecretContents("ocid1.vaultsecret.oc1.phx.amaaaaaavt5ng4aaoxegfwn4wqmf3jevsfsm2bwulr4uwaintojgr2yudsja");

        ResourceServerConfig resourceServerConfig = new ResourceServerConfig(contents);

        String token = JWKUtil.getBearer(resourceServerConfig.getOIC_CLIENT_ID(),
                resourceServerConfig.getOIC_CLIENT_SECRET(), resourceServerConfig.getOIC_CLIENT_SCOPE(),
                resourceServerConfig.getTOKEN_URL());
        System.out.println(token);

        final String input = String.format(INPUT_FORMAT, token,
                resourceServerConfig.getOIC_CLIENT_SCOPE().split("com:443")[1],
                resourceServerConfig.getOIC_CLIENT_SCOPE().split("urn:")[0],
                "ocid1.vaultsecret.oc1.phx.amaaaaaavt5ng4aaoxegfwn4wqmf3jevsfsm2bwulr4uwaintojgr2yudsja");

        System.out.println(input);
        testing.givenEvent().withBody(input).enqueue();
        testing.thenRun(AuthFunction.class, "handleRequest");

        FnResult fnResult = testing.getOnlyResult();

        Result result = mapper.readValue(fnResult.getBodyAsString(), Result.class);
        assertTrue(result.active);
        assertNotNull(result.context.get("authorization"));
        System.out.println(fnResult.getBodyAsString());
    }
}