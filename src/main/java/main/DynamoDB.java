package main;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

public class DynamoDB {

    public static boolean putWidgetToDynamoDB(DynamoDbClient dynamoDbClient, Widget widget, String tableName) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(widget.mapForDynamo())
                .build();

        PutItemResponse response = dynamoDbClient.putItem(request);

        if (response.sdkHttpResponse().isSuccessful()) {
        Consumer.logger.info("Widget inserted into DynamoDB table.");
        return true;
        }
        Consumer.logger.info("Widget was not inserted into DynamoDB table.");
        return false;
    }
}
