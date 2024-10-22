package main;

import java.util.List;
import java.util.regex.Pattern;

public class Widget {
    private String requestType; 
    private String requestId;
    private String widgetId;
    private String owner;
    private String label;
    private String description;
    private List<OtherAttribute> otherAttributes;

    // Constructor
    public Widget(String requestType, String requestId, String widgetId, String owner) {
        this.requestId = requestId;
        this.widgetId = widgetId;
        setRequestType(requestType);
        setOwner(owner);
    }

    // Getters and Setters
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        if (!Pattern.matches("WidgetCreateRequest|WidgetDeleteRequest|WidgetUpdateRequest", requestType)) {
            throw new IllegalArgumentException("Invalid type");
        }
        this.requestType = requestType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerURLString(){
        return owner.toLowerCase().replace(" ", "-");
    }

    public void setOwner(String owner) {
        if (!Pattern.matches("[A-Za-z ]+", owner)) {
            throw new IllegalArgumentException("Invalid owner format");
        }
        this.owner = owner;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OtherAttribute> getOtherAttributes() {
        return otherAttributes;
    }

    public void setOtherAttributes(List<OtherAttribute> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }

    public static class OtherAttribute {
        private String name;
        private String value;

        // Constructor
        public OtherAttribute(String name, String value) {
            this.name = name;
            this.value = value;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
