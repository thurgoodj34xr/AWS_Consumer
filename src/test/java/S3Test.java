import main.S3;
import main.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


import java.util.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class S3Test {

    @Mock
    private S3Client s3Client;
    

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
}