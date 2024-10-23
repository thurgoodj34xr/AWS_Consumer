import main.Widget;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class WidgetTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSerializeToJson() throws Exception {
        Widget widget = new Widget("WidgetCreateRequest", "123", "widget-456", "Kellen Moore");
        widget.setLabel("Sample Widget");
        widget.setDescription("This is a sample widget.");
        widget.setOtherAttributes(Arrays.asList(
            new Widget.OtherAttribute("color", "red"),
            new Widget.OtherAttribute("size", "large")
        ));

        String jsonResult = objectMapper.writeValueAsString(widget);

        assertTrue(jsonResult.contains("\"requestType\":\"WidgetCreateRequest\""));
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
        Widget widget = new Widget("WidgetCreateRequest", "123", "widget-456", "Kellen Moore");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            widget.setOwner("123John");
        });

        assertEquals("Invalid owner format", exception.getMessage());
    }
    @Test
    void testOwnerURLConvert() {
        Widget widget = new Widget("WidgetCreateRequest", "123", "widget-456", "Kellen Moore");

        assertEquals("kellen-moore", widget.getOwnerURLString());
    }

    @Test
    void testKeyOutput() {
        Widget widget = new Widget("WidgetCreateRequest", "123", "widget-456", "Kellen Moore");

        assertEquals("widgets/kellen-moore/widget-456", widget.getKey());
    }
}
