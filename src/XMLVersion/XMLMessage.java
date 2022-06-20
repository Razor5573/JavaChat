package XMLVersion;

public class XMLMessage {
    private String eventType;
    private String message;
    private String name;
    XMLMessage(String event, String name) {
        this.eventType = event;
        this.name = name;
        this.message = "";
    }
    XMLMessage(String event, String message, String name) {
        this.eventType = event;
        this.message = message;
        this.name = name;
    }

    public String getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }
}
