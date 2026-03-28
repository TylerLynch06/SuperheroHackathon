//Represents a request for assistance from a user, generated after they submit the assistance request button
import java.time.LocalDateTime;

public class AssistanceRequest {
    private double latitude;
    private double longitude;
    private String description;
    private LocalDateTime timestamp;

    //Constructor one: pass a LocalDateTime object for the timestamp
    public AssistanceRequest(double latitude, double longitude, String description, LocalDateTime timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.timestamp = timestamp;
    }

    //Constructor two: pass in a String object for timestamp
    public AssistanceRequest(double latitude, double longitude, String description, String timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.timestamp = DateTimeTools.stringToDate(timestamp);
    }

    //Constructor three: pass in no timestamp to get current time by default
    public AssistanceRequest(double latitude, double longitude, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}