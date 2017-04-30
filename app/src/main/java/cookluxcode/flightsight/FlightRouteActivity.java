package cookluxcode.flightsight;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import cookluxcode.flightsight.db.PlaceModel;
import cookluxcode.flightsight.db.RouteModel;
import cookluxcode.flightsight.db.RouteModel_Table;
import cookluxcode.flightsight.model.Place;

public class FlightRouteActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String ROUTE_ID_EXTRA = "routeId";
    private GoogleMap map;
    private RouteModel routeModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        long id = getIntent().getLongExtra(ROUTE_ID_EXTRA, -1);
        if (id != -1) {
            routeModel = SQLite.select()
                    .from(RouteModel.class)
                    .where(RouteModel_Table.id.eq(id))
                    .querySingle();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Map<String, PlaceModel> places = new HashMap<>();
        map = googleMap;

        map.clear();
        // Add a marker in Sydney and move the camera
        if (routeModel != null) {
            for (PlaceModel place : routeModel.getPlaceModels()) {
                places.put(place.getName(), place);
                map.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                                .title(place.getName())).showInfoWindow();
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        PlaceModel place = places.get(marker.getTitle());

                        Dialog dialog = new Dialog(FlightRouteActivity.this);
                        dialog.setContentView(R.layout.dialog_place_details);

                        ((TextView) dialog.findViewById(R.id.title)).setText(place.getName());
                        ((TextView) dialog.findViewById(R.id.text)).setText(place.getDescription());
                        ImageView imageView = (ImageView) dialog.findViewById(R.id.image);
                        if (place.getImageUrl() != null)
                            Picasso.with(FlightRouteActivity.this).load(place.getImageUrl()).into(imageView);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                        return true;
                    }
                });
            }
        }
//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
