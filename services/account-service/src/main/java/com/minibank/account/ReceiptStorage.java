package com.minibank.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

// Saves receipts to S3. Works with LocalStack (endpoint set) and real AWS (endpoint empty).
@Component
public class ReceiptStorage {

    private static final Logger log = LoggerFactory.getLogger(ReceiptStorage.class);

    private final S3Client s3;
    private final String bucket;
    private final ObjectMapper objectMapper;

    public ReceiptStorage(@Value("${receipts.bucket}") String bucket,
                          @Value("${receipts.region}") String region,
                          @Value("${receipts.endpoint}") String endpoint,
                          ObjectMapper objectMapper) {
        // No credentials here: the SDK looks for them in env vars, ~/.aws/credentials, or an IAM role
        S3ClientBuilder builder = S3Client.builder().region(Region.of(region));
        if (!endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint)) // talk to LocalStack instead of AWS
                   .forcePathStyle(true);                  // localhost:4566/bucket/key, not bucket.localhost
        }
        this.s3 = builder.build();
        this.bucket = bucket;
        this.objectMapper = objectMapper;
    }

    public void save(Receipt receipt) {
        String key = "receipts/" + receipt.transferId() + ".json";
        try {
            String json = objectMapper.writeValueAsString(receipt);
            s3.putObject(request -> request.bucket(bucket).key(key).contentType("application/json"),
                    RequestBody.fromString(json));
            log.info("Saved receipt s3://{}/{}", bucket, key);
        } catch (JsonProcessingException | RuntimeException e) {
            // The money has already moved, so we must not fail the transfer here. Log it loudly instead.
            log.error("Could not save receipt s3://{}/{}", bucket, key, e);
        }
    }
}
