import main.S3;
import main.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


import java.util.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class S3Test {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private S3Client s3Client;

    private Widget widget;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Widget widget = new Widget("create", "1234", "widget-456", "Kellen Moore");
        widget.setLabel("Sample Widget");
        widget.setDescription("This is a sample widget.");
        widget.setOtherAttributes(Arrays.asList(
            new Widget.OtherAttribute("color", "red"),
            new Widget.OtherAttribute("size", "large")
        ));
    }

    @Test
    public void testCheckForRequestsSuccess() {
        // Mock ListObjectsV2 response
        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(S3Object.builder().key("testKey").build())
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        String result = S3.checkForRequests(s3Client,"testBucket");
        assertEquals("testKey", result);
    }

    @Test
    public void testCheckForRequestsFailure() {
        // Mock ListObjectsV2 an empty response
        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
        .contents(Collections.emptyList())
        .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        String result = S3.checkForRequests(s3Client,"testBucket");
        assertEquals(null, result);
    }

    @Test
    public void testPutWidgetS3BucketSuccess() {
        // Mock PutObject response
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        S3.putWidgetS3Bucket(s3Client,widget,"testBucket");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    public void testPutWidgetS3BucketFailure() {
        // Mock PutObject to throw an exception
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("PutObject failed").build());

        boolean result = S3.putWidgetS3Bucket(s3Client, widget, "testBucket");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertFalse(result);
    }
}