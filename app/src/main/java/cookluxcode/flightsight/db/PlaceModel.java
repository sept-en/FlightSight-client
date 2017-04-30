package cookluxcode.flightsight.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import cookluxcode.flightsight.model.Place;

/**
 * Created by dan on 29.04.17.
 */

@Table(database = FlightSightDatabase.class)
public class PlaceModel {
    @PrimaryKey(autoincrement = true)
    long id;

    @NotNull
    @Column
    String name;

    @NotNull
    @Column
    String description;

    @NotNull
    @Column
    double latitude;

    @NotNull
    @Column
    double longitude;

    @Column
    String imageUrl;

    @Column
    double progress;

    @ForeignKey(stubbedRelationship = true, tableClass = RouteModel.class)
    RouteModel routeModel;

    public PlaceModel() {

    }

    public PlaceModel(Place place, RouteModel routeModel) {
        this.name = place.name;
        this.description = place.description;
        this.latitude = place.latitude;
        this.longitude = place.longitude;
        this.imageUrl = place.imageUrls.size() != 0 ? place.imageUrls.get(0) : null;
        this.progress = place.progress;
        this.routeModel = routeModel;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
