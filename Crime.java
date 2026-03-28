//Represents an individual crime

public class Crime {
    private String latitude;
    private String longitude;
    private String crimeType;

    public Crime(String latitude, String longitude, String crimeType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.crimeType = crimeType;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getType() {
        return crimeType;
    }
}