package main; 

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.concurrent.TimeUnit;

public class Consumer {

    public static void main(String[] args) {
        S3Client s3 = S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();

    }

    private static void loopPostToS3(S3Client s3){
        
        while (true) {
            String key = S3.checkForRequests(s3);
            
            if (key.isEmpty()) {
                sleepFor100Ms();
                continue;
            }

            Widget widget = S3.requestKeyWidget(s3, key);
            
            if (S3.putWidgetS3Bucket(s3, widget)) {
                S3.deleteKeyInS3(s3, key);
            }
        }
    }

    private static void loopPostToDynamo(S3Client s3){
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                                            .region(Region.US_EAST_1)
                                            .credentialsProvider(ProfileCredentialsProvider.create())
                                            .build();

        while (true) {
            String key = S3.checkForRequests(s3);
            
            if (key.isEmpty()) {
                sleepFor100Ms();
                continue;
            }

            Widget widget = S3.requestKeyWidget(s3, key);
            
            if (DynamoDB.putWidgetToDynamoDB(dynamoDbClient, widget)) {
                S3.deleteKeyInS3(s3, key);
            }
        }
    }

    private static void sleepFor100Ms() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted: " + e.getMessage());
        }
    }
}
