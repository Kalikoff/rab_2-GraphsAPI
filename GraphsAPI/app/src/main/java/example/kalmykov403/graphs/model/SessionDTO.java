package example.kalmykov403.graphs.model;

import com.google.gson.annotations.SerializedName;

public class SessionDTO {
    @SerializedName("id")
    public Integer sessionId;

    @SerializedName("token")
    public String sessionToken;

    @SerializedName("timestamp")
    public Integer sessionTimestamp;
}