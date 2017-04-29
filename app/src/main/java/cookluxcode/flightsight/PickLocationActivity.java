package cookluxcode.flightsight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dan on 29.04.17.
 */

public class PickLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = PickLocationActivity.class.getSimpleName();
    public static final String LABEL_EXTRA = "labelExtra";
    public static final String LAT_LNG_EXTRA = "latLng";

    private GoogleMap map;
    private String label;
    private LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (getIntent().hasExtra(LABEL_EXTRA))
            label = getIntent().getStringExtra(LABEL_EXTRA);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        Log.d(TAG, "Fab click");
        Intent data = new Intent();
        if (location != null) {
            data.putExtra(LAT_LNG_EXTRA, (Parcelable) location);
            setResult(RESULT_OK, data);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
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
        map = googleMap;
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.clear();
        location = latLng;
        map.addMarker(new MarkerOptions().position(latLng).title(label != null ? label : ""));
    }
}
