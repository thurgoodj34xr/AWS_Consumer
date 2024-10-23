package main; 

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Consumer {

    private static String REQUEST_BUCKET = "usu-cs5250-hobbes-requests";
    private static String STORAGE_BUCKET = "usu-cs5250-hobbes-web";
    private static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) {

        S3Client s3 = S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();

        checkForRequests(s3);

        System.exit(0);
    }

    public static void putWidgetS3Bucket(S3Client s3Client, Widget widget){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(STORAGE_BUCKET)
            .key(widget.getKey())
            .build();

        // Upload JSON object to S3 as a string
        try{
            s3Client.putObject(putObjectRequest, RequestBody.fromString(mapper.writeValueAsString(widget)));
        }catch (JsonProcessingException e){
            System.out.println("JSON Object couldn't be converted");                
        }
        
        System.out.println("JSON object uploaded successfully!");
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
}
