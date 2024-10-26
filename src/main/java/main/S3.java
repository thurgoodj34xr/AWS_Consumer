package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class S3 {
    private static ObjectMapper mapper = new ObjectMapper();

    public static boolean putWidgetS3Bucket(S3Client s3Client, Widget widget, String storageBucket){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(storageBucket)
            .key(widget.getKey())
            .build();

        // Upload JSON object to S3 as a string
        try{
            s3Client.putObject(putObjectRequest, RequestBody.fromString(mapper.writeValueAsString(widget)));
            Consumer.logger.info("JSON object uploaded successfully!");
            return true;
        }catch (JsonProcessingException e){
            Consumer.logger.info("JSON Object couldn't be converted");                
        }catch(S3Exception e){
            Consumer.logger.info("S3 rejected put request");                
        }
        return false;
    }

    public static String checkForRequests(S3Client s3Client, String requestBucket){
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
            .bucket(requestBucket)
            .maxKeys(1)
            .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listObjectsRequest);
        if (!listResponse.contents().isEmpty()) {
            S3Object firstObject = listResponse.contents().get(0);
            Consumer.logger.info("First Key: " + firstObject.key());
            return firstObject.key();
        } else {
            Consumer.logger.info("The bucket is empty.");
        }
        return null;
    }

    public static Widget requestKeyWidget(S3Client s3Client, String key, String requestBucket){
         GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(requestBucket)
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
                Widget widget = mapper.readValue(jsonString, Widget.class);
                
                Consumer.logger.info("Widget object created: " + widget);
                return widget;
            } catch (Exception e) {
                Consumer.logger.info("Error retrieving or parsing the object: " + e.getMessage());
            }
            return null;
    }

    public static boolean deleteKeyInS3(S3Client s3Client, String key, String requestBucket){
                try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(requestBucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            // Succeeds if no throw
            Consumer.logger.info("Object deleted successfully!");
            return true;
        } catch (S3Exception e) {
            Consumer.logger.info("Error deleting object: " + e.getMessage());
        }
        return false;
    }
}
