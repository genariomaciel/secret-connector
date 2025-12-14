package com.leicam.secretconnector;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leicam.secretconnector.converter.impl.SecretConverters;
import com.leicam.secretconnector.models.DatabaseCredentials;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        
        String region = "us-east-1";
        String accessKey = "local-stack-id";
        String secretKey = "local-stack-secret";
        String endpoint = "http://localhost:4566";
        String secretName = "product_database_credentials";

        SecretManagerConnector connector = new SecretManagerConnector(Application.createForLocalStack(region, endpoint, accessKey, secretKey));

        logger.info("Secret como String: {}", connector.get(secretName));
        logger.info("Database Credentials: {}", connector.get(secretName, SecretConverters.asObject(DatabaseCredentials.class)));
        
        connector.close();

    }

    private static SecretsManagerClient createForLocalStack(String region, String endpoint, String accessKey, String secretKey) {
        return SecretsManagerClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint)) 
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
    }
}
