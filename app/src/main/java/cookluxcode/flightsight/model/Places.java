package cookluxcode.flightsight.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Places {

    @SerializedName("request_id")
    public int requestId;

    @SerializedName("objects")
    public List<Place> objects;
}
