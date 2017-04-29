package cookluxcode.flightsight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.maps.model.LatLng;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cookluxcode.flightsight.model.Places;
import cookluxcode.flightsight.retrofit.ServerApi;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateRouteActivity extends Activity {
    private static final String TAG = CreateRouteActivity.class.getSimpleName();
    private static final int REQUEST_CODE_FROM_LOCATION = 1;
    private static final int REQUEST_CODE_TO_LOCATION = 2;
    private static final String[] menuTitles = { "New route", "Trips", "Settings" };


    @BindView(R.id.from_text) EditText fromText;
    @BindView(R.id.to_text) EditText toText;
    @BindView(R.id.create_button) Button createButton;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.menu_list_view) ListView menuListView;

    LatLng from;
    LatLng to;
    Retrofit retrofit;
    ServerApi serverApi;
    int requestId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);
        ButterKnife.bind(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        serverApi = retrofit.create(ServerApi.class);
        requestId = new Random().nextInt();

        // Drawer layout
        menuListView.setAdapter(new ArrayAdapter<String>(this, R.layout.item_drawer_layout, menuTitles));
        menuListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    @OnClick(R.id.pick_from_button)
    public void onPickFromClick() {
        Log.d(TAG, "onPickFromClick()");
        Intent intent = new Intent(this, PickLocationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FROM_LOCATION);
    }

    @OnClick(R.id.pick_to_button)
    public void onPickToClick() {
        Log.d(TAG, "onPickToClick()");
        Intent intent = new Intent(this, PickLocationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TO_LOCATION);
    }

    @OnClick(R.id.create_button)
    public void onCreateClick() {
        Log.d(TAG, "onCreateClick()");
        if (from == null || to == null) {
            Toast.makeText(this, R.string.please_pick, Toast.LENGTH_SHORT).show();
            return;
        }
        serverApi.getPlaces(requestId, from.latitude, from.longitude, to.latitude, to.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Places>() {
                           @Override
                           public void accept(Places places) throws Exception {
                               Log.d(TAG, "Places request succesful");
                               createButton.setVisibility(View.VISIBLE);
                               progressBar.setVisibility(View.INVISIBLE);
                               // TODO: write to database
                               Intent intent = new Intent(CreateRouteActivity.this, FlightRouteActivity.class);
                               // TODO: add route id as extra
                               startActivity(intent);
                           }
                       },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "Getting places error");
                                Log.e(TAG, Log.getStackTraceString(throwable));
                            }
                        });
        createButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FROM_LOCATION:
                if (resultCode == RESULT_OK) {
                    from = data.getParcelableExtra(PickLocationActivity.LAT_LNG_EXTRA);
                    String name = Utils.getLocalityName(this, from);
                    fromText.setText(name != null ? name : from.toString());
                }
                break;
            case REQUEST_CODE_TO_LOCATION:
                if (resultCode == RESULT_OK) {
                    to = data.getParcelableExtra(PickLocationActivity.LAT_LNG_EXTRA);
                    String name = Utils.getLocalityName(this, to);
                    toText.setText(name != null ? name : to.toString());
                }
                break;

        }
    }

    private void selectMenuItem(int position) {

    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectMenuItem (position);
        }
    }
}
