/*
# oci-apigw-authorizer-idcs-java version 1.0.
#
# Copyright (c) 2020 Oracle, Inc.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
*/

package com.example.utils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * It contains the resource server configuration and constants
 * Like a properties file, but simpler
 */
public class ResourceServerConfig {

    //YOUR IDENTITY DOMAIN AND APPLICATION CREDENTIALS
    private String AS2_SERVICEUSER_B64;

    public String getAS2_SERVICEUSER_B64() {
        return AS2_SERVICEUSER_B64;
    }



    public  ResourceServerConfig(JsonNode secretContents) {


        AS2_SERVICEUSER_B64 = secretContents.get("AS2_ServiceUser").textValue();

    }
}
