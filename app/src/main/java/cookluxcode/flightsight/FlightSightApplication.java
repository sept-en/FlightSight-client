package cookluxcode.flightsight;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by dan on 29.04.17.
 */

public class FlightSightApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}
