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


    public static class Result {
        // required
        public boolean active = false;
        

        // optional context
        public Map<String, Object> context;
    }

    public Result handleRequest() {
        System.out.println("oci-apigw-authorizer-idcs-java START");
        Result result = new Result();


        try{

            String secretOcid = System.getenv("secretOcid");
            System.out.println("secretOcid" + secretOcid);
            SecretReader secretReader = new SecretReader();
            JsonNode secretContents = secretReader.getSecretContents(secretOcid);

            ResourceServerConfig resourceServerConfig = new ResourceServerConfig(secretContents);


            result.active = true;

            String authorizationHeader = resourceServerConfig.getAS2_SERVICEUSER_B64();
            System.out.println("authheader" + authorizationHeader);
            Map<String, Object> context = new HashMap<>();
            context.put("authorization",authorizationHeader);
            result.context = context;

        } catch (Throwable ex) {
            ex.printStackTrace();

            result.active = false;
        }

        System.out.println("oci-apigw-authorizer-idcs-java END");

        return result;
    }

}
