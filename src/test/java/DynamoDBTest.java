import main.DynamoDB; 
import main.Widget; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DynamoDBTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private DynamoDB dynamoDB;

    private Widget widget;
    private SdkHttpResponse mockHttpResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize the common widget for tests
        widget = new Widget("create", "1234", "widget-456", "Kellen Moore");
        widget.setLabel("Sample Widget");
        widget.setDescription("This is a sample widget.");
        widget.setOtherAttributes(Arrays.asList(
            new Widget.OtherAttribute("color", "red"),
            new Widget.OtherAttribute("size", "large")
        ));
    }

    private void mockPutItemResponse(int statusCode) {
        mockHttpResponse = SdkHttpResponse.builder()
                .statusCode(statusCode)
                .build();

        when(dynamoDbClient.putItem(any(PutItemRequest.class)))
                .thenReturn((PutItemResponse) PutItemResponse.builder()
                        .sdkHttpResponse(mockHttpResponse)
                        .build());
    }

    @Test
    public void testPutWidgetToDynamoDB() {
        // Mock successful response
        mockPutItemResponse(200);

        boolean result = DynamoDB.putWidgetToDynamoDB(dynamoDbClient, widget, "testDynamo");

        // Create expected item from widget's mapForDynamo method
        Map<String, AttributeValue> expectedItem = widget.mapForDynamo();

        // Verify the method was called correctly
        verify(dynamoDbClient, times(1)).putItem(argThat((PutItemRequest request) -> {
            return "testDynamo".equals(request.tableName()) &&
                   request.item().equals(expectedItem);
        }));

        assertTrue(result); 
    }

    @Test
    public void testFailPutWidgetToDynamoDB() {
        // Mock failure response
        mockPutItemResponse(500);

        boolean result = DynamoDB.putWidgetToDynamoDB(dynamoDbClient, widget, "testDynamo");

        assertFalse(result); 
    }
}
