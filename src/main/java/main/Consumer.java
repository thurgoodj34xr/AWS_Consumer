package main; 

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Consumer {

    private static String STORAGE_BUCKET = "usu-cs5250-hobbes-web";
    private static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) {
        System.out.println("Testing Object mapping and put");
        Widget widget = new Widget("WidgetCreateRequest", "123", "widget-456", "Kellen Moore");
        S3Client s3 = S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();

        putWidgetS3Bucket(s3, widget);

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
}
