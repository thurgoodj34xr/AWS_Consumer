package main;

import java.util.List;
import java.util.regex.Pattern;

public class Widget {
    private String type; 
    private String requestId;
    private String widgetId;
    private String owner;
    private String label;
    private String description;
    private List<OtherAttribute> otherAttributes;

    // Default Constructor
    public Widget() {}

    // Constructor
    public Widget(String requestType, String requestId, String widgetId, String owner) {
        this.requestId = requestId;
        this.widgetId = widgetId;
        setType(requestType);
        setOwner(owner);
    }

        // Constructor
        public Widget(String requestType, String requestId, String widgetId, String owner, String label, String description, List<OtherAttribute>otherAttributes) {
            this.requestId = requestId;
            this.widgetId = widgetId;
            setType(requestType);
            setOwner(owner);
            this.label = label;
            this.description = description;
            this.otherAttributes = otherAttributes;
        }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String requestType) {
        if (!Pattern.matches("create|delete|update", requestType)) {
            throw new IllegalArgumentException("Invalid type");
        }
        this.type = requestType;
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

    public String getKey(){
        return "widgets/"+getOwnerURLString()+"/"+widgetId;
    }

    public static class OtherAttribute {
        private String name;
        private String value;

        // Default Constructor
        public OtherAttribute(){}

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
