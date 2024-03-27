/*
# oci-apigw-authorizer-idcs-java version 1.0.
#
# Copyright (c) 2020 Oracle, Inc.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
*/

package com.example.fn;

import com.example.utils.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jwt.JWTClaimsSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthFunction {


    public static class Input {
        public String type;
        public Data data;

    }

    public static class Data {
        public String token;

    }

    public static class Result {
        // required
        public boolean active = false;
        public String principal;
        public String[] scope;
        public String expiresAt;

        // optional
        public String wwwAuthenticate;

        // optional
        public String clientId;

        // optional context
        public Map<String, Object> context;
    }

    public Result handleRequest(Input input) {
        System.out.println("oci-apigw-authorizer-idcs-java START");
        Result result = new Result();


        try {

            String secretOcid = System.getenv("secretOcid");
            SecretReader secretReader = new SecretReader();
            JsonNode secretContents = secretReader.getSecretContents(secretOcid);

            ResourceServerConfig resourceServerConfig = new ResourceServerConfig(secretContents);


            result.active = true;

            String authorizationHeader = resourceServerConfig.getAS2_SERVICEUSER_B64();
            Map<String, Object> context = new HashMap<>();
            context.put("authorization",authorizationHeader);
            result.context = context;

        } catch (InvalidTokenException e) {
            e.printStackTrace();

            result.active = false;
            result.wwwAuthenticate = "Bearer error=\"invalid_token\", error_description=\"" + e.getMessage() + "\"";
        } catch (Throwable ex) {
            ex.printStackTrace();

            result.active = false;
            result.wwwAuthenticate = "Bearer error=\"invalid_token_claim\", error_description=\"" + ex.getMessage() + "\"";
        }

        System.out.println("oci-apigw-authorizer-idcs-java END");

        return result;
    }

}
