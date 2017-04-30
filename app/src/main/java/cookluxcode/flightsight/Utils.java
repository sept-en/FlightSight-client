package cookluxcode.flightsight;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cookluxcode.flightsight.model.Place;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static String getLocalityNameEng(Context context, LatLng latLng) {
        Geocoder gcd = new Geocoder(context, Locale.US);
        try {
            List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String name = address.getCountryName();
                if (address.getLocality() != null)
                    name = address.getLocality();
                return name;
            }
        } catch (IOException ex) {
            Log.d(TAG, Log.getStackTraceString(ex));
        }
        return null;
    }

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

    /**
     *
     * @param context
     * @param start
     * @param end
     * @param step in meters
     * @return
     */
    public static Observable<Pair<Place, String>> getLocalities(
            final Context context,
            final LatLng start,
            final LatLng end,
            final double step) {
        return new Observable<Pair<Place, String>>() {
            @Override
            protected void subscribeActual(Observer<? super Pair<Place, String>> observer) {
                observer.onSubscribe(new Disposable() {
                    boolean disposed = false;

                    @Override
                    public void dispose() {
                        disposed = true;
                    }

                    @Override
                    public boolean isDisposed() {
                        return disposed;
                    }
                });
                double stepFloat = step / distance(start, end);
                String lastName = null;
                for (float t = 0; t < 1; t += stepFloat) {
                    LatLng loc = getIntermediatePoint(start, end, t);
                    String name = getLocalityName(context, loc);
                    String engName = getLocalityNameEng(context, loc);
                    if (name != null && !name.equals(lastName)) {
                        Place place = new Place();
                        place.latitude = loc.latitude;
                        place.longitude = loc.longitude;
                        place.progress = t;
                        place.name = name;
                        place.imageUrls = new ArrayList<>();
                        place.description = "";

                        observer.onNext(new Pair<>(place, engName));
                        lastName = name;
                    }
                }
                observer.onComplete();
            }
        };
    }

    public static LatLng getIntermediatePoint(LatLng start, LatLng end, double t) {
        int[] array = getIntermediatePoint(
                (int) (start.latitude * 1000000),
                (int) (start.longitude * 1000000),
                (int) (end.latitude * 1000000),
                (int) (end.longitude * 1000000),
                t);
        return new LatLng((double)array[0] / 1000000, (double) array[1] / 1000000);
    }

    public static float distance(LatLng start, LatLng end) {
        Location loc1 = new Location("");
        loc1.setLatitude(start.latitude);
        loc1.setLongitude(start.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(end.latitude);
        loc2.setLongitude(end.longitude);

        return loc1.distanceTo(loc2);
    }

    public static int[] getIntermediatePoint(
            int startLatMicroDeg,
            int startLonMicroDeg,
            int endLatMicroDeg,
            int endLonMicroDeg,
            double t // How much of the distance to use, from 0 through 1
    ) {
        // Convert microdegrees to radians
        double alatRad=Math.toRadians((double)startLatMicroDeg/1000000);
        double alonRad=Math.toRadians((double)startLonMicroDeg/1000000);
        double blatRad=Math.toRadians((double)endLatMicroDeg/1000000);
        double blonRad=Math.toRadians((double)endLonMicroDeg/1000000);
        // Calculate distance in longitude
        double dlon=blonRad-alonRad;
        // Calculate common variables
        double alatRadSin=Math.sin(alatRad);
        double blatRadSin=Math.sin(blatRad);
        double alatRadCos=Math.cos(alatRad);
        double blatRadCos=Math.cos(blatRad);
        double dlonCos=Math.cos(dlon);
        // Find distance from A to B
        double distance=Math.acos(alatRadSin*blatRadSin +
                alatRadCos*blatRadCos *
                        dlonCos);
        // Find bearing from A to B
        double bearing=Math.atan2(
                Math.sin(dlon) * blatRadCos,
                alatRadCos*blatRadSin -
                        alatRadSin*blatRadCos*dlonCos);
        // Find new point
        double angularDistance=distance*t;
        double angDistSin=Math.sin(angularDistance);
        double angDistCos=Math.cos(angularDistance);
        double xlatRad = Math.asin( alatRadSin*angDistCos +
                alatRadCos*angDistSin*Math.cos(bearing) );
        double xlonRad = alonRad + Math.atan2(
                Math.sin(bearing)*angDistSin*alatRadCos,
                angDistCos-alatRadSin*Math.sin(xlatRad));
        // Convert radians to microdegrees
        int xlat=(int)Math.round(Math.toDegrees(xlatRad)*1000000);
        int xlon=(int)Math.round(Math.toDegrees(xlonRad)*1000000);
        if(xlat>90000000)xlat=90000000;
        if(xlat<-90000000)xlat=-90000000;
        while(xlon>180000000)xlon-=360000000;
        while(xlon<=-180000000)xlon+=360000000;
        return new int[]{xlat,xlon};
    }
}
