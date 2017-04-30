package cookluxcode.flightsight.db;

import com.google.android.gms.maps.model.LatLng;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by dan on 29.04.17.
 */

@Table(database = FlightSightDatabase.class)
public class RouteModel {
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    double startLat;

    @Column
    double startLng;

    @Column
    double finishLat;

    @Column
    double finishLng;

    List<PlaceModel> placeModels;

    public RouteModel() {

    }

    public RouteModel(LatLng start, LatLng finish) {
        startLat = start.latitude;
        startLng = start.longitude;
        finishLat = finish.latitude;
        finishLng = finish.longitude;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "placeModels")
    public List<PlaceModel> getPlaceModels() {
        if (placeModels == null || placeModels.isEmpty()) {
            placeModels = SQLite.select()
                    .from(PlaceModel.class)
                    .where(PlaceModel_Table.routeModel_id.eq(id))
                    .queryList();
        }
        return placeModels;
    }

    public void setId(long id) {
        this.id = id;
    }

}
