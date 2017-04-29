package cookluxcode.flightsight;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    @Nullable
    public static String getLocalityName(Context context, LatLng latLng) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String name = address.getCountryName();
                if (address.getLocality() != null)
                    name = address.getLocality() + ", " + name;
                return name;
            }
        } catch (IOException ex) {
            Log.d(TAG, Log.getStackTraceString(ex));
        }
        return null;
    }
}
