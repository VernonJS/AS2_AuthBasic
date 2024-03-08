package com.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SessionTokenAuthenticationDetailsProvider;
import com.oracle.bmc.retrier.RetryConfiguration;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;

import java.io.IOException;
import java.util.Base64;

public class SecretReader {
    private static SecretsClient secretsClient = SecretsClient.builder().build(getProvider());

    private  static ObjectMapper mapper = new ObjectMapper();
    private static AbstractAuthenticationDetailsProvider getProvider() {
        if(System.getenv("OCI_RESOURCE_PRINCIPAL_VERSION") == null){
            System.err.println("RPST MISSING, THIS IS DEV MODE");
           return getConfigAuthProvider();
        }else{

           return ResourcePrincipalAuthenticationDetailsProvider.builder().build();
        }
    }

    private static AbstractAuthenticationDetailsProvider getConfigAuthProvider() {

        final String configurationFilePath = "~/.oci/config";
        final String profile = "DEFAULT";
        SessionTokenAuthenticationDetailsProvider provider =
                null;
        try {
            provider = new SessionTokenAuthenticationDetailsProvider(configurationFilePath, profile);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create an Authentication provider: " + e.getMessage());
        }
        return provider;
    }

    public JsonNode getSecretContents(String ocid) throws IOException {

        //print env vars in Functions container
        System.out.println("OCI_RESOURCE_PRINCIPAL_VERSION " + System.getenv("OCI_RESOURCE_PRINCIPAL_VERSION"));
        System.out.println("OCI_RESOURCE_PRINCIPAL_REGION " + System.getenv("OCI_RESOURCE_PRINCIPAL_REGION"));
        System.out.println("OCI_RESOURCE_PRINCIPAL_RPST " + System.getenv("OCI_RESOURCE_PRINCIPAL_RPST"));
        System.out.println("OCI_RESOURCE_PRINCIPAL_PRIVATE_PEM " + System.getenv("OCI_RESOURCE_PRINCIPAL_PRIVATE_PEM"));


        GetSecretBundleRequest secretBundleRequest = GetSecretBundleRequest.builder()
                .secretId(ocid).build();


        secretBundleRequest.setRetryConfiguration(RetryConfiguration.SDK_DEFAULT_RETRY_CONFIGURATION);



        GetSecretBundleResponse secretBundleResponse = secretsClient.getSecretBundle(secretBundleRequest);

        Base64SecretBundleContentDetails base64SecretBundleContentDetails = (Base64SecretBundleContentDetails)
                secretBundleResponse.getSecretBundle().getSecretBundleContent();

        String content = base64SecretBundleContentDetails.getContent();

        byte[] deCodedContent = Base64.getDecoder().decode(content);
        return mapper.readValue(deCodedContent, JsonNode.class);
//        return content;
    }
}
