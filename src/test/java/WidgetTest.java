import main.Widget;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WidgetTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSerializeToJson() throws Exception {
        Widget widget = new Widget("create", "123", "widget-456", "Kellen Moore");
        widget.setLabel("Sample Widget");
        widget.setDescription("This is a sample widget.");
        widget.setOtherAttributes(Arrays.asList(
            new Widget.OtherAttribute("color", "red"),
            new Widget.OtherAttribute("size", "large")
        ));

        String jsonResult = objectMapper.writeValueAsString(widget);

        assertTrue(jsonResult.contains("\"widgetId\":\"widget-456\""));
        assertTrue(jsonResult.contains("\"owner\":\"Kellen Moore\""));
    }

    @Test
    void testInvalidRequestType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Widget("InvalidType", "123", "widget-456", "Kellen Moore");
        });

        assertEquals("Invalid type", exception.getMessage());
    }

    @Test
    void testInvalidOwnerFormat() {
        Widget widget = new Widget("create", "123", "widget-456", "Kellen Moore");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            widget.setOwner("123John");
        });

        assertEquals("Invalid owner format", exception.getMessage());
    }
    @Test
    void testOwnerURLConvert() {
        Widget widget = new Widget("create", "123", "widget-456", "Kellen Moore");

        assertEquals("kellen-moore", widget.getOwnerURLString());
    }

    @Test
    void testKeyOutput() {
        Widget widget = new Widget("create", "123", "widget-456", "Kellen Moore");

        assertEquals("widgets/kellen-moore/widget-456", widget.getKey());
    }
    @Test
    void testMapForDynamo_WithAllAttributes() {
        Widget widget = new Widget("create", "123", "widget-456", "John Doe");
        widget.setLabel("Sample Widget");
        widget.setDescription("This is a sample widget.");

        Widget.OtherAttribute attr1 = new Widget.OtherAttribute("color", "red");
        Widget.OtherAttribute attr2 = new Widget.OtherAttribute("size", "large");
        widget.setOtherAttributes(Arrays.asList(attr1, attr2));

        Map<String, AttributeValue> result = widget.mapForDynamo();

        assertEquals("widget-456", result.get("id").s());
        assertEquals("John Doe", result.get("owner").s());
        assertEquals("Sample Widget", result.get("label").s());
        assertEquals("This is a sample widget.", result.get("description").s());

        List<AttributeValue> otherAttributes = result.get("otherAttributes").l();
        assertEquals(2, otherAttributes.size());

        assertEquals("color", otherAttributes.get(0).m().get("name").s());
        assertEquals("red", otherAttributes.get(0).m().get("value").s());
        assertEquals("size", otherAttributes.get(1).m().get("name").s());
        assertEquals("large", otherAttributes.get(1).m().get("value").s());
    }
}
