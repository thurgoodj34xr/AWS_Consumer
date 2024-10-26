package main; 

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class Consumer implements Runnable {
     // Create a logger instance
    public static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Option(names = {"--request-bucket", "-rb"}, 
    description = "The request bucket name",
    defaultValue = "usu-cs5250-hobbes-requests")
    private static String requestBucket;

    @Option(names = {"--widget-bucket", "-wb"}, 
        description = "The widget bucket name",
        defaultValue = "usu-cs5250-hobbes-web")  
    private static String widgetBucket;

    @Option(names = {"--dynamodb-widget-table", "-dwt"}, 
        description = "The DynamoDB widget table name")
    private static String widgetTable;

    @Override
    public void run() {
        logger.info("Request Bucket: " + requestBucket);
        S3Client s3 = S3Client.builder()
        .region(Region.US_EAST_1)
        .build();
    
        if (widgetTable != null)  {
            logger.info("Using DynamoDB Widget Table: " + widgetTable);
            loopPostToDynamo(s3);
        } 
        logger.info("Using S3 Bucket: " + widgetBucket);
        loopPostToS3(s3);
    }
    

    public static void main(String[] args) {

        int exitCode = new CommandLine(new Consumer()).execute(args);

        System.exit(exitCode);
    }

    private static void loopPostToS3(S3Client s3){
        
        while (true) {
            String key = S3.checkForRequests(s3, requestBucket);
            
            if (key == null) {
                sleepFor100Ms();
                continue;
            }

            Widget widget = S3.requestKeyWidget(s3, key, requestBucket);

            if(widget.getType()=="create"){
                if (S3.putWidgetS3Bucket(s3, widget, widgetBucket)) {
                    S3.deleteKeyInS3(s3, key, requestBucket);
                }
            }
            
        }
    }

    private static void loopPostToDynamo(S3Client s3){
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                                            .region(Region.US_EAST_1)
                                            .build();

        while (true) {
            String key = S3.checkForRequests(s3, requestBucket);
            
            if (key.isEmpty()) {
                sleepFor100Ms();
                continue;
            }

            Widget widget = S3.requestKeyWidget(s3, key, requestBucket);
            if(widget.getType()=="create"){      
                if (DynamoDB.putWidgetToDynamoDB(dynamoDbClient, widget, widgetTable)) {
                    S3.deleteKeyInS3(s3, key, requestBucket);
                }
            }
        }
    }

    private static void sleepFor100Ms() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.debug("Sleep interrupted: " + e.getMessage());
        }
    }
}
