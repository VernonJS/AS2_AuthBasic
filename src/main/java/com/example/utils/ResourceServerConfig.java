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
    private String IDCS_URL;


    //OIC CLIENT CREDENTIALS
    private String OIC_CLIENT_ID;
    private String OIC_CLIENT_SECRET;
    private String OIC_CLIENT_SCOPE;

    //INFORMATION ABOUT IDENTITY CLOUD SERVICES
    private  String JWK_URL ;
    private  String TOKEN_URL ;

    public String getIDCS_URL() {
        return IDCS_URL;
    }

    public String getOIC_CLIENT_ID() {
        return OIC_CLIENT_ID;
    }

    public String getOIC_CLIENT_SECRET() {
        return OIC_CLIENT_SECRET;
    }

    public String getOIC_CLIENT_SCOPE() {
        return OIC_CLIENT_SCOPE;
    }

    public String getJWK_URL() {
        return JWK_URL;
    }

    public String getTOKEN_URL() {
        return TOKEN_URL;
    }

    public  ResourceServerConfig(JsonNode secretContents) {

        IDCS_URL = secretContents.get("idcs_base_url").textValue();
        OIC_CLIENT_ID = secretContents.get("client_id").textValue();
        OIC_CLIENT_SECRET = secretContents.get("client_secret").textValue();
        OIC_CLIENT_SCOPE = secretContents.get("scope").textValue();

       JWK_URL = IDCS_URL + "/admin/v1/SigningCert/jwk";
       TOKEN_URL = IDCS_URL + "/oauth2/v1/token";
    }
}
