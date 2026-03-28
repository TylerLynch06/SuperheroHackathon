//Represents a user-reported crime, generated from pressing the report crime button
import java.time.LocalDateTime;

public class CrimeReport {
    private double latitude;
    private double longitude;
    private String crimeType;
    private LocalDateTime timestamp;

    //Constructor one: pass a LocalDateTime object for the timestamp
    public CrimeReport(double latitude, double longitude, String crimeType, LocalDateTime timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.crimeType = crimeType;
        this.timestamp = timestamp;
    }

    //Constructor two: pass in a String object for timestamp
    public CrimeReport(double latitude, double longitude, String crimeType, String timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.crimeType = crimeType;
        this.timestamp = DateTimeTools.stringToDate(timestamp);
    }

    //Constructor three: pass in no timestamp to get current time by default
    public CrimeReport(double latitude, double longitude, String crimeType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.crimeType = crimeType;
        this.timestamp = LocalDateTime.now();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return crimeType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}