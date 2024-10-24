package main; 

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Consumer {

    private static String REQUEST_BUCKET = "usu-cs5250-hobbes-requests";
    private static String STORAGE_BUCKET = "usu-cs5250-hobbes-web";
    private static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) {

        S3Client s3 = S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();

        while (true) {
            String key = checkForRequests(s3);
            
            if (key.isEmpty()) {
                sleepFor100Ms();
                continue;
            }
    
            Widget widget = requestKeyWidget(s3, key);
            
            if (putWidgetS3Bucket(s3, widget)) {
                deleteKeyInS3(s3, key);
            }
    }
}

    public static boolean putWidgetS3Bucket(S3Client s3Client, Widget widget){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(STORAGE_BUCKET)
            .key(widget.getKey())
            .build();

        // Upload JSON object to S3 as a string
        try{
            s3Client.putObject(putObjectRequest, RequestBody.fromString(mapper.writeValueAsString(widget)));
            System.out.println("JSON object uploaded successfully!");
            return true;
        }catch (JsonProcessingException e){
            System.out.println("JSON Object couldn't be converted");                
        }
        return false;
    }

    public static String checkForRequests(S3Client s3Client){
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
            .bucket(REQUEST_BUCKET)
            .maxKeys(1)
            .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listObjectsRequest);
        if (!listResponse.contents().isEmpty()) {
            S3Object firstObject = listResponse.contents().get(0);
            System.out.println("First Key: " + firstObject.key());
            return firstObject.key();
        } else {
            System.out.println("The bucket is empty.");
        }
        return null;
    }

    public static Widget requestKeyWidget(S3Client s3Client, String key){
         GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(REQUEST_BUCKET)
                .key(key)
                .build();

            // Retrieve the object as a string
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(s3Client.getObject(getObjectRequest)))) {
                StringBuilder jsonStringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }
                String jsonString = jsonStringBuilder.toString();

                // Parse the JSON string into a Widget object
                ObjectMapper mapper = new ObjectMapper();
                Widget widget = mapper.readValue(jsonString, Widget.class);
                
                System.out.println("Widget object created: " + widget);
                return widget;
            } catch (Exception e) {
                System.out.println("Error retrieving or parsing the object: " + e.getMessage());
            }
            return null;
    }

    public static boolean deleteKeyInS3(S3Client s3Client, String key){
                try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(REQUEST_BUCKET)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            // Succeeds if no throw
            System.out.println("Object deleted successfully!");
            return true;
        } catch (S3Exception e) {
            System.err.println("Error deleting object: " + e.getMessage());
        }
        return false;
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
