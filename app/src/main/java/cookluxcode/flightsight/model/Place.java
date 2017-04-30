package cookluxcode.flightsight.model;


import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

public class Place {
    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("path_progress")
    public double progress;

    @SerializedName("images")
    public List<String> imageUrls;

}
