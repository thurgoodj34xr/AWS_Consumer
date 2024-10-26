package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Widget {
    private String widgetId;
    private String owner;
    private String label;
    private String description;
    private List<OtherAttribute> otherAttributes;
    
    @JsonIgnore
    private String type; 
    @JsonIgnore
    private String requestId;
    @JsonIgnore
    private String key;
    
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

    @JsonIgnore
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

    public Map<String, AttributeValue> mapForDynamo() {
        Map<String, AttributeValue> itemValues = new HashMap<>();
    
        itemValues.put("id", AttributeValue.builder().s(getWidgetId()).build());  
        itemValues.put("owner", AttributeValue.builder().s(getOwner()).build());
        itemValues.put("label", AttributeValue.builder().s(getLabel()).build());
        itemValues.put("description", AttributeValue.builder().s(getDescription()).build());
    
        // Handle otherAttributes as a list of maps
        if (getOtherAttributes() != null && !getOtherAttributes().isEmpty()) {
            List<AttributeValue> otherAttributesList = getOtherAttributes().stream()
                .map(attr -> AttributeValue.builder()
                    .m(Map.of(
                        "name", AttributeValue.builder().s(attr.getName()).build(),
                        "value", AttributeValue.builder().s(attr.getValue()).build()
                    ))
                    .build())
                .collect(Collectors.toList());
            itemValues.put("otherAttributes", AttributeValue.builder().l(otherAttributesList).build());
        }
    
        return itemValues;
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
