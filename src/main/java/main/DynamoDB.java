package main;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

public class DynamoDB {

    public static boolean putWidgetToDynamoDB(DynamoDbClient dynamoDbClient, Widget widget) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName("widgets")
                .item(widget.mapForDynamo())
                .build();

        PutItemResponse response = dynamoDbClient.putItem(request);

        if (response.sdkHttpResponse().isSuccessful()) {
        System.out.println("Widget inserted into DynamoDB table.");
        return true;
        }
        System.out.println("Widget was not inserted into DynamoDB table.");
        return false;
    }
}
